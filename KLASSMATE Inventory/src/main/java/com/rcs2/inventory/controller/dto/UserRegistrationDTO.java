package com.rcs2.inventory.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UserRegistrationDTO {
		
	public enum Role{ADMIN, KEEPER, USER};
	
	private String username;
	
	private String name;
	
	private String password;
	
	private String designation;
	
	private Role role; 
		
}
