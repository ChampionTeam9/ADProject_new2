package com.ad.teamnine;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.ad.teamnine.model.Admin;
import com.ad.teamnine.model.Ingredient;
import com.ad.teamnine.model.Member;
import com.ad.teamnine.model.Recipe;
import com.ad.teamnine.repository.AdminRepository;
import com.ad.teamnine.repository.IngredientRepository;
import com.ad.teamnine.repository.MemberRepository;
import com.ad.teamnine.repository.RecipeRepository;

@SpringBootApplication
public class AdProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdProjectApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRun(AdminRepository adminRepo, MemberRepository memberRepo,
			IngredientRepository ingrRepo, RecipeRepository recipeRepo) {
		return args -> {
			if (memberRepo.count() == 0) {
				LocalDate date1 = LocalDate.of(2000, 1, 8);
				Member member1 = new Member("member1Username", "member1Password!", 170, 65.3, date1, "Male",
						"e1221820@u.nus.edu");
				memberRepo.save(member1);

				Ingredient ingredient1 = new Ingredient("1 whole banana", 1.26, 102.97, 26.38, 14.11, 1.16, 0.38, 0.13);
				ingrRepo.save(ingredient1);

				Ingredient ingredient2 = new Ingredient("2 cups all-purpose flour", 25.75, 910.0, 190.75, 0.675, 5.0,
						2.45, 0.3875);
				ingrRepo.save(ingredient2);

				Recipe recipe1 = new Recipe("recipe1Name", "recipe1Description", member1);
				recipe1.getIngredients().add(ingredient1);
				recipe1.getIngredients().add(ingredient2);
				recipe1.setImage("1b06d0cb-3609-4d5e-8c8c-bb7fe73ca345_download.jpg");
				recipe1.setPreparationTime(30);
				recipe1.setServings(4);
				recipe1.setNotes("This is the notes of recipe 1");
				recipe1.getSteps().add("Cut the bananas and mix well with flour");
				recipe1.getTags().add("cuisine");
				recipe1.getTags().add("easy");
				recipe1.setHealthScore(3);
				recipe1.setCalories(350.5);
				recipeRepo.save(recipe1);
				ingredient1.getRecipes().add(recipe1);
				ingredient2.getRecipes().add(recipe1);
				ingrRepo.save(ingredient1);
				ingrRepo.save(ingredient2);
			}
			if (adminRepo.count() == 0) {
				Admin admin1 = new Admin();
				admin1.setId(3);
				admin1.setPassword("Admin1password!");
				admin1.setUsername("Admin1username!");
				admin1.setEmail("e1221820@u.nus.edu");
				adminRepo.save(admin1);
			}
		};
	}
}
