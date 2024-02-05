package com.ad.teamnine.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ad.teamnine.model.*;
import com.ad.teamnine.service.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/recipe")
public class RecipeController {
	private RecipeService recipeService;
	private UserService userService;
	

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
      
        return "createRecipesPage";
    }

	@PostMapping("/create")
    public String addRecipe(@ModelAttribute Recipe recipe) {
		

        
        recipeService.createRecipe(recipe);

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