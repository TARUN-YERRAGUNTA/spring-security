package com.tarun.SpringSecurity.model;

import java.sql.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Table(name="accountInfo")
@Entity
public class AccountInfo {
	
	@Id
	private String username;
	
	@Column(nullable = false)
	private String firstname;
	@Column(nullable = false)
	private String lastname;
	@Column(unique = true,nullable = false)
	private String email;
	private String mobile;
	@Column(nullable=false)
	private String password;
	
	@OneToOne(mappedBy="accountInfo",cascade=CascadeType.ALL)
	private PersonalInfo personalInfo;
	
	public AccountInfo() {}

	@Override
	public String toString() {
		return "AccountInfo [username=" + username + ", firstname=" + firstname + ", lastname="
				+ lastname + ", email=" + email + ", mobile=" + mobile + ", password=" + password + ", personalInfo="
				+ personalInfo + "]";
	}

	public AccountInfo(String username, String firstname, String lastname, String email, String mobile,
			String password, PersonalInfo personalInfo) {
		super();
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.mobile = mobile;
		this.password = password;
		this.personalInfo = personalInfo;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public PersonalInfo getPersonalInfo() {
		return personalInfo;
	}

	public void setPersonalInfo(PersonalInfo personalInfo) {
		this.personalInfo = personalInfo;
	}

	
	
}
