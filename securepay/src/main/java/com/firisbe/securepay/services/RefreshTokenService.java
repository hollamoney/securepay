package com.firisbe.securepay.services;


import com.firisbe.securepay.entities.Customer;
import com.firisbe.securepay.entities.RefreshToken;
import com.firisbe.securepay.repos.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class RefreshTokenService {
	
	@Value("${refresh.token.expires.in}")
	Long expireSeconds;
	
	private RefreshTokenRepository refreshTokenRepository;

	public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
		this.refreshTokenRepository = refreshTokenRepository;
	}
	
	public String createRefreshToken(Customer customer) {
		RefreshToken token = refreshTokenRepository.findByCustomerId(customer.getId());
		if(token == null) {
			token =	new RefreshToken();
			token.setCustomer(customer);
		}
		token.setToken(UUID.randomUUID().toString());
		token.setExpiryDate(Date.from(Instant.now().plusSeconds(expireSeconds)));
		refreshTokenRepository.save(token);
		return token.getToken();
	}
	
	public boolean isRefreshExpired(RefreshToken token) {
		return token.getExpiryDate().before(new Date());
	}

	public RefreshToken getByCustomer(Long customerId) {
		return refreshTokenRepository.findByCustomerId(customerId);	
	}

}
