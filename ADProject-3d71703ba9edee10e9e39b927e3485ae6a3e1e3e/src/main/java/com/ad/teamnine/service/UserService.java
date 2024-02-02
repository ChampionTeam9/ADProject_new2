package com.ad.teamnine.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.ad.teamnine.model.Admin;
import com.ad.teamnine.model.Member;

import com.ad.teamnine.model.User;
import com.ad.teamnine.repository.MemberRepository;
import com.ad.teamnine.repository.RecipeRepository;
import com.ad.teamnine.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {
	@Autowired
	UserRepository userRepo;
	@Autowired
	MemberRepository memberRepo;
	@Autowired
	RecipeRepository recipeRepo;

	// check for login
	public boolean login(User user) {
		if (user != null && user.getUsername() != null && user.getPassword() != null) {
			if (userRepo.findByUsername(user.getUsername()).getPassword() == user.getPassword()) {
				return true;
			}
		}
		return false;
	}

	// Check identity
	public boolean checkIfAdmin(User user) {
		if (user instanceof Admin) {
			return true;
		} else if (user instanceof Member) {
			return false;
		}
		return false;
	}

	// delete
	public void deleteAccount(int id) {
		try {
			userRepo.deleteById(id);
			System.out.println("Account with ID " + id + " has been deleted");
		} catch (EmptyResultDataAccessException e) {
			System.out.println("Account with ID " + id + " does not exist");
		}
	}

	// update profile
	public void updateMyProfile(Member member, String gender, Double height, Double weight, LocalDate birthdate) {
		member.setGender(gender);
		member.setHeight(height);
		member.setWeight(weight);
		member.setBirthdate(birthdate);
		memberRepo.save(member);
	}

	// Register
	public void register(String username, String password, String confirmPassword, String gender, Double height,
			Double weight, LocalDate birthdate) {
		if (password == confirmPassword) {
			Member member = new Member();
			member.setUsername(username);
			member.setPassword(confirmPassword);
			member.setGender(gender);
			member.setHeight(height);
			member.setWeight(weight);
			member.setBirthdate(birthdate);
			memberRepo.save(member);
		}
	}

	// get specific member by id
	public Member getMemberById(Integer id) {
		Optional<Member> member = memberRepo.findById(id);
		return member.orElse(null);
	}

	// Save member
	public void saveMember(Member member) {
		memberRepo.save(member);
	}

	// get all unique tags
	public Set<String> getAllUniqueTags() {
		List<String> tagLists = recipeRepo.findAllDistinctTags();
		Set<String> uniqueTags = new HashSet<>();

		for (String tags : tagLists) {
			uniqueTags.addAll(Arrays.asList(tags.split(",")));
		}

		return uniqueTags;
	}

	public Set<String> getRandomUniqueTags(int count) {
		List<String> allTags = new ArrayList<>(getAllUniqueTags());
		Collections.shuffle(allTags, new Random());
		return allTags.stream().limit(count).collect(Collectors.toCollection(LinkedHashSet::new));
	}

	
}
