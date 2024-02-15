package com.ad.teamnine.controller;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ad.teamnine.model.Recipe;
import com.ad.teamnine.service.RecipeService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MainController {
	private RecipeService recipeService;

	public MainController(RecipeService recipeService) {
		this.recipeService = recipeService;
	}

	@RequestMapping("/")
	public String homePage(Model model, @RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "12") int pageSize, 
			HttpServletRequest request) {
		Page<Recipe> recipePage = recipeService.findAllRecipesByPage(pageNo, pageSize);
		model.addAttribute("results", recipePage.getContent());
		model.addAttribute("recipeRecommended",recipePage.getContent());//换成recommendation 
		model.addAttribute("currentPage", recipePage.getNumber());
		model.addAttribute("totalPages", recipePage.getTotalPages());
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("request", request.getRequestURI());
		return "RecipeViews/HomePage";
	}
}