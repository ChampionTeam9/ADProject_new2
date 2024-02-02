package com.ad.teamnine.controller;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ad.teamnine.model.Ingredient;
import com.ad.teamnine.model.IngredientInfo;
import com.ad.teamnine.model.Member;
import com.ad.teamnine.model.Recipe;
import com.ad.teamnine.service.CsvService;
import com.ad.teamnine.service.IngredientService;
import com.ad.teamnine.service.MemberService;
import com.ad.teamnine.service.RecipeService;

@RestController
@RequestMapping("/api")
public class APIController {
	private final CsvService csvService;
	
	@Autowired
	MemberService memberService;
	@Autowired
	IngredientService ingredientService;
	@Autowired
	RecipeService recipeService;

	public APIController(CsvService csvService) {
		this.csvService = csvService;
	}
	
	@GetMapping("/readCsv")
    public List<String[]> readCsv() {
    	try {
    		URI uri = ClassLoader.getSystemResource("test.csv").toURI();
            Path path = Paths.get(uri);
            List<String[]> results = csvService.readCsv(path);
            saveEntities(results);
            return results;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
	
	@GetMapping("/nutrition")
	public IngredientInfo getNutritionInfo() {
		String appId = "a0eca928";
        String appKey = "2791c4e7ff627b1a94a4a8e41a6e0a14";
        String url = "https://api.edamam.com/api/nutrition-data?app_id=" + appId +
                     "&app_key=" + appKey +
                     "&nutrition-type=cooking&ingr=1banana";
		RestTemplate restTemplate = new RestTemplate();
		IngredientInfo ingredient = restTemplate.getForObject(url, IngredientInfo.class);
		return ingredient;
	}
	
	public void saveEntities(List<String[]> recipes) {
		for (int i = 1; i < recipes.size(); i++) {
			String[] currRecipe = recipes.get(i);
			//Create member
			int memberId = Integer.parseInt(currRecipe[3]);
			Member member = memberService.getMemberById(memberId);
			if (member == null) {
				member = new Member();
				member.setId(memberId);
				memberService.saveMember(member);
			}
			//Create recipe ingredients
			String ingredientsString = currRecipe[12];
			String[] ingredientsArr = extractIngredients(ingredientsString);
			List<Ingredient> ingredientsToAdd = new ArrayList<>();
			for (String ingredientText : ingredientsArr) {
				if (ingredientText.isEmpty())
					continue;
				Ingredient existingIngredient = ingredientService.getIngredientByfoodText(ingredientText);
				if (existingIngredient != null) {
					ingredientsToAdd.add(existingIngredient);
				}
				else {
					Ingredient ingredient = new Ingredient();
					ingredient.setFoodText(ingredientText);
					Ingredient savedIngredient = ingredientService.saveIngredient(ingredient);
					ingredientsToAdd.add(savedIngredient);
				}
			}
			//Create recipe
			int recipeId = Integer.parseInt(currRecipe[1]);
			String recipeName = currRecipe[0];
			String recipeDescription = currRecipe[9];
			double recipeRating = Double.parseDouble(currRecipe[16]);
			int preparationTime = Integer.parseInt(currRecipe[2]);
			int servings = Integer.parseInt(currRecipe[14]);
			int numberOfSteps = Integer.parseInt(currRecipe[7]);
			double calories = Double.parseDouble(currRecipe[18]);
			double protein = Double.parseDouble(currRecipe[22]);
			double carbohydrate = Double.parseDouble(currRecipe[24]);
			double sugar = Double.parseDouble(currRecipe[20]);
			double sodium = Double.parseDouble(currRecipe[21]);
			double fat = Double.parseDouble(currRecipe[19]);
			double saturatedFat = Double.parseDouble(currRecipe[23]);
			List<String> steps = Arrays.asList(extractSteps(currRecipe[8]));
			Recipe recipe = new Recipe(recipeId, recipeName, recipeDescription, recipeRating, preparationTime, 
					servings, numberOfSteps, member, calories, protein, carbohydrate, sugar, sodium, fat, saturatedFat, steps);
			recipeService.createRecipe(recipe);
			//Save recipes to ingredients
			for (Ingredient ingredient : ingredientsToAdd) {
				ingredient.getRecipes().add(recipe);
				ingredientService.saveIngredient(ingredient);
			}
		}
	}
	
	public String[] extractIngredients(String ingredientsString) {
        // Split the string based on commas within double quotes
        String[] ingredientsArr = ingredientsString.substring(1, ingredientsString.length() - 1).split("\",\"");
        for (int i = 0; i < ingredientsArr.length; i++) {
        	ingredientsArr[i] = ingredientsArr[i].trim();
        	ingredientsArr[i] = ingredientsArr[i].replaceAll("\\s+", " ");
        	ingredientsArr[i] = ingredientsArr[i].replaceAll("\"", "");
        }
        return ingredientsArr;
    }
	
	public static String[] extractSteps(String stepsString) {
        // Split the string based on commas followed by a space outside single quotes
        String[] stepsArr = stepsString.substring(1, stepsString.length() - 1).split("', '");
        // Remove surrounding single quotes from each step
        for (int i = 0; i < stepsArr.length; i++) {
            stepsArr[i] = stepsArr[i].replaceAll("'", "");
            stepsArr[i] = stepsArr[i].replaceAll("\"", "");
        }
        return stepsArr;
    }
}
