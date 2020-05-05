package com.example.springsecurity.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import com.example.springsecurity.model.*;
import com.example.springsecurity.student.*;




@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

	private final PasswordEncoder passwordEncoder;
	
	@Autowired
	public ApplicationSecurityConfig(PasswordEncoder passwordEncoder)  {
		this.passwordEncoder = passwordEncoder;
	}
	

	// overriding parent super class using http configure 
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http 	
				.csrf().disable() // todo 
				.authorizeRequests()
				.antMatchers("/", "index", "/css/*", "/js/*")
				.permitAll()
				.antMatchers("/api/**").hasRole(ApplicationUserRole.STUDENT.name())
				.antMatchers(HttpMethod.DELETE, "/management/api/**").hasAnyAuthority(ApplicationUserPermission.COURSE_WRITE.name())
				.antMatchers(HttpMethod.POST, "/management/api/**").hasAnyAuthority(ApplicationUserPermission.COURSE_WRITE.name())
				.antMatchers(HttpMethod.PUT, "/management/api/**").hasAnyAuthority(ApplicationUserPermission.COURSE_WRITE.name())
				.antMatchers(HttpMethod.GET, "/management/api/**").hasAnyRole(ApplicationUserRole.ADMIN.name(), ApplicationUserRole.ADMINTRAINEE.name())
				.anyRequest()
				.authenticated()
				.and()
				.httpBasic(); // it means basic auth
		
		
	}


	@Override
	@Bean
	protected UserDetailsService userDetailsService() {
		// retrieve users from the database
		UserDetails raijayantUser = User.builder()
			.username("raijayant")
			.password(passwordEncoder.encode("password"))
//			.roles("STUDENT") // Role_Student
			.authorities(ApplicationUserRole.STUDENT.getGrantedAuthorities())
			.build();
		
		UserDetails jirelKalpanaUser =  User.builder()
			.username("jirelkalpana")
			.password(passwordEncoder.encode("123"))
//			.roles(ApplicationUserRole.ADMIN.name()) // role admin
			.authorities(ApplicationUserRole.ADMIN.getGrantedAuthorities())
			.build();
		
		UserDetails tomUser =  User.builder()
				.username("tom")
				.password(passwordEncoder.encode("123"))
//				.roles(ApplicationUserRole.ADMINTRAINEE.name()) // role admin_trainee
				.authorities(ApplicationUserRole.ADMINTRAINEE.name())
				.build();
		
		return new InMemoryUserDetailsManager(
				raijayantUser, 
				jirelKalpanaUser,
				tomUser
				);
	}
	
}
