package com.rcs2.inventory.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.rcs2.inventory.controller.dto.UserRegistrationDTO;
import com.rcs2.inventory.model.User;
import com.rcs2.inventory.service.UserService;

@Controller
public class UserController {

	Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;
	
	public UserController(UserService userService) {
		super();
		this.userService = userService;
	}
	
	@GetMapping("/signup")
	public String signupPage(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		LOGGER.info("Signup Page Loaded.");
		return "signup";
	}
	
	@GetMapping("/")
	public String loginPageByIndex(Model model) {
		LOGGER.info("Login Page requst from / ");
		return "login";
	}
	
	@GetMapping("/login")
	public String loginPage(Model model) {
		LOGGER.info("Login Page loaded.");
		return "login";
	}
	
	@PostMapping("/signup")
	public String registration(@ModelAttribute("user") UserRegistrationDTO dto) {
		LOGGER.info("User Registraion request" + dto.getName());
			if(userService.getDetailsByUsername(dto.getUsername()) == null) {
				if(dto.getRole() == UserRegistrationDTO.Role.KEEPER) {
					
					if(userService.createKeeper(dto) != null)
						LOGGER.info("User created : " + dto.getName() + " " + dto.getDesignation());
						return "redirect:/signup?successKeeper";		
				} else if (dto.getRole() == UserRegistrationDTO.Role.USER) {
					if(userService.createUser(dto) != null) {
						LOGGER.info("Keeper created : " + dto.getName() + " " + dto.getDesignation());
						return "redirect:/signup?successUser";
					}
				}
			} else {
				LOGGER.warn("User exist with " + dto.getUsername());
				return "redirect:/signup?exist";
			}
			LOGGER.error("Error in signup page");
			return "redirect:/signup?error";
	}
	
}
