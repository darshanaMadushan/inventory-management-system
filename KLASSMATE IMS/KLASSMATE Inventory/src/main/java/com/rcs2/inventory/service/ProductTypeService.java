package com.rcs2.inventory.service;

import java.util.List;
import java.util.Optional;

import com.rcs2.inventory.model.ProductType;

public interface ProductTypeService {

	List<ProductType> getAllTypes();
	
	Long countProductTypes();
	
	Optional<ProductType> getProductTypeById(String id);

	void updateProductType(String type, String serial);

	void removeProductType(String serial);
	
}
