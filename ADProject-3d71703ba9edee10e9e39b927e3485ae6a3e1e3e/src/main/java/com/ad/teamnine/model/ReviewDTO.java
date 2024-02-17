package com.ad.teamnine.model;

import java.time.LocalDate;




public class ReviewDTO {
	private Integer id;
	private Integer rating;
	private String comment;
	private LocalDate reviewDate;
	private Integer memberId;
	private Integer recipeId;
	public ReviewDTO() {
	}

	public ReviewDTO(Integer id, Integer rating, String comment, LocalDate reviewDate,Integer memberId,Integer recipeId) {
		this.id = id;
		}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public LocalDate getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(LocalDate reviewDate) {
		this.reviewDate = reviewDate;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getRecipeId() {
		return recipeId;
	}

	public void setRecipeId(Integer recipeId) {
		this.recipeId = recipeId;
	}

}