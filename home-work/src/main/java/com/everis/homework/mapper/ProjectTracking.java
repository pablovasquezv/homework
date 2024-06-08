package com.everis.homework.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

/**
 * Instantiates a new project tracking.
 */
@Data
@Entity
@Table(name = "PROJECT_TRACKING")
public class ProjectTracking {

	/** The Constant MAX_LENGHT. */
	private static final int MAX_LENGHT = 255;

	/** The project code. */
	@Id
	@Column(name = "PROJECT_CODE", length = MAX_LENGHT, nullable = false)
	private String projectCode;

	@Embedded
	private Modification modification;

	// General
	/** The project type. */
	@Column(name = "PROJECT_TYPE", length = MAX_LENGHT)
	private String projectType;

	/** The project type. */
	@Column(name = "PROJECT_TYPE_CODE", length = MAX_LENGHT)
	private Integer projectTypeCode;

	@Column(name = "CLIENT", length = MAX_LENGHT)
	private String client;

	/** The sector. */
	@Column(name = "SECTOR", length = MAX_LENGHT)
	private String sector;

	/** The un. */
	@Column(name = "UN", length = MAX_LENGHT)
	private String un;

	/** The ce. */
	@Column(name = "CE", length = MAX_LENGHT)
	private String ce;

	/** The service line. */
	@Column(name = "SERVICE_LINE", length = MAX_LENGHT)
	private String serviceLine;

	/** The project manager. */
	@Column(name = "PROJECT_MANAGER", length = MAX_LENGHT)
	private String projectManager;

	/** The start date. */
	@Column(name = "START_DATE")
	private LocalDate startDate;

	/** The end date. */
	@Column(name = "END_DATE")
	private LocalDate endDate;

	/** The end date. */
	@Column(name = "REAL_END_DATE")
	private LocalDate realEndDate;

	/** The num incidents requested. */
	@Column(name = "ATTEMPTS")
	private Integer attempts;

	/** Nombre proyecto **/
	@Column(name = "PROJECT_NAME", length = MAX_LENGHT)
	private String projectName;

	@Column(name = "PROJECT_EXTENSION")
	private Boolean declaredExtension;

	@Column(name = "EXTENSION_AGAINST_INCOME")
	private Boolean extAgainstIncome;

	@Column(name = "EXTENSION_REASON")
	private String extensionReason;

	@Column(name = "EXTENSION_VALIDATED", columnDefinition = "boolean default false")
	private Boolean extensionValidated;

	// Tradicional
	@Embedded
	private TraditionalFields traditional;

	// Service
	@Embedded
	private ServiceFields service;

	//staff
	@Embedded
	private StaffFields staff;

	//tecnicos
	@Embedded
	private TechnicalFields technical;
	
	// Agile

	@Column(name = "ITEMS_TECHNICAL_DEBT", length = MAX_LENGHT)
	private Integer itemsTechnicalDebt;

	@Column(name = "STORIES_IN_BACKLOG", length = MAX_LENGHT)
	private Integer storiesInBacklog;

	@Column(name = "CLOSED_STORIES", length = MAX_LENGHT)
	private Integer closedStories;

	@Column(name = "PERCENTAGE_COMPLETION", length = MAX_LENGHT)
	private Float percentageCompletion;

	@Column(name = "CYCLE_TIME", length = MAX_LENGHT)
	private Integer cycleTime;

	@Column(name = "REMAINING_SPRINTS", length = MAX_LENGHT)
	private Float remainingSprints;

	@Column(name = "INCIDENCE", length = MAX_LENGHT)
	private Integer incidence;

	@Column(name = "PLANNED_WORK", length = MAX_LENGHT)
	private Integer plannedWork;

	@Column(name = "NUMBER_CELLS", length = MAX_LENGHT)
	private Integer numberCells;

	@Column(name = "SPRINT_DURATION", length = MAX_LENGHT)
	private Integer sprintDuration;

	/** The num finished sprint stories. */
	@Column(name = "NUM_FINISH_SPRINT_STORIES")
	private Integer numFinishedSprintStories;

	/** The num releases. */
	@Column(name = "NUM_RELEASES")
	private Integer numReleases;

	/** The num sprints. */
	@Column(name = "NUM_SPRINTS")
	private Integer numSprints;

	/** The team speed average. */
	@Column(name = "TEAM_SPEED_AVERAGE")
	private BigDecimal teamSpeedAverage;

	/** The percentage req resolved. */
	@Column(name = "SPRINT_CHANGES")
	private String sprintChanges;

	/** The percentage req resolved. */
	@Column(name = "TECHNICAL_DEBT_SPRINT")
	private Integer technicalDebtSprint;

	/** The percentage req resolved. */
	@Column(name = "TECN_DEBT_ACCUMULATED")
	private Integer tecnDebtAccumulated;

	// Num de historias planificadas en el Sprint
	/** Number of sprint planned histories */
	@Column(name = "NUM_SPRINT_PLANNED_HIST")
	private Integer numSprintPlannedHistories;

	// Indicador de cumplimiento de Sprint
	/** Sprint compliance indicator */
	@Column(name = "SPRINT_COMPL_IND", length = MAX_LENGHT)
	private String sprintComplInd;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "PROJECT_CODE", referencedColumnName = "PROJECT_CODE")
	private Funding fundingReport;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "PROJECT_CODE", referencedColumnName = "PROJECT_CODE")
	private Survay survayReport;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "top", column = @Column(name = "MARGIN_TOP_RED")),
			@AttributeOverride(name = "bottom", column = @Column(name = "MARGIN_BOTTOM_RED")) })
	private Range redMargin;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "top", column = @Column(name = "MARGIN_TOP_YELLOW")),
			@AttributeOverride(name = "bottom", column = @Column(name = "MARGIN_BOTTOM_YELLOW")) })
	private Range yellowMargin;

	//Campos de tipo datos historicos ->
	
	/*@OneToMany(fetch = FetchType.EAGER, mappedBy="projectCode", cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	private Set<ProjectHistory> projectHistory;
		*/
	//<- fin campos historicos

	public boolean matches(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjectTracking other = (ProjectTracking) obj;

		if (traditional == null) {
			if (other.traditional != null && !other.traditional.matches(traditional))
				return false;
		} else if (!traditional.matches(other.traditional))
			return false;
		if (service == null) {
			if (other.service != null && !other.service.matches(service))
				return false;
		} else if (!service.matches(other.service))
			return false;
		if (staff == null) {
			if (other.staff != null && !other.staff.matches(staff))
				return false;
		} else if (!staff.matches(other.staff))
			return false;
		
		if (technical == null) {
			if (other.technical != null && !other.technical.matches(technical))
				return false;
		} else if (!technical.matches(other.technical))
			return false;

		
		//agile y general
		
		if (numFinishedSprintStories == null) {
			if (other.numFinishedSprintStories != null)
				return false;
		} else if (!numFinishedSprintStories.equals(other.numFinishedSprintStories))
			return false;

		if (numReleases == null) {
			if (other.numReleases != null)
				return false;
		} else if (!numReleases.equals(other.numReleases))
			return false;

		
		if (numSprintPlannedHistories == null) {
			if (other.numSprintPlannedHistories != null)
				return false;
		} else if (!numSprintPlannedHistories.equals(other.numSprintPlannedHistories))
			return false;
		if (numSprints == null) {
			if (other.numSprints != null)
				return false;
		} else if (!numSprints.equals(other.numSprints))
			return false;
		if (numberCells == null) {
			if (other.numberCells != null)
				return false;
		} else if (!numberCells.equals(other.numberCells))
			return false;
		
		if (sprintDuration == null) {
			if (other.sprintDuration != null)
				return false;
		} else if (!sprintDuration.equals(other.sprintDuration))
			return false;

		if (technicalDebtSprint == null) {
			if (other.technicalDebtSprint != null)
				return false;
		} else if (!technicalDebtSprint.equals(other.technicalDebtSprint))
			return false;
		if (tecnDebtAccumulated == null) {
			if (other.tecnDebtAccumulated != null)
				return false;
		} else if (!tecnDebtAccumulated.equals(other.tecnDebtAccumulated))
			return false;

		
		return true;
	}

}