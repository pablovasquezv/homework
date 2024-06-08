package com.everis.homework.mapper;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="DOC_RECURSOS_CLAVES")
public class ContigencyDocument {
	@Id
	@Column(name = "PROJECT_CODE", nullable = false)
	private String projectCode;
	
	@Column(name = "DOCUM_PLAN_CONTINGENCIA")
	@Lob
	private String contentBase64;
	
	@Column(name = "NOMBRE_DOCUMENTO" )
	private String name;
}
