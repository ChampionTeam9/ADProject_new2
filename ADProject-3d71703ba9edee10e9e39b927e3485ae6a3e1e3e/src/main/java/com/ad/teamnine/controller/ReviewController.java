package com.ad.teamnine.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.ad.teamnine.model.*;
import com.ad.teamnine.service.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/review")
public class ReviewController {
	private ReviewService reviewService;
	@Autowired
	UserService userService;
	@Autowired
	RecipeService recipeService;
	
	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}
	
	@PostMapping("/create")
	public String createReview(@ModelAttribute("review") Review review, HttpSession sessionObj,
			@RequestParam("recipeId") int recipeId) {
		Member member = userService.getMemberById((int)sessionObj.getAttribute("userId"));
		Recipe recipe = recipeService.getRecipeById(recipeId);
		review.setMember(member);
		review.setRecipe(recipe);
		reviewService.createReview(review);
		return "redirect:/";
	}
}
