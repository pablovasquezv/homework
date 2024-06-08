package com.everis.homework.mapper;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="FUNDING_REPORT")
public class Funding {
	
	/** The Constant MAX_LENGHT. */
	private static final int MAX_LENGHT = 255;

	/** The project code. */
	@Id
	@Column(name = "PROJECT_CODE", length = MAX_LENGHT, nullable = false)
	private String projectCode;
	
	@Column(name = "PROJECT_NAME")
	private String projectName;
	
	@Column(name = "NR_N")
	private BigDecimal nRN;
	
	@Column(name = "NR_INCURRIDO")
	private BigDecimal nrIncurrido;
	
	@Column(name = "FUNDING")
	private BigDecimal funding;
	
	@Column(name = "WIP")
	private BigDecimal wip;
	
	@Column(name = "FACTURADO")
	private BigDecimal facturado;
	
	@Column(name = "DIAS_WIP")
	private BigDecimal diasWIP;
	
	@Column(name = "INCURRED_VS_FUNDING")
	private BigDecimal incurredVsFunding;
	
	@Column(name = "WIP_NR_N")
	private BigDecimal wipconNRN;
	}
