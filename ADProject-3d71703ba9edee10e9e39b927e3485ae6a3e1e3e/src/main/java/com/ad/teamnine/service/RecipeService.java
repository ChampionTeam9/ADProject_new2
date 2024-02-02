package com.ad.teamnine.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.ad.teamnine.model.Member;
import com.ad.teamnine.model.Recipe;
import com.ad.teamnine.model.Status;
import com.ad.teamnine.repository.MemberRepository;
import com.ad.teamnine.repository.RecipeRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class RecipeService {
	@Autowired
	RecipeRepository recipeRepo;
	@Autowired
	MemberRepository memberRepo;

	// create new recipe
	public void createRecipe(Recipe newRecipe) {
		recipeRepo.save(newRecipe);
		return;
	}

	// update exist recipe
	public void updateRecipe(Recipe newRecipe) {
		recipeRepo.save(newRecipe);
		return;
	}

	// delete specific recipe by id
	public void deleteRecipe(Integer id) {
		try {
			recipeRepo.deleteById(id);
			System.out.println("Recipe with ID " + id + " has been deleted");
		} catch (EmptyResultDataAccessException e) {
			System.out.println("Recipe with ID " + id + " does not exist");
		}
	}

	// save specific recipe by id
	public void saveRecipe(Recipe recipe, Member member) {
		member.getSavedRecipes().add(recipe);
		memberRepo.save(member);
	}

	// unsubscribe specific recipe by id
	public void unsubscribeRecipe(Recipe recipe, Member member) {
		member.getSavedRecipes().remove(recipe);
		memberRepo.save(member);
	}

	// get specific recipe by id
	public Recipe getRecipeById(Integer id) {
		Optional<Recipe> recipe = recipeRepo.findById(id);
		return recipe.orElse(null);
	};

	// set recipe to public
	public void setStatusToPublicById(Integer id) {
		Recipe recipe = recipeRepo.findById(id).orElse(null);
		if (recipe != null) {
			recipe.setStatus(Status.Public);
			recipeRepo.save(recipe);
		} else {
			System.out.println("Recipe with ID " + id + " not found");
		}
	}

	// set recipe to private
	public void setStatusToPrivateById(Integer id) {
		Recipe recipe = recipeRepo.findById(id).orElse(null);
		if (recipe != null) {
			recipe.setStatus(Status.Private);
			recipeRepo.save(recipe);
		} else {
			System.out.println("Recipe with ID " + id + " not found");
		}
	}
}

