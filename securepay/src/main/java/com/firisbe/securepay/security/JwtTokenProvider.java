	package com.firisbe.securepay.security;

    import io.jsonwebtoken.*;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.stereotype.Component;

    import java.util.Date;
    import java.util.List;

	@Component
	public class JwtTokenProvider {

		@Value("${securepay.app.secret}")
		private String APP_SECRET;

		@Value("${securepay.expires.in}")
		private long EXPIRES_IN;

		public String generateJwtToken(Authentication auth) {
			JwtUserDetails userDetails = (JwtUserDetails) auth.getPrincipal();
			List<String> roles = userDetails.getAuthorities().stream()
					.map(GrantedAuthority::getAuthority)
					.toList();
			Date expireDate = new Date(new Date().getTime() + EXPIRES_IN);
			return Jwts.builder().setSubject(Long.toString(userDetails.getId()))
					.claim("roles", roles)
					.setIssuedAt(new Date()).setExpiration(expireDate)
					.signWith(SignatureAlgorithm.HS512, APP_SECRET).compact();
		}

		public String generateJwtTokenByUserId(Long userId) {
			Date expireDate = new Date(new Date().getTime() + EXPIRES_IN);
			return Jwts.builder().setSubject(Long.toString(userId))
					.setIssuedAt(new Date()).setExpiration(expireDate)
					.signWith(SignatureAlgorithm.HS512, APP_SECRET).compact();
		}

		Long getUserIdFromJwt(String token) {
			Claims claims = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(token).getBody();
			return Long.parseLong(claims.getSubject());
		}

		boolean validateToken(String token) {
			try {
				Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(token);
				return !isTokenExpired(token);
			} catch (SignatureException e) {
				return false;
			} catch (MalformedJwtException e) {
				return false;
			} catch (ExpiredJwtException e) {
				return false;
			} catch (UnsupportedJwtException e) {
				return false;
			} catch (IllegalArgumentException e) {
				return false;
			}
		}

		private boolean isTokenExpired(String token) {
			Date expiration = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(token).getBody().getExpiration();
			return expiration.before(new Date());
		}

	}
