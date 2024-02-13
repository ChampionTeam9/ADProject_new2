from flask import Flask, render_template, request, jsonify
from joblib import load
import pandas as pd
import numpy as np
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import linear_kernel
import re
from surprise import Reader, Dataset, SVD
from surprise.model_selection import cross_validate
from joblib import dump




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
    dump(svd, 'collaborative_model.joblib')
        
    recipes = df.iloc[recipe_indices][['name', 'num_users_rated', 'mean_rating', 'id']]
    recipes['est'] = recipes['id'].apply(lambda x: svd.predict(userId, x).est)
    recipes = recipes.sort_values('est', ascending=False)
    return recipes
    #return svd


def main():

    import os
    print("Current Working Directory:", os.getcwd())
 
   
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
    
    count_5_ratings = df['mean_rating'].value_counts().get(5.0, 0)
    print(f"Number of rows with rating = 5.0: {count_5_ratings}")
    
    # include only rows with higher mean ratings and with more user rating them
    print(df['mean_rating'].quantile(0.95)) # = 5.0
    print(df['num_users_rated'].quantile(0.90)) # = 9.0
    df_filtered = df[(df['mean_rating'] >= df['mean_rating'].quantile(0.95)) & ((df['num_users_rated'] >= df['num_users_rated'].quantile(0.90)))]
    df_filtered = df_filtered.reset_index(drop=True)  # Reset the index
    print(df_filtered.shape)
    
    target_recipe = 'bbq pork steak crock pot'
    # Add the target recipe into df_filtered if it was filtered out
    if target_recipe not in df_filtered['name'].values:
        print("not in df_filtered")
        target_recipe_row = df[df['name'] == target_recipe]
        new_row_values = target_recipe_row.values.flatten()
        new_row_index = len(df_filtered)
        df_filtered.loc[new_row_index] = new_row_values
    
    cols = ['name', 'description', 'ingredients', 'tags']
    # Create merged column
    df_filtered['merged'] = df_filtered.loc[:, cols].copy().apply(lambda row: ' '.join(row.values.astype(str)), axis=1)
    # Remove any non-alphanumeric characters (except for whitespace) from each element 
    df_filtered['merged'] = [re.sub(r'[^\w\s]', '', t) for t in df_filtered['merged']]
    
    # Do content filtering
    recipe_indices = content_recommender(df_filtered, target_recipe)

    #save model
    # content_model_path = 'content_model.pkl'
    # joblib.dump(df_filtered, content_model_path)
    
    reader = Reader()
    data = Dataset.load_from_df(ratings[['user_id', 'recipe_id', 'rating']], reader)
    
    # Set userId
    userId = 57222
    
    # Do collaborative filtering
    recipes_recommended = collaborative_recommender(userId, recipe_indices, df_filtered, data)
    print(recipes_recommended)

    df_filtered.to_csv('df_filtered.csv', index=False)

    #trained_svd_model = collaborative_recommender(userId, recipe_indices, df_filtered, data)
    #print(trained_svd_model)

    
    # svd_model_path = 'svd_model.dump'
    # dump_obj = (trained_svd_model, None)
    # with open(svd_model_path, 'wb') as file:
    #     pickle.dump(dump_obj, file)
    

if __name__ == "__main__":
  main()