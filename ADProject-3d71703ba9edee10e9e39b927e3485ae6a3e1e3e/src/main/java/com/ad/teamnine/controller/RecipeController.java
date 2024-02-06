package com.ad.teamnine.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ad.teamnine.model.*;
import com.ad.teamnine.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/recipe")
public class RecipeController {
	private RecipeService recipeService;
	private UserService userService;
	@Autowired
	IngredientService ingredientService;
	
	public RecipeController(RecipeService recipeService, UserService userService) {
		this.recipeService = recipeService;
		this.userService = userService;
	}

	@PostMapping("/save/{id}")
	public String saveRecipe(@PathVariable Integer id, HttpSession sessionObj) {
		Recipe recipe = recipeService.getRecipeById(id);
		Member member = userService.getMemberById((int) sessionObj.getAttribute("userId"));
		recipeService.saveRecipe(recipe, member);
		return ""; // 改成redirection
	}

	@PostMapping("/unsubscribe/{id}")
	public String unsubscribeRecipe(@PathVariable Integer id, HttpSession sessionObj) {
		Recipe recipe = recipeService.getRecipeById(id);
		Member member = userService.getMemberById((int) sessionObj.getAttribute("userId"));
		recipeService.unsubscribeRecipe(recipe, member);
		return ""; // 改成redirection
	}

	@GetMapping("/review/{id}")
	public String reviewRecipe(@PathVariable Integer id, HttpSession sessionObj, Model model) {
		Recipe recipe = recipeService.getRecipeById(id);
		//Member member = userService.getMemberById((int) sessionObj.getAttribute("userId"));
		Member member = userService.getMemberById(1);
		Review review = new Review();
		review.setmember(member);
		review.setRecipe(recipe);
		model.addAttribute("review", review);
		return "/ReviewViews/createReviewPage";
	}
	
	@GetMapping("/search/{tag}")
	public String searchByTag(@PathVariable String tag,Model model) {
	    List<Recipe> results = recipeService.searchByTag(tag);
	    model.addAttribute("results", results);
	    return "page1";
	}

	//search by title name
	@PostMapping("/search")
	public String searchRecipe(@RequestParam("query") String query,
	                           @RequestParam("type") String type,
	                           Model model) {
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
	    model.addAttribute("results", results);
	    return "page1";
	}
	
	@GetMapping("/create")
    public String showAddRecipeForm(Model model) {
		model.addAttribute("recipe", new Recipe());
        return "createRecipesPage";
    }
	
	@PostMapping("/addItem")
	public ResponseEntity<Map<String, Object>> addItem (@RequestBody Map<String, Object> payload){
		String ingredientName = (String) payload.get("ingredientName");
		Ingredient ingredient = ingredientService.getIngredientByfoodText(ingredientName);
		Map<String, Object> response = new HashMap<>();
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
			@RequestParam("timeUnit") String timeUnit,
			@RequestParam("image") MultipartFile pictureFile,
			@RequestParam("ingredientIds") String ingredientIds,
			Model model) {
		
		// If preparation time entered in hours, convert to mins
        if (timeUnit.equals("hours")) {
        	int preparationTime = recipe.getPreparationTime();
        	recipe.setPreparationTime(preparationTime * 60);
        }
        
        recipe.setNumberOfSteps(recipe.getSteps().size());

        // 获取图片文件名
//        String fileName = pictureFile.getOriginalFilename();

        // 保存图片到服务器的某个位置
//        String filePath = "/path/to/save/images/" + fileName;

//        try {
//            // 写入文件
//            pictureFile.transferTo(new File(filePath));
//
//            // 将图片路径保存到 Recipe 对象中
//            recipe.setImage(filePath);
//        } catch (IOException e) {
//            // Handle the exception (e.g., log it)
//            e.printStackTrace();
//        }
        
        // Get member's shopping list
 		// Member member = memberService.getMemberById((int)sessionObj.getAttribute("userId"));
 		// Hardcode first
 		Member member = userService.getMemberById(1);
        recipe.setMember(member);
        
        recipeService.createRecipe(recipe);
        
        List<Ingredient> ingredients = recipe.getIngredients();
        String[] ingredientsToAdd = ingredientIds.split(",");
        for (int i = 0; i < ingredientsToAdd.length; i ++) {
        	if (ingredientsToAdd[i].equals(""))
        		continue;
			Ingredient ingredient = ingredientService.getIngredientById(Integer.parseInt(ingredientsToAdd[i]));
			System.out.println("Ingredient: " + ingredient);
			ingredient.getRecipes().add(recipe);
			ingredientService.saveIngredient(ingredient);
			ingredients.add(ingredient);
		}
        setRecipeNutrients(recipe);
        recipe.setHealthScore(recipe.calculateHealthScore());
        recipeService.createRecipe(recipe);
        return "redirect:/recipe/viewMyRecipes";
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
		recipe.setCalories(calories / servings);
		// Calculate PDV of each macronutrient by using their reference intake
		Double proteinPDV = (protein / servings) / 50 * 100;
		Double carbohydratePDV = (carbohydrate / servings) / 260 * 100;
		Double sugarPDV = (sugar / servings) / 90 * 100;
		Double sodiumPDV = (sodium / 1000 / servings) / 6 * 100;
		Double fatPDV = (fat / servings) / 70 * 100;
		Double saturatedFatPDV = (saturatedFat / servings) / 20 * 100;
		recipe.setProtein(proteinPDV);
		recipe.setCarbohydrate(carbohydratePDV);
		recipe.setSugar(sugarPDV);
		recipe.setSodium(sodiumPDV);
		recipe.setFat(fatPDV);
		recipe.setSaturatedFat(saturatedFatPDV);
	}
	
	@GetMapping("/viewMyRecipes")
	public String showMyRcipes(Model model) {
		// Member member = memberService.getMemberById((int)sessionObj.getAttribute("userId"));
		// Hardcode first
		Member member = userService.getMemberById(1);
	    List<Recipe> recipes = recipeService.getAllRecipesByMember(member);
	    model.addAttribute("recipes", recipes);
	    return "recipeListPage";
	}
	
	@DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable("id") Integer id) {
        // 通过服务层执行删除操作
        recipeService.deleteRecipe(id);
        
        return ResponseEntity.ok("Recipe deleted successfully");
    }
	
	@RequestMapping("/edit/{id}")
	public String getUpdateUserPage(@PathVariable("id") Integer id, Model model) {
		
		Recipe recipe = recipeService.getRecipeById(id);
	    model.addAttribute("recipe", recipe);
		
		return "updateRecipesPage";
	}
	
	@PostMapping("/edit")
    public String updateRecipe(@ModelAttribute Recipe recipe) {
		
        recipeService.updateRecipe(recipe);

        return "redirect:/recipe/list";
    }
	
	@GetMapping("/view/{id}")
	public String viewRecipe(@PathVariable("id") Integer id, Model model) {
	    Recipe recipe = recipeService.getRecipeById(id);
	    model.addAttribute("recipe", recipe);
	    return "viewPage"; 
	}

}