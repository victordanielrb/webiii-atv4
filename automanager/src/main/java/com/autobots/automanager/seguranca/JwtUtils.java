package com.autobots.automanager.seguranca;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

	@Value("${autobots.jwt.secret}")
	private String secret;

	@Value("${autobots.jwt.expiracao}")
	private long expiracao;

	private Key getKey() {
		return Keys.hmacShaKeyFor(secret.getBytes());
	}

	public String gerarToken(UserDetails userDetails) {
		List<String> roles = userDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		return Jwts.builder()
				.setSubject(userDetails.getUsername())
				.claim("roles", roles)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expiracao))
				.signWith(getKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	public boolean validarToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public String obterNomeUsuario(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getKey())
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}

	@SuppressWarnings("unchecked")
	public List<GrantedAuthority> obterPerfis(String token) {
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(getKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
		List<String> roles = claims.get("roles", List.class);
		return roles.stream()
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}
}
