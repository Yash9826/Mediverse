package com.ecom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

	@Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
//	{
//		http.csrf(csrf->csrf.disable()).cors(cors->cors.disable())
//				.authorizeHttpRequests(req->req.requestMatchers("/user/**").hasRole("USER")
//				.requestMatchers("/admin/**").hasRole("ADMIN")
//				.requestMatchers("/**").permitAll())
//				.formLogin(form->form.loginPage("/signin")
//						.loginProcessingUrl("/login")
//						.defaultSuccessUrl("/")
//						.successHandler(authenticationSuccessHandler))
//				.logout(logout->logout.permitAll());
//		
//		return http.build();
//	}
	
	
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
			.cors(cors -> cors.disable())
			.authorizeHttpRequests(req -> req
				.requestMatchers("/user/**").hasRole("USER")
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.requestMatchers("/**").permitAll()
			)
			.formLogin(form -> form
				.loginPage("/signin")
				.loginProcessingUrl("/login")
				.defaultSuccessUrl("/")
				.successHandler(authenticationSuccessHandler)
			)
			.logout(logout -> logout.permitAll())
			
			// ✅ Add this block for custom 403 page
			.exceptionHandling(ex -> ex
				.accessDeniedPage("/403")
			);

		return http.build();
	}

}
