package com.rcs2.inventory.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.rcs2.inventory.controller.dto.NewProductDTO;
import com.rcs2.inventory.controller.dto.ProductAssembleDTO;
import com.rcs2.inventory.model.ProductType;
import com.rcs2.inventory.model.Project;
import com.rcs2.inventory.model.Project.Priority;
import com.rcs2.inventory.model.Status;
import com.rcs2.inventory.model.User.Role;
import com.rcs2.inventory.service.ProductService;
import com.rcs2.inventory.service.ProductTypeService;
import com.rcs2.inventory.service.ProjectService;
import com.rcs2.inventory.service.UserService;

@Controller
public class WebController {
	
	Logger LOGGER = LoggerFactory.getLogger(WebController.class);
	
	@Autowired
	ProductService productService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	ProductTypeService productTypeService;
	
	@Autowired
	ProjectService projectService;
	
	@GetMapping("/access-denied")
	public String AccessDeniedPage(HttpServletRequest request, Principal principal) {
		LOGGER.info("Access Denied. For the Request " + request + " by " + principal.getName());
		return "error/403";
	}
	
	@GetMapping("/add-new-product")
	public String adminHome(Model model, Principal principal) {
		model.addAttribute("username", principal.getName());
		model.addAttribute("products", productService.getAllProducts());
		model.addAttribute("productTypes", productTypeService.getAllTypes());
		model.addAttribute("product", new NewProductDTO());
		return "/keeper/add-new-product";
	}
	
	@GetMapping("/admin-home")
	public String dashboardPage(Model model, Principal principal) {
		model.addAttribute("username", principal.getName());
		model.addAttribute("userCount", userService.countUsersByRole(Role.ADMIN, "except"));
		model.addAttribute("countProjects", projectService.countProjectsByPrioirty(Priority.HIGH));
		model.addAttribute("finishedProductCount", productService.countProductsByStatus(Status.FINISHED));
		model.addAttribute("categoriesCount", productTypeService.countProductTypes());
		model.addAttribute("hightPriorityProjects", projectService.getProjectsByPriority(Priority.HIGH));
		return "admin/admin-dashboard";
	}
	
	@GetMapping("/allocated-products")
	public String allocatedProductsPage(Model model, Principal principal) {
		model.addAttribute("allocatedProducts", productService.getAllProductsByStatus(Status.ALLOCATED));
		model.addAttribute("username", principal.getName());
		return "admin/allocated-products";
	}
	
	@GetMapping("/all-products")
	public String allProductsPage(Model model, Principal principal) {
		model.addAttribute("allProducts", productService.getAllProducts());
		model.addAttribute("username", principal.getName());
		model.addAttribute("replacedProducts", productService.getProductsByIsReplaced(true));
		return "admin/all-products";
	}
	
	@GetMapping("/all-users")
	public String allUsersPage(Model model, Principal principal) {
		model.addAttribute("username", principal.getName());
		model.addAttribute("userDetails", userService.getAllDetails());
		return "admin/all-users";
	}
	
	@GetMapping("/approve-projects")
	public String approveProjectsPage(Model model, Principal principal) {
		model.addAttribute("username", principal.getName());
		model.addAttribute("projects", projectService.getProjectsByStatus(com.rcs2.inventory.model.Project.Status.SENT_TO_APPROVE));
		model.addAttribute("urgentProject", projectService.getProjectByPriorityAndStatus(com.rcs2.inventory.model.Project.Status.SENT_TO_APPROVE, Priority.URGENT));
		return "workshop-admin/approve-project";
	}
	
	@GetMapping("/finished-products")
	public String finishedProductsPage(Model model, Principal principal) {
		model.addAttribute("finishedProducts", productService.getAllProductsByStatus(Status.FINISHED));
		model.addAttribute("username", principal.getName());
		model.addAttribute("categories", productTypeService.getAllTypes());
		return "admin/finished-products";
	}
	
	@GetMapping("/inventory")
	public String inventoryPage(Model model, Principal principal) {
		model.addAttribute("products", productService.getAllProductsByStatus(Status.IN_INVENTORY));
		model.addAttribute("username", principal.getName());
		model.addAttribute("categories", productTypeService.getAllTypes());
		return "admin/inventory";
	}
	
	@GetMapping("/keeper-home")
	public String keeperDashboard(Model model, Principal principal) {
		model.addAttribute("username", principal.getName());
		model.addAttribute("activeProducts", productService.countProductsByStatus(Status.IN_INVENTORY));
		model.addAttribute("projectsInInventory", projectService.getAllProjectsByStatus(com.rcs2.inventory.model.Project.Status.SENT_TO_STORE).size());
		model.addAttribute("urgentProject", projectService.getProjectByPriorityAndStatus(com.rcs2.inventory.model.Project.Status.SENT_TO_STORE, Priority.URGENT));
		return "keeper/keeper-dashboard";
	}
	
	@GetMapping("/product-assemble")
	public String productAssemblePage(Model model, Principal principal) {
		model.addAttribute("username", principal.getName());
		model.addAttribute("projects", projectService.getProjectsRecievedOrderByPriority());
		return "user/product-assemble";
	}
	
	@GetMapping("/product-repair")
	public String productRepairPage(Model model, Principal principal) {
		model.addAttribute("username", principal.getName());
		model.addAttribute("finishedProducts", productService.getAllProductsByStatus(Status.FINISHED));
		return "user/product-repair";
	}
	
	@GetMapping("/product-types")
	public String adminHomePage(Model model, Principal principal) {
		ProductType type = new ProductType();
		model.addAttribute("productType", type);
		model.addAttribute("productTypes", productTypeService.getAllTypes());
		model.addAttribute("username", principal.getName());
		model.addAttribute("pageName", "Projects in KLIMS");
		model.addAttribute("pageLink", "/projects");
		return "admin/product-type";
	}
	
	@GetMapping("/projects")
	public String projectsPage(Model model, Principal principal) {
		model.addAttribute("project", new Project());
		model.addAttribute("username", principal.getName());
		model.addAttribute("inStoreProjects", projectService.getAllProjectsByStatus(com.rcs2.inventory.model.Project.Status.SENT_TO_STORE));
		model.addAttribute("allProjects", projectService.getAllProjectsOrderByPriorityNotStatusAndRemoved(com.rcs2.inventory.model.Project.Status.ON_HOLD));
		model.addAttribute("highPriorityProjects", projectService.getProjectsByPriority(Priority.HIGH));
		model.addAttribute("inventoryProjects", projectService.getAllProjectsByStatus(com.rcs2.inventory.model.Project.Status.SENT_TO_STORE));
		model.addAttribute("projectsInWorkshop", projectService.getAllProjectsByStatus(com.rcs2.inventory.model.Project.Status.SENT_TO_WORKSHOP));
		model.addAttribute("revertedProjects", projectService.getAllProjectsByStatus(com.rcs2.inventory.model.Project.Status.REVIEW_REJECTED));
		model.addAttribute("urgentProject", projectService.getProjectByPriorityAndStatus(com.rcs2.inventory.model.Project.Status.SENT_TO_STORE, Priority.URGENT));
		model.addAttribute("revertedUrgentProjects", projectService.getProjectByPriorityAndStatus(com.rcs2.inventory.model.Project.Status.REVIEW_REJECTED, Priority.URGENT));
		return "workshop-admin/project";
	}
	
	@GetMapping("/review-projects")
	public String reviewProjectsPage(Model model, Principal principal) {
		model.addAttribute("username", principal.getName());
		model.addAttribute("projectsToReview", projectService.getProjectsByStatus(com.rcs2.inventory.model.Project.Status.SENT_TO_REVIEW));
		model.addAttribute("urgentProjects", projectService.getProjectByPriorityAndStatus(com.rcs2.inventory.model.Project.Status.SENT_TO_REVIEW, Priority.URGENT));
		return "workshop-admin/review-projects";
	}
	
	@GetMapping("/revertedProject")
	public String revertedProjectsPage(Model model, Principal principal) {
		model.addAttribute("username", principal.getName());
		return "user/revertedProject";
	}
	
	@GetMapping("/search-product")
	public String userHomePage(Model model, Principal principal) {
		model.addAttribute("username", principal.getName());
		model.addAttribute("products", productService.getAllProductsByStatus(Status.ON_ASSEMBLE));
		model.addAttribute("productAssemble", new ProductAssembleDTO());
		return "user/search-product";
	}

	@GetMapping("/user-details")
	public String userDetailsPage(Model model, Principal principal) {
		model.addAttribute("username", principal.getName());
		model.addAttribute("user", userService.getDetailsByUsername(principal.getName()).get());
		return "user";
	}
	
	@GetMapping("/user-home")
	public String userDashboardPage(Model model, Principal principal) {
		model.addAttribute("username", principal.getName());
		model.addAttribute("highPriorityProjectsCount", projectService.countProjectsByPrioirty(Priority.HIGH));
		model.addAttribute("workshopProjects", projectService.getAllProjectsByStatus(com.rcs2.inventory.model.Project.Status.SENT_TO_WORKSHOP).size());
		model.addAttribute("productTypesCount", productTypeService.countProductTypes());
		model.addAttribute("revertedProjectCount", projectService.getAllProjectsByStatus(com.rcs2.inventory.model.Project.Status.REVIEW_REJECTED).size());
		model.addAttribute("urgent", projectService.getProjectByPriorityAndStatus(com.rcs2.inventory.model.Project.Status.SENT_TO_WORKSHOP, Priority.URGENT).size());
		model.addAttribute("urgentRevert", projectService.getProjectByPriorityAndStatus(com.rcs2.inventory.model.Project.Status.REVIEW_REJECTED, Priority.URGENT).size());
		return "user/user-dashboard";
	}
	
	@GetMapping("/workshop-admin-home")
	public String workshopAdminHome(Model model, Principal principal) {
		model.addAttribute("username", principal.getName());
		model.addAttribute("userCount", userService.countUsersByRole(Role.WORKSHOP_ADMIN, "except"));
		model.addAttribute("projectsToReview", projectService.getAllProjectsByStatus(com.rcs2.inventory.model.Project.Status.SENT_TO_REVIEW).size());
		model.addAttribute("highPriorityProjects", projectService.countProjectsByPrioirty(Priority.HIGH));
		model.addAttribute("categoriesCount", productTypeService.countProductTypes());
		model.addAttribute("allProjects", selectedProjects());
		model.addAttribute("urgentProjectApprove", projectService.getProjectByPriorityAndStatus(com.rcs2.inventory.model.Project.Status.SENT_TO_APPROVE, Priority.URGENT));
		model.addAttribute("urgentProjectReview", projectService.getProjectByPriorityAndStatus(com.rcs2.inventory.model.Project.Status.SENT_TO_REVIEW, Priority.URGENT).size());
		return "/workshop-admin/workshop-admin-home";
	}
	
	@GetMapping("/workshop-inventory")
	public String workshopInventoryPage(Model model, Principal principal) {
		model.addAttribute("products", productService.getAllProductsByStatus(Status.ON_ASSEMBLE));
		model.addAttribute("username", principal.getName());
		model.addAttribute("categories", productTypeService.getAllTypes());
		return "admin/workshop-inventory";
	}
	
	private List<Project> selectedProjects(){
		List<Project> listingProjects = projectService.getProjectsByStatus(com.rcs2.inventory.model.Project.Status.SENT_TO_APPROVE);
		List<Project> returningList = new ArrayList<>();
		for(int i = 0; i <= (listingProjects.size()/4); i++) {
			returningList.add(listingProjects.get(i));
		}
		return returningList;
	}
	
}