package com.example.Attendance.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Base64;
import java.security.Key;

@Component
public class JwtTokenProvider {
	
	@Value("${jwt.secret}")
	private String secret;
	
	@Value("${jwt.expiration}")
	private long validityInMs;
	
	private Key key;
	
	@PostConstruct
	protected void init()
	{
		String encoded = Base64.getEncoder().encodeToString(secret.getBytes());
		this.key = Keys.hmacShaKeyFor(encoded.getBytes());
	}
	
	public String createToken(String email)
	{
		//JWT : Date + Key + Secret (claims + issueAt + SetExpration + secret + compact)
		Claims claims = Jwts.claims().setSubject(email);
		Date now = new Date();
		Date expiry = new Date(now.getTime() + validityInMs);
		
		return Jwts.builder()
					.setClaims(claims)
					.setIssuedAt(now)
					.setExpiration(expiry)
					.signWith(key, SignatureAlgorithm.HS256)
					.compact();
	}
	
	public boolean validateToken(String token)
	{
		try {
			//Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
			
		}catch (JwtException | IllegalArgumentException e)
		{
			return false;
		}
	}
	
	public String getEmail(String token)
	{
		return Jwts.parserBuilder().setSigningKey(key).build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}
}
