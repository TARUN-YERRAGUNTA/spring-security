package com.tarun.SpringSecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.tarun.SpringSecurity.service.MyUserDetailsService;

@Configuration
public class SecurityConfig {
	
	private final MyUserDetailsService userDetailsService;
	public SecurityConfig(MyUserDetailsService userDetailsService) {
		this.userDetailsService=userDetailsService;
	}
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http
			.csrf(customizer->customizer.disable())
			.authorizeHttpRequests(auth -> auth.requestMatchers("/login","/forgotPassword","/forgotUsername","/resetPassword","/signupP1","/signupP2","/signupP3","/validateOtp","/resendOtp","/setPassword").permitAll().anyRequest().authenticated())
			.logout(logout->logout.logoutUrl("/logout").deleteCookies("jwtss").logoutSuccessUrl("/login?logout").permitAll());
		
		return http.build();
	}
	
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
		provider.setUserDetailsService(userDetailsService);
		return provider;
	}

}
