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

	public RecipeDTO() {
	}

	public RecipeDTO(Integer id, String name, String description, String image, Double rating, Integer numberOfRating,
			Integer numberOfSaved, LocalDate submittedDate, List<String> tags, Integer servings,
			Integer preparationTime, List<String> steps) {
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

}