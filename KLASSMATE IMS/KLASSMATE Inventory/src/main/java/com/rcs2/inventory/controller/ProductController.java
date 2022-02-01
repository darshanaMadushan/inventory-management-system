package com.rcs2.inventory.controller;

import java.security.Principal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.rcs2.inventory.controller.dto.NewProductDTO;
import com.rcs2.inventory.controller.dto.ProductAssembleDTO;
import com.rcs2.inventory.model.Product;
import com.rcs2.inventory.model.Status;
import com.rcs2.inventory.service.ProductService;

@Controller
public class ProductController {

	Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
	
	private ProductService productService;

	public ProductController(ProductService productService) {
		super();
		this.productService = productService;
	}

	@PostMapping("/product/new")
	public String saveProduct(@ModelAttribute("product") NewProductDTO product, Principal principal) {
		String serial = product.getId();
		LOGGER.info("New Product request : " + serial);
		if (productService.getProductBySerialNumber(serial).isEmpty()) {
			productService.saveProduct(product);
			LOGGER.info("Product Creation success: " + serial + " by " + principal.getName());
			return "redirect:/add-new-product?addSuccess";
		} else {
			LOGGER.warn("Product exist on ->" + serial + " requested by " + principal.getName());
			return "redirect:/add-new-product?addError";
		}
	}

//	@GetMapping("/sendToAssemble/{id}")
//	public String sendToAssemble(@PathVariable("id") String serial, Principal principal) {
//		Optional<Product> updatingProduct = productService.getProductBySerialNumber(serial);
//		LOGGER.info("Send to assemble request : " + serial + " Requested by: " + principal.getName());
//		if (updatingProduct.isPresent()) {
//			productService.updateStatus(updatingProduct, Status.ON_ASSEMBLE);
//			LOGGER.info("Sent to assemble : " + serial +" sent by: " + principal.getName());
//			return "redirect:/inventory?successAssemble";
//		} else {
//			LOGGER.warn("Product not found : " + serial);
//			return "redirect:/inventory?failAssemble";
//
//		}
//	}

	@PostMapping("/assembleToProduct")
	public String assembleToProduct(@ModelAttribute("productAssemble") ProductAssembleDTO dto, Principal principal) {
		Optional<Product> assembleTo = productService.getProductBySerialNumber(dto.getSerialNum());
		Optional<Product> assembled = productService.getProductBySerialNumber(dto.getAssembleTo().getSerialNumber());
		if(assembleTo.isPresent()) {			
			if(assembled.isPresent()) {	
				productService.assembleToProduct(assembled, Status.ALLOCATED, assembleTo);
				LOGGER.info("Product " + dto.getSerialNum() + " assembled to " + dto.getAssembleTo().getSerialNumber() + ", Successor: " + principal.getName());
				return "redirect:user-home?assembleSuccess";
				
			}else {
				LOGGER.warn(assembled.get().getSerialNumber() + " not found");
				return "redirect:user-home?productError";
			}
		}else {
			LOGGER.warn(assembleTo.get().getSerialNumber() + " Not found");
			return "redirect:user-home?AssmbleToError";
			
		}
	}

}
