package com.ad.teamnine.model;

import java.time.LocalDate;
import java.util.List;



public class RecipeDTO {
	private Integer id;
	private String name;
	private String description;
	private String image;
	private Double rating;
	private Integer numberOfRating;
	private Integer numberOfSaved;
	private LocalDate submittedDate;
	private List<String> tags;
	private Integer servings;
	private Integer preparationTime;
	private List<String> steps;
	private Integer healthScore;
	private Double calories;
	private Double protein;
	private Double carbohydrate;
	private Double sugar;
	private Double sodium;
	private Double fat;
	private Double saturatedFat;
	private String username;
	private List<String> ingredient;
	public RecipeDTO() {
	}

	public RecipeDTO(Integer id, String name, String description, String image, Double rating, Integer numberOfRating,
			Integer numberOfSaved, LocalDate submittedDate, List<String> tags, Integer servings,
			Integer preparationTime, List<String> steps, Integer healthScore, Double calories, Double protein,
			Double carbohydrate, Double sugar, Double sodium, Double fat, Double saturatedFat,String username,List<String>ingredient) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.image = image;
		this.rating = rating;
		this.numberOfRating = numberOfRating;
		this.numberOfSaved = numberOfSaved;
		this.submittedDate = submittedDate;
		this.tags = tags;
		this.servings = servings;
		this.preparationTime = preparationTime;
		this.steps = steps;
		this.healthScore=healthScore;
		this.calories=calories;
		this.protein = protein;
		this.carbohydrate = carbohydrate;
		this.sugar = sugar;
		this.sodium = sodium;
		this.fat = fat;
		this.saturatedFat = saturatedFat;
		this.username=username;
		this.ingredient=ingredient;
	}

	public List<String> getIngredient() {
		return ingredient;
	}

	public void setIngredient(List<String> ingredient) {
		this.ingredient = ingredient;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public Integer getNumberOfRating() {
		return numberOfRating;
	}

	public void setNumberOfRating(Integer numberOfRating) {
		this.numberOfRating = numberOfRating;
	}

	public Integer getNumberOfSaved() {
		return numberOfSaved;
	}

	public void setNumberOfSaved(Integer numberOfSaved) {
		this.numberOfSaved = numberOfSaved;
	}

	public LocalDate getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(LocalDate submittedDate) {
		this.submittedDate = submittedDate;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public Integer getServings() {
		return servings;
	}

	public void setServings(Integer servings) {
		this.servings = servings;
	}

	public Integer getPreparationTime() {
		return preparationTime;
	}

	public void setPreparationTime(Integer preparationTime) {
		this.preparationTime = preparationTime;
	}

	public List<String> getSteps() {
		return steps;
	}

	public void setSteps(List<String> steps) {
		this.steps = steps;
	}

	public Integer getHealthScore() {
		return healthScore;
	}

	public void setHealthScore(Integer healthScore) {
		this.healthScore = healthScore;
	}

	public Double getCalories() {
		return calories;
	}

	public void setCalories(Double calories) {
		this.calories = calories;
	}

	public Double getProtein() {
		return protein;
	}

	public void setProtein(Double protein) {
		this.protein = protein;
	}

	public Double getCarbohydrate() {
		return carbohydrate;
	}

	public void setCarbohydrate(Double carbohydrate) {
		this.carbohydrate = carbohydrate;
	}

	public Double getSugar() {
		return sugar;
	}

	public void setSugar(Double sugar) {
		this.sugar = sugar;
	}

	public Double getSodium() {
		return sodium;
	}

	public void setSodium(Double sodium) {
		this.sodium = sodium;
	}

	public Double getFat() {
		return fat;
	}

	public void setFat(Double fat) {
		this.fat = fat;
	}

	public Double getSaturatedFat() {
		return saturatedFat;
	}

	public void setSaturatedFat(Double saturatedFat) {
		this.saturatedFat = saturatedFat;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}