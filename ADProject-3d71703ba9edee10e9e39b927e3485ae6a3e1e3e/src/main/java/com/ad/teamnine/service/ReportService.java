package com.ad.teamnine.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ad.teamnine.model.Member;
import com.ad.teamnine.model.MemberReport;
import com.ad.teamnine.model.Recipe;
import com.ad.teamnine.model.RecipeReport;
import com.ad.teamnine.model.Status;
import com.ad.teamnine.repository.MemberReportRepository;
import com.ad.teamnine.repository.MemberRepository;
import com.ad.teamnine.repository.RecipeReportRepository;
import com.ad.teamnine.repository.RecipeRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReportService {
	@Autowired
	RecipeRepository recipeRepository;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	RecipeReportRepository recipeReportRepository;

	@Autowired
	MemberReportRepository memberReportRepository;
	@Autowired
	UserService userService;

	@Autowired
	RecipeService recipeService;
	
	@Autowired
	EmailService emailService;
	// report inappropriate recipes
	public void reportRecipes(RecipeReport report) {
		report.setStatus(Status.PENDING);
		report.setReason(report.getReason().trim());
		recipeReportRepository.save(report);
		if(report.getRecipeReported().getMember().getEmail()!=null) {
			emailService.RecipeReportNotificationToMember(report);
		}
		emailService.ReportNotificationToAdmin(report,"RecipeReport");
		
	}

	// report inappropriate members
	public void reportMembers(MemberReport report) {
		report.setStatus(Status.PENDING);
		report.setReason(report.getReason().trim());
		memberReportRepository.save(report);
		if(report.getMemberReported().getEmail()!=null) {
			emailService.MemberReportNotificationToMemberReported(report);
		}
		emailService.ReportNotificationToAdmin(report,"MemberReport");
	}
	
	public long getRecipeReportCount() {
		return recipeReportRepository.countByStatus(Status.PENDING);
	}
	public long getMemberReportCount() {
		return memberReportRepository.countByStatus(Status.PENDING);
	}
}
