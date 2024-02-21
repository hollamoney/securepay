package com.firisbe.securepay.security;

import com.firisbe.securepay.entities.Customer;
import com.firisbe.securepay.entities.Role;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;


@SpringBootTest
public class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    public void testGenerateJwtToken() {

        Authentication authentication = createMockAuthentication();

        String token = jwtTokenProvider.generateJwtToken(authentication);
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    public void testGenerateJwtTokenByUserId() {
        Long userId = 123L;
        String token = jwtTokenProvider.generateJwtTokenByUserId(userId);
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    public void testGetUserIdFromJwt() {
        Long userId = 123L;
        String token = jwtTokenProvider.generateJwtTokenByUserId(userId);

        Long extractedUserId = jwtTokenProvider.getUserIdFromJwt(token);
        assertEquals(userId, extractedUserId);
    }

    // Add more test cases as needed

    private Authentication createMockAuthentication() {

        Customer adminCustomer = new Customer();
        adminCustomer.setId(1L);
        adminCustomer.setUsername("adminUser");
        adminCustomer.setPassword("adminPassword");
        adminCustomer.setRole(Role.ADMIN);

        JwtUserDetails userDetails = JwtUserDetails.create(adminCustomer);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
