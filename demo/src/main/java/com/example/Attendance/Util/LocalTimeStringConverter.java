package com.example.Attendance.Util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = false)
public class LocalTimeStringConverter implements AttributeConverter<LocalTime, String> {
	private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("HH:mm");
	
	@Override
	public String convertToDatabaseColumn(LocalTime attribute) {
		// TODO Auto-generated method stub
		return attribute == null ? null : attribute.format(F);
	}

	@Override
	public LocalTime convertToEntityAttribute(String dbData) {
		// TODO Auto-generated method stub
		return (dbData == null || dbData.isEmpty() ) ? null : LocalTime.parse(dbData, F);
	}
	
}
