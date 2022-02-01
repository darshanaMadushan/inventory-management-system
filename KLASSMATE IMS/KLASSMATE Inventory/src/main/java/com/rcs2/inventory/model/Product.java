package com.rcs2.inventory.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "product")
public class Product {
	
	@Id
	@Column(name="serial_number")
	private String serialNumber;
	
	@Column(name = "product_name")
	private String productName;
	
	private String description; 
	
	private Status status;
	
	@Column(name="is_replaced", nullable=true)
	private boolean isReplaced = false;
	
	@Column(name="lastUpdated", nullable=true)
	private Date lastUpdate;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="assembled_to")
	private Product assembledTo;
	
	@JsonIgnore
	@OneToMany(mappedBy = "assembledTo")
	private List<Product> assembledProducts = new ArrayList<Product>();
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="replaced_with", nullable=true)
	private Product replacedWith;
	
	@JsonIgnore
	@OneToMany(mappedBy="replacedWith")
	private List<Product> replacedProducts = new ArrayList<Product>();
	
	@ManyToOne(cascade = CascadeType.ALL)
	private ProductType type;
	
	@ManyToOne(cascade= CascadeType.ALL)
	private Project project;
	
	public Product(String serialNumber, String productName, String description, Status status, ProductType type, Date date) {
		super();
		this.serialNumber = serialNumber;
		this.productName = productName;
		this.description = description;
		this.status = status;
		this.type = type;
		this.lastUpdate = date;
	}

	public Product(String serialNumber, Status status, Product assembledTo) {
		super();
		this.serialNumber = serialNumber;
		this.status = status;
		this.assembledTo = assembledTo;
	}

}