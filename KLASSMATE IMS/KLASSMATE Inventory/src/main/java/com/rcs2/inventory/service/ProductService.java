package com.rcs2.inventory.service;

import java.util.List;
import java.util.Optional;

import com.rcs2.inventory.controller.dto.NewProductDTO;
import com.rcs2.inventory.controller.dto.ProductBehavior;
import com.rcs2.inventory.controller.dto.ProductCountDTO;
import com.rcs2.inventory.controller.dto.ProductCountTypeAndStatus;
import com.rcs2.inventory.model.Product;
import com.rcs2.inventory.model.ProductType;
import com.rcs2.inventory.model.Project;
import com.rcs2.inventory.model.Status;

public interface ProductService {
	
	Long countProductsByStatus(Status status);
	
	Product saveProduct(NewProductDTO product);
	
	Optional<Product> getProductByName(String name);
	
	Optional<Product> getProductBySerialNumber(String serial);
		
	List<Product> getAllProductsNameAndId();
	
	List<Product> getAllProducts();
	
	List<Product> getAllProductsInType(ProductType type);
	
	List<Product> getAllProductsByStatus(Status status);
	
	List<Product> getProductsByAssembleTo(Product product);
	
	List<Product> getAllProductsByProductName(String name);
	
	List<Product> getProductsByProject(Project project);

	List<Product> getAllProductDetailsInCustomInput(String input);
	
	List<Product> getProductsByIsReplaced(boolean isReplaced);
	
	List<ProductCountDTO> getProductCountByCategory();
	
	List<ProductCountDTO> getProductsCountByStatus();
	
	List<ProductBehavior> getProductBehaviorByStatus(Status status);
	
	List<ProductCountTypeAndStatus> getCountTypeAndStatus();
	
	List<ProductCountTypeAndStatus> getCountByTypeAndStatus(Status status);
	
	List<Product> getProductsByStatusAndType(Status status, ProductType type);
	
	void replaceAndRepairProduct(Product assembledTo, String serial);
	
	void updateStatus(Product product, Status status);

	void assembleToProduct(Optional<Product> assembleTo, Status status, Optional<Product> assembled);
	
	void removeAssembledProduct(String serial);
	
	void allocateToProject(Project project, String serial);
	
	void removeFromProject(String serial);
	
}
