package com.ad.teamnine.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ad.teamnine.model.Ingredient;
import com.ad.teamnine.repository.IngredientRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class IngredientService {
	@Autowired
	IngredientRepository ingredientRepo;
	
	// get specific ingredient by id
	public Ingredient getIngredientById(Integer id) {
		Optional<Ingredient> ingredient = ingredientRepo.findById(id);
		return ingredient.orElse(null);
	};
	
	// get specific ingredient by foodText
	public Ingredient getIngredientByfoodText(String foodText) {
		Optional<Ingredient> ingredient = ingredientRepo.findByfoodText(foodText);
		return ingredient.orElse(null);
	};
	
	// save ingredient
	public Ingredient saveIngredient(Ingredient ingredient) {
		return ingredientRepo.save(ingredient);
	}
}
