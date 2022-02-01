package com.rcs2.inventory.repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rcs2.inventory.controller.dto.ProductBehavior;
import com.rcs2.inventory.controller.dto.ProductCountDTO;
import com.rcs2.inventory.controller.dto.ProductCountTypeAndStatus;
import com.rcs2.inventory.model.Product;
import com.rcs2.inventory.model.ProductType;
import com.rcs2.inventory.model.Project;
import com.rcs2.inventory.model.Status;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>{

	Optional<Product> findBySerialNumberEquals(String serial);

	Optional<Product> findByProductNameEquals(String name);
	
	List<Product> findByProductName(String productName);
	
	List<Product> findByType(ProductType type);
	
	List<Product> findByStatus(Status status);
	
	List<Product> findByAssembledTo(Product assembledTo);
	
	List<Product> findByProject(Project project);
	
	List<Product>findByIsReplaced(boolean isReplaced);
	
	Long countByStatus(Status status);
	
	List<Product> findBySerialNumberIsContaining(String input);
	
	List<Product> findByStatusAndType(Status status, ProductType type);
	
	@Query("SELECT P.status as status, P.lastUpdate AS date, COUNT(P.serialNumber) AS count FROM Product P WHERE P.status = ?1 GROUP BY P.lastUpdate, P.status ORDER BY P.lastUpdate")
	List<ProductBehavior> productBehaviorCountsByStatus(Status status);
	
	@Query(value = "SELECT P.type_Serial as type, COUNT(P.serial_Number) as count FROM Product P GROUP BY P.type_serial", nativeQuery=true)
	List<ProductCountDTO> getProductCountByCategory();
	
	@Query(value="SELECT P.status as type, COUNT(P.serialNumber) as count FROM Product P GROUP BY P.status")
	List<ProductCountDTO> getProductCountByStatus();
	
	@Query("SELECT P.type AS TYPE, P.status AS STATUS, COUNT(P.serialNumber)AS COUNT FROM Product P GROUP BY P.type, P.status")
	List<ProductCountTypeAndStatus> getCountGroupByProductTypeAndStatus();
	
	@Query("SELECT P.type as type, P.status as status, COUNT(P.serialNumber) as count FROM Product P WHERE P.status = ?1 GROUP BY P.status, P.type")
	List<ProductCountTypeAndStatus> getCountByTypeAndStatus(Status status);
	
	@Transactional
	@Modifying
	@Query("UPDATE Product SET status = ?1, lastUpdate= ?2 WHERE serialNumber = ?3")
	void updateStatus(Status status, Date lastUpdate, String serial);
	
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

	@Transactional
	@Modifying
	@Query("UPDATE Product SET project = ?1 WHERE serialNumber = ?2")
	void allocateProductToProject(Project project, String serial);
	
	@Transactional
	@Modifying
	@Query("UPDATE Product SET project = null, status = ?1 WHERE serialNumber = ?2")
	void removeProject(Status status, String serial);
	
}
