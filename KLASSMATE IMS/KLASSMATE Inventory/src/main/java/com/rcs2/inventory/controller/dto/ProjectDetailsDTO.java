package com.rcs2.inventory.controller.dto;

import java.sql.Date;
import java.util.List;

import com.rcs2.inventory.model.Product;
import com.rcs2.inventory.model.Project.Priority;
import com.rcs2.inventory.model.Project.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDetailsDTO {

	private Integer id;
	private String name; 
	private String description; 
	private Integer quantity; 
	private Date dueDate;
	private Status status;
	private Priority priority;
	private String rejectionReason;
	private List<Product> products;
	
	public ProjectDetailsDTO(Integer id, String name, String description, Integer quantity, Date dueDate, Status status,
			Priority priority) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.quantity = quantity;
		this.dueDate = dueDate;
		this.status = status;
		this.priority = priority;
	}

	public ProjectDetailsDTO(Integer projectId, String name2, String description2, Integer quantity2, Date dueDate2,
			Status status2, Priority priority2, List<Product> productList) {
		this.id = projectId;
		this.name = name2;
		this.description = description2;
		this.quantity = quantity2;
		this.dueDate = dueDate2;
		this.status = status2;
		this.priority = priority2;
		this.products = productList;
	}
}
