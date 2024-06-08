package com.everis.homework.mapper; 

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

/**
 * The Class homework.
 * @author hvergarc
 */
@Data
@Entity
@Table(name="PC_RECURSOS_CLAVES")
public class KeysResources {	

	@Id
	@Column(name = "project_code", nullable = false)
	private String projectCode;
    
	@Column(name = "nombre_proyecto")
	private String nombreProyecto;
	
	@Column(name = "UN")
	private String UN;
	
	@Column(name = "SECTOR")
	private String sector;
	
	@Column(name = "CE")
	private String CE;
	
	@Column(name = "NOMBRE_CLIENTE")
	private String nombreCliente;
	
	@Column(name = "CLIENTE_MANAGER")
	private String clienteManager;
	
	@Column(name = "GERENTE_PROYECTO")
	private String gerenteProyecto;

	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PROJECT_CODE", referencedColumnName = "PROJECT_CODE")
	private ContigencyDocument documPlanContingencia;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy="projectCode", cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	private Set<DetailKeysResources> detailKeysResourcesList;
	
}