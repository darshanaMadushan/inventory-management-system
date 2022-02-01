package com.rcs2.inventory.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rcs2.inventory.controller.dto.NewProductDTO;
import com.rcs2.inventory.controller.dto.ProductBehavior;
import com.rcs2.inventory.controller.dto.ProductCountDTO;
import com.rcs2.inventory.controller.dto.ProductCountTypeAndStatus;
import com.rcs2.inventory.model.Product;
import com.rcs2.inventory.model.ProductType;
import com.rcs2.inventory.model.Project;
import com.rcs2.inventory.model.Status;
import com.rcs2.inventory.repository.ProductRepository;
import com.rcs2.inventory.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository; 
	
	public ProductServiceImpl(ProductRepository repo) {
		super();
		this.productRepository = repo;
	}
	
	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public Product saveProduct(NewProductDTO dto) {
		java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
		Product product = new Product(dto.getId(), dto.getProductName(), dto.getDescription(), Status.IN_INVENTORY, dto.getProductType(), date);
		return productRepository.save(product);
	}

	@Override
	public List<Product> getAllProductsNameAndId() {
		return productRepository.findAll();
	}

	@Override
	public Optional<Product> getProductByName(String name) {
		return productRepository.findByProductNameEquals(name);
	}

	@Override
	public Optional<Product> getProductBySerialNumber(String serial) {
		return productRepository.findBySerialNumberEquals(serial);
	}

	@Override
	public List<Product> getAllProductsInType(ProductType type) {
		return productRepository.findByType(type);
	}

	@Override
	public List<Product> getAllProductsByStatus(Status status) {
		return productRepository.findByStatus(status);
	}

	@Override
	public void updateStatus(Product product, Status status) {
		java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
		productRepository.updateStatus(status, date, product.getSerialNumber());
	}

	@Override
	public void assembleToProduct(Optional<Product> assembleTo, Status status, Optional<Product> assembled) {
		productRepository.AssembleIntoProduct(assembleTo.get(), status, assembled.get().getSerialNumber());
	}

	@Override
	public List<Product> getProductsByAssembleTo(Product product) {
		return productRepository.findByAssembledTo(product);
	}

	@Override
	public List<Product> getAllProductsByProductName(String name) {
		return productRepository.findByProductName(name);
	}

	@Override
	public void replaceAndRepairProduct(Product assembledTo, String serial) {
		Optional<Product> replacingProduct = productRepository.findBySerialNumberEquals(serial);
		if(replacingProduct.isPresent()) {
			Product mainProduct = replacingProduct.get().getAssembledTo();
			productRepository.repairAndReplaceProduct(Status.REPLACED, assembledTo, serial);		//removing existing product and add new product
			productRepository.AssembleIntoProduct(mainProduct, Status.ALLOCATED, assembledTo.getSerialNumber()); //assemble new product
		}
	}

	@Override
	public List<Product> getAllProductDetailsInCustomInput(String input) {
		return productRepository.findBySerialNumberIsContaining(input);
	}

	@Override
	public void removeAssembledProduct(String serial) {
		productRepository.removeAssembledProduct(Status.ON_ASSEMBLE, serial);
	}

	@Override
	public Long countProductsByStatus(Status status) {
		return productRepository.countByStatus(status);
	}

	@Override
	public List<ProductCountDTO> getProductCountByCategory() {
		return productRepository.getProductCountByCategory();
	}

	@Override
	public List<ProductCountDTO> getProductsCountByStatus() {
		return productRepository.getProductCountByStatus();
	}

	@Override
	public List<ProductBehavior> getProductBehaviorByStatus(Status status) {
		System.out.print(productRepository.productBehaviorCountsByStatus(status).toString());
		return productRepository.productBehaviorCountsByStatus(status);
	}

	@Override
	public List<Product> getProductsByIsReplaced(boolean isReplaced) {
		return productRepository.findByIsReplaced(isReplaced);
	}

	@Override
	public List<Product> getProductsByProject(Project project) {
		return productRepository.findByProject(project);
	}

	@Override
	public void allocateToProject(Project project, String serial) {
		productRepository.allocateProductToProject(project, serial);
	}

	@Override
	public List<ProductCountTypeAndStatus> getCountTypeAndStatus() {
		return productRepository.getCountGroupByProductTypeAndStatus();
	}

	@Override
	public void removeFromProject(String serial) {
		productRepository.removeProject(Status.IN_INVENTORY, serial);
	}

	@Override
	public List<ProductCountTypeAndStatus> getCountByTypeAndStatus(Status status) {
		return productRepository.getCountByTypeAndStatus(status);
	}

	@Override
	public List<Product> getProductsByStatusAndType(Status status, ProductType type) {
		return productRepository.findByStatusAndType(status, type);
	}
}
