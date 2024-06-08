package com.everis.homework.controller;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.everis.homework.mapper.Profile;
import com.everis.homework.mapper.Rol;
import com.everis.homework.repository.IProfileRepository;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/user")
public class UserController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private IProfileRepository repository;

	@GetMapping("get/by/{username}")
	@ResponseStatus(HttpStatus.OK)
	public Profile findByUsername(@PathVariable("username") String username) {
		return repository.findById(username).orElse(null);
	}

	@PostMapping("create/")
	@ResponseStatus(HttpStatus.OK)
	Profile newEmployee(@RequestBody Profile user) {
		return repository.save(user);
	}

	@PutMapping("update/")
	@ResponseStatus(HttpStatus.OK)
	public Profile update(@RequestBody Profile user) {

		return repository.save(user);
	}

	/**
	 * Delete by id.
	 *
	 * @param id the id
	 * @return true, if successful
	 */
	@DeleteMapping("delete/{username}/{rol}")
	@ResponseStatus(HttpStatus.OK)
	public boolean removeRol(@PathVariable("username") String username, @PathVariable("rol") Integer rol) {
		Profile profile = repository.findById(username).orElse(null);
		Set<Rol> roles = profile.getRoles();
		if (roles != null && roles.isEmpty() == false) {
			Rol toRemove = null;
			for (Rol prol : roles) {
				if (prol.getId() == rol) {
					toRemove = prol;
					break;
				}
			}

			if (toRemove != null) {
				roles.remove(toRemove);
				profile.setRoles(roles);
			}
			
			if (roles.isEmpty())
				repository.delete(profile);
			else
				repository.save(profile);
			
			repository.flush();
		}

		return true;
	}
}
