package com.rcs2.inventory.controller.dto;

import com.rcs2.inventory.model.ProductType;
import com.rcs2.inventory.model.Status;

public interface ProductCountTypeAndStatus {

	ProductType getType();
	
	Status getStatus();
	
	Long getCount();
	
}
