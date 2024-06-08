package com.everis.homework.dto;

import java.util.Date;

import com.everis.homework.mapper.ServiceFields;
import com.everis.homework.mapper.StaffFields;
import com.everis.homework.mapper.TechnicalFields;
import com.everis.homework.mapper.TraditionalFields;

import lombok.Data;

@Data

public class VisualProjectHistory {

	private Integer id;

	private String projectCode;

	private Date lastModified;

	private String username;

	private TraditionalFields traditional;

	private ServiceFields service;

	private StaffFields staff;

	private TechnicalFields technical;

}
