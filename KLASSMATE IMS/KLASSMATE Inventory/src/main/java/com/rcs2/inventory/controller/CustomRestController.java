package com.rcs2.inventory.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rcs2.inventory.controller.dto.ProductBehavior;
import com.rcs2.inventory.controller.dto.ProductCountDTO;
import com.rcs2.inventory.controller.dto.ProductCountTypeAndStatus;
//import com.rcs2.inventory.controller.dto.ProductCountTypeAndStatus;
import com.rcs2.inventory.controller.dto.ProjectDetailsDTO;
import com.rcs2.inventory.controller.dto.UserRegistrationDTO;
import com.rcs2.inventory.model.Product;
import com.rcs2.inventory.model.ProductType;
import com.rcs2.inventory.model.Project;
import com.rcs2.inventory.model.Status;
import com.rcs2.inventory.model.User;
import com.rcs2.inventory.model.User.Role;
import com.rcs2.inventory.service.ProductService;
import com.rcs2.inventory.service.ProductTypeService;
import com.rcs2.inventory.service.ProjectService;
import com.rcs2.inventory.service.UserService;


@RestController
@RequestMapping("/api")
public class CustomRestController {

	Logger LOGGER = LoggerFactory.getLogger(CustomRestController.class);
	
	@Autowired
	ProductService productService;
	
	@Autowired
	UserService userService; 
	
	@Autowired
	ProjectService projectService;
	
	@Autowired
	ProductTypeService productTypeService;
	
	@PutMapping("/allocateProduct")
	public ResponseEntity<Product> allocateProduct(@RequestParam("assembleToSerial")String assembleToSerial ,@RequestParam("assemble") String productSerial){
		Optional<Product> product = productService.getProductBySerialNumber(productSerial);
		if(product.isPresent()) {
			if(product.get().getStatus() == Status.ON_ASSEMBLE) {
				Optional<Product> assembleTo = productService.getProductBySerialNumber(assembleToSerial);
				if(assembleTo.isPresent()) {
					productService.assembleToProduct(assembleTo, Status.ALLOCATED, product);
					LOGGER.info("Success assemble : " + productSerial + " to " + assembleToSerial);
					return new ResponseEntity<Product>(product.get(), HttpStatus.CREATED);				
				}
				LOGGER.warn("Assembling product not found.");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);				
			} else {
				LOGGER.warn(product.get().getSerialNumber() + " is not in the workshop.");
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		} else { 
			LOGGER.error("Process cannot be done  : " + productSerial);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@GetMapping("/allocateToProject")
	public ResponseEntity<Product> allocateToProject(@RequestParam("project") int projectId, @RequestParam("serial")String serial) {
		try {
			Optional<Project> project = projectService.getProjectById(projectId);
			Optional<Product> product = productService.getProductBySerialNumber(serial);
			if(project.isPresent()) {
				if(product.get().getProject() == null) {
					productService.allocateToProject(project.get(), serial);
					LOGGER.info("Product : " + serial + " Allocated to Project: " + project.get().getName());
					return new ResponseEntity<Product>(productService.getProductBySerialNumber(serial).get(), HttpStatus.CREATED);					
				} else { 
					LOGGER.warn(serial + " product is allocated to the project " + product.get().getProject().getName() + ". Cant' allocate to another project");
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			} else {
				LOGGER.warn("Project not found for projectId: " + projectId);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/approveProject")
	public ResponseEntity<String> approveProject(@RequestParam("projectId")int id) {
		try {
			Optional<Project> project = projectService.getProjectById(id);
			if(project.isPresent()) {
				projectService.updateProjectStatus(com.rcs2.inventory.model.Project.Status.SENT_TO_STORE, id);
				return new ResponseEntity<>(HttpStatus.OK);			
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
 		} catch(Exception e) {
 			LOGGER.error(e.getMessage());
 			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
 		}
	}
	
	@GetMapping("/checkProjectName")
	public ResponseEntity<String> checkProjectName(@RequestParam("name")String name) {
		try {
			Optional<Project> project = projectService.getProjectInName(name);
			if(project.isPresent()) {
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}			
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/checkUsername")
	public ResponseEntity<String> checkUsername(@RequestParam("username")String username){
		try {
			Optional<User> user = userService.getDetailsByUsername(username);
			if(user.isPresent()) {
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		}catch(Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/createUser")
	public ResponseEntity<String> createAdmin(@RequestBody UserRegistrationDTO user){
		try {
			userService.createAdmin(user);
			return new ResponseEntity<>(HttpStatus.CREATED);
			
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/deleteUser")
	public ResponseEntity<String> deleteUser (@RequestParam("userId") int userId) {
		try {
			Optional<User> user = userService.getDetailsById(userId);
			if(user.isPresent()) {
				userService.deleteUser(user.get());
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/finishAssemble")
	public ResponseEntity<String> finishAssemble(@RequestParam("projectId") int projectId) {
		try {
			Optional<Project> project = projectService.getProjectById(projectId);
			if(project.isPresent()) {
				projectService.updateProjectStatus(com.rcs2.inventory.model.Project.Status.COMPLETED, project.get().getProjectId());
				LOGGER.info("Finished the process of the Project: {}", project.get().getName());
				List<Product> products = productService.getProductsByProject(project.get());
				for(Product product : products) {
					productService.updateStatus(product, Status.FINISHED);
				}
				LOGGER.info("All Products Related to the Project {} was finished.", project.get().getName());
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				LOGGER.error("Project finishing failure : " + projectId + " not found.");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);				
			}
		}catch(Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getAssembled/{serial}")
	public ResponseEntity<List<Product>> getAssembledProducts(@PathVariable("serial") String serialNumber) {
		Optional<Product> mainProduct = productService.getProductBySerialNumber(serialNumber);
		try {
			if(mainProduct.isPresent()) {																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																								
				List<Product> listOfAssembled = productService.getProductsByAssembleTo(mainProduct.get());
				if(!listOfAssembled.isEmpty()) {
					return new ResponseEntity<List<Product>>(listOfAssembled, HttpStatus.OK);
				} else {
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				}
				
			} else {
				LOGGER.error("Wrong input : " + serialNumber );
				return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
			}
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getCategoryProductCount")
	public ResponseEntity<List<ProductCountDTO>> getProductsCountOnCategory(){
		try {
			return new ResponseEntity<List<ProductCountDTO>>(productService.getProductCountByCategory(), HttpStatus.OK);
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getCountByTypeAndStatus")
	public ResponseEntity<List<ProductCountTypeAndStatus>> getCountByTypeAndStatus(@RequestParam("status") int status){
		try {
			switch(status) {
				case 0:
					return new ResponseEntity<List<ProductCountTypeAndStatus>>(productService.getCountByTypeAndStatus(Status.IN_INVENTORY), HttpStatus.OK);
				case 1: 
					return new ResponseEntity<List<ProductCountTypeAndStatus>>(productService.getCountByTypeAndStatus(Status.ON_ASSEMBLE), HttpStatus.OK);
				case 2: 
					return new ResponseEntity<List<ProductCountTypeAndStatus>>(productService.getCountByTypeAndStatus(Status.ALLOCATED), HttpStatus.OK);
				case 5: 
					return new ResponseEntity<List<ProductCountTypeAndStatus>>(productService.getCountByTypeAndStatus(Status.FINISHED), HttpStatus.OK);
				case 6: 
					return new ResponseEntity<List<ProductCountTypeAndStatus>>(productService.getCountByTypeAndStatus(Status.REPLACED), HttpStatus.OK);
				default:
					return new ResponseEntity<List<ProductCountTypeAndStatus>>(HttpStatus.BAD_REQUEST);					
			}
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<List<ProductCountTypeAndStatus>>(HttpStatus.INTERNAL_SERVER_ERROR);			
		}
	}
	
	@GetMapping("/getDetails")
	public ResponseEntity<List<Product>> getDetailsInSearch(@RequestParam("input")String input){
		try {
			List<Product> results = productService.getAllProductDetailsInCustomInput(input);
			if(results.isEmpty()) {				
				LOGGER.info("Product not found for request " + input);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			} else {
				return new ResponseEntity<List<Product>>(results, HttpStatus.OK);
			}
			
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/getProduct/{serial}", method=RequestMethod.GET)
	public ResponseEntity<Product>  getAllRelatedProductRequest(@PathVariable("serial") String serial) {		
		try {
			Optional<Product> product = productService.getProductBySerialNumber(serial);
			if(product.isPresent()) {
				return new ResponseEntity<Product>(product.get(), HttpStatus.OK);
				
			} else {
				LOGGER.warn("Not found the product on serial number: " + serial );
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	

	@GetMapping("/getProductsByStatusAndType")
	public ResponseEntity<List<Product>> getProductsByStatusAndType(@RequestParam("status")int status, @RequestParam("type") String type ) {
		try {
			Optional<ProductType> productType = productTypeService.getProductTypeById(type);
			if(productType.isPresent()) {
				switch(status) {
				case 0: 
					return new ResponseEntity<List<Product>>(productService.getProductsByStatusAndType(Status.IN_INVENTORY, productType.get()), HttpStatus.OK);
				case 1: 
					return new ResponseEntity<List<Product>>(productService.getProductsByStatusAndType(Status.ON_ASSEMBLE, productType.get()), HttpStatus.OK);
				case 2 : 
					return new ResponseEntity<List<Product>>(productService.getProductsByStatusAndType(Status.ALLOCATED, productType.get()), HttpStatus.OK);
				case 5: 
					return new ResponseEntity<List<Product>>(productService.getProductsByStatusAndType(Status.FINISHED, productType.get()), HttpStatus.OK);
				default: 
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getProductTypes")
	public ResponseEntity<List<ProductType>> getProductTypes(){
		try {
			return new ResponseEntity<List<ProductType>>(productTypeService.getAllTypes(), HttpStatus.OK);
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getProductType")
	public ResponseEntity<ProductType> getProductType(@RequestParam("id") String id) {
		try {
			Optional<ProductType> type = productTypeService.getProductTypeById(id);
			if(type.isPresent()) {
				return new ResponseEntity<ProductType>(type.get(), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getProjectDetails")
	public ResponseEntity<ProjectDetailsDTO> getProjectDetails(@RequestParam("id") int id) {
		try {
			Optional<Project> project = projectService.getProjectById(id);
			if(project.isPresent()) {
					List<Product> productList =  productService.getProductsByProject(project.get());
					projectService.updateProjectStatus(com.rcs2.inventory.model.Project.Status.IN_PROGRESS, project.get().getProjectId());
					LOGGER.info("Changed the status of project " + project.get().getName() + " from " + project.get().getStatus() + " to IN_PROGRESS");
					if(productList.isEmpty()) {
						LOGGER.warn("No Products Assembled for the Project: " + project.get().getName());
					}
					ProjectDetailsDTO details = new ProjectDetailsDTO(
							project.get().getProjectId(),
							project.get().getName(), 
							project.get().getDescription(),
							project.get().getQuantity(), 
							project.get().getDueDate(), 
							project.get().getStatus(), 
							project.get().getPriority(), 
							productList
							);
					return new ResponseEntity<ProjectDetailsDTO>(details, HttpStatus.OK);
			}else {
				LOGGER.warn("No any projects at the Project Id: " + id);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getProject")
	public ResponseEntity<ProjectDetailsDTO> getProject(@RequestParam("id") int id) {
		try {
			Optional<Project> project = projectService.getProjectById(id);
			if(project.isPresent()) {
				List<Product> productList =  productService.getProductsByProject(project.get());
				if(productList.isEmpty()) {
					LOGGER.warn("No Products Assembled for the Project: " + project.get().getName());
				}
				ProjectDetailsDTO details = new ProjectDetailsDTO(
						project.get().getProjectId(), 
						project.get().getName(), 
						project.get().getDescription(), 
						project.get().getQuantity(), 
						project.get().getDueDate(), 
						project.get().getStatus(),
						project.get().getPriority(), 
						project.get().getRejectionReason(),
						productList
						);
				return new ResponseEntity<ProjectDetailsDTO>(details, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getRoleCount")
	public ResponseEntity<List<User>> getRoleWiseCount() {
		return new ResponseEntity<List<User>>(userService.getRoleWiseCount(), HttpStatus.OK);
	}

	@GetMapping("/getStatusProductCount")
	public ResponseEntity<List<ProductCountDTO>> getStatusProductCount () {
		try {
			return new ResponseEntity<List<ProductCountDTO>>(productService.getProductsCountByStatus(), HttpStatus.OK);			
		} catch(Exception e) { 
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/productBehavior")
	public ResponseEntity<List<ProductBehavior>> getproductBehavior(@RequestParam("status") int status) {
		try {
			switch(status) {
				case 0 :
					return new ResponseEntity<List<ProductBehavior>>(productService.getProductBehaviorByStatus(Status.IN_INVENTORY), HttpStatus.OK);
				case 1 :
					List<ProductBehavior> behavior1 = productService.getProductBehaviorByStatus(Status.ON_ASSEMBLE);
					return new ResponseEntity<List<ProductBehavior>>(behavior1, HttpStatus.OK);
				case 2:
					List<ProductBehavior> behavior2 = productService.getProductBehaviorByStatus(Status.ALLOCATED);
					return new ResponseEntity<List<ProductBehavior>>(behavior2, HttpStatus.OK);
				case 3 :
					List<ProductBehavior> behavior3 = productService.getProductBehaviorByStatus(Status.SENT_TO_REVIEW);
					return new ResponseEntity<List<ProductBehavior>>(behavior3, HttpStatus.OK);
				case 4 :
					List<ProductBehavior> behavior4 = productService.getProductBehaviorByStatus(Status.REVIEW_REJECTED);
					return new ResponseEntity<List<ProductBehavior>>(behavior4, HttpStatus.OK);
				case 5 :
					List<ProductBehavior> behavior5 = productService.getProductBehaviorByStatus(Status.FINISHED);
					return new ResponseEntity<List<ProductBehavior>>(behavior5, HttpStatus.OK);
				case 6 :
					List<ProductBehavior> behavior6 = productService.getProductBehaviorByStatus(Status.REPLACED);
					return new ResponseEntity<List<ProductBehavior>>(behavior6, HttpStatus.OK);
			}
			LOGGER.warn("Status: " + status + " not found");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/projectStats")
	public ResponseEntity<List<ProductCountDTO>> getProjectStatistics() {
		try {
			List<ProductCountDTO> list = projectService.getProjectStatistics();
			return new ResponseEntity<List<ProductCountDTO>>(list, HttpStatus.OK);
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/rejectProject")
	public ResponseEntity<String> rejectProject(@RequestParam("projectId")int projectId, @RequestParam("reason")String reason, @RequestParam("username") String username){
		try {
			Optional<Project> project = projectService.getProjectById(projectId);
			Optional<User> user = userService.getDetailsByUsername(username);
			if(project.isPresent() && user.isPresent()) {
				projectService.rejectProjectApproval(reason, projectId, user.get());
				LOGGER.info("Project " + project.get().getName() + " has been rejected due to " + reason + " by the user " + username );
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				LOGGER.warn("Editing user or Project is not found");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/removeProduct")
	public ResponseEntity<String> removeProductFromMainProduct(@RequestParam("remove") String removingSerial, @RequestParam("from")String fromSerial) {
		try {
			Optional<Product> removingProduct = productService.getProductBySerialNumber(removingSerial);
			
			if(removingProduct.isPresent()) {
				if(removingProduct.get().getStatus() == Status.ALLOCATED && removingProduct.get().getAssembledTo().getSerialNumber().equals(fromSerial) ) {
					productService.removeAssembledProduct(removingProduct.get().getSerialNumber());
					LOGGER.info("Removed " + removingSerial + " product from " + fromSerial);
					return new ResponseEntity<>(HttpStatus.OK);
				} else {
					LOGGER.warn("Removing Product is not assembled to the Main product: " + fromSerial);
					return new ResponseEntity<String>("Removing product is not assembled to this product", HttpStatus.BAD_REQUEST);
				}
			} else {
				LOGGER.warn("Removing Product Not Found!");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/removeProductType")
	public ResponseEntity<String> removeProductType (@RequestParam("typeSerial") String serial) {
		try {
			Optional<ProductType> category = productTypeService.getProductTypeById(serial);
			if(category.isPresent()) {
				List<Product> products = productService.getAllProductsInType(category.get());
				if(products.size() == 0) {
					LOGGER.info("{} Product type removed", category.get().getSerial());
					productTypeService.removeProductType(category.get().getSerial());
				} else {
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return null;
	}
	
	@PutMapping("/removeFromProject")
	public ResponseEntity<String> removeFromProject(@RequestParam("productSerial") String serial, @RequestParam("projectId") int projectId) {
		try {
			Optional<Product> product = productService.getProductBySerialNumber(serial);
			if(product.isPresent()) {
				if(product.get().getProject().getProjectId() == projectId) {
					productService.removeFromProject(product.get().getSerialNumber());
					LOGGER.info("{} was removed from the project {}", product.get().getSerialNumber(), product.get().getProject().getName());
					return new ResponseEntity<>(HttpStatus.OK);
				} else {
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			LOGGER.info(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/removeProject")
	public ResponseEntity<String> removeProject(@RequestParam("id") String id){
		try {
			int intId = Integer.parseInt(id);
			Optional<Project> project = projectService.getProjectById(intId);
			if(project.isPresent()) {
				projectService.removeProject(intId);
				LOGGER.info("Removed Project: " + project.get().getName());
				return new ResponseEntity<String>(project.get().getName(), HttpStatus.CREATED);
			} else {
				LOGGER.warn("Removing Project ID is not available in the server");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/replaceProduct")
	public ResponseEntity<Product> replaceProduct(@RequestParam("replacedBy")String replacedBy, @RequestParam("removed")String removedSerial) {
		try {
			Optional<Product>replacedByProduct = productService.getProductBySerialNumber(replacedBy);
			if(replacedByProduct.isPresent()) {
				if(replacedByProduct.get().getStatus() == Status.ON_ASSEMBLE) {
					Optional<Product> removingProduct = productService.getProductBySerialNumber(removedSerial);
					if(removingProduct.isPresent()) {
						productService.replaceAndRepairProduct(replacedByProduct.get(), removedSerial);
						LOGGER.info("Successfully replaced: " + removedSerial + " with " + replacedBy);
						return new ResponseEntity<Product>(replacedByProduct.get(), HttpStatus.CREATED);
					} else {
						LOGGER.info("Removing product not found: " + removedSerial);
						return new ResponseEntity<>(HttpStatus.NOT_FOUND);
					}
				} else {
					LOGGER.error(replacedBy + " is not in the workshop");
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			} else {
				LOGGER.error(replacedBy + " Product not found");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/revertReview")
	public ResponseEntity<String> revertReviewProject(@RequestParam("projectId")int projectId, @RequestParam("reason")String reason, @RequestParam("username") String username){
		try {
			Optional<Project> project = projectService.getProjectById(projectId);
			Optional<User> user = userService.getDetailsByUsername(username);
			if(project.isPresent() && user.isPresent()) {
				projectService.revertProjectReview(reason, projectId, user.get());
				List<Product> products = productService.getProductsByProject(project.get());
				for(Product product : products) {
					productService.updateStatus(product, Status.REVIEW_REJECTED);
					LOGGER.info("{} status has been changed to REVIEW_REJECTED", product.getSerialNumber());
				}
				LOGGER.info("Project " + project.get().getName() + " has been rejected due to " + reason + " by the user " + username );
				return new ResponseEntity<String>(HttpStatus.OK);
			} else {
				LOGGER.warn("Editing user or Project is not found");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
		
	@GetMapping("/sendToReview")
	public ResponseEntity<String> sendToReview(@RequestParam("projectId") int projectId) {
		try {
			Optional<Project> project = projectService.getProjectById(projectId);
			if(project.isPresent()) {
				List<Product> products = productService.getProductsByProject(project.get());
				for(Product p : products) {
					Optional<Product> product = productService.getProductBySerialNumber(p.getSerialNumber());
					if(product.isPresent()) {
						productService.updateStatus(product.get(), Status.SENT_TO_REVIEW);
						LOGGER.info("Product sent to review: " + product.get().getSerialNumber());						
					} else {
						LOGGER.error("No Product found for serial: " + p.getSerialNumber());
					}
				}
				projectService.updateProjectStatus(com.rcs2.inventory.model.Project.Status.SENT_TO_REVIEW, projectId);
				return new ResponseEntity<String>("Project Sent to Review Success",HttpStatus.CREATED);
			}
			LOGGER.error("Project sending to review failure : Not found a project on " + projectId);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);			
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/sendToWorkshopProduct")
	public ResponseEntity<String> sendToWorkshop(@RequestParam("id") String serial) {
		try {
			Optional<Product> product = productService.getProductBySerialNumber(serial);
			if(product.isPresent()) {
				if(product.get().getStatus() == Status.IN_INVENTORY) {					
					productService.updateStatus(product.get(), Status.ON_ASSEMBLE);
					LOGGER.info("{} Product sent to the workshop.", product.get().getSerialNumber());
					return new ResponseEntity<String>(HttpStatus.OK);
				} else {
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			} else {
				LOGGER.warn("Product is not found for serial: " + serial);
				 return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch(Exception e)  {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/sendToWorkshopProject")
	public ResponseEntity<String> sendProjectToWorkshop(@RequestParam("projectName") String projectName) {
		try {
			Optional<Project> project = projectService.getProjectInName(projectName);
			if(project.isPresent()) {
				projectService.updateProjectStatus(com.rcs2.inventory.model.Project.Status.SENT_TO_WORKSHOP, project.get().getProjectId());
				LOGGER.info("Product allocations success and sent to the Workshop. Project: " + project.get().getName());
				return new ResponseEntity<String>("success", HttpStatus.CREATED);
			} else { 
				return new ResponseEntity<String>("Project not found.", HttpStatus.NOT_FOUND);
			}
		} catch(Exception e ) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/updateUser")
	public ResponseEntity<String> updateUser(@RequestParam("userId") String userId, @RequestBody UserRegistrationDTO dto){
		Integer intUserId = Integer.parseInt(userId);
		User userDetails = userService.getUserById(intUserId);
		if(userDetails != null) {
			if(dto.getPassword() == "") {
				dto.setPassword(userDetails.getPassword());
				userService.updateUser(intUserId, dto, false);
			} else {
				userService.updateUser(intUserId, dto, true);
				LOGGER.info(userDetails.getUsername() + " user account was updated with new password.");
				return new ResponseEntity<String>("password", HttpStatus.OK);
			}			
			LOGGER.info(userDetails.getUsername() + "user account was updated.");
			return new ResponseEntity<String>(HttpStatus.CREATED);
		} else {
			LOGGER.error("User details Not found for the user ID: " + userId);
			return new ResponseEntity<String>("Updating user Details Not found", HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping("/updateProductType")
	public ResponseEntity<String> updateProductType(@RequestParam("serial") String serial, @RequestParam("categoryName") String categoryName) {
		try {
			Optional<ProductType> oldType = productTypeService.getProductTypeById(serial);
			if(oldType.isPresent()) {
				productTypeService.updateProductType(categoryName, serial);
				return new ResponseEntity<>(HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/userCount")
	public ResponseEntity<Integer> getUserCount() {
		return new ResponseEntity<Integer>(userService.countUsersByRole(Role.ADMIN, "except"), HttpStatus.OK);
	}	
} 