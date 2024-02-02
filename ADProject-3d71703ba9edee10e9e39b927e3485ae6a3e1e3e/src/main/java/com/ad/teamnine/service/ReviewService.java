package com.ad.teamnine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ad.teamnine.model.*;
import com.ad.teamnine.repository.*;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReviewService {
	@Autowired
	ReviewRepository reviewRepo;
	public void createReview(Review review) {
		reviewRepo.save(review);
		return;
	}
}
