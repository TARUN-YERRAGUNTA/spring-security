package com.tarun.SpringSecurity.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        // Invalidate the session completely
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        // Clear all cookies (including any Google OAuth2 related cookies)
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // Create a new cookie with the same name but expired
                Cookie expiredCookie = new Cookie(cookie.getName(), null);
                expiredCookie.setMaxAge(0);
                expiredCookie.setPath("/");
                expiredCookie.setHttpOnly(true);
                response.addCookie(expiredCookie);
                
                // Also clear cookies with different paths
                Cookie expiredCookieRoot = new Cookie(cookie.getName(), null);
                expiredCookieRoot.setMaxAge(0);
                expiredCookieRoot.setPath("/SpringSecurity");
                response.addCookie(expiredCookieRoot);
            }
        }
        
        // Explicitly clear JWT cookie again to be sure
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setMaxAge(0);
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true);
        response.addCookie(jwtCookie);
        
        // Clear any Spring Security session cookies
        Cookie springSecCookie = new Cookie("JSESSIONID", null);
        springSecCookie.setMaxAge(0);
        springSecCookie.setPath("/");
        response.addCookie(springSecCookie);
        
        // Add cache control headers to prevent browser caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        
        // Build redirect URL with context path
        String redirectUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/login")
                .queryParam("logout", "true")
                .toUriString();
                
        response.sendRedirect(redirectUrl);
    }
}