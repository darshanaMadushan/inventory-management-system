package com.rcs2.inventory.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rcs2.inventory.model.Product;
import com.rcs2.inventory.model.Status;
import com.rcs2.inventory.service.ProductService;


@RestController
@RequestMapping("/api")
public class CustomRestController {

	Logger LOGGER = LoggerFactory.getLogger(CustomRestController.class);
	
	@Autowired
	ProductService productService;
	
	@RequestMapping(value = "/getProduct/{serial}", method=RequestMethod.GET)
	public ResponseEntity<Product>  getAllRelatedProductRequest(@PathVariable("serial") String serial) {
		
		try {
			LOGGER.info("Trying to search product : " + serial);
			Optional<Product> product = productService.getProductBySerialNumber(serial);
			if(product.isPresent()) {
				LOGGER.info("Product Found: " + serial);
				return new ResponseEntity<>(product.get(), HttpStatus.OK);
				
			} else {
				LOGGER.warn("Not found the product on serial number: " + serial);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				
			}
			
		} catch(Exception e) {
			LOGGER.error(e.toString());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getAssembled/{serial}")
	public ResponseEntity<List<Product>> getAssembledProducts(@PathVariable("serial") String serialNumber) {
		Optional<Product> mainProduct = productService.getProductBySerialNumber(serialNumber);
		try {
			LOGGER.info("Search for assembled products : " + serialNumber);
			if(mainProduct.isPresent()) {
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																			
				List<Product> listOfAssembled = productService.getProductsByAssembleTo(mainProduct.get());
				
				if(!listOfAssembled.isEmpty()) {
					LOGGER.info("List found: " + serialNumber);
					return new ResponseEntity<List<Product>>(listOfAssembled, HttpStatus.OK);
				} else {
					LOGGER.info("No assembled products in : " + serialNumber);
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				}
				
			} else {
				LOGGER.error("Wrong input : " + serialNumber);
				return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
			}
			
		} catch (Exception e) {
			LOGGER.error(e.toString());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/allocateProduct")
	public ResponseEntity<String> allocateProduct(@RequestParam("assembleToSerial")String assembleToSerial ,@RequestParam("assemble") String productSerial){
		Optional<Product> product = productService.getProductBySerialNumber(productSerial);
		LOGGER.info("Request to assemble : " + productSerial);
		if(product.isPresent()) {
			if(product.get().getStatus() == Status.ON_ASSEMBLE) {
				Optional<Product> assembleTo = productService.getProductBySerialNumber(assembleToSerial);
				if(assembleTo.isPresent()) {
					productService.assembleToProduct(assembleTo, Status.ALLOCATED, product);
					LOGGER.info("Success assemble : " + productSerial + " to " + assembleToSerial);
					return new ResponseEntity<String>("Succcessfully Assembled", HttpStatus.CREATED);				
				}
				LOGGER.warn("Assembling product not found");
				return new ResponseEntity<String>("Not found AssembleToProduct", HttpStatus.NOT_FOUND);				
			} else {
				LOGGER.warn(product.get().getSerialNumber() + " is not in the workshop");
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}
		} else { 
			LOGGER.error("Process cannot be done  : " + productSerial);
			return new ResponseEntity<String>("No Product Found", HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@GetMapping("/finishAssemble")
	public ResponseEntity<String> finishAssemble(@RequestParam("serial") String serial) {
		LOGGER.info("Finish request: " + serial);
		Optional<Product> product = productService.getProductBySerialNumber(serial);
		if(product.isPresent()) {
			productService.updateStatus(product, Status.FINISHED);
			LOGGER.info("Finished Assemble : " + serial);
			return new ResponseEntity<String>("Product Assemble Finished",HttpStatus.CREATED);
		}
		LOGGER.error("Product finishing failure :" + serial + " not found");
		return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@GetMapping("/replaceProduct")
	public ResponseEntity<String> replaceProduct(@RequestParam("replacedBy")String replacedBy, @RequestParam("removed")String removedSerial) {
		LOGGER.info("replace request on " + removedSerial);
		Optional<Product>replacedByProduct = productService.getProductBySerialNumber(replacedBy);
		if(replacedByProduct.isPresent()) {
			if(replacedByProduct.get().getStatus() == Status.ON_ASSEMBLE) {
				Optional<Product> removingProduct = productService.getProductBySerialNumber(removedSerial);
				if(removingProduct.isPresent()) {
					String mainProductSerial = productService.replaceAndRepairProduct(replacedByProduct.get(), removedSerial);
					LOGGER.info("Successfully replaced: " + removedSerial + " with " + replacedBy);
					return new ResponseEntity<String>(mainProductSerial, HttpStatus.CREATED);
				} else {
					LOGGER.trace("Product not found: " + removedSerial);
					return new ResponseEntity<String>("Removing Product Not Found", HttpStatus.NOT_FOUND);
				}
			} else {
				LOGGER.error(replacedBy + " is not in the workshop");
				return new ResponseEntity<String>("Product is not in the WORKSHOP", HttpStatus.BAD_REQUEST);
			}
		} else {
			LOGGER.error(replacedBy + " Product not found");
			return new ResponseEntity<String>("Assembling Product is Not Found", HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@GetMapping("/getDetails")
	public ResponseEntity<List<Product>> getDetailsInSearch(@RequestParam("input")String input){
		try {
			LOGGER.info("Product Search request : " + input);
			List<Product> results = productService.getAllProductDetailsInCustomInput(input);
			if(results.isEmpty()) {				
				LOGGER.trace("Product not found " + input);
				return new ResponseEntity<List<Product>>(HttpStatus.NOT_FOUND);
			} else {
				LOGGER.info("Total Requested product :" + results.size());
				return new ResponseEntity<List<Product>>(results, HttpStatus.OK);
			}
			
		} catch(Exception e) {
			LOGGER.error(e.toString());
			return new ResponseEntity<List<Product>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/removeProduct")
	public ResponseEntity<String> removeProductFromMainProduct(@RequestParam("remove") String removingSerial, @RequestParam("from")String fromSerial) {
		try {
			LOGGER.info("Request to remove product: " + removingSerial + " from " + fromSerial);
			Optional<Product> removingProduct = productService.getProductBySerialNumber(removingSerial);
			
			if(removingProduct.isPresent()) {
				if(removingProduct.get().getStatus() == Status.ALLOCATED && removingProduct.get().getAssembledTo().getSerialNumber().equals(fromSerial) ) {
					productService.removeAssembledProduct(removingProduct.get().getSerialNumber());
					LOGGER.info("Removed " + removingSerial + " product from " + fromSerial);
					return new ResponseEntity<String>(HttpStatus.OK);
				} else {
					LOGGER.warn("Removing Product is not assembled to the Main product: " + fromSerial);
					return new ResponseEntity<String>("Removing product is not assembled to this product", HttpStatus.BAD_REQUEST);
				}
			} else {
				LOGGER.warn("Removing Product Not Found!");
				return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
			}
		} catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}