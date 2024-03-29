package com.ad.teamnine.service;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.ad.teamnine.model.Ingredient;
import com.ad.teamnine.model.Member;
import com.ad.teamnine.model.Recipe;
import com.ad.teamnine.model.RecipeDTO;
import com.ad.teamnine.model.ShoppingListItem;
import com.ad.teamnine.model.ShoppingListItemDTO;
import com.ad.teamnine.model.Status;
import com.ad.teamnine.repository.MemberRepository;
import com.ad.teamnine.repository.RecipeRepository;
import com.ad.teamnine.repository.ReviewRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class RecipeService {
	@Autowired
	RecipeRepository recipeRepo;
	@Autowired
	MemberRepository memberRepo;
	@Autowired
	ReviewRepository reviewRepo;

	// create new recipe
	public Recipe createRecipe(Recipe newRecipe) {
		return recipeRepo.save(newRecipe);
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
		recipe.getMembersWhoSave().add(member);
		recipe.setNumberOfSaved(recipe.getNumberOfSaved() + 1);
		memberRepo.save(member);
		recipeRepo.save(recipe);
	}

	// unsubscribe specific recipe by id
	public void unsubscribeRecipe(Recipe recipe, Member member) {

		member.getSavedRecipes().remove(recipe);
		recipe.getMembersWhoSave().remove(member);
		recipe.setNumberOfSaved(recipe.getNumberOfSaved() - 1);
		memberRepo.save(member);
		recipeRepo.save(recipe);

	}

	// get number of people who rated a specific recipe
	public int getNumberOfUsersRatings(int recipeId) {
		return reviewRepo.getNumberOfUsersRatings(recipeId);
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
			recipe.setStatus(Status.PUBLIC);
			recipeRepo.save(recipe);
		} else {
			System.out.println("Recipe with ID " + id + " not found");
		}
	}

	// set recipe to private
	public void setStatusToPrivateById(Integer id) {
		Recipe recipe = recipeRepo.findById(id).orElse(null);
		if (recipe != null) {
			recipe.setStatus(Status.PRIVATE);
			recipeRepo.save(recipe);
		} else {
			System.out.println("Recipe with ID " + id + " not found");
		}
	}

	public List<Recipe> searchByName(String query) {
		List<Recipe> results = recipeRepo.findByNameContaining(query);
		return results;
	}

	public Page<Recipe> searchByName(String query, int pageNo, int pageSize) {
		PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
		Page<Recipe> results = recipeRepo.findByNameContainingByPage(query, pageRequest);
		return results;
	}

	public List<Recipe> searchByTag(String tag) {
		List<Recipe> results = recipeRepo.findByTagsContaining(tag);
		return results;
	}

	public Page<Recipe> searchByTag(String tag, int pageNo, int pageSize) {
		PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
		Page<Recipe> results = recipeRepo.findByTagsContainingByPage(tag, pageRequest);
		return results;
	}

	public List<Recipe> searchByDescription(String query) {
		List<Recipe> results = recipeRepo.findByDescriptionContaining(query);
		return results;
	}

	public Page<Recipe> searchByDescription(String query, int pageNo, int pageSize) {
		PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
		Page<Recipe> results = recipeRepo.findByDescriptionContainingByPage(query, pageRequest);
		return results;
	}

	public List<Recipe> searchAll(String query) {
		List<Recipe> results1 = recipeRepo.findByNameContaining(query);
		List<Recipe> results2 = recipeRepo.findByTagsContaining(query);
		List<Recipe> results3 = recipeRepo.findByDescriptionContaining(query);
		return mergeLists(results1, results2, results3);
	}

	// merge
	public List<Recipe> mergeLists(List<Recipe> listByName, List<Recipe> listByTag, List<Recipe> listByDescription) {
		return Stream.of(listByName, listByTag, listByDescription).flatMap(List::stream).distinct()
				.collect(Collectors.toList());
	}

	public List<Recipe> getAllRecipes() {
		return recipeRepo.findAll();
	}

	public List<Recipe> getRecipesByOrder(String orderBy, String order) {
		List<Recipe> recipes = new ArrayList<>();
		if (orderBy.equals("rating")) {
			if (order.equals("asc")) {
				recipes = recipeRepo.findAllByOrderByRatingAsc();
			} else if (order.equals("desc")) {
				recipes = recipeRepo.findAllByOrderByRatingDesc();
			}
		} else if (orderBy.equals("numberOfSaved")) {
			if (order.equals("asc")) {
				recipes = recipeRepo.findAllByOrderByNumberOfSavedAsc();
			} else if (order.equals("desc")) {
				recipes = recipeRepo.findAllByOrderByNumberOfSavedDesc();
			}
		} else if (orderBy.equals("healthScore")) {
			if (order.equals("asc")) {
				recipes = recipeRepo.findAllByOrderByHealthScoreAsc();
			} else if (order.equals("desc")) {
				recipes = recipeRepo.findAllByOrderByHealthScoreDesc();
			}
		} else {
			recipes = recipeRepo.findAll();
		}
		return recipes;
	}

	// get all unique tags
	public Set<String> getAllUniqueTags() {
		List<String> tagLists = recipeRepo.findAllDistinctTags();
		Set<String> uniqueTags = new HashSet<>();

		for (String tags : tagLists) {
			uniqueTags.addAll(Arrays.asList(tags.split(",")));
		}

		return uniqueTags;
	}

	public Set<String> getRandomUniqueTags(int count) {
		List<String> allTags = new ArrayList<>(getAllUniqueTags());
		Collections.shuffle(allTags, new Random());
		return allTags.stream().limit(count).collect(Collectors.toCollection(LinkedHashSet::new));
	}

	public List<String> findMatchingTags(String keyword) {
		Set<String> allUniqueTags = getAllUniqueTags();

		List<String> matchingTags = allUniqueTags.stream()
				.filter(tag -> tag.toLowerCase().contains(keyword.toLowerCase())).collect(Collectors.toList());

		return matchingTags;
	}

	public List<Recipe> getAllRecipesByMember(Member member, Status status) {
		return recipeRepo.findByMember(member, status);
	}

	public List<Recipe> getAllRecipesByYear(int year) {
		return recipeRepo.getAllRecipesByYear(year);
	}

	public List<Object[]> getRecipeCountByTag() {
		return recipeRepo.getRecipeCountByTag();
	}

	public List<Object[]> getRecipeCountByYear() {
		return recipeRepo.countRecipesBeforeEachYear(LocalDate.now());
	}

	public int getRecipeCountAddedToday() {
		return recipeRepo.countRecipesAddedToday();
	}

	public int getRecipeCountAddedThisYear() {
		return recipeRepo.countRecipesAddedThisYear();
	}

	public Page<Recipe> findAllRecipesByPage(int pageNo, int pageSize) {
		PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
		Page<Recipe> recipePage = recipeRepo.findAllPublic(pageRequest);
		return recipePage;
	}

	public List<Recipe> getRecipes(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Recipe> pageX = recipeRepo.findAll(pageable);
		return pageX.getContent();

	}

	public List<RecipeDTO> recipeTurnToDTO(List<Recipe> recipes) {
		List<RecipeDTO> recipeDTOs = new ArrayList<>();
		
		for (Recipe recipe : recipes) {
			RecipeDTO recipeDTO = new RecipeDTO();
			recipeDTO.setId(recipe.getId());
			recipeDTO.setName(recipe.getName());
			recipeDTO.setDescription(recipe.getDescription());
			recipeDTO.setImage(recipe.getImage());
			recipeDTO.setNumberOfRating(recipe.getNumberOfRating());
			recipeDTO.setRating(recipe.getRating());
			recipeDTO.setNumberOfSaved(recipe.getNumberOfSaved());
			recipeDTO.setPreparationTime(recipe.getPreparationTime());
			recipeDTO.setSteps(recipe.getSteps());
			recipeDTO.setSubmittedDate(recipe.getSubmittedDate());
			recipeDTO.setTags(recipe.getTags());
			recipeDTO.setServings(recipe.getServings());
			recipeDTO.setCalories(recipe.getCalories());
			recipeDTO.setCarbohydrate(recipe.getCarbohydrate());
			recipeDTO.setFat(recipe.getFat());
			recipeDTO.setHealthScore(recipe.getHealthScore());
			recipeDTO.setProtein(recipe.getProtein());
			recipeDTO.setSugar(recipe.getSugar());
			recipeDTO.setSaturatedFat(recipe.getSaturatedFat());
			recipeDTO.setSodium(recipe.getSodium());
			recipeDTO.setUsername(recipe.getMember().getUsername());

			List<String> ingredients = new ArrayList<>();
			for (Ingredient ingredient : recipe.getIngredients()) {
				String foodText = ingredient.getFoodText();
				ingredients.add(foodText);
			}
			recipeDTO.setIngredients(ingredients);

			recipeDTOs.add(recipeDTO);
		}
		return recipeDTOs;
	}
	public List<ShoppingListItemDTO> shoppingListTurnToDTO(List<ShoppingListItem> shoppinglist){
		List<ShoppingListItemDTO> DTOs=new ArrayList<>();
		for(ShoppingListItem item:shoppinglist) {
			ShoppingListItemDTO DTO=new ShoppingListItemDTO();
			DTO.setChecked(item.isChecked());
			DTO.setId(item.getId());
			DTO.setIngredientName(item.getIngredientName());
			DTOs.add(DTO);
		}
		return DTOs;
	}
}
