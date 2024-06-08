package com.everis.homework.mapper;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "PROJECT_HISTORY")
public class ProjectHistory {
	/** The Constant MAX_LENGHT. */
	private static final int MAX_LENGHT = 255;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "PROJECT_CODE", length = MAX_LENGHT, nullable = false)
	private String projectCode;

	@Column(name = "LAST_MODIFIED")
	private Date lastModified;

	@Column(name = "USERNAME")
	private String username;

	@Column(name = "TRADITIONAL", length = 10240)
	private String traditional;

	@Column(name = "SERVICE", length = 10240)
	private String service;

	@Column(name = "STAFF", length = 10240)
	private String staff;

	@Column(name = "TECHNICAL", length = 10240)
	private String technical;

	public void loadModification(Modification modification) {
		this.username = modification.getUsername();
		this.lastModified = modification.getLastModified();
	}
}
