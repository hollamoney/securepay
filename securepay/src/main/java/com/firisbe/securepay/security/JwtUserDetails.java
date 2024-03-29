package com.firisbe.securepay.security;

import com.firisbe.securepay.entities.Customer;
import com.firisbe.securepay.entities.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class JwtUserDetails implements UserDetails {

	public Long id;
	private String username;
	private String password;
	private Collection<? extends GrantedAuthority> authorities;
	
    private JwtUserDetails(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public static JwtUserDetails create(Customer customer) {
        List<GrantedAuthority> authoritiesList = new ArrayList<>();
		if(customer.getRole() == Role.ADMIN){
			authoritiesList.add(new SimpleGrantedAuthority("ADMIN"));
		}
		else{
			authoritiesList.add(new SimpleGrantedAuthority("USER"));
		}
        return new JwtUserDetails(customer.getId(), customer.getUsername(), customer.getPassword(), authoritiesList);
    }

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
