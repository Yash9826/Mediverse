package com.ecom.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecom.model.Category;

public interface CategoryService {

	public Category saveCategory(Category c);
	public List<Category> getAllCategories();
	public Boolean existCategory(String name);
	public Boolean deleteCategory(int id);
	public Category getCategoryById(int id);
	
	public List<Category> getAllActiveCategory(String category);
	public List<Category> getAllActiveCategory();
	

}
