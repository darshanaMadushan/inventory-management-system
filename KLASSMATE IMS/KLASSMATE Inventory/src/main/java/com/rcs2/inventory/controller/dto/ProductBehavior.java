package com.rcs2.inventory.controller.dto;

import java.sql.Date;

import com.rcs2.inventory.model.Status;

public interface ProductBehavior {

	Status getStatus();
	Date getDate();
	Long getCount();
}
