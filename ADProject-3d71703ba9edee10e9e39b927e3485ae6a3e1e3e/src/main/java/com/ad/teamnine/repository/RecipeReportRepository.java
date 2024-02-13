package com.ad.teamnine.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ad.teamnine.model.RecipeReport;
import com.ad.teamnine.model.Status;

public interface RecipeReportRepository extends JpaRepository<RecipeReport,Integer>{

	List<RecipeReport> findByStatus(Status pending);

}
