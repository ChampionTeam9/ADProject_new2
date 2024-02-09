package com.ad.teamnine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ad.teamnine.model.*;
import com.ad.teamnine.repository.*;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReviewService {
	@Autowired
	ReviewRepository reviewRepo;
	@Autowired
	RecipeRepository recipeRepo;

	public void createReview(Review review) {
		reviewRepo.save(review);
		Recipe recipe = review.getRecipe();
		recipe.setNumberOfRating(recipe.getNumberOfRating() + 1);
		// Update mean rating of recipe
		double meanRating = reviewRepo.getMeanRating(recipe.getId());
		double roundedMeanRating = Math.round(meanRating * 10.0) / 10.0;
		recipe.setRating(roundedMeanRating);
		recipeRepo.save(recipe);
		return;
	}
}
