/**
 * 
 */
package com.everis.homework.dto;

import lombok.Data;

/**
 * The Class Usuario.
 *
 * @author gsaravia
 */
@Data
public class Usuario {
	
	/** The username. */
	private String username;
	
	/** The nombre completo. */
	private String nombreCompleto;
	
	/** The employee number. */
	private String employeeNumber;
	
	/** The unidad. */
	private String unidad;
	
	/** The token. */
	private String token;

	/** The code. */
	private String code;
	
	/** The error. */
	private String error;
}
