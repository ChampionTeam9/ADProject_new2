package com.ad.teamnine.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ad.teamnine.service.CsvService;

@Controller
public class MainController {
	private final CsvService csvService;

	public MainController(CsvService csvService) {
		this.csvService = csvService;
	}
	@RequestMapping("/")
	public String homePage() {
		return "HomePage";
		
	}
	@RequestMapping("/read")
	public String processCsvFile() {
		// 传入CSV文件的路径
		//csvService.readCsvFile("classpath:qing.csv");
		return "page1";
	}
}
