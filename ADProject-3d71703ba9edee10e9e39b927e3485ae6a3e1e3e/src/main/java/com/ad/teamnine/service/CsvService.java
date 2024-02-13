package com.ad.teamnine.service;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ad.teamnine.repository.RecipeRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

@Service
public class CsvService {
	@Autowired
	RecipeRepository recipeRepo;

	public List<String[]> readCsv(Path filePath) throws IOException, CsvException {
		List<String[]> list = new ArrayList<>();
		try (Reader reader = Files.newBufferedReader(filePath)) {
			try (CSVReader csvReader = new CSVReader(reader)) {
				String[] line;
				while ((line = csvReader.readNext()) != null) {
					list.add(line);
				}
			}
		}
		return list;
	}

	public List<String[]> readCsvWithDecoder(Path filePath, Charset charset) throws IOException, CsvException {
		List<String[]> list = new ArrayList<>();
		try (Reader reader = Files.newBufferedReader(filePath, charset)) {
			try (CSVReader csvReader = new CSVReader(reader)) {
				String[] line;
				int i = 1;
				while ((line = csvReader.readNext()) != null) {
					System.out.println(i);
					System.out.println(line);
					list.add(line);
					i++;
				}
			}
		}
		return list;
	}

	public <T> void generateCsv(List<T> data, String csvName) throws IOException {
		String csvFileName = csvName + ".csv";

		try (CSVWriter writer = new CSVWriter(new FileWriter(csvFileName))) {
			if (!data.isEmpty()) {
				T firstEntity = data.get(0);
				Field[] fields = firstEntity.getClass().getDeclaredFields();

				// 创建头部行
				String[] headers = new String[fields.length];
				for (int i = 0; i < fields.length; i++) {
					headers[i] = fields[i].getName();
				}
				writer.writeNext(headers);

				// 写入数据行
				for (T entity : data) {
					String[] rowData = new String[fields.length];
					for (int i = 0; i < fields.length; i++) {
						fields[i].setAccessible(true);
						try {
							Object value = fields[i].get(entity);
							rowData[i] = (value != null) ? value.toString() : "";
						} catch (IllegalAccessException e) {
							e.printStackTrace(); // 处理异常
						}
					}
					writer.writeNext(rowData);
				}
			}
		}
	}
}
