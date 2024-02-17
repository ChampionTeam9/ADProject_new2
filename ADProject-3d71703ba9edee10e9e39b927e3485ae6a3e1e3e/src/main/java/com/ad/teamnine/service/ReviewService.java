package com.ad.teamnine.service;

import java.util.ArrayList;
import java.util.List;

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
	
	public void saveReview(Review review) {
		reviewRepo.save(review);
	}
	
	public List<Review> getReviewsByRecipe(Recipe recipe){
		return recipeRepo.getReviewsByRecipe(recipe);
	}

	public List<ReviewDTO> reviewTurnToDTO(List<Review> reviews) {
		List<ReviewDTO> rdlist = new ArrayList<>();
		for(Review r :reviews) {
			ReviewDTO rd = new ReviewDTO();
			rd.setId(r.getId());
			rd.setComment(r.getComment());
			rd.setMemberId(r.getMember().getId());
			rd.setRating(r.getRating());
			rd.setReviewDate(r.getReviewDate());
			rd.setRecipeId(r.getRecipe().getId());
			rdlist.add(rd);
		}
		return rdlist;
	}
}
