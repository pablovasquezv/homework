package com.everis.homework.controller;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.everis.homework.mapper.Profile;
import com.everis.homework.mapper.Rol;
import com.everis.homework.repository.IProfileRepository;

/**
 * 
 * @author sgutierc
 *
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/profile")
public class ProfileController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/** The repository. */
	@Autowired
	private IProfileRepository repository;

	@GetMapping("/raw/{username}")
	@ResponseStatus(HttpStatus.OK)
	public Profile findFor(@PathVariable("username") String username) {
		Profile profile = repository.findById(username).orElse(null);
		if (profile == null) {
			logger.warn(String.format("no profile found for provided input {%s}",username));
		}
		return profile;
	}

	@GetMapping("/filtered/{username}/{domain}")
	@ResponseStatus(HttpStatus.OK)
	public Profile findFor(@PathVariable("username") String username, @PathVariable("domain") String domain) {
		Profile profile = repository.findById(username).orElse(null);
		if (profile == null) {
			logger.warn(String.format("no profile found for provided inputs user{%s} domain{%s}",username,domain));
			return profile;
		}

		Set<Rol> filteredRoles = new CopyOnWriteArraySet<Rol>();
		for (Rol rol : profile.getRoles()) {
			if (rol.getDomain().equals(domain))
				filteredRoles.add(rol);
		}
		profile.setRoles(filteredRoles);
		return profile;
	}
	
	/**
	 * Find all.
	 *
	 * @return the list
	 */
	@GetMapping("/get/all")
	@ResponseStatus(HttpStatus.OK)
	public List<Profile> findAll() {
		List<Profile> entities = repository.findAll();
		return entities;
	}

}
