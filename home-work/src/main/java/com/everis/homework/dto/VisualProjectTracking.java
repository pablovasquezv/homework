package com.everis.homework.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import com.everis.homework.helper.ProjectTypeHelper;
import com.everis.homework.mapper.Funding;
import com.everis.homework.mapper.Modification;
import com.everis.homework.mapper.Range;
import com.everis.homework.mapper.ServiceFields;
import com.everis.homework.mapper.StaffFields;
import com.everis.homework.mapper.Survay;
import com.everis.homework.mapper.TechnicalFields;
import com.everis.homework.mapper.TraditionalFields;

import lombok.Data;

@Data
public class VisualProjectTracking {
	/**
	 * Define niveles de alerta para la visualizacion de este objeto
	 * 
	 * @author sgutierc
	 *
	 */
	public enum AlertLevel {
		DRAFT(4), LOW(3), MEDIUM(2), HIGH(1), DEFAULT(9999);

		private int level = 0;

		private AlertLevel(int level) {
			this.level = level;
		}

		public int getValue() {
			return this.level;
		}

		@Override
		public String toString() {
			return String.valueOf(level);
		}
	};

	/**
	 * Define tipologias de proyectos
	 * 
	 * @author sgutierc
	 *
	 */

	public enum ProjectType {
		PROJECT(ProjectTypeHelper.PROJECT_TRAD_CODE), SERVICE(ProjectTypeHelper.SERVICE_CODE),
		PROJECT_AGILE(ProjectTypeHelper.PROJECT_AGILE_CODE);

		/**
		 * Retorna un ProjectType dependiendo del String type ingresado, por defecto
		 * devuelve PROJECT
		 * 
		 * @param type
		 * @return
		 */
		private Integer code;

		private ProjectType(Integer code) {
			this.code = code;
		}

		/**
		 * 
		 * @return
		 */
		private int getCode() {
			return this.code;
		}

		/**
		 * 
		 * @param code
		 * @return
		 */
		public static ProjectType getType(Integer code) {
			if (code == null)
				return PROJECT;
			for (ProjectType type : ProjectType.values()) {
				if (type.getCode() == code)
					return type;
			}
			return PROJECT;
		}
	};
	
	//visuales
	private ProjectType projectTypeClass;
	private ProjectAlarm alarm;
	
	//heredados
	private String projectCode;

	private Integer projectTypeCode;

	private String projectType;
	
	private Modification modification; 
	
	private String client;

	/** The sector. */ 
	private String sector;

	/** The un. */ 
	private String un;

	/** The ce. */ 
	private String ce;

	/** The service line. */ 
	private String serviceLine;

	/** The project manager. */ 
	private String projectManager;

	/** The start date. */ 
	private LocalDate startDate;

	/** The end date. */ 
	private LocalDate endDate;

	/** The end date. */ 
	private LocalDate realEndDate;

	/** The num incidents requested. */ 
	private Integer attempts;

	/** Nombre proyecto **/ 
	private String projectName;
 
	private Boolean declaredExtension;
 
	private Boolean extAgainstIncome;
 
	private String extensionReason;
 
	private Boolean extensionValidated;

	// Tradicional 
	private TraditionalFields traditional;

	// Service 
	private ServiceFields service;

	//staff 
	private StaffFields staff;

	//technical
	private TechnicalFields technical;
	
	// Agile
	private Integer itemsTechnicalDebt;
 
	private Integer storiesInBacklog;
 
	private Integer closedStories;
 
	private Float percentageCompletion;
 
	private Integer cycleTime;
 
	private Float remainingSprints;
 
	private Integer incidence;
 
	private Integer plannedWork;
 
	private Integer numberCells;
 
	private Integer sprintDuration;

	/** The num finished sprint stories. */ 
	private Integer numFinishedSprintStories;

	/** The num releases. */ 
	private Integer numReleases;

	/** The num sprints. */ 
	private Integer numSprints;

	/** The team speed average. */ 
	private BigDecimal teamSpeedAverage;

	/** The percentage req resolved. */ 
	private String sprintChanges;

	/** The percentage req resolved. */ 
	private Integer technicalDebtSprint;

	/** The percentage req resolved. */ 
	private Integer tecnDebtAccumulated;

	// Num de historias planificadas en el Sprint
	/** Number of sprint planned histories */ 
	private Integer numSprintPlannedHistories;

	// Indicador de cumplimiento de Sprint
	/** Sprint compliance indicator */ 
	private String sprintComplInd;
 
	private Funding fundingReport;
 
	private Survay survayReport;
 
	private Range redMargin;
 
	private Range yellowMargin; 
 
	private Set<VisualProjectHistory> projectHistory;
}
