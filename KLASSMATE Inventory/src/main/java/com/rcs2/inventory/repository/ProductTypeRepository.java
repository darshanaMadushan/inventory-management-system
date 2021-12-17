package com.rcs2.inventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rcs2.inventory.model.ProductType;

public interface ProductTypeRepository extends JpaRepository<ProductType, String>{

	Optional<ProductType> findByCategoryName(String name);
	
	Optional<ProductType> findBySerial(String serial);
	
}