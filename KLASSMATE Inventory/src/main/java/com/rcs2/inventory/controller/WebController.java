package com.rcs2.inventory.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.rcs2.inventory.controller.dto.NewProductDTO;
import com.rcs2.inventory.controller.dto.ProductAssembleDTO;
import com.rcs2.inventory.model.ProductType;
import com.rcs2.inventory.model.Status;
import com.rcs2.inventory.service.ProductService;
import com.rcs2.inventory.service.ProductTypeService;
import com.rcs2.inventory.service.UserService;

@Controller
public class WebController {
	
	Logger LOGGER = LoggerFactory.getLogger(WebController.class);
	
	@Autowired
	ProductService productService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	ProductTypeService productTypeService;
	
	
	@GetMapping("/keeper-home")
	public String adminHome(Model model) {
		LOGGER.info("Keeper home loaded.");
		model.addAttribute("products", productService.getAllProducts());
		model.addAttribute("productTypes", productTypeService.getAllTypes());
		model.addAttribute("product", new NewProductDTO());
		return "/keeper/keeper-home";
	}
	
	@GetMapping("/admin-home")
	public String adminHomePage(Model model, Principal principal) {
		LOGGER.info("Admin home loaded.");
		ProductType type = new ProductType();
		model.addAttribute("productType", type);
		model.addAttribute("productTypes", productTypeService.getAllTypes());
		model.addAttribute("username", principal.getName());
		return "admin/admin-home";
	}
	
	@GetMapping("/user-home")
	public String userHomePage(Model model, Principal principal) {
		LOGGER.info("User home loaded.");
		model.addAttribute("username", principal.getName());
		model.addAttribute("products", productService.getAllProductsByStatus(Status.ON_ASSEMBLE));
		model.addAttribute("productAssemble", new ProductAssembleDTO());
		return "user/user-home";
	}
	
	@GetMapping("/inventory")
	public String inventoryPage(Model model) {
		LOGGER.info("Inventory Page loaded.");
		model.addAttribute("products", productService.getAllProductsByStatus(Status.IN_INVENTORY));
		return "admin/inventory";
	}
	
	@GetMapping("/workshop-inventory")
	public String workshopInventoryPage(Model model) {
		LOGGER.info("Workshop Inventory Page Loaded.");
		model.addAttribute("products", productService.getAllProductsByStatus(Status.ON_ASSEMBLE));
		return "admin/workshop-inventory";
	}
	
	@GetMapping("/product-assemble")
	public String productAssemblePage(Model model, Principal principal) {
		LOGGER.info("Product Assmbling Page Loaded.");
		model.addAttribute("username", principal.getName());
		return "user/product-assemble";
	}
	
	@GetMapping("/product-repair")
	public String productRepairPage(Model model, Principal principal) {
		LOGGER.info("Product Repair Page Loaded.");
		model.addAttribute("username", principal.getName());
		model.addAttribute("finishedProducts", productService.getAllProductsByStatus(Status.FINISHED));
		return "user/product-repair";
	}
	
	@GetMapping("/allocated-products")
	public String allocatedProductsPage(Model model) {
		LOGGER.info("Allocated Products Page Loaded.");
		model.addAttribute("allocatedProducts", productService.getAllProductsByStatus(Status.ALLOCATED));
		return "admin/allocated-products";
	}
	
	@GetMapping("/finished-products")
	public String finishedProductsPage(Model model, Principal principal) {
		LOGGER.info("Finished Product Page Loaded.");
		model.addAttribute("finishedProducts", productService.getAllProductsByStatus(Status.FINISHED));
		return "admin/finished-products";
	}
	
	@GetMapping("/all-products")
	public String allProductsPage(Model model) {
		LOGGER.info("All Products Page Loaded.");
		model.addAttribute("allProducts", productService.getAllProducts());
		return "admin/all-products";
	}
	
	@GetMapping("/categories")
	public String categoryPage (Model model) {
		LOGGER.info("Categories Page loaded.");
		model.addAttribute("productTypes", productTypeService.getAllTypes());
		return "/keeper/categories";
	}
	
	@GetMapping("/access-denied")
	public String AccessDeniedPage() {
		LOGGER.info("Access Denied.");
		return "error/access-denied";
	}
	
	@GetMapping("/error")
	public String errorPage() {
		LOGGER.info("Requested content Not Found : 404");
		return "/error/404";
	}

}