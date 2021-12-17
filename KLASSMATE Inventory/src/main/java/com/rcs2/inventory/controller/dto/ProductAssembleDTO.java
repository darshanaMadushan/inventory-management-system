package com.rcs2.inventory.controller.dto;

import com.rcs2.inventory.model.Product;
import com.rcs2.inventory.model.ProductType;
import com.rcs2.inventory.model.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductAssembleDTO {

	String serialNum; 
	
	Product assembleTo;
	
	Status status;
	
	ProductType type;
	
}
