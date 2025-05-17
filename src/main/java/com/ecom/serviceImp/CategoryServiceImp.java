package com.ecom.serviceImp;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.Category;
import com.ecom.repository.CategoryRepository;
import com.ecom.service.CategoryService;

@Service
public class CategoryServiceImp implements CategoryService {

	@Autowired
	private CategoryRepository repo;
	
	@Override
	public Category saveCategory(Category c) {
		
		return repo.save(c);
	}

	@Override
	public List<Category> getAllCategories() {
		
		return repo.findAll();
	}

	@Override
	public Boolean existCategory(String name) {
		
		return repo.existsByName(name);
	}

	@Override
	public Boolean deleteCategory(int id) {
		
		Category category = repo.findById(id).orElse(null);
		 if(!ObjectUtils.isEmpty(category))
		 {
			 repo.delete(category);
			 return true;
		 }
		return false;
	}
	
	@Override
	public Category getCategoryById(int id) {
		Category category = repo.findById(id).orElse(null);
		return category;
	}

	@Override
	public List<Category> getAllActiveCategory(String category) {
		List<Category> categories = repo.findByIsActiveTrue();
		return categories;
	}

	@Override
	public List<Category> getAllActiveCategory() {
		List<Category> categories = repo.findByIsActiveTrue();
		return categories;
	}

	

	

}
