package com.ad.teamnine.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
@Entity
public class RecipeReport extends Report{
	@ManyToOne
	private Recipe recipeReported;

	// getter and setter
	public Recipe getRecipeReported() {
		return recipeReported;
	}

	public void setRecipeReported(Recipe recipeReported) {
		this.recipeReported = recipeReported;
	}
	
}
