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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.User;
import com.ecom.repository.CategoryRepository;
import com.ecom.repository.UserRepository;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;

import jakarta.servlet.http.HttpSession;





@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private ProductService productService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private  CategoryService service;
	
	@Autowired
	private UserRepository repo;
	
	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			User userDtls = userService.getUserByEmail(email);
			m.addAttribute("user", userDtls);
		}
		

		List<Category> allActiveCategory = service.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);
	}
	
	@GetMapping("/")
	public String home(Model m) {
//		return "user/home";
		
		List<Category> categories = service.getAllActiveCategory();
		m.addAttribute("categories", categories);

		List<Product> products = productService.getAllProducts();
		m.addAttribute("products", products);
		
		m.addAttribute("paramValue", categories);
		
		
		return "index";
	}
	
//	@GetMapping("/dashboard")
//	public String dashboard() {
//		return "/user/user_dashboard";
//	}
	
	@GetMapping("/dashboard")
	public String dashboard(Principal principal) {
	    User user = userService.getUserByEmail(principal.getName());
	    
	    // Check role and redirect if not user
	    if (!user.getRole().equals("ROLE_USER")) {
	        return "redirect:/index";
	    }

	    return "user/user_dashboard";
	}
	
	
	
	@GetMapping("/addProduct")
	public String addProduct(Model m)
	{
		List<Category> categories = service.getAllCategories();
		m.addAttribute("categories", categories);
		
		return "user/addProduct";
	}

	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session) throws IOException {

		
		  org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        String email = authentication.getName();

	       
			// Step 2: Fetch full user object from database
	        User user = repo.findByEmail(email);
		
		
		
		
		String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
		product.setImage(imageName);
		product.setDiscount(0);
		product.setDiscountPrice(product.getPrice());
		product.setUser(user);
		Product saveProduct = productService.saveProduct(product);

		if (!ObjectUtils.isEmpty(saveProduct)) {

			File saveFile = new ClassPathResource("static/img").getFile();

			Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "products_img" + File.separator
					+ image.getOriginalFilename());

			// System.out.println(path);
			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			session.setAttribute("succMsg", "Product Saved successfully");
		} else {
			session.setAttribute("errorMsg", "Something went wrong");
		}

//		return "redirect:/admin/addProduct" + product.getId();
		return "redirect:/user/dashboard";
	}
	
	@GetMapping("/products")
	public String loadViewProduct(Model m)
	{
		m.addAttribute("products",productService.getAllProducts());
		return "user/view_only_products";
	}
	
	
	
	
	@GetMapping("/editProduct")
	public String loadProduct(Model m,Principal p)
	{
		String email = p.getName();
		User userDtls = userService.getUserByEmail(email);
		m.addAttribute("user", userDtls);
		
		List<Product> allProductsOfUser = productService.getAllProductsOfUser(userDtls);
		
		m.addAttribute("products",allProductsOfUser);
		return "user/products";
	}
	
	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		Boolean deleteProduct = productService.deleteProduct(id);
		if (deleteProduct) {
			session.setAttribute("succMsg", "Product deleted successfully");
		} else {
			session.setAttribute("errorMsg", "Something went wrong");
		}
		return "redirect:/user/editProduct";
	}

	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session, Model m) {

		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errorMsg", "invalid Discount");
		} else {
			Product updateProduct = productService.updateProduct(product, image);
			if (!ObjectUtils.isEmpty(updateProduct)) {
				session.setAttribute("succMsg", "Product Saved successfully");
			} else {
				session.setAttribute("errorMsg", "Something went wrong");
			}
		}

		return "redirect:/user/editProduct";
	}
	
	
	
	
	
	
	
	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model m) {
		m.addAttribute("product", productService.getProductById(id));
		m.addAttribute("categories", service.getAllCategories());
		return "user/edit_product";
	}
	
}
