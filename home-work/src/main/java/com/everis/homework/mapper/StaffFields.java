package com.everis.homework.mapper;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.constraints.Digits;

import com.everis.homework.dto.Matches;

import lombok.Data;

@Data
@Embeddable
public class StaffFields implements Matches {
	// anadir suma
	@Digits(integer = 9, fraction = 9)
	@Column(name = "SUM_TARIFAS", precision = 18, scale = 9)
	private BigDecimal sumTarifas;
	/**
	 * 0 = Tarifa fija mensual 1 = Tarifa por hora
	 */
	@Column(name = "RATE_TYPE")
	private Integer rateType;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "projectCode", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
	private Set<ProjectProfile> detailProjectProfileList;

	private boolean matchSet(Set<? extends Matches> one, Set<? extends Matches> other) {
		boolean matches = true;
		/*
		 * if (one.equals(other) == false) return false;
		 */
		if (one != null && other == null)
			return false;
		if (other != null && one == null)
			return false;
		
		if (one.size() != other.size())
			return false;
		
		for (Matches from : one) {
			for (Matches to : other) {
				if (from.equals(to) && from.matches(to) == false) {
					matches = false;
					break;
				}
			}
			if (matches == false)
				break;
		}

		return matches;
	}

	public boolean matches(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			obj=new StaffFields();
		if (getClass() != obj.getClass())
			return false;
		StaffFields other = (StaffFields) obj;

		if (detailProjectProfileList == null) {
			if (other.detailProjectProfileList != null)
				return false;
		} else if (!matchSet(detailProjectProfileList, other.detailProjectProfileList))
			return false;
		if (rateType == null) {
			if (other.rateType != null)
				return false;
		} else if (!rateType.equals(other.rateType))
			return false;

		return true;
	}

}
