package com.rcs2.inventory.model;

import java.sql.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="project")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project {

	public enum Status{SENT_TO_APPROVE, SENT_TO_STORE, SENT_TO_WORKSHOP, IN_PROGRESS, SENT_TO_REVIEW, ON_HOLD, COMPLETED, APPROVAL_REJECTED, REVIEW_REJECTED}
	
	public enum Priority{HIGH, MEDIUM, LOW, URGENT}
	
	@Id
	@Column(name="project_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer projectId;
	
	private String name;
	
	private String description;
	
	private Integer quantity;
	
	private Date dueDate;
	
	private Status status;
	
	private boolean isAvailable;
	
	private Priority priority;
	
	private String rejectionReason;
	
	private Date rejectedDate;
	
	private Date lastUpdatedDate;
	
	@JsonIgnore
	@ManyToOne
	private User user;
	
	@JsonIgnore
	@OneToMany(mappedBy = "project")
	private Set<Product> product;

	public Project(String name, String description, Integer quantity, Date dueDate, Status status, boolean isAvailable, Priority priority, Date updatedDate) {
		super();
		this.name = name;
		this.description = description;
		this.quantity = quantity;
		this.dueDate = dueDate;
		this.status = status;
		this.isAvailable = isAvailable;
		this.priority = priority;
		this.lastUpdatedDate = updatedDate;
	}
	
}