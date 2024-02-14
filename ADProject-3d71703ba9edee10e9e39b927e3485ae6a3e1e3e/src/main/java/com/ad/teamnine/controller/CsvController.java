package com.ad.teamnine.controller;

import com.ad.teamnine.model.Recipe;
import com.ad.teamnine.service.*;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/csv")
public class CsvController {
	@Autowired
	private RecipeService recipeService;
	@Autowired
	private CsvService csvService;

	@GetMapping("/download/{orderBy}/{order}")
	public void downloadCsv(HttpServletResponse response, @PathVariable String orderBy, @PathVariable String order) {
		try {
			String fileName = "output";
			// 生成 CSV 文件
			List<Recipe> dataList = recipeService.getRecipesByOrder(orderBy, order);
			csvService.generateCsv(dataList, fileName);

			// 设置响应头
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".csv\"");

			// 读取生成的 CSV 文件并写入响应流
			FileInputStream fileInputStream = new FileInputStream(fileName + ".csv");
			IOUtils.copy(fileInputStream, response.getOutputStream());
			response.flushBuffer();
			fileInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			// 处理异常
		}
	}
}