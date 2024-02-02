package com.ad.teamnine.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ad.teamnine.model.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe,Integer>{

}
