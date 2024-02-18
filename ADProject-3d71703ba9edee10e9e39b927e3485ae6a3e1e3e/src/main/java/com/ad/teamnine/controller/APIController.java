package com.ad.teamnine.controller;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ad.teamnine.model.*;
import com.ad.teamnine.model.IngredientInfo.Parsed;
import com.ad.teamnine.service.*;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class APIController {
	private final CsvService csvService;
	private static final Logger logger = LoggerFactory.getLogger(APIController.class);

	@Autowired
	UserService userService;
	@Autowired
	IngredientService ingredientService;
	@Autowired
	RecipeService recipeService;
	@Autowired
	ReviewService reviewService;
	@Autowired
	ShoppingListItemService shopser;;

	Map<Integer, Integer> csvIdToDbIdRecipe = new HashMap<>();
	Map<Integer, Integer> csvIdToDbIdMember = new HashMap<>();

	public APIController(CsvService csvService) {
		this.csvService = csvService;
	}

	@GetMapping("/readCsv")
	public List<String[]> readCsv() {
		try {
			URI uri = ClassLoader.getSystemResource("test.csv").toURI();
			Path path = Paths.get(uri);
			List<String[]> results = csvService.readCsv(path);
//			List<String[]> results = csvService.readCsvWithDecoder(path, Charset.forName("ISO-8859-1"));
			saveEntities(results);
			URI uri2 = ClassLoader.getSystemResource("interactions_test.csv").toURI();
			Path path2 = Paths.get(uri2);
			List<String[]> results2 = csvService.readCsv(path2);
			saveInteractions(results2);
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
			// No such ingredient found
			return new ResponseEntity<Ingredient>(HttpStatus.NOT_FOUND);
		}
		String status = parsed.get(0).getStatus();
		if (status.equals("MISSING_QUANTITY")) {
			return new ResponseEntity<Ingredient>(HttpStatus.BAD_REQUEST);
		}
		IngredientInfo.Nutrients nutrients = parsed.get(0).getNutrients();
		double protein = 0.0;
		if (nutrients.getPROCNT() != null)
			protein = nutrients.getPROCNT().getQuantity();
		double calories = 0.0;
		if (nutrients.getENERC_KCAL() != null)
			calories = nutrients.getENERC_KCAL().getQuantity();
		double carbohydrate = 0.0;
		if (nutrients.getCHOCDF() != null)
			carbohydrate = nutrients.getCHOCDF().getQuantity();
		double sugar = 0.0;
		if (nutrients.getSUGAR() != null)
			sugar = nutrients.getSUGAR().getQuantity();
		double sodium = 0.0;
		if (nutrients.getNA() != null)
			sodium = nutrients.getNA().getQuantity();
		double fat = 0.0;
		if (nutrients.getFAT() != null)
			fat = nutrients.getFAT().getQuantity();
		double saturatedFat = 0.0;
		if (nutrients.getFASAT() != null)
			saturatedFat = nutrients.getFASAT().getQuantity();
		Ingredient ingredient = new Ingredient(foodText, protein, calories, carbohydrate, sugar, sodium, fat,
				saturatedFat);
		return new ResponseEntity<Ingredient>(ingredient, HttpStatus.OK);
	}

	public void saveEntities(List<String[]> recipes) {
		String[] imageFiles = { "download.jpg", "download2.jpg", "download3.jpg", "download4.jpg", "download5.jpg",
				"download6.jpg", "download7.jpg", "download8.jpg", "download9.jpg", "download10.jpg" };
		for (int i = 1; i < recipes.size(); i++) {
			String[] currRecipe = recipes.get(i);
			System.out.println(i + ": " + currRecipe[0]);
			// Create member
			int memberId = Integer.parseInt(currRecipe[3]);
			Member member = null;
			// In case member already exists
			if (csvIdToDbIdMember.containsKey(memberId)) {
				member = userService.getMemberById(csvIdToDbIdMember.get(memberId));
			} else {
				// Randomise their attributes as not provided in dataset
				String username = "member" + memberId;
				String password = "member" + memberId + "Password!";
				Random rnd = new Random();
				Double height = Math.round((140 + (190 - 140) * rnd.nextDouble()) * 10.0) / 10.0;
				Double weight = Math.round((45 + (90 - 50) * rnd.nextDouble()) * 10.0) / 10.0;
				LocalDate startDate = LocalDate.of(1950, 1, 1);
				LocalDate endDate = LocalDate.of(2005, 12, 31);
				long startEpochDay = startDate.toEpochDay();
				long endEpochDay = endDate.toEpochDay();
				long randomEpochDay = startEpochDay + (long) (Math.random() * (endEpochDay - startEpochDay + 1));
				LocalDate birthdate = LocalDate.ofEpochDay(randomEpochDay);
				String gender;
				if (Math.random() > 0.5)
					gender = "Male";
				else
					gender = "Female";
				member = new Member(username, password, height, weight, birthdate, gender, null);
				LocalDate startDate2 = LocalDate.of(2000, 1, 1);
				LocalDate endDate2 = LocalDate.of(2024, 2, 17);
				long startEpochDay2 = startDate2.toEpochDay();
				long endEpochDay2 = endDate2.toEpochDay();
				long randomEpochDay2 = startEpochDay2 + (long) (Math.random() * (endEpochDay2 - startEpochDay2 + 1));
				LocalDate registrationDate = LocalDate.ofEpochDay(randomEpochDay2);
				member.setRegistrationDate(registrationDate);
				Member savedMember = userService.saveMember(member);
				csvIdToDbIdMember.put(memberId, savedMember.getId());
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
			recipeName = recipeName.substring(0, 1).toUpperCase() + recipeName.substring(1);
			String recipeDescription = currRecipe[9];
			if (!recipeDescription.isEmpty()) {
				recipeDescription = recipeDescription.substring(0, 1).toUpperCase() + recipeDescription.substring(1);
			}
			double recipeRating = Double.parseDouble(currRecipe[16]);
			recipeRating = Math.round(recipeRating * 10.0) / 10.0;
			int numberOfRating = Integer.parseInt(currRecipe[17]);
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
			// Uppercase the start of each step
			for (int j = 0; j < steps.size(); j++) {
				String currStep = steps.get(j);
				String firstLetter = currStep.substring(0, 1).toUpperCase();
		        String restOfString = currStep.substring(1);
		        String step = firstLetter + restOfString;
		        steps.set(j, step);
			}
			Recipe recipe = new Recipe(recipeName, recipeDescription, recipeRating, preparationTime, servings,
					numberOfSteps, member, calories, protein, carbohydrate, sugar, sodium, fat, saturatedFat, steps);
			recipe.setTags(tagsList);
			recipe.setImage("download.jpg");
			recipe.setNumberOfRating(numberOfRating);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
			recipe.setSubmittedDate(LocalDate.parse(currRecipe[4], formatter));
			// set image
			int imageIndex = i % imageFiles.length;
			String imageName = imageFiles[imageIndex];
			recipe.setImage(imageName);
			Recipe savedRecipe = recipeService.createRecipe(recipe);
			csvIdToDbIdRecipe.put(recipeId, savedRecipe.getId());

			// Save recipes to ingredients
			for (Ingredient ingredient : ingredientsToAdd) {
				ingredient.getRecipes().add(savedRecipe);
				ingredientService.saveIngredient(ingredient);
			}
		}
	}

	public String[] extractIngredients(String ingredientsString) {
		// Split the string based on commas within double quotes
		String[] ingredientsArr = ingredientsString.substring(1, ingredientsString.length() - 1).split("','");
		for (int i = 0; i < ingredientsArr.length; i++) {
			ingredientsArr[i] = ingredientsArr[i].trim();
			ingredientsArr[i] = ingredientsArr[i].replaceAll("\\s+", " ");
			ingredientsArr[i] = ingredientsArr[i].replaceAll("'", "");
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

	public void saveInteractions(List<String[]> interactions) {
		for (int i = 1; i < interactions.size(); i++) {
			String[] currInteraction = interactions.get(i);
			int memberId = Integer.parseInt(currInteraction[0]);
			Member member = null;
			// Get member from Db if exists
			if (csvIdToDbIdMember.containsKey(memberId)) {
				member = userService.getMemberById(csvIdToDbIdMember.get(memberId));
			} else {
				member = new Member();
				member.setUsername("member" + memberId);
				member.setPassword("member" + memberId + "Password!");
				member = userService.saveMember(member);
				csvIdToDbIdMember.put(memberId, member.getId());
			}
			int recipeId = Integer.parseInt(currInteraction[1]);
			Recipe recipe = recipeService.getRecipeById(csvIdToDbIdRecipe.get(recipeId));
			int rating = Integer.parseInt(currInteraction[3]);
			String comment = currInteraction[4];
			Review review = new Review(rating, comment, member, recipe);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate reviewDate = LocalDate.parse(currInteraction[2], formatter);
			review.setReviewDate(reviewDate);
			reviewService.saveReview(review);
		}
	}

	@GetMapping("/user/status")
	@ResponseBody
	public Map<String, Object> getUserStatus(HttpSession session) {
		Map<String, Object> status = new HashMap<>();

		Integer userId = (Integer) session.getAttribute("userId");
		boolean isLoggedIn = userId != null;
		status.put("isLoggedIn", isLoggedIn);

		if (isLoggedIn) {

			String username = userService.getUsernameById(userId);
			status.put("username", username);
		}
		return status;
	}

	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody Member user1) {
		logger.info("Attempting to log in user: {}", user1.getUsername());
		User user = userService.getUserByUsername(user1.getUsername());
		if (user != null && user1.getPassword().equals(user.getPassword())) {
			logger.info("Login successful for user: {}", user1.getUsername());
			return ResponseEntity.ok().body("Login succeeded!");
		} else {
			logger.error("Login failed for user: {}", user1.getUsername());
			return ResponseEntity.badRequest().body("Username or password wrong!");
		}
	}

	@PostMapping("/getMySavedRecipes")
	public ResponseEntity<?> getSavedRecipes(@RequestBody Member member) {
		try {
			Member member1 = userService.getMemberByUsername(member.getUsername());
			if (member1 == null) {
				return ResponseEntity.notFound().build();
			}
			return ResponseEntity.ok().body(recipeService.recipeTurnToDTO(member1.getSavedRecipes()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while retrieving saved recipes.");
		}
	}

	@GetMapping("/getRecipeCountByMonth")
	public ResponseEntity<Map<String, Object>> getRecipeData(@RequestParam int year) {
		List<Recipe> recipesByYear = recipeService.getAllRecipesByYear(year);
		List<String> months = new ArrayList<>();
		Collections.addAll(months, "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
		// Initialize list with 0s for each month
		List<Integer> counts = new ArrayList<>(Collections.nCopies(12, 0));
		for (Recipe recipe : recipesByYear) {
			int monthIndex = recipe.getSubmittedDate().getMonthValue() - 1;
			counts.set(monthIndex, counts.get(monthIndex) + 1);
		}

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("months", months);
		responseData.put("counts", counts);

		return new ResponseEntity<>(responseData, HttpStatus.OK);
	}

	@GetMapping("/getRecipeData")

    public ResponseEntity<?> getRecipes(
            @RequestParam(name = "start", defaultValue = "0") int start,
            @RequestParam(name = "limit", defaultValue = "30") int limit) {
        
        //  getRecipes(start, limit) 来获取数据
        List<Recipe> recipes = recipeService.getRecipes(start, limit);
        return ResponseEntity.ok(recipeService.recipeTurnToDTO(recipes));
    }
	
	@GetMapping("/getReviewData")
    public ResponseEntity<?> getReviews(
            @RequestParam(name = "recipeid") int id) {
        
        //  getRecipes(start, limit) 来获取数据
        List<Review> reviews = recipeService.getRecipeById(id).getReviews();
        return ResponseEntity.ok(reviewService.reviewTurnToDTO(reviews));
    }
	
	@GetMapping("/getMemberUsername")
    public String getMemberUsername(@RequestParam Integer id) {
        
        return userService.getUsernameById(id);
    }
	@PostMapping("/getShoppingList")
	public ResponseEntity<?> getShoppingList(@RequestBody Map<String, String> requestBody) {
		System.out.println("getShoppingList called by android");
	    String username = requestBody.get("username");
	    List<ShoppingListItem> shoppingList = userService.getMemberByUsername(username).getShoppingList();
	    return ResponseEntity.ok(recipeService.shoppingListTurnToDTO(shoppingList));
	}
	
	@PostMapping("/addToShoppingList")
	public String addToShoppingList(@RequestBody Map<String, String> requestBody) {
		System.out.println("addToShoppingList called by android");
	    List<String> selectedItems = Arrays.asList((requestBody.get("selectedItems")).split(","));
	    String username = requestBody.get("username");
	    for (String item : selectedItems) {
	    	System.out.println("item: " + item);
	    	ShoppingListItem newItem = shopser.saveItemFromAndroid(username, false, item);
	    	System.out.println("saved item: " + newItem.getIngredientName());
	    }
	    return "addedToShoppingList";
	}
	
	@PostMapping("/addItemToShoppingList")
	public ResponseEntity<?> addItemToShoppingList(@RequestBody Map<String, String> requestBody) {
		System.out.println("addItemToShoppingList called by android");
	    String itemToAdd = requestBody.get("newItem");
	    String username = requestBody.get("username");
	    ShoppingListItem newItem = shopser.saveItemFromAndroid(username, false, itemToAdd);
	    System.out.println("saved item: " + newItem.getIngredientName());
	    ShoppingListItemDTO itemDTO = shopser.turnToShoppingListItemDTO(newItem);
	    return ResponseEntity.ok(itemDTO);
	}

	@GetMapping("/test")
    public List<ShoppingListItemDTO> test() {
		List<ShoppingListItem> shoppingList = userService.getMemberByUsername("zhangsibo").getShoppingList();
		for (ShoppingListItem item : shoppingList) {
	        System.out.print(item.getIngredientName());
		}
		
        return recipeService.shoppingListTurnToDTO(shoppingList);
    }
	@GetMapping("/test2")
	public String test2() {
		shopser.saveItemFromAndroid("zhangsibo", false, "milk");
		return null;
	}
	@PostMapping("/updateIsChecked")
    public String updateIsChecked(@RequestBody ShoppingListItemDTO request) {
        boolean isChecked = request.isChecked();
        int id = request.getId();
        
        shopser.updateItemFromAndroid(id, isChecked);
        System.out.print(id);
        System.out.print(isChecked);
        return "Update successful"; 
    }
	@PostMapping("/saveShoppingList")
	public String saveShoppingList(@RequestBody ShoppingListItemDTO request) {
		boolean isChecked=request.isChecked();
		String username=request.getUsername();
		String ingredientName=request.getIngredientName();
		shopser.saveItemFromAndroid(username, isChecked, ingredientName);
		return"save successful";
	}
	@PostMapping("/deleteShoppingList")
	public String deleteShoppingList(@RequestBody List<Integer> itemIds) {
	    for (Integer id : itemIds) {
	        System.out.println("Deleting item with ID: " + id);
	        shopser.deleteItemFromAndroid(id);
	    }
	    return "delete successful";
	}
	@GetMapping("/deleteAll")
	public String deleteAll() {
		shopser.deleteAll();
		return "delete all successful";
	}
	@PostMapping("/clearSelectedItemsInDb")
	public String clearSelectedItemsInDb(@RequestBody Map<String, String> requestBody) {
		List<String> selectedItems = Arrays.asList((requestBody.get("itemsToClear")).split(","));
		for (String itemId : selectedItems) {
			int id = Integer.parseInt(itemId);
			shopser.deleteShoppingListItemById(id);
		}
		return "clear selected items";
	}
}
