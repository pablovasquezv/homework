package com.everis.homework.mapper;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "REAL_END_DATE_LOG")
public class RealEndDateLog {
	/** The Constant MAX_LENGHT. */
	private static final int MAX_LENGHT = 255;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	/** The project code. */
	@Column(name = "PROJECT_CODE", length = MAX_LENGHT, nullable = false)
	private String projectCode;
	
	@Column(name = "USERNAME")
	private String username;
	
	@Column(name = "REAL_END_DATE_INIT")
	private LocalDate realEndDateInitial;
	
	@Column(name = "REAL_END_DATE_MOD")
	private LocalDate realEndDateModified;
	
	@Column(name ="EXTENSION_AGAINST_INCOME")
	private Boolean extAgainstIncome;
	
	@Column(name ="EXTENSION_REASON")
    private String extensionReason;
	
	@Column(name="MODIFICATION_DATE")
	private LocalDate modificationDate;
}
