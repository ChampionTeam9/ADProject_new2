package com.ad.teamnine.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ad.teamnine.model.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient,Integer>{

	@Query("SELECT i FROM Ingredient i "
			+ "WHERE i.foodText = :foodText")
	Optional<Ingredient> findByfoodText(@Param("foodText") String foodText);

}
