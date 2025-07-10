package com.example.Attendance.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.example.Attendance.dto.EvolutionData;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class EvolutionConfig {
	
	private Map<Integer, EvolutionData> evolutionMap = new HashMap<>();
	
	@PostConstruct
	public void loadEvolutionData() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		
		InputStream is = new ClassPathResource("data/evolution_data.json").getInputStream();
		EvolutionData[] dataArray = objectMapper.readValue(is, EvolutionData[].class);
		
		for(EvolutionData data : dataArray) {
			evolutionMap.put(data.getEvolveStage(), data);
		}
		
		System.out.println("EvolutionConfig Loaded" + evolutionMap.size());
	}
	
	public EvolutionData getData(int stage) {
		return evolutionMap.get(stage);
	}
	
	public Collection<EvolutionData> getAllData(){
		return evolutionMap.values();
	}
	

}
