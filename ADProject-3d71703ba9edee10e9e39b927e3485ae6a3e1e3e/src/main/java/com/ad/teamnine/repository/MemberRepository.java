package com.ad.teamnine.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ad.teamnine.model.Member;
import com.ad.teamnine.model.Status;

public interface MemberRepository extends JpaRepository<Member, Integer> {
	List<Member> findByMemberStatusNot(Status status);

	Member findByUsername(String username);

	@Query("SELECT COUNT(m) FROM Member m WHERE m.registrationDate = CURRENT_DATE")
	int countMembersRegisteredToday();

	@Query("SELECT COUNT(m) FROM Member m WHERE YEAR(m.registrationDate) = YEAR(CURRENT_DATE)")
	int countMembersRegisteredThisYear();
}
