package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.User;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;
	
	@Autowired
	private UserService userService;
	
	
	@GetMapping("/")
	public String index()
	{
		
		return "index";
	}
	
	
	
	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			User userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
		}
		
		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);
	}
	
	
	
	@GetMapping("/signin")
	public String login()
	{
		return "login";
	}
	
	
	@GetMapping("/register")
	public String register()
	{
		return "register";
	}
	
	@GetMapping("/products")
	public String products(Model m ,@RequestParam(value = "category", defaultValue = "") String category) {

		System.out.println("categories = " + category);
		List<Category> categories = categoryService.getAllActiveCategory(category);
		m.addAttribute("categories", categories);

		List<Product> products = productService.getAllActiveProducts(category);
		m.addAttribute("products", products);
		m.addAttribute("paramValue", category);
		

		return "product";
	}
	
	@GetMapping("/product/{id}")
	public String product(@PathVariable int id, Model m) {
		Product productById = productService.getProductById(id);
		m.addAttribute("product", productById);
		return "view_product";
	}
	
	
	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute User user, @RequestParam("img") MultipartFile file, HttpSession session)
			throws IOException {

//		Boolean existsEmail = userService.existsEmail(user.getEmail());

//		if (existsEmail) {
//			session.setAttribute("errorMsg", "Email already exist");
//		} else {
			String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
			user.setProfileImage(imageName);
			User saveUser = userService.saveUser(user);

			if (!ObjectUtils.isEmpty(saveUser)) {
				if (!file.isEmpty()) {
					File saveFile = new ClassPathResource("static/img").getFile();

					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
							+ file.getOriginalFilename());

//					System.out.println(path);
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				}
				session.setAttribute("succMsg", "Register successfully");
			} else {
				session.setAttribute("errorMsg", "something wrong on server");
			}
//		}

		return "redirect:/register";
	}
	
	
	@Controller
	public class ErrorController {

		@GetMapping("/403")
		public String accessDenied() {
			return "403"; // maps to templates/403.html
		}
	}
	
	
	@GetMapping("/home_products")
	public String home_products(Model m ) {

		
		List<Category> categories = categoryService.getAllActiveCategory();
		m.addAttribute("categories", categories);

		List<Product> products = productService.getAllProducts();
		m.addAttribute("products", products);
		
		m.addAttribute("paramValue", categories);
		

		return "index";
	}


}
