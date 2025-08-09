package com.tarun.SpringSecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tarun.SpringSecurity.model.AccountInfo;

@Repository
public interface UserRepository extends JpaRepository<AccountInfo,String>{
	
	AccountInfo findByUsername(String username);
	
	AccountInfo findByEmail(String email);
	
	AccountInfo findByMobile(String mobile);
	
	AccountInfo save(AccountInfo user);
}
