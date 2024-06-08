package com.everis.homework.dto;

import java.util.Set;

import com.everis.homework.mapper.DetailKeysResources;

import lombok.Data;

@Data
public class VisualContigency {

	private String projectCode;
    
	private String nombreProyecto;
	
	private String UN;
	
	private String sector;
	
	private String CE;
	
	private String nombreCliente;
	
	private String clienteManager;
	
	private String gerenteProyecto;
		
	private boolean poseePlan;
	
	private Set<DetailKeysResources> detailKeysResourcesList;
}
