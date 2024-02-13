package com.ad.teamnine.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ad.teamnine.model.Member;
import com.ad.teamnine.model.Recipe;
import com.ad.teamnine.model.Review;
@Repository
public interface RecipeRepository extends JpaRepository<Recipe,Integer>{

	@Query("SELECT DISTINCT r.tags FROM Recipe r")
    List<String> findAllDistinctTags();
	@Query("SELECT r FROM Recipe r WHERE r.name LIKE %?1%")
    List<Recipe> findByNameContaining(String name);
	@Query("SELECT r FROM Recipe r JOIN r.tags t WHERE t LIKE %?1%")
	List<Recipe> findByTagsContaining(String tag);
	@Query("SELECT r FROM Recipe r WHERE r.description LIKE %?1%")
    List<Recipe> findByDescriptionContaining(String description);
	
	List<Recipe> findByMember(Member member);
	
	@Query("SELECT r FROM Recipe r WHERE FUNCTION('YEAR', r.submittedDate) = :year ORDER BY r.submittedDate")
	List<Recipe> getAllRecipesByYear(@Param("year") int year);
	
	@Query("SELECT t, COUNT(r) AS recipeCount FROM Recipe r JOIN r.tags t GROUP BY t ORDER BY recipeCount DESC")
	List<Object[]> getRecipeCountByTag();
	
	@Query("SELECT r FROM Review r WHERE r.recipe = :recipe ORDER BY r.reviewDate DESC")
	List<Review> getReviewsByRecipe(@Param("recipe") Recipe recipe);
}
