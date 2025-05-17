package com.ecom.service;

import com.ecom.model.User;

public interface UserService {

	public User saveUser(User user);

	public Boolean existsEmail(String email);

	public User getUserByEmail(String email);
}
