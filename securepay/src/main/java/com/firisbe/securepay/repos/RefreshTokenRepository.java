package com.firisbe.securepay.repos;

import com.firisbe.securepay.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{

	RefreshToken findByCustomerId(Long userId);
	
}
