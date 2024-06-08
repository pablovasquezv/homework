package com.everis.homework.mapper;

import java.time.LocalDateTime;

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
@Table(name = "HOMEWORK_LOG")
public class HomeworkLog {

	/** The homework log id. */
	@Id
    @GeneratedValue
    @Column(name = "HOMEWORK_LOG_ID", nullable = false)
    private Long homeworkLogId;
	
	/**  Id de Usuario que realiza la accion. */
	@Column(name = "USERID_ACTION", nullable = false)
	private String usersadAction;

	/**  Id de Usuario actualizado. */
	@Column(name = "USERID_UPDATED", nullable = false)
	private Integer updatedUsersad;

	/**  Fecha de actualizacion. */
	@Column(name = "UPDATE_DATE", nullable = false)
	private LocalDateTime updateDate;

	/** Informacion antigua */
	@Column(name = "OLD_INFORMATION", length = 10240)
	private String oldInformation;

	/** Informacion actualizada*/
	@Column(name = "UPDATED_INFORMATION", length = 10240)
	private String updateInformation;
}