package com.ad.teamnine.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
		if (ingredient == null) {
			// Create ingredient 
			ingredient = APIController.getNutritionInfo(ingredientName);
			ingredientService.saveIngredient(ingredient);
			System.out.println(ingredient);
		}
		int id = ingredient.getId();
		Map<String, Object> response = new HashMap<>();
	    response.put("id", id);
	    System.out.println("id: " + id);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/create")
    public String addRecipe(@ModelAttribute("recipe") @Valid Recipe recipe, BindingResult bindingResult,
			@RequestParam("preparationTime") int preparationTime,
			@RequestParam("timeUnit") String timeUnit,
			@RequestParam("image") MultipartFile pictureFile,
			@RequestParam("ingredientIds") String ingredientIds,
			Model model) {
		
        if (timeUnit.equals("Minutes")) {
        	recipe.setPreparationTime(preparationTime);
        }
        else {
        	preparationTime = preparationTime * 60;
        	recipe.setPreparationTime(preparationTime);
        }

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
        Recipe savedRecipe = recipeService.createRecipe(recipe);
        
        String[] ingredientsToAdd = ingredientIds.split(",");
        for (int i = 0; i < ingredientsToAdd.length; i ++) {
        	if (ingredientsToAdd[i].equals(""))
        		continue;
			Ingredient ingredient = ingredientService.getIngredientById(Integer.parseInt(ingredientsToAdd[i]));
			System.out.println("Ingredient: " + ingredient);
			ingredient.getRecipes().add(savedRecipe);
			ingredientService.saveIngredient(ingredient);
		}
        return "redirect:/recipe/list";
    }
	
	@GetMapping("/list")
	public String showAddRecipeList(Model model) {
	    //List<Recipe> recipeList = RecipeService.getAllRecipes();
	    model.addAttribute("recipes", recipeService.getAllRecipes());
	    
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