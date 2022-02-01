package com.rcs2.inventory.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.rcs2.inventory.config.CustomUserDetails;
import com.rcs2.inventory.controller.dto.UserRegistrationDTO;
import com.rcs2.inventory.model.User;
import com.rcs2.inventory.model.User.Role;
import com.rcs2.inventory.repository.UserRepository;
import com.rcs2.inventory.service.UserService;


@Service
public class UserServiceImpl implements UserService{

	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;	
	
	public UserServiceImpl(UserRepository repo) {
		super();
		this.userRepository = repo;
	}

	@Override
	public User createKeeper(UserRegistrationDTO dto) {
		User user = new User(dto.getUsername(), dto.getName(), dto.getEmail(), dto.getDesignation(), passwordEncoder.encode(dto.getPassword()), User.Role.KEEPER);
		return userRepository.save(user);
	}
	
	@Override
	public User createWorkshopUser(UserRegistrationDTO dto) {
		User user = new User(dto.getUsername(), dto.getName(), dto.getEmail(), dto.getDesignation(), passwordEncoder.encode(dto.getPassword()), User.Role.WORKSHOP_USER);
		return userRepository.save(user);
	}
	
	@Override
	public User createWorkshopAdmin(UserRegistrationDTO dto) {
		User user = new User(dto.getUsername(), dto.getName(), dto.getEmail(), dto.getDesignation(), passwordEncoder.encode(dto.getPassword()), User.Role.WORKSHOP_ADMIN);
		return userRepository.save(user);
	}

	@Override
	public User createAdmin(UserRegistrationDTO dto) {
		User user = new User(dto.getUsername(), dto.getName(), dto.getEmail(), dto.getDesignation(), passwordEncoder.encode(dto.getPassword()), User.Role.ADMIN);
		return userRepository.save(user);
	}
	
	@Override
	public Optional<User> getDetailsByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public Optional<User> getDetailsByName(String name) {
		return userRepository.findByNameEquals(name);
	}

	@Override
	public void updateUser(Integer userId, UserRegistrationDTO dto, boolean isPasswordUpdating) {
		if(isPasswordUpdating) {
			String password = passwordEncoder.encode(dto.getPassword());
			userRepository.updateUserDetails(dto.getName(), dto.getEmail(), dto.getDesignation(), password, userId);
		} else {
			userRepository.updateUserDetails(dto.getName(), dto.getEmail(), dto.getDesignation(), dto.getPassword(), userId);
		}
	}
	
	@Override
	public User getUserById(Integer id) {
		return userRepository.findById(id).get();
	}	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Optional<User> user = userRepository.findByUsername(username);
		if(user.isEmpty()) {
			throw new UsernameNotFoundException("User not Found.");
		}
		return new CustomUserDetails(user.get());
	}

	@Override
	public Integer countUsersByRole(Role role, String method) {
		if(method.equals("except")) {
			return userRepository.countByExceptRole(role);		
		} else {
			return userRepository.countByRole(role);
		}
	}

	@Override
	public List<User> getAllDetails() {
		return userRepository.findAllOrderByRoleDesc();
	}

	@Override
	public List<User> getRoleWiseCount() {
		return null;
	}

	@Override
	public Optional<User> getDetailsById(int id) {
		return userRepository.findById(id);
	}

	@Override
	public void deleteUser(User user) {
		userRepository.delete(user);
	}


	
	
}
