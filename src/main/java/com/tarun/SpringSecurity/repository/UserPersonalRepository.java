package com.tarun.SpringSecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tarun.SpringSecurity.model.PersonalInfo;

public interface UserPersonalRepository extends JpaRepository<PersonalInfo, String> {
	
	PersonalInfo save(PersonalInfo user);
}
