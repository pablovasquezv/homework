package com.everis.homework.controller;

import java.util.List;

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

import com.everis.homework.mapper.Rol;
import com.everis.homework.repository.IRolRepository;

/**
 * 
 * @author sgutierc
 *
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/rol")
public class RolController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private IRolRepository rolRepository;

	@GetMapping("get/all")
	@ResponseStatus(HttpStatus.OK)
	public List<Rol> findRoles() {
		return rolRepository.findAll();

	}

	@GetMapping("get/by/{domain}")
	@ResponseStatus(HttpStatus.OK)
	public List<Rol> findByDomain(@PathVariable("domain") String domain) {
		return rolRepository.findByDomain(domain).orElse(null);

	}
}
