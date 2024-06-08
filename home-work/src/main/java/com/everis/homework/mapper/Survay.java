package com.everis.homework.mapper;


import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.everis.homework.dto.Matches;

import lombok.Data;

@Data
@Entity
@Table(name="SURVAY_SUMMARY")
public class Survay implements Matches{
	
	/** The Constant MAX_LENGHT. */
	private static final int MAX_LENGHT = 255;
	
	/** The project code. */
	@Id
	@Column(name = "PROJECT_CODE", length = MAX_LENGHT, nullable = false)
	private String projectCode;
	
	@Column(name = "PROJECT_NAME")
	private String projectName;
	
	@Column(name = "GRADE")
	private Float grade;
	
	@Column(name = "SURVAY_DATE")
	private LocalDate survayDate;
	
	@Column(name = "ACTION_PLANE")
	private String actionPlane;
	
	@Column(name = "COMMENTS")
	private String comments;

	
	public boolean matches(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Survay other = (Survay) obj;
		if (actionPlane == null) {
			if (other.actionPlane != null)
				return false;
		} else if (!actionPlane.equals(other.actionPlane))
			return false;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (grade == null) {
			if (other.grade != null)
				return false;
		} else if (!grade.equals(other.grade))
			return false;
		if (projectCode == null) {
			if (other.projectCode != null)
				return false;
		} else if (!projectCode.equals(other.projectCode))
			return false;
		if (projectName == null) {
			if (other.projectName != null)
				return false;
		} else if (!projectName.equals(other.projectName))
			return false;
		if (survayDate == null) {
			if (other.survayDate != null)
				return false;
		} else if (!survayDate.equals(other.survayDate))
			return false;
		return true;
	}
	
	
	
}
