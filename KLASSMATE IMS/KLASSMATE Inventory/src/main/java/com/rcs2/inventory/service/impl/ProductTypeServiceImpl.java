package com.rcs2.inventory.service.impl;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rcs2.inventory.model.ProductType;
import com.rcs2.inventory.repository.ProductTypeRepository;
import com.rcs2.inventory.service.ProductTypeService;

@Service
public class ProductTypeServiceImpl implements ProductTypeService{

	@Autowired
	ProductTypeRepository productTypeRepository;
	
	@Override
	public List<ProductType> getAllTypes() {
		return productTypeRepository.findAllByOrderByCategoryNameAsc();	
	}

	@Override
	public Long countProductTypes() {
		return productTypeRepository.count();
	}

	@Override
	public Optional<ProductType> getProductTypeById(String id) {
		return productTypeRepository.findById(id);
	}

	@Override
	public void updateProductType(String type, String serial) {
		Date date = new java.sql.Date(new java.util.Date().getTime());
		productTypeRepository.updateProductType(type, date, serial);
	}

	@Override
	public void removeProductType(String serial) {
		productTypeRepository.removeProductType(serial);
	}

}
