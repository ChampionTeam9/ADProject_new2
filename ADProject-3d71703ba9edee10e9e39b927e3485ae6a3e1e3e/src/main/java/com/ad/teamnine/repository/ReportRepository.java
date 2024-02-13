package com.ad.teamnine.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ad.teamnine.model.Report;
import com.ad.teamnine.model.Status;

public interface ReportRepository extends JpaRepository<Report,Integer>{
	long countByStatus(Status status);
}
