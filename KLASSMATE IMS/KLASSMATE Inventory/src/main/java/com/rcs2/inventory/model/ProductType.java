package com.rcs2.inventory.model;

import java.sql.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="product_type")
public class ProductType {

	@Id
	private String serial; 
	
	private String categoryName;
	
	private Date created;
	
	@JsonIgnore
	@OneToMany(mappedBy="type")
	private Set<Product> product;

	public ProductType(String categoryName) {
		super();
		this.categoryName = categoryName;
	}

	public ProductType(String serial, String categoryName) {
		super();
		this.serial = serial;
		this.categoryName = categoryName;
	}
	
}
