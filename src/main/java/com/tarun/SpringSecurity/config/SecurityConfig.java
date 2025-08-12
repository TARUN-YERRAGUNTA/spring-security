package com.tarun.SpringSecurity.config;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.tarun.SpringSecurity.filter.JwtFilter;
import com.tarun.SpringSecurity.service.CustomLogoutSuccessHandler;
import com.tarun.SpringSecurity.service.CustomOAuth2UserService;
import com.tarun.SpringSecurity.service.MyUserDetailsService;
import com.tarun.SpringSecurity.service.OAuth2LoginSuccessHandler;

import jakarta.servlet.http.Cookie;

@Configuration
public class SecurityConfig {
	
	private final MyUserDetailsService userDetailsService;
	private final JwtFilter jwtFilter;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
	private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
	public SecurityConfig(MyUserDetailsService userDetailsService,JwtFilter jwtFilter,CustomOAuth2UserService customOAuth2UserService,OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,CustomLogoutSuccessHandler customLogoutSuccessHandler) {
		this.userDetailsService=userDetailsService;
		this.jwtFilter=jwtFilter;
		this.customOAuth2UserService = customOAuth2UserService;
		this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
		this.customLogoutSuccessHandler = customLogoutSuccessHandler;
	}
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http
			.csrf(customizer->customizer.disable())
			.authorizeHttpRequests(auth -> auth.requestMatchers("/login","/forgotPassword","/forgotUsername","/resetPassword","/signupP1","/signupP2","/signupP3","/validateOtp","/resendOtp","/setPassword").permitAll().anyRequest().authenticated())
			.authenticationProvider(authenticationProvider())
			.logout(logout -> logout
				    .logoutUrl("/logout") // allow POST (default) and GET if you want
				    .invalidateHttpSession(true)
				    .clearAuthentication(true)
				    .deleteCookies("jwt","JSESSIONID")
				    .logoutSuccessHandler(customLogoutSuccessHandler)
				    .permitAll()
				)
			.formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/home"))
			.oauth2Login(oauth2 -> oauth2.loginPage("/login").userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)).successHandler(oAuth2LoginSuccessHandler).failureHandler((request,response,exception)-> {response.sendRedirect("/login?error=google_email_not_registered");}))
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);		
		return http.build();
	}
	
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(passwordEncoder());
		provider.setUserDetailsService(userDetailsService);
		return provider;
	}
	
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
