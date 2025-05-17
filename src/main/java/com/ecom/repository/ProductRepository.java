package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ecom.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {


	List<Product> findByIsActiveTrue();
	List<Product> findByCategory(String category);
	 @Query("FROM Product")
     List<Product> findAllProduct();
}
