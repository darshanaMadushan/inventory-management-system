package com.rcs2.inventory.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rcs2.inventory.model.Product;
import com.rcs2.inventory.model.ProductType;
import com.rcs2.inventory.model.Status;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>{

	Optional<Product> findBySerialNumber(String serial);

	Optional<Product> findByProductName(String name);
	
	List<Product> findByType(ProductType type);
	
	List<Product> findByStatus(Status status);
	
	List<Product> findByAssembledTo(Product assembledTo);
	
	@Query("FROM Product WHERE productName = ?1")
	List<Product> findAllByProductName(String name);
	
	@Transactional
	@Modifying
	@Query("UPDATE Product SET status = ?1 WHERE serialNumber = ?2")
	void updateStatus(Status status, String serial);
	
	@Transactional
	@Modifying
	@Query("UPDATE Product SET assembledTo = ?1, status = ?2 WHERE serialNumber = ?3")
	void AssembleIntoProduct(Product product, Status status, String serialNumber);
	
	@Transactional
	@Modifying
	@Query("UPDATE Product SET assembledTo = NULL, status = ?1 WHERE serialNumber = ?2")
	void removeAssembledProduct(Status status, String serial);
	
	
	@Transactional
	@Modifying
	@Query("UPDATE Product SET status = ?1, replacedWith = ?2, isReplaced = TRUE WHERE serialNumber = ?3")
	void repairAndReplaceProduct(Status status, Product replaceWith, String serial);
	
	@Query("FROM Product WHERE serialNumber LIKE ?1 || '%' ORDER BY serialNumber")
	List<Product> findBySerialNumberInput(String input);
	
}
