package com.everis.homework.mapper;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * Instantiates a new homework log.
 */
@Data
@Entity
@Table(name = "PROJECT_TRACKING_LOG")
public class ProjectTrackingLog {
	/** The Constant MAX_LENGHT. */
	private static final int MAX_LENGHT = 255;
	
	/** The homework log id. */
	@Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id;
	
	/**  Id de Usuario que realiza la accion. */
	@Column(name = "USERNAME", nullable = false)
	private String username;

	/** The project code. */
	@Column(name = "PROJECT_CODE", length = MAX_LENGHT, nullable = false)
	private String projectCode;
	
	/**  Fecha de actualizacion. */
	@Column(name = "UPDATE_DATE", nullable = false)
	private LocalDate updateDate;

	/** Informacion antigua */
	@Column(name = "OLD_INFORMATION", length = 10240)
	private String oldInformation;

	/** Informacion actualizada*/
	@Column(name = "UPDATED_INFORMATION", length = 10240)
	private String updatedInformation;
}