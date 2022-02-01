package com.rcs2.inventory.repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.rcs2.inventory.model.ProductType;

public interface ProductTypeRepository extends JpaRepository<ProductType, String>{
	
	Optional<ProductType> findByCategoryNameEquals(String name);
	
	Optional<ProductType> findBySerialEquals(String serial);
	
	List<ProductType> findAllByOrderByCategoryNameAsc();
	
	@Transactional
	@Modifying
	@Query("UPDATE ProductType SET categoryName = ?1, created = ?2 WHERE serial = ?3")
	void updateProductType(String cateogryName, Date created, String existSerial);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM ProductType WHERE serial = ?1")
	void removeProductType(String serial);
	
}