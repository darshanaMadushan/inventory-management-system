package com.rcs2.inventory.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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

	public enum Role {ADMIN, KEEPER, WORKSHOP_ADMIN, WORKSHOP_USER};

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id; 

	private String username; 
	
	private String name;
	
	private String email;
	
	private String designation;
	
	private String password;
	
	private Role role;

	@OneToMany(mappedBy = "user")
	private Set<Project> project; 
	
	public User(String username, String name, String email, String designation, String password, Role role) {
		super();
		this.username = username;
		this.name = name;
		this.email = email;
		this.designation = designation;
		this.password = password;
		this.role = role;
	}	
}
