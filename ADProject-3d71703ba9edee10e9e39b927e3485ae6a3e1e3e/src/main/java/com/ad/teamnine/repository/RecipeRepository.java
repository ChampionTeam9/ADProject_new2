package com.ad.teamnine.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ad.teamnine.model.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe,Integer>{

	@Query("SELECT DISTINCT r.tags FROM Recipe r")
    List<String> findAllDistinctTags();
	@Query("SELECT r FROM Recipe r WHERE r.name LIKE %?1%")
    List<Recipe> findByNameContaining(String name);
	@Query("SELECT r FROM Recipe r JOIN r.tags t WHERE t LIKE %?1%")
	List<Recipe> findByTagsContaining(String tag);
	@Query("SELECT r FROM Recipe r WHERE r.description LIKE %?1%")
    List<Recipe> findByDescriptionContaining(String description);
}
