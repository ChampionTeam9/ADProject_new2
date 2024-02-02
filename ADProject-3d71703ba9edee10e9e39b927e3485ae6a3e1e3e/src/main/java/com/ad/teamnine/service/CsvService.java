package com.ad.teamnine.service;

import java.io.IOException;
import java.io.Reader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ad.teamnine.repository.RecipeRepository;
import com.opencsv.CSVReader;
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
}
