package com.ad.teamnine.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ad.teamnine.model.Admin;
import com.ad.teamnine.model.Member;
import com.ad.teamnine.model.MemberReport;
import com.ad.teamnine.model.Report;
import com.ad.teamnine.repository.MemberReportRepository;
import com.ad.teamnine.repository.RecipeReportRepository;
import com.ad.teamnine.repository.ReportRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReportService {

	@Autowired
	MemberReportRepository MReRepo;
	@Autowired
	RecipeReportRepository ReReRepo;
	@Autowired
	ReportRepository ReRepo;

	// Determine the type of reporting
	public boolean determineIfMemberReport(int id) {
		Optional<Report> report = ReRepo.findById(id);
		if (report.isPresent()) {
			return report.get() instanceof MemberReport;
		}
		return false;
	}
	

}