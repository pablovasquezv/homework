package com.everis.homework.mapper;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * The Class homework.
 * 
 * @author gsaravia
 */
@Data
@Entity
@Table(name = "HOME_WORK")
public class Homework {

	/** The usersad. */
	@Id
	@Column(name = "USUARIO", nullable = false)
	private Integer nroEmpleado;

	@Column(name = "USERSAD")
	private String userName;

	/** The nombre. */
	@Column(name = "NAME")
	private String nombre;

	/** The apellido. */
	@Column(name = "SURNAME")
	private String apellido;

	/** The unidad. */
	@Column(name = "UNIT")
	private String unidad;

	/** The remotoCasa. */
	@Column(name = "DONDE")
	private String donde;

	/** The direccion. */
	@Column(name = "ADDRESS", length = 4096)
	private String direccion;

	/** The comuna. */
	@Column(name = "COMMUNE")
	private String comuna;

	/** The region. */
	@Column(name = "REGION")
	private String region;

	@Column(name = "OFICINA_EVERIS")
	private String oficinaEveris;

	/** The nombreCliente. */
	@Column(name = "NOMBRE_CLIENTE")
	private String cliente;

	/** The fechaInicio. */
	@Column(name = "START_DATE")
	private LocalDate fechaInicio;

	/** The fechaFin. */
	@Column(name = "END_DATE")
	private LocalDate fechaFin;

	@Column(name = "SECTOR")
	private String sector;

	@Column(name = "PROYECT")
	private String proyect;

	@Column(name = "PROYECT2")
	private String proyect2;

	@Column(name = "COMENTARIO", length = 4096)
	private String comentario;

	@Column(name = "CUARENTENA")
	private boolean cuarentena;

	@Column(name = "INICIO_CUARENTENA")
	private LocalDate inicioCuarentena;

	@Column(name = "COVID19")
	private boolean covid19;

	@Column(name = "FECHA_DIAGNOSTICO")
	private LocalDate fechaDiagnostico;

	/** The telefono. */
	@Column(name = "PHONE")
	private String telefono;
}