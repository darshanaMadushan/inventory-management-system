package com.rcs2.inventory.service.impl;

import java.util.List;

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
		return productTypeRepository.findAll();	
	}

}
