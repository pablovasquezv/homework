package com.everis.homework.mapper; 

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;

/**
 * The Class homework.
 * @author hvergarc
 */
@Data
@Entity
@Table(name="DET_RECURSOS_CLAVES")
public class DetailKeysResources {	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_det_recursos_claves")
	private Integer idDetKeyResources;
	
	@Column(name = "PERFIL_CLAVE")
	private String perfilClave;
	
	@Column(name = "NOMBRE_PERFIL_CLAVE")
	private String nombrePerfilClave;
	
	@Column(name = "NOMBRE_BACKUP")
	private String nombreBackup;
	
	/**
	 * 0<-No
	 * 1<- SI
	 * 2<-N/A
	 */
	@Column(name = "PLAN_CONTINGENCIA")
	private Integer planContingencia;
	
	/**
	 * 0<-No
	 * 1<- SI
	 * 2<-N/A
	 */
	@Column(name = "BACKUP_ACTIVIDADES")
	private Integer backupActividades;
	
	/**
	 * 0<-No
	 * 1<- SI
	 * 2<-N/A
	 */
	@Column(name = "BACKUP_HERRAMIENTAS")
	private Integer backupHerramientas;
	
	/**
	 * 0<-No
	 * 1<- SI
	 * 2<-N/A
	 */
	@Column(name = "P_CLAVE_REUNION_SEG")
	private Integer pclaveReunionSeg;
	
	/**
	 * 0<-No
	 * 1<- SI
	 * 2<-N/A
	 */
	@Column(name = "COMUNICACION_CLI_EQUIPO")
	private Integer comunicacionCliEquipo;
	
	@Column(name = "COMENTARIOS")
	@Lob
	private String comentarios;
	
	@Column(name = "PROJECT_CODE")
	private String projectCode;
	
}