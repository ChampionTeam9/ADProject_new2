package com.ad.teamnine.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.ad.teamnine.model.Admin;
import com.ad.teamnine.model.Member;
import com.ad.teamnine.model.Recipe;
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
	
}
