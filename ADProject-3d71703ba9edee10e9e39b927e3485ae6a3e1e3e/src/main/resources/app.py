import joblib
import pickle
from surprise import dump
import numpy as np
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import linear_kernel
import re
from surprise import Reader, Dataset, SVD
from surprise.model_selection import cross_validate
from flask import Flask, render_template, request, jsonify

import joblib
from surprise.dump import dump

from joblib import load

import pickle

app = Flask(__name__)

# @app.route('/recommend', methods=['POST'])
# @app.route('/')
# def recommend():
#     # 从请求中获取目标菜谱名称
#     data = request.get_json()
#     target_recipe = data.get('recipe_name')

#     # 获得内容过滤模型的推荐
#     content_recommendations = content_model_recommendations(target_recipe)

#     # 获得SVD模型的推荐
#     svd_recommendations = svd_model_recommendations(target_recipe)

#     # 将推荐结果返回为 JSON 格式
#     recommendations = {
#         'content_recommendations': content_recommendations,
#         'svd_recommendations': svd_recommendations
#     }

#     return jsonify(recommendations)

# # 内容过滤模型的推荐函数
# def content_model_recommendations(target_recipe):
#     recommendations = [f"Content Filtered Recommendation {i}" for i in range(1, 6)]
#     return recommendations

# # SVD模型的推荐函数
# def svd_model_recommendations(target_recipe):
#     recommendations = [f"SVD Recommendation {i}" for i in range(1, 6)]
#     return recommendations

# if __name__ == '__main__':
#     app.run(debug=True, port=5000)


app = Flask(__name__)

# 加载模型和数据
loaded_svd = load('collaborative_model.joblib')
df_filtered = pd.read_csv('df_filtered.csv')

# 加载模型和数据
def content_recommender(df, target_recipe):
    tfidf = TfidfVectorizer()
    matrix = tfidf.fit_transform(df['merged'])
    cosine_similarities = linear_kernel(matrix,matrix)
    recipe_name = df['name']
    indices = pd.Series(df.index, index=df['name'])
    
    idx = indices[target_recipe]
    sim_scores = list(enumerate(cosine_similarities[idx]))
    sim_scores = sorted(sim_scores, key=lambda x: x[1], reverse=True)
    sim_scores = sim_scores[1:11]
    recipe_indices = [i[0] for i in sim_scores]
    print(recipe_name.iloc[recipe_indices])
    return recipe_indices


def collaborative_recommender(userId, recipe_indices, df, data):
    svd = SVD()
    # cross_validate(svd, data, measures=['RMSE', 'MAE'], cv=5, verbose=True)
    trainset = data.build_full_trainset()
    svd.fit(trainset)

    return svd
        
def output_recipe(userId, recipe_indices, df, svd): 
    recipes = df.iloc[recipe_indices][['name', 'num_users_rated', 'mean_rating', 'id']]
    recipes['est'] = recipes['id'].apply(lambda x: svd.predict(userId, x).est)
    recipes = recipes.sort_values('est', ascending=False)
    return recipes

try: 
    df = pd.read_csv('ADProject-3d71703ba9edee10e9e39b927e3485ae6a3e1e3e/src/main/resources/maybefinal_file4.csv')
except UnicodeDecodeError:
    df = pd.read_csv('ADProject-3d71703ba9edee10e9e39b927e3485ae6a3e1e3e/src/main/resources/maybefinal_file4.csv', encoding='ISO-8859-1')
print(df.shape)

# Change mean_rating column from str to numeric
df['mean_rating'] = pd.to_numeric(df['mean_rating'], errors='coerce')

ratings = pd.read_csv("ADProject-3d71703ba9edee10e9e39b927e3485ae6a3e1e3e/src/main/resources/RAW_interactions.csv")
# Get the ratings with the recipes that are in df
ratings = ratings[ratings['recipe_id'].isin(df['id'])]

# Get the number of users who rated each recipe
merged_df = pd.merge(ratings, df, left_on='recipe_id', right_on='id')
user_counts = merged_df.groupby('recipe_id')['user_id'].count().reset_index()
user_counts.columns = ['recipe_id', 'num_users_rated']

# Create a column in df with the data of number of users who rated each recipe
df['num_users_rated'] = user_counts['num_users_rated']

ratings = pd.read_csv("ADProject-3d71703ba9edee10e9e39b927e3485ae6a3e1e3e/src/main/resources/RAW_interactions.csv")
 # Get the ratings with the recipes that are in df
ratings = ratings[ratings['recipe_id'].isin(df['id'])]

reader = Reader()
data = Dataset.load_from_df(ratings[['user_id', 'recipe_id', 'rating']], reader)

# 首页路由
@app.route('/')
def home():
    return render_template('index.html')

# 推荐路由
@app.route('/recommend', methods=['POST'])
def recommend():
    target_recipe = request.form.get('target_recipe')
    user_id = int(request.form.get('user_id'))

 # 进行推荐
    recipe_indices = content_recommender(df_filtered, target_recipe)
    recipes_recommended = output_recipe(user_id, recipe_indices, df_filtered, loaded_svd)

# 渲染推荐结果页面
    return render_template('MLresult.html', recommendations=recipes_recommended.to_dict(orient='records'))

if __name__ == '__main__':
     app.run(debug=True)

