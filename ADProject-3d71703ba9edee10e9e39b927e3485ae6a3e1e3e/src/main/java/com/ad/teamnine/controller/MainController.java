package com.ad.teamnine.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ad.teamnine.service.CsvService;

@Controller
public class MainController {
	public MainController() {
	}
	@RequestMapping("/")
	public String homePage() {
		return "HomePage";
	}
}