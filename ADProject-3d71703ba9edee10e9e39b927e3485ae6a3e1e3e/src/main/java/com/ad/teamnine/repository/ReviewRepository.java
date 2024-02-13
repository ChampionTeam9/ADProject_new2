package com.ad.teamnine.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ad.teamnine.model.Review;

public interface ReviewRepository extends JpaRepository<Review,Integer>{
	
	@Query("SELECT COUNT(r) FROM Review r WHERE r.recipe.id = :recipeId")
    int getNumberOfUsersRatings(@Param("recipeId") int recipeId);

	@Query("SELECT AVG(r.rating) FROM Review r WHERE r.recipe.id = :recipeId")
	double getMeanRating(@Param("recipeId")Integer recipeId);
}
