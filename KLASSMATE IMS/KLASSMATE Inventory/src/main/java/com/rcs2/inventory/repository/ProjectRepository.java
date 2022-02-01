package com.rcs2.inventory.repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rcs2.inventory.controller.dto.ProductCountDTO;
import com.rcs2.inventory.model.Project;
import com.rcs2.inventory.model.Project.Priority;
import com.rcs2.inventory.model.Project.Status;
import com.rcs2.inventory.model.User;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

	Optional<Project> findByName(String name);
	
	List<Project> findByIsAvailable(boolean isAvailable);
	
	List<Project> findByPriority(Priority priority);
	
	List<Project> findByStatus(Status status);

	List<Project> findByStatusOrderByPriority(Status status);
	
	List<Project> findByStatusNotLikeAndIsAvailableOrderByPriorityAsc(Status status, boolean isAvailable);
	
	List<Project> findByStatusAndIsAvailableOrderByLastUpdatedDate(Status status, boolean isAvailable);
	
	List<Project> findByStatusAndPriorityOrderByLastUpdatedDate(Status status, Priority prority);
	
	Long countByPriority(Priority priority);
	
	@Query("SELECT status as type, COUNT(projectId) as count FROM Project GROUP BY status")
	List<ProductCountDTO> getCountProjectsOrderByStatus();
	
	@Transactional
	@Modifying
	@Query("UPDATE Project SET status = ?1, rejectionReason = ?2, user = ?3, rejectedDate = ?4 WHERE projectId = ?5")
	void rejectApprovalProject(Status status, String reason, User user, Date date, int id);
		
	@Transactional
	@Modifying
	@Query("UPDATE Project SET isAvailable = FALSE, lastUpdatedDate = ?2 WHERE projectId = ?1")
	void removeProject(int id, Date date);
	
	@Transactional
	@Modifying
	@Query("UPDATE Project SET status = ?1, lastUpdatedDate = ?3 WHERE projectId = ?2")
	void updateProjectStatus(Status status, int projectId, Date date);
	
}