package com.ad.teamnine.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.ad.teamnine.model.Admin;
import com.ad.teamnine.model.Member;

import com.ad.teamnine.model.User;
import com.ad.teamnine.repository.MemberRepository;
import com.ad.teamnine.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {
	@Autowired
	UserRepository userRepo;
	@Autowired
	MemberRepository memberRepo;

	// check for login
	public boolean login(User user) {
		if (user != null && user.getUsername() != null && user.getPassword() != null) {
			if (userRepo.findByUsername(user.getUsername()).getPassword() == user.getPassword()) {
				return true;
			}
		}
		return false;
	}
	//Check identity
	public boolean checkIfAdmin(User user){
		    if(user instanceof Admin) 
		    {     
		     return true;    
		    }
		    else if (user instanceof Member) 
		    {
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

		//Save member
		public void saveMember(Member member) {
			memberRepo.save(member);
		}
}
