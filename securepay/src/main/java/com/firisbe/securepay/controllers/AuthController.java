package com.firisbe.securepay.controllers;

import com.firisbe.securepay.entities.Customer;
import com.firisbe.securepay.entities.RefreshToken;
import com.firisbe.securepay.entities.Role;
import com.firisbe.securepay.requests.LoginRequest;
import com.firisbe.securepay.requests.RefreshRequest;
import com.firisbe.securepay.requests.RegisterCustomerRequest;
import com.firisbe.securepay.responses.AuthResponse;
import com.firisbe.securepay.security.JwtTokenProvider;
import com.firisbe.securepay.services.CustomerService;
import com.firisbe.securepay.services.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;

	private final JwtTokenProvider jwtTokenProvider;

	private final CustomerService customerService;


	private final PasswordEncoder passwordEncoder;

	private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, CustomerService customerService,
    		PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.customerService = customerService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest loginRequest) {
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
		Authentication auth = authenticationManager.authenticate(authToken);

		SecurityContextHolder.getContext().setAuthentication(auth);


		System.out.println("---------------" + auth + "------------");
		System.out.println("---------------" + auth.getName() + "------------");

		String jwtToken = jwtTokenProvider.generateJwtToken(auth);
		Customer customer = customerService.getOneCustomerByUsername(loginRequest.getUsername());
		AuthResponse authResponse = new AuthResponse();
		authResponse.setMessage("Login succesfully.");
		authResponse.setAccessToken("Bearer " + jwtToken);
		authResponse.setRefreshToken(refreshTokenService.createRefreshToken(customer));
		authResponse.setUserId(customer.getId());

		return authResponse;
	}

	@PostMapping("/register-admin")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegisterCustomerRequest registerRequest, BindingResult bindingResult) {
		AuthResponse authResponse = new AuthResponse();

		boolean isUsernameInUse = customerService.getOneCustomerByUsername(registerRequest.getUsername()) != null;
		boolean isEmailInUse = customerService.getOneCustomerByEmail(registerRequest.getEmail()) != null;

		if (isUsernameInUse && isEmailInUse) {
			authResponse.setMessage("Username and Email already in use.");
			return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
		}

		if (isUsernameInUse) {
			authResponse.setMessage("Username already in use.");
			return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
		}

		if (isEmailInUse) {
			authResponse.setMessage("Email already in use.");
			return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
		}

		if (bindingResult.hasErrors()) {
			String errorMessage = bindingResult.getAllErrors().stream()
					.map(ObjectError::getDefaultMessage)
					.collect(Collectors.joining(" -> "));
			authResponse.setMessage("Bad Request: " + errorMessage);
			return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
		}

		Customer customer = new Customer();
		customer.setUsername(registerRequest.getUsername());
		customer.setPassword(registerRequest.getPassword());
		customer.setEmail(registerRequest.getEmail());
		customer.setRole(Role.ADMIN);
		customerService.saveOneCustomer(customer);

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(registerRequest.getUsername(), registerRequest.getPassword());
		Authentication auth = authenticationManager.authenticate(authToken);
		SecurityContextHolder.getContext().setAuthentication(auth);
		String jwtToken = jwtTokenProvider.generateJwtToken(auth);


		authResponse.setMessage("User successfully registered.");
		authResponse.setAccessToken("Bearer " + jwtToken);
		authResponse.setRefreshToken(refreshTokenService.createRefreshToken(customer));
		authResponse.setUserId(customer.getId());
		return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
	}

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterCustomerRequest registerRequest, BindingResult bindingResult) {
		AuthResponse authResponse = new AuthResponse();

		boolean isUsernameInUse = customerService.getOneCustomerByUsername(registerRequest.getUsername()) != null;
		boolean isEmailInUse = customerService.getOneCustomerByEmail(registerRequest.getEmail()) != null;

		if (isUsernameInUse && isEmailInUse) {
			authResponse.setMessage("Username and Email already in use.");
			return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
		}

		if (isUsernameInUse) {
			authResponse.setMessage("Username already in use.");
			return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
		}

		if (isEmailInUse) {
			authResponse.setMessage("Email already in use.");
			return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
		}
		if (bindingResult.hasErrors()) {
			String errorMessage = bindingResult.getAllErrors().stream()
					.map(ObjectError::getDefaultMessage)
					.collect(Collectors.joining(" -> "));
			authResponse.setMessage("Bad Request: " + errorMessage);
			return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
		}

		Customer customer = new Customer();
		customer.setUsername(registerRequest.getUsername());
		customer.setPassword(registerRequest.getPassword());
		customer.setEmail(registerRequest.getEmail());
		customer.setRole(Role.USER);
		customerService.saveOneCustomer(customer);

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(registerRequest.getUsername(), registerRequest.getPassword());
		Authentication auth = authenticationManager.authenticate(authToken);
		SecurityContextHolder.getContext().setAuthentication(auth);
		String jwtToken = jwtTokenProvider.generateJwtToken(auth);


		authResponse.setMessage("User successfully registered.");
		authResponse.setAccessToken("Bearer " + jwtToken);
		authResponse.setRefreshToken(refreshTokenService.createRefreshToken(customer));
		authResponse.setUserId(customer.getId());
		return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest refreshRequest) {
		AuthResponse response = new AuthResponse();
		RefreshToken token = refreshTokenService.getByCustomer(refreshRequest.getUserId());
		if(token.getToken().equals(refreshRequest.getRefreshToken()) &&
				!refreshTokenService.isRefreshExpired(token)) {

			Customer customer = token.getCustomer();
			String jwtToken = jwtTokenProvider.generateJwtTokenByUserId(customer.getId());
			response.setMessage("token successfully refreshed.");
			response.setAccessToken("Bearer " + jwtToken);
			response.setUserId(customer.getId());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.setMessage("refresh token is not valid.");
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		}

	}


}
