package com.ad.teamnine.controller;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestParam;

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
		// To get from query string after integretion, hard code first
		Recipe recipe = recipeService.getRecipeById(1);
		model.addAttribute("recipe", recipe);
		AddIngredientForm addIngredientForm = new AddIngredientForm();
		List<String> ingredientNames = new ArrayList<>();
		for (Ingredient ingredient : recipe.getIngredients()) {
			ingredientNames.add(ingredient.getFoodText());
		}
		addIngredientForm.setIngredientNames(ingredientNames);
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
		// Member member = memberService.getMemberById((int)sessionObj.getAttribute("userId"));
		// Hardcode first
		Member member = userService.getMemberById(1);
		List<ShoppingListItem> shoppingList = member.getShoppingList();
		List<String> ingredientNames = addIngredientForm.getIngredientNames();
		List<Integer> selectedIngredients = addIngredientForm.getSelectedIngredients();
		for (int i = 0; i < selectedIngredients.size(); i++) {
			int pos = selectedIngredients.get(i);
			String ingredientName = ingredientNames.get(pos);
			ShoppingListItem shoppingListItem = new ShoppingListItem(member, ingredientName);
			shoppingListItemService.saveShoppingListItem(shoppingListItem);
			shoppingList.add(shoppingListItem);
			System.out.println("ingredientName: " + ingredientName);
		}
		userService.saveMember(member);
		return "redirect:/user/shoppingList/view";
	}

	// View the shopping list
	@GetMapping("shoppingList/view")
	public String viewShoppingListIngredient(Model model) {
		// Get member's shopping list
		// Member member = memberService.getMemberById((int)sessionObj.getAttribute("userId"));
		// Hardcode first
		Member member = userService.getMemberById(1);
		List<ShoppingListItem> shoppingList = member.getShoppingList();
		model.addAttribute("shoppingList", shoppingList);
		return "/UserViews/viewShoppingListPage";
	}

	// Update database if shoppingListItem was checked off or not
//	@PostMapping("shoppingList/updateCheckedStatus")
//	public ResponseEntity<Void> updateCheckedStatus(@RequestBody Map<String, Object> payload) {
//		int id = (int) payload.get("id");
//		System.out.println("Received Payload: " + payload);
//		boolean isChecked = (boolean) payload.get("isChecked");
//		ShoppingListItem shoppingListItem = shoppingListItemService.getShoppingListItemById(id);
//		shoppingListItem.setChecked(isChecked);
//		shoppingListItemService.saveShoppingListItem(shoppingListItem);
//		return ResponseEntity.ok().build();
//	}

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
		// Member member = memberService.getMemberById((int)sessionObj.getAttribute("userId"));
		// Hardcode first
		Member member = userService.getMemberById(1);
		List<ShoppingListItem> shoppingList = member.getShoppingList();
		// Use iterator to prevent ConcurrentModificationException
		Iterator<ShoppingListItem> iterator = shoppingList.iterator();
		while (iterator.hasNext()) {
			ShoppingListItem item = iterator.next();
			System.out.println(item.getIngredientName());
			if ((message.equals("clearChecked") && item.isChecked()) || message.equals("clearAll")) {
				iterator.remove();
				shoppingListItemService.deleteShoppingListItem(item);
			}
		}
		return ResponseEntity.ok().build();
	}
	
	// Add ShoppingListItem at editShoppingListPage
	@PostMapping("shoppingList/addItem")
	public ResponseEntity<Map<String, Object>> addItem (@RequestBody Map<String, Object> payload){
		String ingredientName = (String) payload.get("ingredientName");
		// Member member = memberService.getMemberById((int)sessionObj.getAttribute("userId"));
		// Hardcode first
		Member member = userService.getMemberById(1);
		ShoppingListItem newItem = new ShoppingListItem(member, ingredientName);
		ShoppingListItem savedItem = shoppingListItemService.saveShoppingListItem(newItem);
		int id = savedItem.getId();
		Map<String, Object> response = new HashMap<>();
	    response.put("id", id);
		return ResponseEntity.ok(response);
	}
	
	//set preference
	@GetMapping("/setPreference")
	public String setPreference(Model model) {
		Set<String> tags = userService.getRandomUniqueTags(7);
		model.addAttribute("tags", tags);
		return "/UserViews/setPreference";
	}
	
	@PostMapping("/setPreference")
	public String receivePreference(@RequestParam(value = "tags", required = false) List<String> tags,HttpSession session) {
		List<String> oldTags = (List<String>) session.getAttribute("tags");
		Member member=new Member();
		if (oldTags == null) {
			member.setPrefenceList(tags);
			userService.saveMember(member);
		} else {
			Set<String> selectedTags = new HashSet<>(oldTags);
			selectedTags.addAll(tags);
			List<String> combinedTags = new ArrayList<>(selectedTags);
			member.setPrefenceList(combinedTags);
			userService.saveMember(member);
		}	
		return "test";
	}
	//refresh tags on the website
	@PostMapping("/refresh")
	public String refreshTags(Model model, @RequestParam("tags") List<String> tags, HttpSession session) {
		List<String> oldTags = (List<String>) session.getAttribute("tags");
		if (oldTags == null) {
			session.setAttribute("tags", tags);
		} else {
			Set<String> selectedTags = new HashSet<>(oldTags);
			selectedTags.addAll(tags);
			List<String> combinedTags = new ArrayList<>(selectedTags);
			session.setAttribute("tags", combinedTags);
			Set<String> newTags = userService.getRandomUniqueTags(7);
			model.addAttribute("tags", newTags);
		}
		Set<String> newTags = userService.getRandomUniqueTags(7);
		model.addAttribute("tags", newTags);
		return "redirect:/user/setPreference";
	}

	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("member", new Member());
		return "/UserViews/register";
	}

	@PostMapping("/register")
	public String registermember(@Valid @ModelAttribute("member") Member inMember, BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			return "/UserViews/register";
		}
		inMember.setStatus(Status.CREATED);
		LocalDate birthdate = inMember.getBirthdate();
		LocalDate currentDate = LocalDate.now();
		if (birthdate != null) {
			int age = Period.between(birthdate, currentDate).getYears();
			inMember.setAge(age);
		}
		userService.saveMember(inMember);
		return "redirect:/page1";
	}

}
