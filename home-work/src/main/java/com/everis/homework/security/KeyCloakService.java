/**
 * 
 */
package com.everis.homework.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.everis.homework.dto.KeyClock;
import com.everis.homework.dto.UserCredentials;
import com.everis.homework.dto.Usuario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.impl.DefaultJwtParser;

/**
 * The Class KeyCloakService.
 * @author gsaravia
 */
@Service
public class KeyCloakService {

	/** The clientid. */
	@Value("${keycloak.resource}")
	private String clientid;

	/** The authurl. */
	@Value("${keycloak.auth-server-url}")
	private String authurl;

	/** The realm. */
	@Value("${keycloak.realm}")
	private String realm;
	
	/** The gson. */
	private Gson gson = new Gson();

	/**
	 * Authenticate.
	 *
	 * @param userCredentials the user credentials
	 * @return the usuario
	 */
	public Usuario authenticate(UserCredentials userCredentials) {
		Usuario usuario = new Usuario();
		String responseToken = null;

		try {

			String username = userCredentials.getUsername();
			List<NameValuePair> urlParameters = new ArrayList<>();
			urlParameters.add(new BasicNameValuePair("grant_type", "password"));
			urlParameters.add(new BasicNameValuePair("client_id", clientid));
			urlParameters.add(new BasicNameValuePair("username", username));
			urlParameters.add(new BasicNameValuePair("password", userCredentials.getPassword()));

			responseToken = sendPost(urlParameters);
			Gson gsonBuilder = new GsonBuilder().create();
			LinkedTreeMap<?, ?> map = gsonBuilder.fromJson(responseToken, LinkedTreeMap.class);
			
			if(responseToken.contains("access_token")) {
				usuario.setUsername(userCredentials.getUsername());
				String acces_token = (String) map.get("access_token");
				Claims claims = decodeTokenClaims(acces_token);
				usuario.setCode("200");
			}else {
				usuario.setCode("401");
				usuario.setError("Usuario y/o password incorrectos");
			}

		} catch (Exception e) {
			usuario.setCode("401");
			usuario.setError("Usuario y/o password incorrectos");
		}

		if("200".equals(usuario.getCode())) {
			try {
				this.logout(responseToken);
			}catch (Exception e) {
				System.out.println("No se pudo el logout");
			}
		}
		return usuario;
	}
	
	/**
	 * Send post.
	 *
	 * @param urlParameters the url parameters
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String sendPost(List<NameValuePair> urlParameters) throws IOException {

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(authurl + "/realms/" + realm + "/protocol/openid-connect/token");
		post.setEntity(new UrlEncodedFormEntity(urlParameters));

		HttpResponse response = client.execute(post);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuilder result = new StringBuilder();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}
	
	/**
	 * Logout.
	 *
	 * @param session the session
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void logout(String session) throws IOException {
		KeyClock key = gson.fromJson(session, KeyClock.class);
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(authurl + "/realms/" + realm + "/protocol/openid-connect/logout");
		post.setHeader("Authorization", "Bearer " + key.getAccess_token());
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");

		List<NameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair("client_id", clientid));
		urlParameters.add(new BasicNameValuePair("refresh_token", key.getRefresh_token()));
		post.setEntity(new UrlEncodedFormEntity(urlParameters));
		client.execute(post);
	}

	/**
	 * Decode token claims.
	 *
	 * @param token the token
	 * @return the claims
	 */
	public Claims decodeTokenClaims(String token) {
		String[] splitToken = token.split("\\.");
		String unsignedToken = splitToken[0] + "." + splitToken[1] + ".";

		DefaultJwtParser parser = new DefaultJwtParser();
	        Jwt<?, ?> jwt = parser.parse(unsignedToken);
	        Claims claims = (Claims) jwt.getBody();
		return claims;
	}
}
