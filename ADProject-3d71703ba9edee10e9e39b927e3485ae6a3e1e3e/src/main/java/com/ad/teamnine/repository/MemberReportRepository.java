package com.ad.teamnine.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ad.teamnine.model.Member;
import com.ad.teamnine.model.MemberReport;
import com.ad.teamnine.model.Status;

public interface MemberReportRepository extends JpaRepository<MemberReport,Integer>{

	List<MemberReport> findByMemberReportedAndStatus(Member member, Status approved);

	List<MemberReport> findByStatus(Status pending);

}
