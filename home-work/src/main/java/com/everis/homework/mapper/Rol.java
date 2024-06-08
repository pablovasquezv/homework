package com.everis.homework.mapper;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "ROLES")
public class Rol {

	public Rol() {

	}

	public Rol(String name, String domain) {
		this.name = name;
		this.domain = domain;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private Integer id;
	
	/** rol name */
	@Column(name = "NAME", nullable = false)
	private String name;

	/** rol domain */
	@Column(name = "DOMAIN", nullable = false)
	private String domain;
}
