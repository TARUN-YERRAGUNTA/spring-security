package com.tarun.SpringSecurity.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.tarun.SpringSecurity.model.AccountInfo;
import com.tarun.SpringSecurity.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
	
	private final JwtService jwtService;
	private final UserRepository userRepo;
	
	public OAuth2LoginSuccessHandler(JwtService jwtService,UserRepository userRepo) {
		this.jwtService=jwtService;
		this.userRepo=userRepo;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// TODO Auto-generated method stub
		
		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		String email = oAuth2User.getAttribute("email");
		
		AccountInfo user = userRepo.findByEmail(email);
		
		if(user!=null) {
			String token = jwtService.generateToken(user.getUsername());
			
			Cookie jwtCookie = new Cookie("jwt",token);
			jwtCookie.setHttpOnly(true);
			jwtCookie.setPath("/");
			jwtCookie.setMaxAge(24*60*60);
			response.addCookie(jwtCookie);
			
			response.sendRedirect("/SpringSecurity/home");
		}else {
			response.sendRedirect("/SpringSecurity/login?error=google_email_not_registered");
		}
		
	}
	
	

}
