package com.rcs2.inventory.service;

import java.util.List;
import java.util.Optional;

import com.rcs2.inventory.controller.dto.NewProductDTO;
import com.rcs2.inventory.model.Product;
import com.rcs2.inventory.model.ProductType;
import com.rcs2.inventory.model.Status;

public interface ProductService {
	
	List<Product> getAllProducts();
	
	Product saveProduct(NewProductDTO product);
	
	List<Product> getAllProductsNameAndId();
	
	Optional<Product> getProductByName(String name);
		
	Optional<Product> getProductBySerialNumber(String serial);
	
	List<Product> getAllProductsInType(ProductType type);
	
	List<Product> getAllProductsByStatus(Status status);
	
	List<Product> getProductsByAssembleTo(Product product);
	
	List<Product> getAllProductsByProductName(String name);
		
	String replaceAndRepairProduct(Product assembledTo, String serial);

	List<Product> getAllProductDetailsInCustomInput(String input);
	
	void updateStatus(Optional<Product> product, Status status);

	void assembleToProduct(Optional<Product> assembleTo, Status status, Optional<Product> assembled);
	
	void removeAssembledProduct(String serial);
	
}
