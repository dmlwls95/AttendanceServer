package com.example.Attendance.Util;

import java.time.Duration;

import org.springframework.http.ResponseCookie;

public class CookieUtil {
	public static ResponseCookie buildRefreshCookie(String token, Duration maxAge, boolean crossSite) {
		ResponseCookie.ResponseCookieBuilder b = ResponseCookie.from("refresh_token", token)
				.httpOnly(true).secure(true)
				.path("/auth")
				.maxAge(maxAge);
		if(crossSite) b.sameSite("None"); else b.sameSite("Lax");
		return b.build();
	}
	
	public static ResponseCookie deleteRefreshCookie(boolean crossSite) {
        ResponseCookie.ResponseCookieBuilder b = ResponseCookie.from("refresh_token", "")
            .httpOnly(true).secure(true)
            .path("/auth").maxAge(0);
        if (crossSite) b.sameSite("None"); else b.sameSite("Lax");
        return b.build();
    }
}