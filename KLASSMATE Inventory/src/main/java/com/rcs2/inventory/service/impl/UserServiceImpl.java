package com.rcs2.inventory.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.rcs2.inventory.config.CustomUserDetails;
import com.rcs2.inventory.controller.dto.UserRegistrationDTO;
import com.rcs2.inventory.model.User;
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
		User user = new User(dto.getUsername(), dto.getName(), dto.getDesignation(), passwordEncoder.encode(dto.getPassword()), User.Role.KEEPER);
		return userRepository.save(user);
	}
	
	@Override
	public User createUser(UserRegistrationDTO dto) {
		User user = new User(dto.getUsername(), dto.getName(), dto.getDesignation(), passwordEncoder.encode(dto.getPassword()), User.Role.USER);
		return userRepository.save(user);
	}
	
	@Override
	public User getDetailsByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	@Override
	public User getDetailsByName(String name) {
		return userRepository.findByName(name);
	}

	@Override
	public User updateUserWithoutPassword(User user) {
		return userRepository.save(user);
	}
	
	@Override
	public User getUserById(Integer id) {
		return userRepository.findById(id).get();
	}	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user = userRepository.findByUsername(username);
		if(user == null) {
			throw new UsernameNotFoundException("User not Found.");
		}
		return new CustomUserDetails(user);
	}


}
