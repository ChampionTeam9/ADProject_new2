package com.ad.teamnine.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ad.teamnine.model.Member;

public interface MemberRepository extends JpaRepository<Member,Integer>{

}
