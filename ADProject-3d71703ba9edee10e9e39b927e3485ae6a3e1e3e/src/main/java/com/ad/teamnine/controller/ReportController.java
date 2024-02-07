package com.ad.teamnine.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ad.teamnine.model.Member;
import com.ad.teamnine.model.MemberReport;
import com.ad.teamnine.model.Recipe;
import com.ad.teamnine.model.RecipeReport;
import com.ad.teamnine.service.RecipeService;
import com.ad.teamnine.service.ReportService;
import com.ad.teamnine.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/report")
public class ReportController {
	
	@Autowired
	private ReportService reportService;
	
	@Autowired
	private RecipeService recipeService;
	
	@Autowired
	private UserService userService;
	
	//report recipes
	@GetMapping("/reportRecipe/{recipeId}")
	public String getReportRecipe(@PathVariable(value = "recipeId") Integer recipeId,
			Model model,
			HttpSession sessionObj) {
		RecipeReport report = new RecipeReport();
		Member member = userService.getMemberById((int)sessionObj.getAttribute("userId"));
		Recipe recipe=recipeService.getRecipeById(recipeId);
		report.setMember(member);
		report.setRecipeReported(recipe);
		model.addAttribute("report",report);
		return "ReportViews/showRecipeReportPage";
	}
	
	@PostMapping("/reportRecipe")
	public String reportRecipe(@ModelAttribute("report") RecipeReport report) {
		reportService.reportRecipes(report);
		return "redirect:/recipe/detail/"+report.getRecipeReported().getId();
		
	}
	
	@GetMapping("/reportMember/{memberId}")
	public String getReportMember(@PathVariable Integer memberId,
			Model model,
			HttpSession sessionObj) {
		MemberReport report = new MemberReport();
//		Member member = 
//				userService.getMemberById((int)sessionObj.getAttribute("userId"));
		Member member =userService.getMemberById(1);
		Member reportedMember = userService.getMemberById(memberId);
		report.setMember(member);
		report.setMemberReported(reportedMember);
		model.addAttribute("report",report);
		return "ReportViews/showMemberReportPage";
	}
	
	@PostMapping("/reportMember")
	public String reportMember(@ModelAttribute("report") MemberReport report) {
		
		reportService.reportMembers(report);
		return "redirect:/user/profile/"+report.getMemberReported().getId();
		
	}
	


}
