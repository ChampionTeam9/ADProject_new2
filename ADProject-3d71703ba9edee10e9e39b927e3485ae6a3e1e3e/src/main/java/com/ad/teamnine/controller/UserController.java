package com.ad.teamnine.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
	private UserService userService;
	@Autowired
	private ShoppingListItemService shoppingListItemService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private ReportService reportService;

	// Show page for adding ingredients to shopping list
	@GetMapping("/shoppingList/add/{id}")
	public String addShoppingListIngredient(Model model, @PathVariable("id") int recipeId) {
		Recipe recipe = recipeService.getRecipeById(recipeId);
		model.addAttribute("recipe", recipe);
		AddIngredientForm addIngredientForm = new AddIngredientForm();
		List<String> ingredientNames = new ArrayList<>();
		for (Ingredient ingredient : recipe.getIngredients()) {
			ingredientNames.add(ingredient.getFoodText());
		}
		addIngredientForm.setIngredientNames(ingredientNames);
		model.addAttribute("addIngredientForm", addIngredientForm);
		return "/UserViews/addShoppingListIngredientPage";
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
		Member member = userService.getMemberById((int) sessionObj.getAttribute("userId"));
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
	public String viewShoppingListIngredient(Model model, HttpSession sessionObj) {
		// Get member's shopping list
		Member member = userService.getMemberById((int) sessionObj.getAttribute("userId"));
		List<ShoppingListItem> shoppingList = member.getShoppingList();
		model.addAttribute("shoppingList", shoppingList);
		return "/UserViews/viewShoppingListPage";
	}

	// Update database if shoppingListItem was checked off or not
	@PostMapping("shoppingList/updateCheckedStatus")
	public ResponseEntity<Void> updateCheckedStatus(@RequestBody Map<String, Object> payload) {
		int id = (int) payload.get("id");
		System.out.println("Received Payload: " + payload);
		boolean isChecked = (boolean) payload.get("isChecked");
		ShoppingListItem shoppingListItem = shoppingListItemService.getShoppingListItemById(id);
		shoppingListItem.setChecked(isChecked);
		shoppingListItemService.saveShoppingListItem(shoppingListItem);
		return ResponseEntity.ok().build();
	}

	// Clear off ShoppingListItems
	@PostMapping("shoppingList/clearItems")
	public ResponseEntity<Void> clearItems(@RequestBody Map<String, Object> payload, HttpSession sessionObj) {
		String message = (String) payload.get("message");
		System.out.println(message);
		// Get member's shopping list
		Member member = userService.getMemberById((int) sessionObj.getAttribute("userId"));
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
	public ResponseEntity<Map<String, Object>> addItem(@RequestBody Map<String, Object> payload,
			HttpSession sessionObj) {
		String ingredientName = (String) payload.get("ingredientName");
		Member member = userService.getMemberById((int) sessionObj.getAttribute("userId"));
		ShoppingListItem newItem = new ShoppingListItem(member, ingredientName);
		ShoppingListItem savedItem = shoppingListItemService.saveShoppingListItem(newItem);
		int id = savedItem.getId();
		Map<String, Object> response = new HashMap<>();
		response.put("id", id);
		return ResponseEntity.ok(response);
	}

	// set preference
	@GetMapping("/setPreference")
	public String setPreference(Model model) {
		Set<String> tags = userService.getRandomUniqueTags(7);
		model.addAttribute("tags", tags);
		return "/UserViews/setPreferencePage";
	}

	@PostMapping("/setPreference")
	public String receivePreference(@RequestParam(value = "tags", required = false) List<String> tags,
			HttpSession session) {
		List<String> oldTags = (List<String>) session.getAttribute("tags");
		Member member = userService.getMemberById((int) session.getAttribute("userId"));
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
		return "redirect:/";
	}

	// refresh tags on the website
	@PostMapping("/refresh")
	public String refreshTags(Model model, @RequestParam(value = "tags", required = false) List<String> tags,
			HttpSession session) {
		List<String> oldTags = (List<String>) session.getAttribute("tags");
		System.out.println("refresh method called");
		if (oldTags == null) {
			session.setAttribute("tags", tags);
		} else {
			Set<String> selectedTags = new HashSet<>(oldTags);
			if (tags != null) {
				selectedTags.addAll(tags);
				List<String> combinedTags = new ArrayList<>(selectedTags);
				session.setAttribute("tags", combinedTags);
			}
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
	public String registerMember(@Valid @ModelAttribute("member") Member newMember, BindingResult bindingResult,
			Model model, HttpSession httpSession) {
		if (bindingResult.hasErrors()) {
			return "/UserViews/register";
		}
		newMember.setMemberStatus(Status.CREATED);
		newMember.setRegistrationDate(LocalDate.now());
		if (newMember.getEmail() == null || newMember.getEmail().isEmpty()) {
			userService.saveMember(newMember);
			return "redirect:/user/setPreference";
		}
		String code = EmailService.generateVerificationCode();
		emailService.SendEmailVerificationCodeToMember(newMember, code);
		model.addAttribute("newMember", newMember);
		model.addAttribute("verifyCode", code);
		return "UserViews/verificationMailBoxPage";
	}

	@PostMapping("/checkIfUsernameAvailable")
	public ResponseEntity<Map<String, Object>> checkIfUsernameAvailable(@RequestBody Map<String, Object> payload) {
		String username = (String) payload.get("username");
		Boolean userAlrExists = userService.checkifUserExist(username);
		Map<String, Object> response = new HashMap<>();
		response.put("userAlrExists", userAlrExists);
		return ResponseEntity.ok(response);
	}

	// Show my profile for updating of personal information
	@GetMapping("/myProfile")
	public String viewMyProfile(HttpSession sessionObj, Model model) {
		Integer id = (Integer) sessionObj.getAttribute("userId");
		return "redirect:/user/profile/" + id;
	}

	@GetMapping("/editProfile")
	public String viewMemberProfile(HttpSession sessionObj, Model model) {
		Integer id = (Integer) sessionObj.getAttribute("userId");
		Member member = userService.getMemberById(id);
		model.addAttribute("member", member);
		return "UserViews/showMyProfile";
	}

	// Save my profile
	@PostMapping("/saveProfile")
	public String saveProfile(@ModelAttribute("member") @Valid Member member, BindingResult bindingResult, Model model,
			HttpSession sessionObj) {
		if (bindingResult.hasErrors()) {
			return "UserViews/showMyProfile";
		}
		member.calculateAge();
		member.calculateCalorieIntake();
		userService.saveMember(member);
		return "redirect:/user/myProfile";
	}

	// Look at others' profile
	@GetMapping("/profile/{id}")
	public String viewUserProfile(@PathVariable("id") Integer memberId, HttpSession sessionObj, Model model) {
		Member member = userService.getMemberById(memberId);
		if (member.getMemberStatus() == Status.DELETED) {
			return "UserViews/memberDeletedPage";
		}
		model.addAttribute("member", member);
		Object userIdObj = sessionObj.getAttribute("userId");
		if (userIdObj != null) {
			Integer userId = Integer.valueOf(userIdObj.toString());
			if (userId.equals(member.getId())) {
				model.addAttribute("isMe", true);
			} else {
				model.addAttribute("isMe", false);
			}
		} else {
			model.addAttribute("isMe", false);
		}
		List<Recipe> publicRecipes = recipeService.getAllRecipesByMember(member, Status.PUBLIC);
		model.addAttribute("recipes", publicRecipes);
		Object userType = sessionObj.getAttribute("userType");
		Object userId = sessionObj.getAttribute("userId");
		if (userId != null && userType != null && userType.toString().equals("admin")) {
			model.addAttribute("ifAdmin", true);
		} else {
			model.addAttribute("ifAdmin", false);
		}
		model.addAttribute("numberOfAdded", userService.getMemberAddedRecipesCount(member.getId()));
		model.addAttribute("numberOfSaved", member.getSavedRecipes().size());
		model.addAttribute("numberOfReviews", member.getReviews().size());
		return "UserViews/userProfile";
	}

	@GetMapping("/admin/dashboard")
	public String getDashboard(Model model) {
		model.addAttribute("numberOfRecipeAddedToday", recipeService.getRecipeCountAddedToday());
		model.addAttribute("numberOfMemberAddedToday", userService.getMemberCountAddedToday());
		model.addAttribute("numberOfRecipeAddedThisYear", recipeService.getRecipeCountAddedThisYear());
		model.addAttribute("numberOfMemberAddedThisYear", userService.getMemberCountAddedThisYear());
		model.addAttribute("numberOfMemberReports", reportService.getMemberReportCount());
		model.addAttribute("numberOfRecipeReports", reportService.getRecipeReportCount());

		// Get data to plot number of recipes submitted per month in a certain year
		int year = LocalDate.now().getYear();
		List<Recipe> recipesByYear = recipeService.getAllRecipesByYear(year);
		List<String> months = new ArrayList<>();
		Collections.addAll(months, "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
		// Initialize list with 0s for each month
		List<Integer> recipeCountByMonth = new ArrayList<>(Collections.nCopies(12, 0));
		for (Recipe recipe : recipesByYear) {
			int monthIndex = recipe.getSubmittedDate().getMonthValue() - 1;
			recipeCountByMonth.set(monthIndex, recipeCountByMonth.get(monthIndex) + 1);
		}
		model.addAttribute("months", months);
		model.addAttribute("recipeCountByMonth", recipeCountByMonth);

		// Get data to plot top 10 tags
		List<Object[]> tagCounts = recipeService.getRecipeCountByTag();
		List<String> tags = new ArrayList<>();
		List<Long> recipeCountByTag = new ArrayList<>();
		System.out.println("size = " + tagCounts.size());
		Long tenthTagCount = 0L;
		for (int i = 0; i < tagCounts.size(); i++) {
			Object[] tagCount = tagCounts.get(i);
			String tag = (String) tagCount[0];
			Long recipeCount = (Long) tagCount[1];
			if (i < 10) {
				tags.add(tag);
				recipeCountByTag.add(recipeCount);
				tenthTagCount = recipeCount;
			} else {
				// Include tag if recipeCount is same as tenthTagCount
				if (recipeCount.equals(tenthTagCount)) {
					tags.add(tag);
					recipeCountByTag.add(recipeCount);
				} else {
					break;
				}
			}
		}
		model.addAttribute("tags", tags);
		model.addAttribute("recipeCountByTag", recipeCountByTag);
		// get the number of recipes every year
		List<Object[]> yearCounts = recipeService.getRecipeCountByYear();
		List<String> yearsForRecipe = new ArrayList<>();
		List<Long> recipeCountByYear = new ArrayList<>();
		long sum = 0l;
		for (int i = 0; i < yearCounts.size(); i++) {
			Object[] yearCount = yearCounts.get(i);
			String Year = yearCount[0].toString();
			Long recipeCount = (Long) yearCount[1];
			sum += recipeCount;
			yearsForRecipe.add(Year);
			recipeCountByYear.add(sum);
		}
		model.addAttribute("yearsForRecipe", yearsForRecipe);
		if (yearsForRecipe.contains(String.valueOf(LocalDate.now().getYear()))) {
			model.addAttribute("currentYear", String.valueOf(LocalDate.now().getYear()));
		} else {
			String latestYear = String.valueOf(
					yearsForRecipe.stream().mapToInt(Integer::parseInt).max().orElse(LocalDate.now().getYear()));
			model.addAttribute("currentYear", latestYear);
		}

		model.addAttribute("recipeCountByYear", recipeCountByYear);
		return "UserViews/dashboard";
	}

	// Member Management
	// show all members
	@GetMapping("/admin/memberManage")
	public String showMemberList(Model model) {
		List<Member> members = userService.getAllMembers();
		model.addAttribute("members", members);
		return "UserViews/memberList";
	}

	@PostMapping("/admin/memberManage/search")
	public String showMemberById(@RequestParam("query") String query, Model model) {
		List<Member> members = new ArrayList<>();
		if (query.equals("")) {
			return "redirect:/user/admin/memberManage";
		}
		try {
			int id = Integer.parseInt(query);
			Member member = userService.getMemberById(id);
			if (!member.equals(null)) {
				members.add(member);
			}
		} catch (Exception e) {
			System.out.println("error searching");
		}
		model.addAttribute("members", members);
		return "UserViews/memberList";
	}

	// show member's history of being reported
	@GetMapping("/admin/memberManage/{id}/reports")
	public String showMemberReports(@PathVariable("id") Integer memberId, Model model) {
		Member member = userService.getMemberById(memberId);
		List<MemberReport> memberReports = userService.getReportsByMember(member);
		model.addAttribute("member", member);
		model.addAttribute("memberReports", memberReports);
		return "UserViews/memberReports";
	}

	// delete members
	@GetMapping("/admin/memberManage/delete/{id}")
	public String deleteMember(@PathVariable("id") Integer memberId) {
		userService.deleteMember(memberId);
		return "redirect:/user/admin/memberManage";
	}

	// show all reported recipes
	@GetMapping("/admin/recipeReport")
	public String showPendingRecipeReports(Model model) {
		List<RecipeReport> pendingReports = userService.getPendingRecipeReport();
		model.addAttribute("pendingReports", pendingReports);
		return "ReportViews/recipeReports";
	}

	// approve or reject recipe report
	@GetMapping("/admin/recipeReport/{id}")
	public String showRecipeReportDetails(@PathVariable(value = "id") Integer id, Model model) {
		RecipeReport report = userService.getRecipeReportById(id);
		model.addAttribute("report", report);
		model.addAttribute("recipe", report.getRecipeReported());
		return "ReportViews/recipeReportDetails";
	}

	@GetMapping("/admin/recipeReport/{id}/approve")
	public String approveRecipeReport(@PathVariable(value = "id") Integer id) {
		userService.approveRecipeReport(id);
		return "redirect:/user/admin/recipeReport";
	}

	@GetMapping("/admin/recipeReport/{id}/reject")
	public String rejectRecipeReport(@PathVariable(value = "id") Integer id) {
		userService.rejectRecipeReport(id);
		return "redirect:/user/admin/recipeReport";
	}

	// show all reported members
	@GetMapping("/admin/memberReport")
	public String showPendingMemberReports(Model model) {
		List<MemberReport> pendingReports = userService.getPendingMemberReport();
		model.addAttribute("pendingReports", pendingReports);
		return "ReportViews/memberReports";
	}

	// approve or reject member report
	@GetMapping("/admin/memberReport/{id}")
	public String showMemberReportDetails(@PathVariable(value = "id") Integer id, Model model) {
		MemberReport report = userService.getMemberReportById(id);
		model.addAttribute("report", report);
		return "ReportViews/memberReportDetails";
	}

	@GetMapping("/admin/memberReport/{id}/approve")
	public String approveMemberReport(@PathVariable(value = "id") Integer id) {
		userService.approveMemberReport(id);
		return "redirect:/user/admin/memberReport";
	}

	@GetMapping("/admin/memberReport/{id}/reject")
	public String rejectMemberReport(@PathVariable(value = "id") Integer id) {
		userService.rejectMemberReport(id);
		return "redirect:/user/admin/memberReport";
	}

	@GetMapping("/member/savedList")
	public String showSavedList(Model model, HttpSession sessionObj) {
		Integer id = (Integer) sessionObj.getAttribute("userId");
		Member member = userService.getMemberById(id);
		List<Recipe> recipes = member.getSavedRecipes();
		model.addAttribute("recipes", recipes);
		return "UserViews/showSavedListPage";
	}

	@GetMapping("/member/myRecipeList")
	public String showMyRecipeList(Model model, HttpSession sessionObj) {
		Integer id = (Integer) sessionObj.getAttribute("userId");
		Member member = userService.getMemberById(id);
		List<Recipe> recipes = member.getAddedRecipes().stream().filter(r -> r.getStatus() != Status.DELETED)
				.collect(Collectors.toList());
		model.addAttribute("recipes", recipes);
		return "UserViews/showMyRecipePage";
	}

	@GetMapping("/member/myReview")
	public String showMyReviewList(Model model, HttpSession sessionObj) {
		Integer id = (Integer) sessionObj.getAttribute("userId");
		Member member = userService.getMemberById(id);
		List<Review> reviews = member.getReviews();
		model.addAttribute("reviews", reviews);
		return "UserViews/showMyReviewPage";
	}

	@GetMapping("/login")
	public String login() {
		return "UserViews/login";
	}

	@PostMapping("/login")
	public String loginlogic(@RequestParam(name = "username") String username,
			@RequestParam(name = "password") String password, Model model, HttpSession httpSession) {
		User user = userService.getUserByUsername(username);
		if (user != null && user.getPassword().equals(password)) {
			httpSession.setAttribute("userId", user.getId());
			if (userService.checkIfAdmin(user)) {
				httpSession.setAttribute("userType", "admin");
				return "redirect:/user/admin/dashboard";
			} else {
				httpSession.setAttribute("userType", "member");
				if (userService.getMemberById(user.getId()).getMemberStatus() == Status.DELETED) {
					httpSession.invalidate();
					model.addAttribute("errorMessage", "Account has been deleted");
					return "UserViews/login";
				}
				return "redirect:/";
			}
		} else {
			model.addAttribute("errorMessage", "Incorrect username or password, please try again");
			return "UserViews/login";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/user/login";
	}

	@PostMapping("/verifyEmail")
	public String verifyEmail(@ModelAttribute("member") Member member, HttpSession httpSession) {
		userService.saveMember(member);
		httpSession.setAttribute("userId", member.getId());
		httpSession.setAttribute("userType", "member");
		if (member.getPerfenceList() == null || member.getPerfenceList().isEmpty()) {
			return "redirect:/user/setPreference";
		}
		return "redirect:/";
	}

	@GetMapping("/admin/generateCsvReport")
	public String generateCsvReport(Model model) {
		List<Recipe> recipes = recipeService.getAllRecipes();
		model.addAttribute("recipes", recipes);
		model.addAttribute("previousOrderBy", "rating");
		model.addAttribute("previousOrder", "desc");
		return "UserViews/generateCsvReportsPage";
	}

	@PostMapping("admin/generateCsvReport")
	public String generateCsvReportByOrder(@RequestParam("orderBy") String orderBy, @RequestParam("order") String order,
			Model model) {
		List<Recipe> recipes = recipeService.getRecipesByOrder(orderBy, order);
		model.addAttribute("recipes", recipes);
		model.addAttribute("previousOrderBy", orderBy);
		model.addAttribute("previousOrder", order);
		return "UserViews/generateCsvReportsPage";
	}
}
