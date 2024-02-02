package com.ad.teamnine.model;

import java.util.List;
import java.util.Set;

public class AddIngredientForm {
	private List<Ingredient> ingredients;
	private Set<Integer> selectedIngredients;
	
	public AddIngredientForm() {}

	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}

	public Set<Integer> getSelectedIngredients() {
		return selectedIngredients;
	}

	public void setSelectedIngredients(Set<Integer> selectedIngredients) {
		this.selectedIngredients = selectedIngredients;
	}
}
