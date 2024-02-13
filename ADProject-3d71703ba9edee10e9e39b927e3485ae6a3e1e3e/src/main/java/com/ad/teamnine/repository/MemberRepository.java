package com.ad.teamnine.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ad.teamnine.model.Member;
import com.ad.teamnine.model.Status;

public interface MemberRepository extends JpaRepository<Member,Integer>{
	List<Member> findByMemberStatusNot(Status status);


	Member findByUsername(String username);

}
