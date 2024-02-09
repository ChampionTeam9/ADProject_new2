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
	public CommandLineRunner commandLineRun(AdminRepository adminRepo, MemberRepository memberRepo, IngredientRepository ingrRepo, RecipeRepository recipeRepo) {
		return args -> {
			LocalDate date1 = LocalDate.of(2000, 1, 8);
			Member member1 = new Member("member1Username", "member1Password!", 170, 65.3, date1, "Male");
			memberRepo.save(member1);
			
			Member member2 = new Member("member2Username", "member2Password!", 160, 53.0, date1, "Female");
			memberRepo.save(member2);
			
			Ingredient ingredient1 = new Ingredient("1 whole banana", 1.26, 102.97, 26.38, 14.11, 1.16, 0.38, 0.13);
			ingrRepo.save(ingredient1);
			
			Ingredient ingredient2 = new Ingredient("2 cups all-purpose flour", 25.75, 910.0, 190.75, 0.675, 5.0, 2.45, 0.3875);
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
			recipeRepo.save(recipe1);

			Recipe recipe2 = new Recipe("recipe2Name", "recipe2Description", member1);
			recipe2.getIngredients().add(ingredient1);
			recipe2.getIngredients().add(ingredient2);
			recipe2.setPreparationTime(30);
			recipe2.setServings(4);
			recipe2.setNotes("This is the notes of recipe 1");
			recipe2.getSteps().add("Cut the bananas and mix well with flour");
			recipe2.setImage("1b06d0cb-3609-4d5e-8c8c-bb7fe73ca345_download.jpg");
			recipeRepo.save(recipe2);
			
			Recipe recipe3 = new Recipe("recipe3Name", "recipe3Description", member1);
			recipe3.getIngredients().add(ingredient1);
			recipe3.getIngredients().add(ingredient2);
			recipe3.setPreparationTime(30);
			recipe3.setServings(4);
			recipe3.setNotes("This is the notes of recipe 1");
			recipe3.getSteps().add("Cut the bananas and mix well with flour");
			recipe3.setImage("816befd0-f0bf-4e87-b991-2f01269dd55b_download2.jpg");
			recipeRepo.save(recipe3);
			
			Recipe recipe4 = new Recipe("recipe4Name", "recipe4Description", member1);
			recipe4.getIngredients().add(ingredient1);
			recipe4.getIngredients().add(ingredient2);
			recipe4.setPreparationTime(30);
			recipe4.setServings(4);
			recipe4.setNotes("This is the notes of recipe 1");
			recipe4.getSteps().add("Cut the bananas and mix well with flour");
			recipe4.setImage("816befd0-f0bf-4e87-b991-2f01269dd55b_download2.jpg");
			recipeRepo.save(recipe4);
			
			Recipe recipe5 = new Recipe("recipe5Name", "recipe5Description", member1);
			recipe5.getIngredients().add(ingredient1);
			recipe5.getIngredients().add(ingredient2);
			recipe5.setPreparationTime(30);
			recipe5.setServings(4);
			recipe5.setNotes("This is the notes of recipe 1");
			recipe5.getSteps().add("Cut the bananas and mix well with flour");
			recipe5.setImage("1b06d0cb-3609-4d5e-8c8c-bb7fe73ca345_download.jpg");
			recipeRepo.save(recipe5);
			
			ingredient1.getRecipes().add(recipe1);
			ingredient1.getRecipes().add(recipe2);
			ingredient1.getRecipes().add(recipe3);
			ingredient1.getRecipes().add(recipe4);
			ingredient1.getRecipes().add(recipe5);
			ingredient2.getRecipes().add(recipe1);
			ingredient2.getRecipes().add(recipe2);
			ingredient2.getRecipes().add(recipe3);
			ingredient2.getRecipes().add(recipe4);
			ingredient2.getRecipes().add(recipe5);
			ingrRepo.save(ingredient1);
			ingrRepo.save(ingredient2);
			Admin admin1 = new Admin();
			admin1.setId(3);
			admin1.setPassword("Admin1password!");
			admin1.setUsername("Admin1username!");
			adminRepo.save(admin1);
		};
	}
}
