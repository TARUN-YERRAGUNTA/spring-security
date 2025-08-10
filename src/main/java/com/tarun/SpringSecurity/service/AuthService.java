package com.tarun.SpringSecurity.service;

import java.time.Year;

import java.util.List;
import java.util.Random;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tarun.SpringSecurity.model.AccountInfo;
import com.tarun.SpringSecurity.model.PersonalInfo;
import com.tarun.SpringSecurity.repository.UserPersonalRepository;
import com.tarun.SpringSecurity.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
public class AuthService {
	
	private String OtpGenerated="";
	private long OtpGeneratedTime=0;
	private String resetEmail="";
	private String resetPassUsername="";
	
	private  AccountInfo userSignupP1=new AccountInfo();
	private  PersonalInfo  userSignupP2 = new PersonalInfo();
	
	
	private final AuthenticationManager authManager;
	private final UserRepository userRepo;
	private final JavaMailSender mailSender;
	private EntityManager em;
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	private final JwtService jwtService;
	
	public AuthService(AuthenticationManager authManager,UserRepository userRepo,JavaMailSender mailSender,EntityManager em,JwtService jwtService) {
		this.authManager = authManager;
		this.userRepo = userRepo;
		this.mailSender=mailSender;
		this.em=em;
		this.jwtService=jwtService;
	}

	public String postLoginVerify(String username, String password,ModelMap map,HttpServletResponse response) {
		
		try {
			Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			AccountInfo user = userRepo.findByUsername(username);
	        if (user == null) {
	            map.addAttribute("loginError", "Username and Password does not match");
	            return "auth/login";
	        }
			if(authentication.isAuthenticated()) {
					        	
	            String token = jwtService.generateToken(username);
	            Cookie jwtCookie = new Cookie("jwt", token);
	            jwtCookie.setHttpOnly(true);
	            jwtCookie.setPath("/");
	            jwtCookie.setMaxAge(24 * 60 * 60);
	            response.addCookie(jwtCookie);
				return "pages/home";
			}
		}catch (Exception e) {
			// TODO: handle exception
			map.addAttribute("loginError","Username and Password does not match");
			return "auth/login";
		}
		map.addAttribute("loginError","Username and Password does not match");
		return "auth/login";
		
		
	}

	public String postSignupP1(String firstname, String lastname, String email, String confEmail, String phn,
			String username,ModelMap map) {
		// TODO Auto-generated method stub
		map.addAttribute("firstname",firstname);
		map.addAttribute("lastname",lastname);
		boolean validateEmail  = email.equals(confEmail);
		if(!validateEmail) {
			map.addAttribute("emailError2","Email and Confirm Email does not match");
		}
		map.addAttribute("email",email);
		AccountInfo emailPresent = userRepo.findByEmail(email);
		if(emailPresent!=null) {
			map.addAttribute("emailError1","Email already Present!");	
		}else {
			map.addAttribute("confEmail",confEmail);
		}
		

		
		boolean validatePhn = phn.length()==12||phn.length()==0?true:false;
		if(!validatePhn) {
			map.addAttribute("phnError","Phone number should contain 10 digits");
	
		}
		AccountInfo phnPresent=null;
		if(phn.length()!=0) {
			phnPresent = userRepo.findByMobile(phn);
		}
		
		if(phnPresent != null) {
			map.addAttribute("phnError","Phone number already exists!");

		}
		map.addAttribute("phn",phn);
		
		boolean usernameCredibility = true;
		AccountInfo user = userRepo.findByUsername(username);
		if(user!=null) {
			map.addAttribute("usernameError","That Username is already taken!");
		}else {
			usernameCredibility = checkUsernameCredibility(username);
			if(!usernameCredibility) {
				map.addAttribute("usernameError2","Username not valid");
				map.addAttribute("username",username);
			}
		}
		
		
		if(user==null && validatePhn && phnPresent==null && emailPresent==null &&  validateEmail && user==null && usernameCredibility) {
			
			userSignupP1.setEmail(email);
			userSignupP1.setFirstname(firstname);
			userSignupP1.setLastname(lastname);
			if(phn==null || phn.isEmpty()) {
				userSignupP1.setMobile(null);
			}else {
				userSignupP1.setMobile(phn);
			}
			userSignupP1.setUsername(username);
			
			
			return "redirect:/signupP2";
		}else {
			return "auth/signupP1";
		}
		
	
		
	}
	
	public boolean checkUsernameCredibility(String username) {
		if(username.length()<=4 || username.length()>=128) {
			return false;
		}
		for(int i=0;i<username.length();i++) {
			if(username.charAt(i)==32) {
				return false;
			}
			
		}
		return true;
	}
	
	

	public String postSignupP2(String street, String apt, String city, String state, String zip, String dob, ModelMap map,RedirectAttributes redirectAttributes) {
	    boolean validZip = zip != null && zip.length() == 5;
	    if(validZip) {
	    	for(int i=0;i<zip.length();i++) {
	    		if(zip.charAt(i)>=0 && zip.charAt(i)<=47 || zip.charAt(i)>=58 && zip.charAt(i)<=127) {
	    			validZip=false;
	    			break;
	    		}
	    	}
	    }
	    boolean validDate = false;

	    try {
	        int month = Integer.parseInt(dob.substring(0, 2));
	        int day = Integer.parseInt(dob.substring(3, 5));
	        int year = Integer.parseInt(dob.substring(6));

	        validDate = day > 0 && day < 32 && month > 0 && month <= 12 && year > 1900 && year < Year.now().getValue();
	    } catch (Exception e) {
	        validDate = false;
	    }

	    // If all valid, save and proceed
	    if (validDate && validZip) {
	        userSignupP2.setStreet(street);
	        userSignupP2.setApt(apt);
	        userSignupP2.setCity(city);
	        userSignupP2.setZip(zip);
	        userSignupP2.setDob(dob);
	        userSignupP2.setState(state);
	        userSignupP2.setUsername(userSignupP1.getUsername());
	        

	        redirectAttributes.addFlashAttribute("dob", userSignupP2.getDob());
	        redirectAttributes.addFlashAttribute("zip", userSignupP2.getZip());
	        redirectAttributes.addFlashAttribute("street", userSignupP2.getStreet());
	        redirectAttributes.addFlashAttribute("apt", userSignupP2.getApt());
	        redirectAttributes.addFlashAttribute("city", userSignupP2.getCity());
	        redirectAttributes.addFlashAttribute("state", userSignupP2.getState());
	        redirectAttributes.addFlashAttribute("username", userSignupP1.getUsername());
	        redirectAttributes.addFlashAttribute("firstname", userSignupP1.getFirstname());
	        redirectAttributes.addFlashAttribute("lastname", userSignupP1.getLastname());
	        redirectAttributes.addFlashAttribute("phn", userSignupP1.getMobile());
	        redirectAttributes.addFlashAttribute("email", userSignupP1.getEmail());


	        return "redirect:/signupP3";
	    } else {
	        // Repopulate user input on error
	        map.addAttribute("street", street);
	        map.addAttribute("apt", apt);
	        map.addAttribute("city", city);
	        map.addAttribute("state", state);
	        map.addAttribute("zip", zip);
	        map.addAttribute("dob", dob);

	        if (!validZip) {
	            map.addAttribute("zipError", "Enter correct zip code");
	        }
	        if (!validDate) {
	            map.addAttribute("dobError", "Invalid Date of Birth");
	        }

	        // Always add the list of states for the dropdown
	        List<String> states = List.of(
	            "Alabama", "Alaska", "Arizona", "Arkansas", "California",
	            "Colorado", "Connecticut", "Delaware", "Florida", "Georgia",
	            "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa",
	            "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland",
	            "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri",
	            "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey",
	            "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio",
	            "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina",
	            "South Dakota", "Tennessee", "Texas", "Utah", "Vermont",
	            "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"
	        );
	        map.addAttribute("states", states);

	        return "auth/signupP2";
	    }
	}


	public void getSignupP2(ModelMap map) {
		
		List<String> states = List.of(
			    "Alabama", "Alaska", "Arizona", "Arkansas", "California",
			    "Colorado", "Connecticut", "Delaware", "Florida", "Georgia",
			    "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa",
			    "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland",
			    "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri",
			    "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey",
			    "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio",
			    "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina",
			    "South Dakota", "Tennessee", "Texas", "Utah", "Vermont",
			    "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"
			);
		
		map.addAttribute("states", states);
		
		String selectedState = userSignupP2.getState();
		if (selectedState == null || selectedState.isBlank()) {
			selectedState = "New York";
		}
		
		map.addAttribute("state", selectedState);
		 
		 

		map.addAttribute("street",userSignupP2.getStreet());
		map.addAttribute("city",userSignupP2.getCity());
		map.addAttribute("apt",userSignupP2.getApt());
		map.addAttribute("zip",userSignupP2.getZip());
		map.addAttribute("dob",userSignupP2.getDob());
		
	}
	
	public void getSignupP1(ModelMap map) {
		map.addAttribute("username",userSignupP1.getUsername());
        map.addAttribute("firstname",userSignupP1.getFirstname());
        map.addAttribute("lastname",userSignupP1.getLastname());
        map.addAttribute("phn",userSignupP1.getMobile());
        map.addAttribute("email",userSignupP1.getEmail());
        map.addAttribute("confEmail",userSignupP1.getEmail());
	}

	public String postForgotUsername(String firstname, String lastname, String email,ModelMap map) throws MessagingException {
		// TODO Auto-generated method stub
		map.addAttribute("firstname",firstname);
		map.addAttribute("lastname",lastname);
		map.addAttribute("email",email);
		
		AccountInfo user = userRepo.findByEmail(email);
		
		if(user==null || !user.getFirstname().equals(firstname) || !user.getLastname().equals(lastname)) {
			map.addAttribute("forgotUsernameMailError","true");
			return "auth/forgotUsername";
		}
		
		MimeMessage msg = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(msg,true);
		
		helper.setTo(email);
		helper.setSubject("Information you requested from MyConnect");
		
		String content =  
				"<p><strong>This email was sent in response to your \"Forgot Username\" request.</strong></p>" +
			    "<p>Dear " + user.getFirstname() +" "+user.getLastname()+ ",</p>" +
			    "<p>You recently requested that we provide you with your MyConnect Username." +
			    "<strong>If you DID NOT request the Username, you may disregard this email.</strong></p>" +
			    "<p>Your MyConnect Username is shown below.</p>" +
			    "<table border=\"1\" cellpadding=\"5\" cellspacing=\"0\">" +
			    "<tr><th>Username</th></tr>" +
			    "<tr><td>" + user.getUsername() + "</td></tr>" +
			    "</table>" +
			    "<p>Thank you<br>MyConnect</p>";
		
		helper.setText(content, true);
		
		mailSender.send(msg);
		
		map.addAttribute("forgotUsernameMailMessage","true");
		
		return "auth/forgotUsername";
		
		
		
	}

	public String postForgotPassword(String username, ModelMap map) throws MessagingException {
		// TODO Auto-generated method stub
		AccountInfo user = userRepo.findByUsername(username);
		if(user==null) {
			map.addAttribute("forgotPasswordError","true");
			return "auth/forgotPassword";
		}
		resetEmail=user.getEmail();
		MimeMessage msg = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(msg,true);
		
	    int otpGenerated =new Random().nextInt(900_000)+100_000;
	    OtpGenerated = ""+otpGenerated;
		
	    String content =
	    	    "<p><strong>This email was sent in response to your \"Forgot Password\" request.</strong></p>" +
	    	    "<p>Dear " + user.getFirstname() + " " + user.getLastname() + ",</p>" +
	    	    "<p>This email was sent in response to your \"Forgot Password\" request. If you DID NOT make the request, you may disregard this email.</p>" +
	    	    "<p>By selecting 'Forgot Password', you have begun the account reactivation process. For security reasons, you will be asked to complete two steps:</p>" +
	    	    "<ol>" +
	    	        "<li>Enter the given OTP within 2 minutes</li>" +
	    	        "<li>Create a new password</li>" +
	    	    "</ol>" +
	    	    "<p><strong>OTP:</strong> " + OtpGenerated + "</p>" +
	    	    "<p>Thank you,<br>MyConnect</p>";

		helper.setTo(user.getEmail());
		helper.setSubject("Information you requested from MyConnect");
		helper.setText(content,true);
		mailSender.send(msg);
		
		OtpGeneratedTime = System.currentTimeMillis();
		map.addAttribute("username", username);
		resetPassUsername = username;
		return "redirect:/resetPassword";
	}

	public String validateOtp(String otp,String username, ModelMap map) {
		
		// TODO Auto-generated method stub
		map.addAttribute("username", username);
		if(!otp.equals(OtpGenerated)) {
			map.addAttribute("otpError","Invalid OTP!");
			
		}else if(otp.equals(OtpGenerated) && System.currentTimeMillis()-OtpGeneratedTime >120_000) {
			map.addAttribute("otpError","OTP Timeout!");
		}else {
			map.remove("otpError");
			map.addAttribute("validOtp",true);
		}
		return "auth/resetPassword";
	}

	public String resendOtp() throws MessagingException {
		// TODO Auto-generated method stub
		AccountInfo user = userRepo.findByEmail(resetEmail);
		
		MimeMessage msg = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(msg,true);
		
		int otpGenerated =new Random().nextInt(900_000)+100_000;
	    OtpGenerated = ""+otpGenerated;
		
		String content =
			    "<p><strong>This email was sent in response to your \"Forgot Password\" request.</strong></p>" +
			    "<p>Dear " + user.getFirstname() + " " + user.getLastname() + ",</p>" +
			    "<p>This email was sent in response to your \"Forgot Password\" request. If you DID NOT make the request, you may disregard this email.</p>" +
			    "<p>By selecting 'Forgot Password', you have begun the account reactivation process. For security reasons, you will be asked to complete two steps:</p>" +
			    "<ol>" +
			        "<li>Enter the given OTP within 2 minutes</li>" +
			        "<li>Create a new password</li>" +
			    "</ol>" +
			    "<p><strong>OTP:</strong> " + OtpGenerated + "</p>" +
			    "<p>Thank you,<br>MyConnect</p>";

		
		helper.setTo(user.getEmail());
		helper.setSubject("Information you requested from MyConnect");
		helper.setText(content,true);
		mailSender.send(msg);
		
		OtpGeneratedTime = System.currentTimeMillis();
		return "redirect:/resetPassword";
	}

	public String postResetPassword(String password, String confPassword, Boolean validOtp,String username, ModelMap map) {
		// TODO Auto-generated method stub
		map.addAttribute("username",username);
		if (validOtp == null || !validOtp) {
	        // OTP not validated, send error or redirect to OTP page
	        map.addAttribute("otpError2", "Please validate OTP before resetting password.");
	        map.addAttribute("validOtp", false);
	        return "auth/resetPassword"; // show the resetPassword page with OTP error
	    }
	    
	    // OTP validated, proceed with password reset logic
	    map.addAttribute("validOtp", true);
	    
		if(!password.equals(confPassword)) {
			map.addAttribute("passwordMatchError","Passwords do not match!");
			map.addAttribute("validOtp",true);
			return "auth/resetPassword";
		}
		
		String validatePassword = passwordCriteria(password, confPassword);
		if(validatePassword.length()!=0) {
			map.addAttribute("resetPasswordError",true);
			map.addAttribute("validOtp",true);
			return "auth/resetPassword";
		}
		
		AccountInfo user = userRepo.findByUsername(resetPassUsername);
		user.setPassword(encoder.encode(password)); // Encode the password
		userRepo.save(user);
		
		return "redirect:/login";
	}
	
	public String passwordCriteria(String password,String confPassword) {
		
		if(password.length()<8) {
			return "Password should contain at lease 8 characters";
		}
		
		int cap = 0;
		int normal = 0;
		int symbol = 0;
		int number =0;
		
		for(int i=0;i<password.length();i++) {
			int ascii = password.charAt(i);
			
			if(ascii>=33 && ascii<=47 || ascii>=58 && ascii<=64 || ascii>=91 && ascii<=96) {
				symbol++;
			}else if(ascii>=48 && ascii<=57) {
				number++;
			}else if(ascii>=65 && ascii<=90) {
				cap++;
			}else {
				normal++;
			}
		}
		
		if(cap ==0) {
			return "Password should contain a capital letter";
		}else if(symbol==0) {
			return "Password should contain a special character";
		}else if(number==0) {
			return "Password should contain a number";
		}else {
			return "";
		}
	}
	
	@Transactional
	public String postSetPassword(String password, String confPassword, ModelMap map) {
		// TODO Auto-generated method stub
		if(!password.equals(confPassword)) {
			map.addAttribute("setPasswordError","Passwords does not match");
			return "auth/setPassword";
		}
		
		String validatePassword = passwordCriteria(password, confPassword);
		if(validatePassword.length()!=0) {
			map.addAttribute("passwordCriteriaError",true);
			return "auth/setPassword";
		}
		
		
		AccountInfo account =  new AccountInfo();
		account.setUsername(userSignupP1.getUsername());
		account.setFirstname(userSignupP1.getFirstname());
		account.setLastname(userSignupP1.getLastname());
		account.setEmail(userSignupP1.getEmail());
		account.setMobile(userSignupP1.getMobile());
		account.setPassword(encoder.encode(password));
		
		em.persist(account);
		
		PersonalInfo personal = new PersonalInfo();
		personal.setStreet(userSignupP2.getStreet());
		personal.setApt(userSignupP2.getApt());
		personal.setCity(userSignupP2.getCity());
		personal.setState(userSignupP2.getState());
		personal.setDob(userSignupP2.getDob());
		personal.setZip(userSignupP2.getZip());
		personal.setUsername(userSignupP2.getUsername());
		
		personal.setAccountInfo(account);
		account.setPersonalInfo(personal);
		
		em.persist(personal);
		
		userSignupP1=null;
		userSignupP2=null;
		
		return "redirect:/login";
		
		
	}

	
	
	
	
}
