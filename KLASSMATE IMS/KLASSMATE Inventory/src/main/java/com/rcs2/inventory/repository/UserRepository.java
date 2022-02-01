package com.rcs2.inventory.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rcs2.inventory.model.User;
import com.rcs2.inventory.model.User.Role;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByUsername(String username);
	
	Optional<User> findByNameEquals(String name);
	
	Optional<User> findByIdLike(Integer id);

	@Query("FROM User ORDER BY role DESC")
	List<User> findAllOrderByRoleDesc();
	
	@Query("SELECT COUNT(id) FROM User WHERE NOT role = ?1")
	Integer countByExceptRole(Role role);
	
	@Query("SELECT COUNT(id) FROM User WHERE role = ?1")
	Integer countByRole(Role role);
	
	@Transactional
	@Modifying
	@Query("UPDATE User SET name = ?1, email = ?2, designation = ?3, password = ?4 WHERE id = ?5")
	void updateUserDetails(String name, String email, String designation, String password, Integer id);
	
}
