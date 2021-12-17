package com.rcs2.inventory.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rcs2.inventory.controller.dto.NewProductDTO;
import com.rcs2.inventory.model.Product;
import com.rcs2.inventory.model.ProductType;
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
		String serialNumber = dto.getProductType().getSerial().concat("-").concat(dto.getId());
		Product product = new Product(serialNumber, dto.getProductName(), dto.getDescription(), Status.IN_INVENTORY, dto.getProductType());
		return productRepository.save(product);
	}

	@Override
	public List<Product> getAllProductsNameAndId() {
		return productRepository.findAll();
	}

	@Override
	public Optional<Product> getProductByName(String name) {
		return productRepository.findByProductName(name);
	}

	@Override
	public Optional<Product> getProductBySerialNumber(String serial) {
		return productRepository.findBySerialNumber(serial);
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
	public void updateStatus(Optional<Product> product, Status status) {
		productRepository.updateStatus(status, product.get().getSerialNumber());
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
		return productRepository.findAllByProductName(name);
	}

	@Override
	public String replaceAndRepairProduct(Product assembledTo, String serial) {
		Optional<Product> replacingProduct = productRepository.findBySerialNumber(serial);
		if(replacingProduct.isPresent()) {
			Product mainProduct = replacingProduct.get().getAssembledTo();
			productRepository.repairAndReplaceProduct(Status.REPLACED, assembledTo, serial);
			productRepository.AssembleIntoProduct(mainProduct, Status.ALLOCATED, assembledTo.getSerialNumber());
			return mainProduct.getSerialNumber();
		}
		return "";
	}

	@Override
	public List<Product> getAllProductDetailsInCustomInput(String input) {
		return productRepository.findBySerialNumberInput(input);
	}

	@Override
	public void removeAssembledProduct(String serial) {
		productRepository.removeAssembledProduct(Status.ON_ASSEMBLE, serial);
	}
	
}
