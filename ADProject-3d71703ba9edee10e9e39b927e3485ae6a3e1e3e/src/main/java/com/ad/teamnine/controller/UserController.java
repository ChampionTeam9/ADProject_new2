package com.ad.teamnine.controller;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ad.teamnine.model.*;
import com.ad.teamnine.service.*;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private RecipeService recipeService;
	@Autowired
	private IngredientService ingredientService;
	@Autowired
	private UserService userService;
	@Autowired
	private ShoppingListItemService shoppingListItemService;

	// Show page for adding ingredients to shopping list
	@GetMapping("/shoppingList/add")
	public String addShoppingListIngredient(Model model) {
		Recipe recipe = recipeService.getRecipeById(1);
		model.addAttribute("recipe", recipe);
		AddIngredientForm addIngredientForm = new AddIngredientForm();
		addIngredientForm.setIngredients(recipe.getIngredients());
		addIngredientForm.setSelectedIngredients(new HashSet<>());
		model.addAttribute("addIngredientForm", addIngredientForm);
		return "UserViews/addShoppingListIngredientPage";
	}

	// Save the ingredients selected as ShoppingListItem
	@PostMapping("/shoppingList/add")
	public String addShoppingListIngredient(
			@ModelAttribute("addIngredientForm") @Valid AddIngredientForm addIngredientForm,
			BindingResult bindingResult, Model model, HttpSession sessionObj) {
		if (bindingResult.hasErrors()) {
			return "UserViews/addShoppingListIngredientPage";
		}
		// Get member's shopping list
		// Member member =
		// memberService.getMemberById((int)sessionObj.getAttribute("userId"));
		// Hardcode first
		Member member = userService.getMemberById(1);
		List<ShoppingListItem> shoppingList = member.getShoppingList();
		Set<Integer> selectedIngredients = addIngredientForm.getSelectedIngredients();
		for (Integer id : selectedIngredients) {
			Ingredient ingredient = ingredientService.getIngredientById(id);
			ShoppingListItem shoppingListItem = new ShoppingListItem(member, ingredient);
			shoppingListItemService.saveShoppingListItem(shoppingListItem);
			shoppingList.add(shoppingListItem);
			System.out.println(ingredient);
		}
		userService.saveMember(member);
		// Add ingredients to member's shopping list
		return "redirect:/user/shoppingList/view";
	}

	// View the shopping list
	@GetMapping("shoppingList/view")
	public String viewShoppingListIngredient(Model model) {
		// Get member's shopping list
		// Member member =
		// memberService.getMemberById((int)sessionObj.getAttribute("userId"));
		// Hardcode first
		Member member = userService.getMemberById(1);
		List<ShoppingListItem> shoppingList = member.getShoppingList();
		model.addAttribute("shoppingList", shoppingList);
		return "/UserViews/viewShoppingListPage";
	}

	// Update database if shoppingListItem was checked off or not
	@PostMapping("shoppingList/updateCheckedStatus")
	public ResponseEntity<Void> updateCheckedStatus(@RequestBody Map<String, Object> payload) {
		int id = (int) payload.get("id");
		boolean isChecked = (boolean) payload.get("isChecked");
		ShoppingListItem shoppingListItem = shoppingListItemService.getShoppingListItemById(id);
		shoppingListItem.setChecked(isChecked);
		shoppingListItemService.saveShoppingListItem(shoppingListItem);
		return ResponseEntity.ok().build();
	}

	// Edit the shopping list
	@GetMapping("shoppingList/edit")
	public String editShoppingList(Model model) {
		// Get member's shopping list
		// Member member =
		// memberService.getMemberById((int)sessionObj.getAttribute("userId"));
		// Hardcode first
		Member member = userService.getMemberById(1);
		List<ShoppingListItem> shoppingList = member.getShoppingList();
		model.addAttribute("shoppingList", shoppingList);
		return "/UserViews/editShoppingListPage";
	}

	// Clear off ShoppingListItems
	@PostMapping("shoppingList/clearItems")
	public ResponseEntity<Void> clearItems(@RequestBody Map<String, Object> payload) {
		String message = (String) payload.get("message");
		System.out.println(message);
		// Get member's shopping list
		// Member member =
		// memberService.getMemberById((int)sessionObj.getAttribute("userId"));
		// Hardcode first
		Member member = userService.getMemberById(1);
		List<ShoppingListItem> shoppingList = member.getShoppingList();
		// Use iterator to prevent ConcurrentModificationException
		Iterator<ShoppingListItem> iterator = shoppingList.iterator();
		while (iterator.hasNext()) {
			ShoppingListItem item = iterator.next();
			System.out.println(item.getIngredient());
			if ((message.equals("clearChecked") && item.isChecked()) || message.equals("clearAll")) {
				iterator.remove();
				shoppingListItemService.deleteShoppingListItem(item);
			}
		}
		return ResponseEntity.ok().build();
	}
}
