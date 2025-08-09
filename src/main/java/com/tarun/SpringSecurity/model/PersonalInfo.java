package com.tarun.SpringSecurity.model;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


@Table(name="personalInfo")
@Entity
public class PersonalInfo {
	
	@Id
	private String username;
	
	private String street;
	private String apt;
	@Column(nullable = false)
	private String city;
	@Column(nullable = false)
	private String state;
	private String zip;
	@Column(nullable = false)
	private String dob;
	
	@OneToOne
	@MapsId
	@JoinColumn(name="username")
	private AccountInfo accountInfo;

	
	public PersonalInfo() {}
	@Override
	public String toString() {
		return "PersonalInfo [username=" + username + ", street=" + street + ", apt=" + apt + ", city=" + city
				+ ", state=" + state + ", zip=" + zip + ", dob=" + dob + ", accountInfo=" + accountInfo + "]";
	}

	public PersonalInfo(String username, String street, String apt, String city, String state, String zip, String dob,
			AccountInfo accountInfo) {
		super();
		this.username = username;
		this.street = street;
		this.apt = apt;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.dob = dob;
		this.accountInfo = accountInfo;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getApt() {
		return apt;
	}

	public void setApt(String apt) {
		this.apt = apt;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public AccountInfo getAccountInfo() {
		return accountInfo;
	}

	public void setAccountInfo(AccountInfo accountInfo) {
		this.accountInfo = accountInfo;
	}
	
	
	
}
