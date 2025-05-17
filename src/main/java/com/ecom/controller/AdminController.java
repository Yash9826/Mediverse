package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
import com.ecom.repository.ProductRepository;
import com.ecom.repository.UserRepository;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	
	@Autowired
    private CategoryService service;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ProductRepository productRepo;

	@GetMapping("/")
	public String index(Model m)
	{
		
		

		List<Category> categories = service.getAllActiveCategory();
		m.addAttribute("categories", categories);

		List<Product> products = productService.getAllProducts();
		m.addAttribute("products", products);
		
		m.addAttribute("paramValue", categories);
		
		return "admin/index";
	}
	
	
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
	
	
	
	@GetMapping("/addProduct")
	public String addProduct(Model m)
	{
		List<Category> categories = service.getAllCategories();
		m.addAttribute("categories", categories);
		
		return "admin/addProduct";
	}
	@GetMapping("/category")
	public String category(Model m)
	{
		m.addAttribute("categorys", service.getAllCategories());
		return "admin/category";
	}
	
	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		String imageName = !file.isEmpty() ? file.getOriginalFilename() : "default.jpg";
		category.setImageName(imageName);

		Boolean existCategory = service.existCategory(category.getName());

		if (existCategory) {
			session.setAttribute("errorMsg", "Category Name already exists");
		} else {

			Category saveCategory = service.saveCategory(category);

			if (ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errorMsg", "Not saved ! internal server error");
			} else {
				
				File saveFile = new ClassPathResource("static/img").getFile();
//				String pathImg = "C:/Users/yashc/Documents/workspace-spring-tool-suite-4-4.22.1.RELEASE/Mediverse/src/main/resources/static/img/category_img";
//				File saveFile = new File(pathImg);

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
						+ file.getOriginalFilename());
				
//				Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);


				 System.out.println("PATH = " + path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				session.setAttribute("succMsg", "Saved successfully");
			}
		}

		return "redirect:/admin/category";
	}
	
	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id,HttpSession session)
	{
	    Boolean category = service.deleteCategory(id);
		if(category)
		{
			session.setAttribute("succMsg", "Category deleted Sucessfully");
		}
		else
		{

			session.setAttribute("errorMsg", "Something went wrong");
		}
		return "redirect:/admin/category";
	}
	
	@GetMapping("/loadEditCategory/{id}")
	public String loadEditCategory(@PathVariable int id, Model m) {
		m.addAttribute("category", service.getCategoryById(id));
		return "admin/edit_category";
	}
	
	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		Category oldCategory = service.getCategoryById(category.getId());
		String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();

		if (!ObjectUtils.isEmpty(category)) {

			oldCategory.setName(category.getName());
			oldCategory.setIsActive(category.getIsActive());
			oldCategory.setImageName(imageName);
		}

		Category updateCategory = service.saveCategory(oldCategory);

		if (!ObjectUtils.isEmpty(updateCategory)) {

			if (!file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
						+ file.getOriginalFilename());

				// System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}

			session.setAttribute("succMsg", "Category update success");
		} else {
			session.setAttribute("errorMsg", "something wrong on server");
		}

//		return "redirect:/admin/loadEditCategory/" + category.getId();
		return "redirect:/admin/category";

	}
	
	
	
	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session) throws IOException {

		
		  org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        String email = authentication.getName();

	        // Step 2: Fetch full user object from database
	        User user = userRepo.findByEmail(email);
		
		
		
		
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
		return "redirect:/admin/products";
	}
	
	@GetMapping("/products")
	public String loadViewProduct(Model m)
	{
		m.addAttribute("products",productService.getAllProducts());
		return "admin/products";
	}
	
	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		Boolean deleteProduct = productService.deleteProduct(id);
		if (deleteProduct) {
			session.setAttribute("succMsg", "Product deleted successfully");
		} else {
			session.setAttribute("errorMsg", "Something went wrong");
		}
		return "redirect:/admin/products";
	}
	

	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model m) {
		m.addAttribute("product", productService.getProductById(id));
		m.addAttribute("categories", service.getAllCategories());
		return "admin/edit_product";
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
//		return "redirect:/admin/editProduct/"+product.getId();
		return "redirect:/admin/products";
	}
	
	@GetMapping("/product/{id}")
		public String showProductDetails(@PathVariable int id, Model model) {
		    Product product = productService.getProductById(id); // Assume service is ready
		    
		    
		    
		    int id2 = product.getUser().getId();
		    String mobileNumber = product.getUser().getMobileNumber();
		    System.out.println("############################################");
		    System.out.println("USER ID = " + id2);
		    System.out.println("USER MOBILE NO. = " + mobileNumber);
		    System.out.println("############################################");
		    
		    
		    
		    String message = "Hello, I am interested in your product:\n"
		            + "Name: " + product.getTitle() + "\n"
		            + "Price: ₹" + product.getPrice() + "\n"
		            + "Is it available in stock?";
		    String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);

		    model.addAttribute("encodedMessage", encodedMessage);
		    model.addAttribute("product", product);

		    return "view_product"; // Thymeleaf page name
		}
}

