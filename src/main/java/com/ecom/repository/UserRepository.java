package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecom.model.Product;
import com.ecom.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	Boolean existsByEmail(String email);

	public User findByEmail(String username);
	
	@Query("FROM Product p WHERE p.user.id = :userId")
	List<Product> getProductsByUserId(@Param("userId") int id);

	
}
