package com.firisbe.securepay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firisbe.securepay.controllers.AuthController;
import com.firisbe.securepay.entities.Customer;
import com.firisbe.securepay.entities.RefreshToken;
import com.firisbe.securepay.requests.RefreshRequest;
import com.firisbe.securepay.requests.RegisterCustomerRequest;
import com.firisbe.securepay.security.JwtTokenProvider;
import com.firisbe.securepay.services.CustomerService;
import com.firisbe.securepay.services.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CustomerService customerService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void testLogin() throws Exception {
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));

        when(customerService.getOneCustomerByUsername(anyString())).thenReturn(mock(Customer.class));

        when(jwtTokenProvider.generateJwtToken(any())).thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new RegisterCustomerRequest("username", "password","ahmet@mail.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("Bearer mocked-jwt-token"))
                .andExpect(jsonPath("$.userId").exists());

        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    public void testRegister() throws Exception {
        when(customerService.getOneCustomerByUsername(anyString())).thenReturn(null);

        when(passwordEncoder.encode(anyString())).thenReturn("hashed-password");

        when(jwtTokenProvider.generateJwtToken(any())).thenReturn("mocked-jwt-token");

        when(customerService.saveOneCustomer(any(Customer.class))).thenAnswer(invocation -> {
            Customer customerArg = invocation.getArgument(0);
            customerArg.setId(1L);
            return customerArg;
        });

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new RegisterCustomerRequest("newUsername", "newPassword", "newEmail@mail.com"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("Bearer mocked-jwt-token"))
                .andExpect(jsonPath("$.userId").exists());

        verify(customerService, times(1)).saveOneCustomer(any());
    }

    @Test
    public void testRefresh() throws Exception {
        RefreshToken mockToken = mock(RefreshToken.class);
        when(mockToken.getToken()).thenReturn("valid-refresh-token");

        doReturn(mockToken).when(refreshTokenService).getByCustomer(anyLong());

        when(mockToken.getCustomer()).thenReturn(mock(Customer.class));


        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new RefreshRequest(1L, "valid-refresh-token"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.userId").exists());

        verify(refreshTokenService, times(1)).getByCustomer(anyLong());
    }
}
