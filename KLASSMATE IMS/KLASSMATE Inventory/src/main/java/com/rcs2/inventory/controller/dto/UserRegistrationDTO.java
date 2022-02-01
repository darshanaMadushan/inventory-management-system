package com.rcs2.inventory.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDTO {
		
	public enum Role{ADMIN, KEEPER, WORKSHOP_ADMIN, WORKSHOP_USER};
	
	private String username;
	
	private String name;
	
	private String email;
	
	private String password;
	
	private String designation;
	
	private Role role;

	public UserRegistrationDTO(String name, String email, String password, String designation, Role role) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
		this.designation = designation;
		this.role = role;
	} 
}
