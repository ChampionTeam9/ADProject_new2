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

	// report inappropriate recipes
	public void reportRecipes(RecipeReport report) {
		report.setStatus(Status.PENDING);
		recipeReportRepository.save(report);
	}

	// report inappropriate members
	public void reportMembers(MemberReport report) {
		report.setStatus(Status.PENDING);
		memberReportRepository.save(report);
	}
}
