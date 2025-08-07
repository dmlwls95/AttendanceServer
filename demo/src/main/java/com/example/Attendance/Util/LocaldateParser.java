package com.example.Attendance.Util;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class LocaldateParser {
	public static LocalDate parseToLocalDate(String isoString) {
        if (isoString == null || isoString.isEmpty()) {
            throw new IllegalArgumentException("날짜 문자열이 null이거나 비어 있습니다.");
        }

        OffsetDateTime offsetDateTime = OffsetDateTime.parse(isoString);
        return offsetDateTime.toLocalDate();
    }

}
