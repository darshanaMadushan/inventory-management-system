package com.rcs2.inventory.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rcs2.inventory.controller.dto.ProductCountDTO;
import com.rcs2.inventory.model.Project;
import com.rcs2.inventory.model.Project.Priority;
import com.rcs2.inventory.model.Project.Status;
import com.rcs2.inventory.model.User;
import com.rcs2.inventory.repository.ProjectRepository;
import com.rcs2.inventory.service.ProjectService;

@Service
public class ProjectServiceImpl implements ProjectService{

	@Autowired
	ProjectRepository projectRepository;
	
	@Override
	public Optional<Project> getProjectInName(String project) {
		return projectRepository.findByName(project);
	}
	
	@Override
	public Optional<Project> getProjectById(int id) {
		return projectRepository.findById(id);
	}

	@Override
	public Project saveProject(Project project) {
		java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
		Project projectRecieved = new Project(project.getName(), project.getDescription(), project.getQuantity(), project.getDueDate(), Status.SENT_TO_APPROVE, true, project.getPriority(), date);
		return projectRepository.save(projectRecieved);
	}

	@Override
	public List<Project> getAllProjects() {
		return projectRepository.findByIsAvailable(true);
	}

	@Override
	public void removeProject(int id) {
		java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
		projectRepository.removeProject(id, date);
	}

	@Override
	public List<Project> getProjectsByPriority(Priority priority) {
		return projectRepository.findByPriority(priority);
	}

	@Override
	public Long countProjectsByPrioirty(Priority priority) {
		return projectRepository.countByPriority(priority);
	}

	@Override
	public void updateProjectStatus(Status status, int projectId) {
		java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
		projectRepository.updateProjectStatus(status, projectId, date);
	}
	
	@Override
	public List<Project> getProjectsRecievedOrderByPriority() {
		List<Project> sentToWorkshopProjects = projectRepository.findByStatusOrderByPriority(Status.SENT_TO_WORKSHOP);
		List<Project> inProgressProjects = projectRepository.findByStatusOrderByPriority(Status.IN_PROGRESS);
		List<Project> returningList = new ArrayList<Project>(); 
		if(inProgressProjects != null) {
			for(Project project : inProgressProjects) {
				returningList.add(project);
			}
		}
		if(sentToWorkshopProjects != null) {
			for(Project project : sentToWorkshopProjects) {
				returningList.add(project);
			}
		}
		return returningList;
	}

	@Override
	public List<Project> getProjectsByStatus(Status status) {
		return projectRepository.findByStatus(status);
	}

	@Override
	public List<ProductCountDTO> getProjectStatistics() {
		return projectRepository.getCountProjectsOrderByStatus();
	}

	@Override
	public List<Project> getAllProjectsOrderByPriorityNotStatusAndRemoved(Status status) {
		return projectRepository.findByStatusNotLikeAndIsAvailableOrderByPriorityAsc(status, true);
	}

	@Override
	public void rejectProjectApproval(String reason, int projectId, User user) {
		java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
		projectRepository.rejectApprovalProject(Status.APPROVAL_REJECTED, reason, user, date, projectId);
	}

	@Override
	public List<Project> getAllProjectsByStatus(Status status) {
		return projectRepository.findByStatusAndIsAvailableOrderByLastUpdatedDate(status, true);
	}

	@Override
	public void revertProjectReview(String reason, int projectId, User user) {
		java.sql.Date date = new java.sql.Date(new java.util.Date().getTime());
		projectRepository.rejectApprovalProject(Status.REVIEW_REJECTED, reason, user, date, projectId);
	}

	@Override
	public List<Project> getProjectByPriorityAndStatus(Status status, Priority priority) {
		return projectRepository.findByStatusAndPriorityOrderByLastUpdatedDate(status, priority);
	}
	
}
