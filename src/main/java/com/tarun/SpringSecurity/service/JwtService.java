package com.tarun.SpringSecurity.service;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.tarun.SpringSecurity.model.AccountInfo;
import com.tarun.SpringSecurity.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	
	// Fixed secret key - this will remain constant across application restarts
	private String secretkey = "bXlTZWNyZXRLZXlGb3JKV1RUb2tlblNpZ25hdHVyZUZvckRldmVsb3BtZW50UHVycG9zZXNPbmx5";
	
	private final UserRepository userRepo;
	
	public JwtService(UserRepository userRepo) {
		this.userRepo = userRepo;
		// No more random key generation - using fixed key above
	}
	
	public String generateToken(String username) {
		Map<String,Object> claims = new HashMap<>();
		
		AccountInfo user = userRepo.findByUsername(username);
		claims.put("name", user.getFirstname());
		claims.put("email", user.getEmail());
		
		return Jwts
			.builder()
			.claims()
			.add(claims)
			.subject(username)
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30)) // 30 days
			.and()
			.signWith(getKey())
			.compact();		
	}
	
	public SecretKey getKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretkey);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}
	
	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(getKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
	
	public boolean validateToken(String token, UserDetails userDetails) {
		final String userName = extractUsername(token);
		return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	
	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
	
	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
}