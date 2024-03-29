package com.ad.teamnine.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ad.teamnine.model.*;
import com.ad.teamnine.service.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/recipe")
public class RecipeController {
	private RecipeService recipeService;
	private UserService userService;
	@Autowired
	IngredientService ingredientService;
	@Autowired
	ReviewService reviewService;

	public RecipeController(RecipeService recipeService, UserService userService) {
		this.recipeService = recipeService;
		this.userService = userService;
	}

	@GetMapping("/save/{id}")
	public String saveRecipe(@PathVariable Integer id, HttpSession sessionObj) {
		Recipe recipe = recipeService.getRecipeById(id);
		Member member = userService.getMemberById((int) sessionObj.getAttribute("userId"));
		recipeService.saveRecipe(recipe, member);
		return "redirect:/recipe/detail/" + id;
	}

	@GetMapping("/unsubscribe/{id}")
	public String unsubscribeRecipe(@PathVariable Integer id, HttpSession sessionObj) {
		Recipe recipe = recipeService.getRecipeById(id);
		Member member = userService.getMemberById((int) sessionObj.getAttribute("userId"));
		recipeService.unsubscribeRecipe(recipe, member);
		return "redirect:/user/member/savedList";
	}

	@GetMapping("/unsubscribeOnDetailPage/{id}")
	public String unsubscribeRecipeOnDetailPage(@PathVariable Integer id, HttpSession sessionObj) {
		Recipe recipe = recipeService.getRecipeById(id);
		Member member = userService.getMemberById((int) sessionObj.getAttribute("userId"));
		recipeService.unsubscribeRecipe(recipe, member);
		return "redirect:/recipe/detail/" + id;
	}

	@GetMapping("/review/{id}")
	public String reviewRecipe(@PathVariable Integer id, HttpSession sessionObj, Model model) {
		Recipe recipe = recipeService.getRecipeById(id);
		Review review = new Review();
		review.setRecipe(recipe);
		model.addAttribute("review", review);
		return "/ReviewViews/createReviewPage";
	}

	@GetMapping("/search/{tag}")
	public String searchByTag(@PathVariable String tag, Model model, @RequestParam(defaultValue = "0") int pageNo,
			@RequestParam(defaultValue = "12") int pageSize, HttpServletRequest request) {
		Page<Recipe> recipePage = recipeService.searchByTag(tag, pageNo, pageSize);
		model.addAttribute("results", recipePage.getContent());
		model.addAttribute("recipeRecommended", recipePage.getContent());// 换成recommendation
		model.addAttribute("currentPage", recipePage.getNumber());
		model.addAttribute("totalPages", recipePage.getTotalPages());
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("request", request.getRequestURI());
		System.out.println("request: " + request.getRequestURI());
		System.out.println("results.size = " + recipePage.getContent().size());
		return "/RecipeViews/HomePage";
	}

	// search by query
	@PostMapping("/search")
	public String searchRecipe(@RequestParam("query") String query, @RequestParam("searchtype") String type,
			Model model, @RequestParam(name = "filter1", defaultValue = "false") boolean filter1,
			@RequestParam(name = "filter2", defaultValue = "false") boolean filter2, HttpSession sessionObj,
			@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "12") int pageSize,
			HttpServletRequest request) {
		List<Recipe> results;
		switch (type) {
		case "tag":
			results = recipeService.searchByTag(query);
			break;
		case "name":
			results = recipeService.searchByName(query);
			break;
		case "description":
			results = recipeService.searchByDescription(query);
			break;
		default:
			results = recipeService.searchAll(query);
			break;
		}
		// Filter results
		List<Recipe> filteredResults = results;
		if (filter1) {
			filteredResults = results.stream().filter(r -> r.getHealthScore() >= 4).collect(Collectors.toList());
		}
		if (filter2) {
			Integer memberId = (Integer) sessionObj.getAttribute("userId");
			if (memberId == null) {
				return "redirect:/user/login";
			}
			Member member = userService.getMemberById((Integer) sessionObj.getAttribute("userId"));
			Double calorieIntake = member.getCalorieIntake();
			if (member.getCalorieIntake() == null) {
				return "redirect:/user/editProfile";
			}
			filteredResults = results.stream().filter(r -> r.getCalories() <= (calorieIntake / 3))
					.collect(Collectors.toList());
		}
		int totalRecipes = filteredResults.size();
		int startIndex = pageNo * pageSize;
		int endIndex = Math.min(startIndex + pageSize, totalRecipes);
		int currentPage = pageNo;
		int totalPages = totalRecipes / pageSize;
		if (totalRecipes % pageSize != 0) {
			totalPages++;
		}
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("request", request.getRequestURI());
		System.out.println("request: " + request.getRequestURI());
		System.out.println("results.size = " + totalRecipes);
		System.out.println("pageSize: " + pageSize);
		System.out.println("currentPage: " + currentPage);
		model.addAttribute("results", filteredResults.subList(startIndex, endIndex));
		model.addAttribute("recipeRecommended", filteredResults.subList(startIndex, endIndex));// 换成recommendation
		model.addAttribute("query", query);
		model.addAttribute("searchtype", type);
		return "/RecipeViews/HomePage";
	}

	@GetMapping("/create")
	public String showAddRecipeForm(Model model) {
		model.addAttribute("recipe", new Recipe());
		return "/RecipeViews/createRecipesPage";
	}

	@PostMapping("/addItem")
	public ResponseEntity<Map<String, Object>> addItem(@RequestBody Map<String, Object> payload) {
		String ingredientName = (String) payload.get("ingredientName");
		Ingredient ingredient = ingredientService.getIngredientByfoodText(ingredientName);
		Map<String, Object> response = new HashMap<>();
		if (ingredient != null && ingredient.getCalories() == null) {
			ingredientService.deleteIngredient(ingredient);
			ingredient = null;
		}
		if (ingredient == null) {
			// Create ingredient
			ResponseEntity<Ingredient> ingredientResponse = APIController.getNutritionInfo(ingredientName);
			if (ingredientResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
				response.put("statusMessage", "NOT_FOUND");
				return ResponseEntity.ok(response);
			}
			if (ingredientResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
				response.put("statusMessage", "MISSING_QUANTITY");
				return ResponseEntity.ok(response);
			}
			ingredient = ingredientResponse.getBody();
			ingredientService.saveIngredient(ingredient);
			System.out.println(ingredient);
		}
		int id = ingredient.getId();
		response.put("id", id);
		System.out.println("id: " + id);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/create")
	public String addRecipe(@ModelAttribute("recipe") @Valid Recipe recipe, BindingResult bindingResult,
			@RequestParam("timeUnit") String timeUnit, @RequestParam("pictureInput") MultipartFile pictureFile,
			@RequestParam("ingredientIds") String ingredientIds, Model model, HttpSession sessionObj, 
			@RequestParam(name="id", required = false) Integer recipeId) {
		if (bindingResult.hasErrors()) {
			System.out.println("Binding error at recipe creation");
			// Print out binding errors
			System.out.println("Binding errors:");
			for (ObjectError error : bindingResult.getAllErrors()) {
				System.out.println(error.getDefaultMessage());
			}
			return "/RecipeViews/createRecipesPage";
		}
		
		// If preparation time entered in hours, convert to mins
		if (timeUnit.equals("hours")) {
			int preparationTime = recipe.getPreparationTime();
			recipe.setPreparationTime(preparationTime * 60);
		}

		// Set the first letter of name and description as uppercase
		String recipeName = recipe.getName().trim();
		recipeName = recipeName.substring(0, 1).toUpperCase() + recipeName.substring(1);
		recipe.setName(recipeName);

		String recipeDescription = recipe.getName().trim();
		if (!recipeDescription.isEmpty()) {
			recipeDescription = recipeDescription.substring(0, 1).toUpperCase() + recipeDescription.substring(1);
			recipe.setDescription(recipeDescription);
		}

		List<String> steps = recipe.getSteps();
		List<String> newSteps = new ArrayList<>();
		for (String step : steps) {
			if (step != null && !step.isEmpty())
				newSteps.add(step);
		}
		recipe.setSteps(newSteps);
		recipe.setNumberOfSteps(newSteps.size());

		if (pictureFile != null && !pictureFile.isEmpty()) {
			String uploadDirectory = "src/main/resources/static/images";
			String uniqueFileName = UUID.randomUUID().toString() + "_" + pictureFile.getOriginalFilename();
			Path uploadPath = Path.of(uploadDirectory);
			Path filePath = uploadPath.resolve(uniqueFileName);
			try {
				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}
				Files.copy(pictureFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			recipe.setImage(uniqueFileName);
		} else {
			// No new image file uploaded, retain the existing image
			Recipe existingRecipe = recipeService.getRecipeById(recipe.getId());
			recipe.setImage(existingRecipe.getImage());
		}

		Member member = userService.getMemberById((Integer) sessionObj.getAttribute("userId"));
		recipe.setMember(member);
		
		recipeService.createRecipe(recipe);
		
		List<Ingredient> ingredients = new ArrayList<>();
		if (recipeId != null)
			// if editing existing recipe
			ingredients = recipeService.getRecipeById(recipeId).getIngredients();
		String[] ingredientsToAdd = ingredientIds.split(",");
		List<Integer> ingredientIdsList = new ArrayList<>();
		for (int i = 0; i < ingredientsToAdd.length; i++) {
			if (ingredientsToAdd[i].equals(""))
				continue;
			int ingredientId = Integer.parseInt(ingredientsToAdd[i]);
			ingredientIdsList.add(ingredientId);
		    // Check if the ingredient is already in the ingredients list (for edit recipe)
		    boolean ingredientAlreadyAdded = false;
		    for (Ingredient existingIngredient : ingredients) {
		        if (existingIngredient.getId() == ingredientId) {
		            ingredientAlreadyAdded = true;
		            break;
		        }
		    }
		    if (!ingredientAlreadyAdded) {
		    	Ingredient ingredient = ingredientService.getIngredientById(ingredientId);
				ingredient.getRecipes().add(recipe);
				ingredientService.saveIngredient(ingredient);
				ingredients.add(ingredient);
		    }
		}
		// Remove ingredients not present in the ingredientIdsList (in case ingredient was deleted)
		Iterator<Ingredient> iterator = ingredients.iterator();
		while (iterator.hasNext()) {
		    Ingredient existingIngredient = iterator.next();
		    if (!ingredientIdsList.contains(existingIngredient.getId())) {
		        iterator.remove();
		        existingIngredient.getRecipes().remove(recipe);
		        ingredientService.saveIngredient(existingIngredient);
		    }
		}
		recipe.setIngredients(ingredients);
		setRecipeNutrients(recipe);
		recipe.setHealthScore(recipe.calculateHealthScore());
		recipeService.createRecipe(recipe);
		return "redirect:/user/member/myRecipeList";
	}

	public void setRecipeNutrients(Recipe recipe) {
		// Sum up nutrients from each recipe
		List<Ingredient> ingredients = recipe.getIngredients();
		int servings = recipe.getServings();
		Double calories = 0.0;
		Double protein = 0.0;
		Double carbohydrate = 0.0;
		Double sugar = 0.0;
		Double sodium = 0.0;
		Double fat = 0.0;
		Double saturatedFat = 0.0;
		for (Ingredient ingredient : ingredients) {
			calories += ingredient.getCalories();
			protein += ingredient.getProtein();
			carbohydrate += ingredient.getCarbohydrate();
			sugar += ingredient.getSugar();
			sodium += ingredient.getSodium();
			fat += ingredient.getFat();
			saturatedFat += ingredient.getSaturatedFat();
		}
		recipe.setCalories(Math.round((calories / servings) * 10.0) / 10.0);

		// Calculate PDV of each macronutrient by using their reference intake
		Double proteinPDV = (protein / servings) / 50 * 100;
		proteinPDV = Math.round(proteinPDV * 10.0) / 10.0;
		Double carbohydratePDV = (carbohydrate / servings) / 260 * 100;
		carbohydratePDV = Math.round(carbohydratePDV * 10.0) / 10.0;
		Double sugarPDV = (sugar / servings) / 90 * 100;
		sugarPDV = Math.round(sugarPDV * 10.0) / 10.0;
		Double sodiumPDV = (sodium / 1000 / servings) / 6 * 100;
		sodiumPDV = Math.round(sodiumPDV * 10.0) / 10.0;
		Double fatPDV = (fat / servings) / 70 * 100;
		fatPDV = Math.round(fatPDV * 10.0) / 10.0;
		Double saturatedFatPDV = (saturatedFat / servings) / 20 * 100;
		saturatedFatPDV = Math.round(saturatedFatPDV * 10.0) / 10.0;
		recipe.setProtein(proteinPDV);
		recipe.setCarbohydrate(carbohydratePDV);
		recipe.setSugar(sugarPDV);
		recipe.setSodium(sodiumPDV);
		recipe.setFat(fatPDV);
		recipe.setSaturatedFat(saturatedFatPDV);
	}

	@GetMapping("/delete/{id}")
	public String deleteRecipe(@PathVariable("id") Integer id) {
		Recipe recipe = recipeService.getRecipeById(id);
		List<Ingredient> ingredients = recipe.getIngredients();
		for (Ingredient ingredient : ingredients) {
			// Remove recipe from ingredient before deletion (referential integrity)
			ingredient.getRecipes().remove(recipe);
			ingredientService.saveIngredient(ingredient);
		}
		recipe.setStatus(Status.DELETED);
		recipeService.updateRecipe(recipe);
		return "redirect:/user/member/myRecipeList";
	}

	@GetMapping("/edit/{id}")
	public String getUpdateRecipePage(@PathVariable("id") Integer id, Model model) {
		Recipe recipe = recipeService.getRecipeById(id);
		model.addAttribute("recipe", recipe);
		return "/RecipeViews/updateRecipesPage";
	}

	@GetMapping("/detail/{id}")
	public String viewRecipe(@PathVariable("id") Integer id, Model model, HttpSession sessionObj) {
		Recipe recipe = recipeService.getRecipeById(id);
		if (recipe.getStatus() == Status.DELETED) {
			return "RecipeViews/recipeDeletedPage";
		}
		if (recipe.getStatus() == Status.PRIVATE) {
			if (sessionObj.getAttribute("userId") == null) {
				return "RecipeViews/recipePrivatePage";
			}
			Integer userId = (int) sessionObj.getAttribute("userId");
			if (!userId.equals(recipe.getMember().getId())) {
				return "RecipeViews/recipePrivatePage";
			}
		}
		model.addAttribute("recipe", recipe);
		// get number of people who rated
		int numberOfUsersRatings = recipeService.getRecipeById(id).getNumberOfRating();
		model.addAttribute("numberOfUserRatings", numberOfUsersRatings);
		// get reviews ordered by review date
		List<Review> reviews = reviewService.getReviewsByRecipe(recipe);
		model.addAttribute("reviews", reviews);
		if (sessionObj.getAttribute("userId") != null) {
			Integer userId = (int) sessionObj.getAttribute("userId");
			Object userType = sessionObj.getAttribute("userType");
			if (userType != null && userType.toString().equals("member")) {
				Member member = userService.getMemberById(userId);
				Boolean ifsave = !member.getSavedRecipes().contains(recipe);
				model.addAttribute("ifsave", ifsave);
			}
			if (userType != null && userType.toString().equals("admin")) {
				model.addAttribute("ifAdmin", true);
			} else {
				model.addAttribute("ifAdmin", false);
			}
			if (userId.equals(recipe.getMember().getId())) {
				model.addAttribute("isMine", true);
			} else {
				model.addAttribute("isMine", false);
			}
		} else {
			model.addAttribute("ifsave", true);
			model.addAttribute("isMine", false);
		}

		return "RecipeViews/recipeDetailPage";
	}
}