package com.firisbe.securepay.security;

import com.firisbe.securepay.entities.Customer;
import com.firisbe.securepay.entities.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import static org.junit.jupiter.api.Assertions.*;

class JwtUserDetailsTest {

    @Test
    void createJwtUserDetails_AdminRole() {
        // Arrange
        Customer adminCustomer = new Customer();
        adminCustomer.setId(1L);
        adminCustomer.setUsername("adminUser");
        adminCustomer.setPassword("adminPassword");
        adminCustomer.setRole(Role.ADMIN);

        // Act
        JwtUserDetails userDetails = JwtUserDetails.create(adminCustomer);

        // Assert
        assertNotNull(userDetails);
        assertEquals(1L, userDetails.getId());
        assertEquals("adminUser", userDetails.getUsername());
        assertEquals("adminPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN")));
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void createJwtUserDetails_UserRole() {
        // Arrange
        Customer userCustomer = new Customer();
        userCustomer.setId(2L);
        userCustomer.setUsername("userUser");
        userCustomer.setPassword("userPassword");
        userCustomer.setRole(Role.USER);

        // Act
        JwtUserDetails userDetails = JwtUserDetails.create(userCustomer);

        // Assert
        assertNotNull(userDetails);
        assertEquals(2L, userDetails.getId());
        assertEquals("userUser", userDetails.getUsername());
        assertEquals("userPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("USER")));
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }
}