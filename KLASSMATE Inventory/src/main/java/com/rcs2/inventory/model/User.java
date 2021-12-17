package com.rcs2.inventory.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
public class User {

	public enum Role {ADMIN, KEEPER, USER}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id; 

	private String username; 
	
	private String name;
	
	private String designation;
	
	private String password;
	
	private Role role;

	public User(String username, String name, String designation, String password, Role role) {
		super();
		this.username = username;
		this.name = name;
		this.designation = designation;
		this.password = password;
		this.role = role;
	}
	
}
