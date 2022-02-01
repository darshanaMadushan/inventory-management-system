package com.rcs2.inventory.controller;

import java.security.Principal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rcs2.inventory.model.Project;
import com.rcs2.inventory.model.Project.Status;
import com.rcs2.inventory.service.ProjectService;

@Controller
@RequestMapping("/project")
public class ProjectController {

	private final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class); 
	
	@Autowired
	ProjectService projectService;
	
	@PostMapping("/new")
	public String addNewProject(@ModelAttribute("project") Project project, Principal principal) {
		try {
			Optional<Project> existProject = projectService.getProjectInName(project.getName());
			if(existProject.isEmpty()) {
				projectService.saveProject(project);
				LOGGER.info("New Project Created by the User: " + principal.getName());
				return "redirect:/projects?success";
			} else {
				LOGGER.warn("Existing Project Name Selected. Can't Create Project");
				return "redirect:/projects?projectExistError";
			}
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return "redirect:/projects?internal";
		}
	}
	
	@PostMapping("/getApproval")
	public String getApprovalForProject(@ModelAttribute("project") Project project, Principal principal) {
		try {
			Project projectSaved = projectService.saveProject(project);			
			projectService.updateProjectStatus(Status.SENT_TO_APPROVE, projectSaved.getProjectId());
			LOGGER.info("Project "+ projectSaved.getName() + " sent to get the approval by Workshop admin by the user " + principal.getName());
			return "redirect:/projects?success";
		} catch(Exception e) {
			LOGGER.error(e.getMessage());
			return "redirect:/projects?internal";
		}
		
	}
}
