package com.ad.teamnine.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

	@PostMapping("/review/{id}")
	public String reviewRecipe(@PathVariable Integer id, HttpSession sessionObj, Model model) {
		Recipe recipe = recipeService.getRecipeById(id);
		Member member = userService.getMemberById((int) sessionObj.getAttribute("userId"));
		Review review = new Review();
		model.addAttribute("review", review);// 没有设置Id
		model.addAttribute("recipe", recipe);
		model.addAttribute("member", member);
		return "/ReviewViews/createReviewPage";
	}
}