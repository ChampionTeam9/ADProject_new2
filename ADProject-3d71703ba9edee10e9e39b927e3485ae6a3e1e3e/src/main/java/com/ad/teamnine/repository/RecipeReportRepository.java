package com.ad.teamnine.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ad.teamnine.model.RecipeReport;
import com.ad.teamnine.model.Status;

public interface RecipeReportRepository extends JpaRepository<RecipeReport,Integer>{

	List<RecipeReport> findByStatus(Status pending);
	@Query("SELECT COUNT(r) FROM RecipeReport r WHERE r.status = :status")
	long countByStatus(@Param("status") Status status);
}
