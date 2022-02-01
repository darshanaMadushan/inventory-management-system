package com.rcs2.inventory.service;

import java.util.List;
import java.util.Optional;

import com.rcs2.inventory.controller.dto.ProductCountDTO;
import com.rcs2.inventory.model.Project;
import com.rcs2.inventory.model.Project.Priority;
import com.rcs2.inventory.model.Project.Status;
import com.rcs2.inventory.model.User;

public interface ProjectService {

	Optional<Project> getProjectInName(String project);
	
	Optional<Project> getProjectById(int id);
	
	Project saveProject(Project project);

	List<Project> getAllProjects();
	
	List<Project> getProjectsRecievedOrderByPriority();
	
	List<Project> getAllProjectsOrderByPriorityNotStatusAndRemoved(Status status);
	
	List<Project> getAllProjectsByStatus(Status status);
	
	List<Project> getProjectsByPriority(Priority priority);
	
	List<Project> getProjectsByStatus(Status status);
	
	Long countProjectsByPrioirty(Priority priority);
	
	List<ProductCountDTO> getProjectStatistics();
	
	List<Project> getProjectByPriorityAndStatus(Status status, Priority priority);
	
	void updateProjectStatus(Status status, int projectId);
	
	void removeProject(int id);
	
	void rejectProjectApproval(String reason, int projectId, User user);
	
	void revertProjectReview(String reason, int projectId, User user);
}
