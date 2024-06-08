package com.everis.homework.mapper;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.everis.homework.dto.Matches;

import lombok.Data;

@Data
@Entity
@Table(name="PROJECT_PROFILE")
public class ProjectProfile implements Matches{
	
	private static final int MAX_LENGHT = 255;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PROFILE_ID")
	private Integer profileID;
	
	@Column(name = "PROFILE_NAME")
	private String profileName;
	
	@Column(name = "TARIFA", precision = 18, scale = 9)
	private BigDecimal tarifa;
	
	/** The project code. */
	@Column(name = "PROJECT_CODE", length = MAX_LENGHT, nullable = false)
	private String projectCode;

	public boolean matches(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjectProfile other = (ProjectProfile) obj;
		if (profileID == null) {
			if (other.profileID != null)
				return false;
		} else if (!profileID.equals(other.profileID))
			return false;
		if (profileName == null) {
			if (other.profileName != null)
				return false;
		} else if (!profileName.equals(other.profileName))
			return false;
		if (projectCode == null) {
			if (other.projectCode != null)
				return false;
		} else if (!projectCode.equals(other.projectCode))
			return false;
		if (tarifa == null) {
			if (other.tarifa != null)
				return false;
		} else if (!tarifa.equals(other.tarifa))
			return false;
		return true;
	}

	
}
