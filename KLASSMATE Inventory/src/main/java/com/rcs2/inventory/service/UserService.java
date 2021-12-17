package com.rcs2.inventory.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.rcs2.inventory.controller.dto.UserRegistrationDTO;
import com.rcs2.inventory.model.User;

public interface UserService extends UserDetailsService {

	User createUser(UserRegistrationDTO dto);
	
	User createKeeper(UserRegistrationDTO dto);
	
	User getDetailsByUsername(String username);
	
	User getDetailsByName(String name);
	
	User updateUserWithoutPassword(User user);
	
	User getUserById(Integer id);
	
}
