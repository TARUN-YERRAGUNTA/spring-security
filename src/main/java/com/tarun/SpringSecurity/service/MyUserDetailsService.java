package com.tarun.SpringSecurity.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tarun.SpringSecurity.model.AccountInfo;
import com.tarun.SpringSecurity.model.UserPrincipal;
import com.tarun.SpringSecurity.repository.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService{
	
	private final UserRepository userRepo;
	
	public MyUserDetailsService(UserRepository userRepo) {
		this.userRepo=userRepo;
	}
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		AccountInfo user = userRepo.findByUsername(username);
		
		if(user == null) {
			throw  new UsernameNotFoundException("User Not Found");
		}
		return new UserPrincipal(user);
	}
	
	

}
