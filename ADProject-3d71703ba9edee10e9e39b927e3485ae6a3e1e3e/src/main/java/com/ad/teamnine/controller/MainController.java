package com.ad.teamnine.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ad.teamnine.service.RecipeService;

@Controller
public class MainController {
	private RecipeService recipeService;

	public MainController(RecipeService recipeService) {
		this.recipeService = recipeService;
	}

	@RequestMapping("/")
	public String homePage(Model model) {
		model.addAttribute("results", recipeService.getAllRecipes());
		return "RecipeViews/HomePage";
	}
}