/**
 * 
 */
package com.everis.homework.controller;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.everis.homework.dto.UserCredentials;
import com.everis.homework.dto.Usuario;
import com.everis.homework.security.JWTAuthorizationFilter;
import com.everis.homework.security.KeyCloakService;

import io.jsonwebtoken.Jwts;

/**
 * @author gsaravia
 *
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/user")
public class LoginController {

	@Autowired
	private KeyCloakService keyCloakService;

	@PostMapping("/login")
	public Usuario login(@RequestBody UserCredentials userCredentials) {
		Usuario usuario = new Usuario();

		if (userCredentials.getUsername().contains("@") || userCredentials.getUsername().contains(".")) {
			usuario.setCode("401");
			usuario.setError("Usuario y/o password incorrectos");
		} /*else
			usuario = keyCloakService.authenticate(userCredentials);*/
		usuario.setCode("200");
		usuario.setUsername(userCredentials.getUsername());
		if ("200".equals(usuario.getCode())) {
			String token = getJWTToken(userCredentials.getUsername());
			usuario.setToken(token);
		}
		
		return usuario;
	}

	private String getJWTToken(String username) {
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");

		String token = Jwts.builder().setId("softtekJWT").setSubject(username)
				.claim("authorities",
						grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 10800000)).signWith(JWTAuthorizationFilter.KEY)
				.compact();

		return "Bearer " + token;
	}

}
