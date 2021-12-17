package com.rcs2.inventory.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
	public String saveProduct(@ModelAttribute("product") NewProductDTO product) {
		String serial = product.getProductType().getSerial().concat("-").concat(product.getId());
		LOGGER.info("New Product request : " + serial);
		if (productService.getProductBySerialNumber(serial).isEmpty()) {
			productService.saveProduct(product);
			LOGGER.info("Product Creation success: " + serial);
			return "redirect:/keeper-home?addSuccess";
		} else {
			LOGGER.warn("Product exist on ->" + serial);
			return "redirect:/keeper-home?addError";
		}
	}

	@GetMapping("/sendToAssemble/{id}")
	public String sendToAssemble(@PathVariable("id") String serial) {
		Optional<Product> updatingProduct = productService.getProductBySerialNumber(serial);
		LOGGER.info("Send to assemble request : " + serial);
		if (updatingProduct.isPresent()) {
			productService.updateStatus(updatingProduct, Status.ON_ASSEMBLE);
			LOGGER.info("Sent to assemble : " + serial);
			return "redirect:/inventory?successAssemble";

		} else {
			LOGGER.warn("Product not found : " + serial);
			return "redirect:/inventory?failAssemble";

		}
	}

	@PostMapping("/assembleToProduct")
	public String assembleToProduct(@ModelAttribute("productAssemble") ProductAssembleDTO dto) {
		Optional<Product> assembleTo = productService.getProductBySerialNumber(dto.getSerialNum());
		Optional<Product> assembled = productService.getProductBySerialNumber(dto.getAssembleTo().getSerialNumber());
		LOGGER.info("Product assemble to request  : " + assembleTo.get().getSerialNumber());
		if(assembleTo.isPresent()) {			
			if(assembled.isPresent()) {	
				productService.assembleToProduct(assembled, Status.ALLOCATED, assembleTo);
				LOGGER.info("Product " + dto.getSerialNum() + " assembled to " + dto.getAssembleTo().getSerialNumber());
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
