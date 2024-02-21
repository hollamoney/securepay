package com.firisbe.securepay.services;

import com.firisbe.securepay.entities.Customer;
import com.firisbe.securepay.repos.CustomerRepository;
import com.firisbe.securepay.security.JwtUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private CustomerRepository customerRepository;
	
    public UserDetailsServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Customer customer = customerRepository.findByUsername(username);
		return JwtUserDetails.create(customer);
	}
	
	public UserDetails loadUserById(Long id) {
		Customer customer = customerRepository.findById(id).get();
		return JwtUserDetails.create(customer); 
	}

}
