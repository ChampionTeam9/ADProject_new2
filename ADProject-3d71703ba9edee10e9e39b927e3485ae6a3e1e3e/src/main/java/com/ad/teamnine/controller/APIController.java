package com.ad.teamnine.controller;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ad.teamnine.model.*;
import com.ad.teamnine.model.IngredientInfo.Parsed;
import com.ad.teamnine.service.*;

@RestController
@RequestMapping("/api")
public class APIController {
	private final CsvService csvService;

	@Autowired
	UserService userService;
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
	public static ResponseEntity<Ingredient> getNutritionInfo(String foodText) {
		String appId = "a0eca928";
		String appKey = "2791c4e7ff627b1a94a4a8e41a6e0a14";
		String url = "https://api.edamam.com/api/nutrition-data?app_id=" + appId + "&app_key=" + appKey
				+ "&nutrition-type=cooking&ingr=" + foodText;
		RestTemplate restTemplate = new RestTemplate();
		IngredientInfo ingredientInfo = restTemplate.getForObject(url, IngredientInfo.class);
		List<Parsed> parsed = ingredientInfo.getIngredients().get(0).getParsed();
		if (parsed == null) {
			//No such ingredient found
			return new ResponseEntity<Ingredient>(HttpStatus.NOT_FOUND);
		}
		String status = parsed.get(0).getStatus();
		if (status.equals("MISSING_QUANTITY")) {
			return new ResponseEntity<Ingredient>(HttpStatus.BAD_REQUEST);
		}
		IngredientInfo.Nutrients nutrients = parsed.get(0).getNutrients();
		double protein = getDefaultIfNull(nutrients.getPROCNT().getQuantity());
		double calories = getDefaultIfNull(nutrients.getENERC_KCAL().getQuantity());
		double carbohydrate = getDefaultIfNull(nutrients.getCHOCDF().getQuantity());
		double sugar = getDefaultIfNull(nutrients.getSUGAR().getQuantity());
		double sodium = getDefaultIfNull(nutrients.getNA().getQuantity());
		double fat = getDefaultIfNull(nutrients.getFAT().getQuantity());
		double saturatedFat = getDefaultIfNull(nutrients.getFASAT().getQuantity());
		Ingredient ingredient = new Ingredient(foodText, protein, calories, carbohydrate, sugar, sodium, fat, saturatedFat);
		return new ResponseEntity<Ingredient>(ingredient, HttpStatus.OK);
	}
	
	private static double getDefaultIfNull(Double value) {
		// If any of the nutrients is null as info not available on API, set default to zero
	    return value != null ? value : 0.0;
	}

	public void saveEntities(List<String[]> recipes) {
		for (int i = 1; i < recipes.size(); i++) {
			String[] currRecipe = recipes.get(i);
			System.out.println(currRecipe[0]);
			// Create member
			int memberId = Integer.parseInt(currRecipe[3]);
			Member member = userService.getMemberById(memberId);
			if (member == null) {
				// Randomise their attributes as not provided in dataset
				String username = "member" + memberId;
				String password = "member" + memberId + "Password!";
				Random rnd = new Random();
				Double height = 140 + (190 - 140) * rnd.nextDouble();
				Double weight = 45 + (90 - 50) * rnd.nextDouble();
				LocalDate startDate = LocalDate.of(1950, 1, 1);
		        LocalDate endDate = LocalDate.of(2005, 12, 31);
		        long startEpochDay = startDate.toEpochDay();
		        long endEpochDay = endDate.toEpochDay();
		        long randomEpochDay = startEpochDay + (long) (Math.random() * (endEpochDay - startEpochDay + 1));
		        LocalDate birthdate = LocalDate.ofEpochDay(randomEpochDay);
		        String gender;
		        if (Math.random() > 0.5) 
		        	gender  = "Male";
		        else 
		        	gender = "Female";
				member = new Member(memberId, username, password, height, weight, birthdate, gender);
				userService.saveMember(member);
			}
			// Create recipe ingredients
			String ingredientsString = currRecipe[12];
			String[] ingredientsArr = extractIngredients(ingredientsString);
			List<Ingredient> ingredientsToAdd = new ArrayList<>();
			for (String ingredientText : ingredientsArr) {
				if (ingredientText.isEmpty())
					continue;
				Ingredient existingIngredient = ingredientService.getIngredientByfoodText(ingredientText);
				if (existingIngredient != null) {
					ingredientsToAdd.add(existingIngredient);
				} else {
					// Not able to use api for dataset as need to pay $$$ if we use > 20/min
					// Ingredient ingredient = getNutritionInfo(ingredientText);
					Ingredient ingredient = new Ingredient();
					ingredient.setFoodText(ingredientText);
					Ingredient savedIngredient = ingredientService.saveIngredient(ingredient);
					ingredientsToAdd.add(savedIngredient);
				}
			}
			// Create tagsList for the recipe
			String tagsString = currRecipe[5];
			List<String> tagsList = Arrays.asList(extractItems(tagsString));
			// Create recipe
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
			List<String> steps = Arrays.asList(extractItems(currRecipe[8]));
			Recipe recipe = new Recipe(recipeId, recipeName, recipeDescription, recipeRating, preparationTime, servings,
					numberOfSteps, member, calories, protein, carbohydrate, sugar, sodium, fat, saturatedFat, steps);
			recipe.setTags(tagsList);
			Recipe savedRecipe = recipeService.createRecipe(recipe);
			// Save recipes to ingredients
			for (Ingredient ingredient : ingredientsToAdd) {
				ingredient.getRecipes().add(savedRecipe);
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

	public static String[] extractItems(String stepsString) {
		// Split the string based on commas followed by a space outside single quotes
		String[] itemsArr = stepsString.substring(1, stepsString.length() - 1).split("', '");
		// Remove surrounding single quotes from each step
		for (int i = 0; i < itemsArr.length; i++) {
			itemsArr[i] = itemsArr[i].replaceAll("'", "");
			itemsArr[i] = itemsArr[i].replaceAll("\"", "");
		}
		return itemsArr;
	}
}


