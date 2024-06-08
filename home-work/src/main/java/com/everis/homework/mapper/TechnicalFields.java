package com.everis.homework.mapper;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.everis.homework.dto.Matches;

import lombok.Data;

@Data
@Embeddable
public class TechnicalFields implements Matches {
	@Column(name = "CASOS_PRUEBA_PLANIF")
	private Integer testCasesPlanned;

	@Column(name = "CASOS_PRUEBA_REAL")
	private Integer testCasesReal;

	@Column(name = "CASOS_PRUEBA_INDICE")
	private Float testCasesIndex;

	@Column(name = "TOTAL_REQ_CLOSED_PER")
	private Integer totalReqClosedPeriod;

	@Column(name = "TOTAL_REQ_ERROR_PER")
	private Integer totalReqErrorPeriod;

	@Column(name = "PROD_ERROR_INDICE")
	private Float prodErrorIndex;

	// Requerimientos con rollback del periodo
	@Column(name = "NUM_ROLLBACK_REQ_PERIOD")
	private Integer numRollbakReqPer;

	@Column(name = "ROLLBACK_PROD")
	private BigDecimal rollbackProd;

	@Column(name = "PERIOD_QUALITY_COMPLIANCE")
	private Float periodQualityComp;

	@Override
	public boolean matches(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			obj=new TechnicalFields();
		if (getClass() != obj.getClass())
			return false;
		TechnicalFields other = (TechnicalFields) obj;
		if (numRollbakReqPer == null) {
			if (other.numRollbakReqPer != null)
				return false;
		} else if (!numRollbakReqPer.equals(other.numRollbakReqPer))
			return false;
		if (periodQualityComp == null) {
			if (other.periodQualityComp != null)
				return false;
		} else if (!periodQualityComp.equals(other.periodQualityComp))
			return false;

		if (testCasesPlanned == null) {
			if (other.testCasesPlanned != null)
				return false;
		} else if (!testCasesPlanned.equals(other.testCasesPlanned))
			return false;
		if (testCasesReal == null) {
			if (other.testCasesReal != null)
				return false;
		} else if (!testCasesReal.equals(other.testCasesReal))
			return false;

		if (totalReqClosedPeriod == null) {
			if (other.totalReqClosedPeriod != null)
				return false;
		} else if (!totalReqClosedPeriod.equals(other.totalReqClosedPeriod))
			return false;
		if (totalReqErrorPeriod == null) {
			if (other.totalReqErrorPeriod != null)
				return false;
		} else if (!totalReqErrorPeriod.equals(other.totalReqErrorPeriod))
			return false;

		return true;
	}
}
