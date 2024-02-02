package com.ad.teamnine.model;

import java.util.List;
import java.util.Set;

public class AddIngredientForm {
	private List<String> ingredientNames;
	private List<Integer> selectedIngredients;
	
	public AddIngredientForm() {}

	public List<String> getIngredientNames() {
		return ingredientNames;
	}

	public void setIngredientsNames(List<String> ingredientNames) {
		this.ingredientNames = ingredientNames;
	}

	public List<Integer> getSelectedIngredients() {
		return selectedIngredients;
	}

	public void setSelectedIngredients(List<Integer> selectedIngredients) {
		this.selectedIngredients = selectedIngredients;
	}
}
