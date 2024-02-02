package com.ad.teamnine.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.ad.teamnine.model.*;
import com.ad.teamnine.service.*;

@Controller
@RequestMapping("/review")
public class ReviewController {
	private ReviewService reviewService;
	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}
	@PostMapping("/create")
	public String createReview(@ModelAttribute("review") Review review) {
		reviewService.createReview(review);
		return "";
	}
}
//hello