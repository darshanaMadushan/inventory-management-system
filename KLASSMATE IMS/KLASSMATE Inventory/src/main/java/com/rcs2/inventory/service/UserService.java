package com.rcs2.inventory.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.rcs2.inventory.controller.dto.UserRegistrationDTO;
import com.rcs2.inventory.model.User;
import com.rcs2.inventory.model.User.Role;

public interface UserService extends UserDetailsService {

	User createWorkshopUser(UserRegistrationDTO dto);
	
	User createWorkshopAdmin(UserRegistrationDTO dto);
	
	User createKeeper(UserRegistrationDTO dto);
	
	User createAdmin(UserRegistrationDTO dto);
	
	Optional<User> getDetailsByUsername(String username);
	
	Optional<User> getDetailsByName(String name);
	
	Optional<User> getDetailsById(int id);
	
	User getUserById(Integer id);
	
	Integer countUsersByRole(Role role, String method);
	
	List<User> getAllDetails();
	
	List<User> getRoleWiseCount();
	
	void deleteUser(User user);
	
	void updateUser(Integer id, UserRegistrationDTO user, boolean isPasswordUpdating);
	
}
