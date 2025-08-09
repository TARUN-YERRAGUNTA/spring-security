package com.tarun.SpringSecurity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tarun.SpringSecurity.service.AuthService;

import jakarta.mail.MessagingException;

@Controller
public class AuthController {
	
	
	private  final AuthService authService;
	
	public AuthController(AuthService authService) {
		this.authService=authService;
	}
	  
	
	@GetMapping("/login")
	public String getLogin() {
		return "auth/login";
	}
	
	@PostMapping("/login")
	public String postLogin(@RequestParam String username,@RequestParam String password,ModelMap map) {
		return authService.postLoginVerify(username,password,map);
	}
	
	@GetMapping("/home")
	public String getHome() {
		return "pages/home";
	}
	
	@GetMapping("/forgotPassword")
	public String getForgotPassword() {
		return "auth/forgotPassword";
	}
	
	@GetMapping("/resetPassword")
	public String getResetPassword() {
		return "auth/resetPassword";
	}
	
	@GetMapping("/signupP1")
	public String getSignupP1(ModelMap map) {
		authService.getSignupP1(map);
		return "auth/signupP1";
	}
	
	@PostMapping("/signupP1")
	public String postSignupP1(@RequestParam String firstname,@RequestParam String lastname,@RequestParam String email,@RequestParam String confEmail,@RequestParam String phn,@RequestParam String username,ModelMap map) {
		return authService.postSignupP1(firstname,lastname,email,confEmail,phn,username,map);
	}
	
	@GetMapping("/signupP2")
	public String getSignupP2(ModelMap map) {
		authService.getSignupP2(map);
		return "auth/signupP2";
	}
	@PostMapping("/signupP2")
	public String postSignupP2(@RequestParam String street,@RequestParam String apt,@RequestParam String city,@RequestParam String state,@RequestParam String zip,@RequestParam String dob,ModelMap map,RedirectAttributes redirectAttributes) {
		return authService.postSignupP2(street,apt,city,state,zip,dob,map,redirectAttributes);
	}
	
	@GetMapping("/signupP3")
	public String getSignupP3() {
		return "auth/signupP3";
	}
	
	@PostMapping("/signupP3")
	public String postSignupP3() {
		return "redirect:/setPassword";
	}
	
	@GetMapping("/setPassword")
	public String getSetPassword() {
		return "auth/setPassword";
	}
	
	@PostMapping("/setPassword")
	public String postSetPassword(@RequestParam String password,@RequestParam String confPassword,ModelMap map) {
		return authService.postSetPassword(password,confPassword,map);
	}
	
	@GetMapping("/forgotUsername")
	public String getForgotUsername() {
		return "auth/forgotUsername";
	}
	
	@PostMapping("/forgotUsername")
	public String postForgtoUsername(@RequestParam String firstname,@RequestParam String lastname,@RequestParam String email,ModelMap map) throws MessagingException {
		return authService.postForgotUsername(firstname,lastname,email,map);
	}
	
	@PostMapping("/forgotPassword")
	public String postForgotPassword(@RequestParam String username,ModelMap map) throws MessagingException {
		return authService.postForgotPassword(username,map);
	}
	
	@PostMapping("/validateOtp")
	public String validateOtp(@RequestParam String otp,@RequestParam String username,ModelMap map) {
		return authService.validateOtp(otp,username,map);
	}
	
	@PostMapping("/resendOtp")
	public String resendOtp() throws MessagingException {
		return authService.resendOtp();
	}
	
	@PostMapping("/resetPassword")
	public String postResetPassword(@RequestParam String password,@RequestParam String confPassword, @RequestParam(required = false) Boolean validOtp,@RequestParam String username, ModelMap map) {
		return authService.postResetPassword(password,confPassword,validOtp,username,map);
	}
	
	

}
