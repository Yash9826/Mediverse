package com.ecom.serviceImp;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecom.model.User;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;

@Service
public class UserServiceImp  implements UserService{

	@Autowired
	private UserRepository userRepository;
	

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public User saveUser(User user) {

		user.setRole("ROLE_USER");
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		User saveUser = userRepository.save(user);
		return saveUser;
	}

	@Override
	public Boolean existsEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public User getUserByEmail(String email) {
		
		return userRepository.findByEmail(email);
		
	}

}
