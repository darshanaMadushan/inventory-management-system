package com.rcs2.inventory.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name="product_type")
public class ProductType {

	@Id
	private String serial; 
	
	private String categoryName;
	
	private String created;
	
	@JsonIgnore
	@OneToMany(mappedBy="type")
	private Set<Product> product;
	
}
