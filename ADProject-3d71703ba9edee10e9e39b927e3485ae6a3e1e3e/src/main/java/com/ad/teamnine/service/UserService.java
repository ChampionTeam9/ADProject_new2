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
import com.ad.teamnine.model.MemberReport;
import com.ad.teamnine.model.Recipe;
import com.ad.teamnine.model.RecipeReport;
import com.ad.teamnine.model.Status;
import com.ad.teamnine.model.User;
import com.ad.teamnine.repository.MemberReportRepository;
import com.ad.teamnine.repository.MemberRepository;
import com.ad.teamnine.repository.RecipeReportRepository;
import com.ad.teamnine.repository.RecipeRepository;
import com.ad.teamnine.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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
	
	@Autowired
	private MemberRepository memberRepository; 
	
	@Autowired
	private RecipeRepository recipeRepository;
	
	@Autowired
	private RecipeReportRepository recipeReportRepository;
	
	@Autowired
	private MemberReportRepository memberReportRepository;
	
	@Autowired
	private EntityManager entityManager;

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
	
	//Searching and Filtering methods	
		public Member getMemberById(Integer id) {
			Optional<Member> member = memberRepository.findById(id);
			return member.orElse(null);
		}
		public RecipeReport getRecipeReportById(Integer id) {
			Optional<RecipeReport> report = recipeReportRepository.findById(id);
			return report.orElse(null);
			
		}
		public MemberReport getMemberReportById(Integer id) {
			Optional<MemberReport> report = memberReportRepository.findById(id);
			return report.orElse(null);
		}
		
		 public List<Member> getAllMembers() {
		        return memberRepository.findByMemberStatusNot(Status.DELETED);
		    }
		 public List<MemberReport> getReportsByMember(Member member) {
		        return memberReportRepository.findByMemberReportedAndStatus(member, Status.APPROVED);
		    }
		
		
		
		//Pending RecipeReportList Operation	
		public List<RecipeReport> getPendingRecipeReport() {
			List<RecipeReport> pendingReport= recipeReportRepository.findByStatus(Status.PENDING);
			return pendingReport;	
		}
		
		public void approveRecipeReport(Integer reportId) {
			RecipeReport recipeReport = recipeReportRepository.findById(reportId).orElse(null);
			recipeReport.setStatus(Status.APPROVED);		
		}
		
		public void rejectRecipeReport(Integer reportId) {
			RecipeReport recipeReport = recipeReportRepository.findById(reportId).orElse(null);
			recipeReport.setStatus(Status.REJECTED);		
		}
		
		
		
		
		//Pending MemberReportList Operation
		public List<MemberReport> getPendingMemberReport() {
			List<MemberReport> pendingReport= memberReportRepository.findByStatus(Status.PENDING);
			return pendingReport;	
		}
		
		public void approveMemberReport(Integer reportId) {
			MemberReport memberReport = memberReportRepository.findById(reportId).orElse(null);
			memberReport.setStatus(Status.APPROVED);		
		}
		
		public void rejectMemberReport(Integer reportId) {
			MemberReport memberReport = memberReportRepository.findById(reportId).orElse(null);
			memberReport.setStatus(Status.REJECTED);		
		}
		
		//delete member and invalid the recipes he or she uploaded
		 public void deleteMember(Integer memberId) {
		        Optional<Member> optionalMember = memberRepository.findById(memberId);
		        if (optionalMember.isPresent()) {
		            Member member = optionalMember.get();
		            member.setMemberStatus(Status.DELETED);
		            memberRepository.save(member);
		            List<Recipe> recipes = member.getAddedRecipes();
		            for (Recipe recipe : recipes) {
		                recipe.setStatus(Status.DELETED);
		                recipeRepository.save(recipe);
		            }
		        }
		    }
		
		
		
		
		//get daily created data
		public List<Object[]> getDailyMemberData() {
		    String sql = "SELECT CAST(registrationDate AS DATE) AS date, COUNT(*) AS new_users " +
		                 "FROM user " +
		                 "WHERE registrationDate >= CURRENT_DATE " +
		                 "GROUP BY CAST(registrationDate AS DATE)";	    
		    TypedQuery<Object[]> query = entityManager.createQuery(sql, Object[].class);
		    List<Object[]> resultList = query.getResultList();
		    return resultList;
		}
		public Member getMemberByUsername(String username)
		{
			return (Member) userRepo.findByUsername(username);
		}

		public boolean checkifUserExist(String username) {
			if (userRepo.findByUsername(username) == null) {
				return false;
			} else
				return true;
		}

		public String getUsernameById(Integer userId) {
			 return getUserById(userId).getUsername();
			
			
		}
		  public User getUserById(Integer id) {
			   Optional<User> user = userRepo.findById(id);
			   return user.orElse(null);
			  }
}


	
	
	
	
	
	

