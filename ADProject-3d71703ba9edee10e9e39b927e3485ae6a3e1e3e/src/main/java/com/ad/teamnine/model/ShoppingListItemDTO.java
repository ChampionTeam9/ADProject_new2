package com.ad.teamnine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ShoppingListItemDTO {

	
	private Integer id;
	private String ingredientName;
	@JsonProperty("isChecked")
	private boolean isChecked;
	private String username;
	
	public ShoppingListItemDTO() {}
	
	public ShoppingListItemDTO( String ingredientName,boolean isChecked,String username) {
		this.ingredientName = ingredientName;
		this.isChecked = isChecked;
		this.setUsername(username);
	}
	
	


	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getIngredientName() {
		return ingredientName;
	}
	public void setIngredientName(String ingredientName) {
		this.ingredientName = ingredientName;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	
}
