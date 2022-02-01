package com.rcs2.inventory.controller.dto;

import com.rcs2.inventory.model.ProductType;
import com.rcs2.inventory.model.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewProductDTO {

	private String id;
	
	private String productName;
	
	private String description;
	
	private ProductType productType;
	
	private Status status;
	
}
