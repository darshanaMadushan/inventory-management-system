package com.rcs2.inventory.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.rcs2.inventory.model.ProductType;
import com.rcs2.inventory.repository.ProductTypeRepository;

@Controller
public class ProductTypeController {

	Logger LOGGER = LoggerFactory.getLogger(ProductTypeController.class);
	
	@Autowired
	ProductTypeRepository productTypeRepository;
	
	@PostMapping("/addProductType")
	public String addNewProductType(@ModelAttribute("productType") ProductType type) {
		LOGGER.info("Request to add type : " + type.getCategoryName());
		if(productTypeRepository.findById(type.getSerial()).isEmpty() && productTypeRepository.findByCategoryName(type.getCategoryName()).isEmpty()) {
			productTypeRepository.save(type);
			LOGGER.info(type.getCategoryName() + " product type saved successfully.");
			return "redirect:/admin-home?typeSuccess";
		}else {
			LOGGER.warn(type.getCategoryName() + "type exist or" + type.getSerial() + " serial exist error");
			return "redirect:/admin-home?typeExist";
		}
	}
}
