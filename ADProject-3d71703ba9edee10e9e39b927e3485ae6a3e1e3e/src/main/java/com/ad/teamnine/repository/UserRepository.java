package com.ad.teamnine.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ad.teamnine.model.*;

public interface UserRepository extends JpaRepository<User,Integer> {
	User findByUsername(String username);

}
