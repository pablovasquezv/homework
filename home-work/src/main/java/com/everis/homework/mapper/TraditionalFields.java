package com.everis.homework.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.everis.homework.dto.Matches;

import lombok.Data;

@Data
@Embeddable
public class TraditionalFields implements Matches {
	/** The Constant MAX_LENGHT. */
	private static final int MAX_LENGHT = 255;

	// Traditional Project
	/** The current project phase. */
	@Column(name = "CURRENT_PROJECT_PHASE", length = MAX_LENGHT)
	private String currentProjectPhase;

	/** The actual percentage. */
	@Column(name = "ACTUAL_PERCENTAGE")
	private BigDecimal actualPercentage;

	/** The planned percentage. */
	@Column(name = "PLANNED_PERCENTAGE")
	private BigDecimal plannedPercentage;

	/** The deviation percentage. */
	@Column(name = "DEVIATION_PERCENTAGE")
	private BigDecimal deviationPercentage;

	/** The relevant milestone one. */
	@Column(name = "REL_MIL_ONE", length = MAX_LENGHT)
	private String relevantMilestoneOne;

	/** The relevant milestone two. */
	@Column(name = "REL_MIL_TWO", length = MAX_LENGHT)
	private String relevantMilestoneTwo;

	/** The relevant milestone three. */
	@Column(name = "REL_MIL_THREE", length = MAX_LENGHT)
	private String relevantMilestoneThree;

	/** The relevant milestone four. */
	@Column(name = "REL_MIL_FOUR", length = MAX_LENGHT)
	private String relevantMilestoneFour;

	/** The relevant milestone five. */
	@Column(name = "REL_MIL_FIVE", length = MAX_LENGHT)
	private String relevantMilestoneFive;

	/** The milestone date one. */
	@Column(name = "MIL_DATE_ONE")
	private LocalDate milestoneDateOne;

	/** The milestone date two. */
	@Column(name = "MIL_DATE_TWO")
	private LocalDate milestoneDateTwo;

	/** The milestone date three. */
	@Column(name = "MIL_DATE_THREE")
	private LocalDate milestoneDateThree;

	/** The milestone date four. */
	@Column(name = "MIL_DATE_FOUR")
	private LocalDate milestoneDateFour;

	/** The milestone date five. */
	@Column(name = "MIL_DATE_FIVE")
	private LocalDate milestoneDateFive;

	/** The actual progress percentage one. */
	@Column(name = "PROGRESS_PER_ONE")
	private BigDecimal actualProgressPercentageOne;

	/** The actual progress percentage two. */
	@Column(name = "PROGRESS_PER_TWO")
	private BigDecimal actualProgressPercentageTwo;

	/** The actual progress percentage three. */
	@Column(name = "PROGRESS_PER_THREE")
	private BigDecimal actualProgressPercentageThree;

	/** The actual progress percentage four. */
	@Column(name = "PROGRESSL_PER_FOUR")
	private BigDecimal actualProgressPercentageFour;

	/** The actual progress percentage five. */
	@Column(name = "PROGRESS_PER_FIVE")
	private BigDecimal actualProgressPercentageFive;

	/** The compliance percentage one. */
	@Column(name = "COMPL_PER_ONE")
	private BigDecimal compliancePercentageOne;

	/** The compliance percentage two. */
	@Column(name = "COMPL_PER_TWO")
	private BigDecimal compliancePercentageTwo;

	/** The compliance percentage three. */
	@Column(name = "COMPL_PER_THREE")
	private BigDecimal compliancePercentageThree;

	/** The compliance percentage four. */
	@Column(name = "COMPL_PER_FOUR")
	private BigDecimal compliancePercentageFour;

	/** The compliance percentage five. */
	@Column(name = "COMPL_PER_FIVE")
	private BigDecimal compliancePercentageFive;

	/** The deviation percentage one. */
	@Column(name = "DEV_PER_ONE")
	private BigDecimal deviationPercentageOne;

	/** The deviation percentage two. */
	@Column(name = "DEV_PER_TWO")
	private BigDecimal deviationPercentageTwo;

	/** The deviation percentage three. */
	@Column(name = "DEV_PER_THREE")
	private BigDecimal deviationPercentageThree;

	/** The deviation percentage four. */
	@Column(name = "DEV_PER_FOUR")
	private BigDecimal deviationPercentageFour;

	/** The deviation percentage five. */
	@Column(name = "DEV_PER_FIVE")
	private BigDecimal deviationPercentageFive;

	public boolean matches(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			obj = new TraditionalFields();
		if (getClass() != obj.getClass())
			return false;
		TraditionalFields other = (TraditionalFields) obj;

		if (actualPercentage == null) {
			if (other.actualPercentage != null)
				return false;
		} else if (actualPercentage.compareTo(other.actualPercentage) != 0)
			return false;
		if (actualProgressPercentageFive == null) {
			if (other.actualProgressPercentageFive != null)
				return false;
		} else if (actualProgressPercentageFive.compareTo(other.actualProgressPercentageFive) != 0)
			return false;
		if (actualProgressPercentageFour == null) {
			if (other.actualProgressPercentageFour != null)
				return false;
		} else if (actualProgressPercentageFour.compareTo(other.actualProgressPercentageFour) != 0)
			return false;
		if (actualProgressPercentageOne == null) {
			if (other.actualProgressPercentageOne != null)
				return false;
		} else if (actualProgressPercentageOne.compareTo(other.actualProgressPercentageOne) != 0)
			return false;
		if (actualProgressPercentageThree == null) {
			if (other.actualProgressPercentageThree != null)
				return false;
		} else if (actualProgressPercentageThree.compareTo(other.actualProgressPercentageThree) != 0)
			return false;
		if (actualProgressPercentageTwo == null) {
			if (other.actualProgressPercentageTwo != null)
				return false;
		} else if (actualProgressPercentageTwo.compareTo(other.actualProgressPercentageTwo) != 0)
			return false;
		if (compliancePercentageFive == null) {
			if (other.compliancePercentageFive != null)
				return false;
		} else if (compliancePercentageFive.compareTo(other.compliancePercentageFive) != 0)
			return false;
		if (compliancePercentageFour == null) {
			if (other.compliancePercentageFour != null)
				return false;
		} else if (compliancePercentageFour.compareTo(other.compliancePercentageFour) != 0)
			return false;
		if (compliancePercentageOne == null) {
			if (other.compliancePercentageOne != null)
				return false;
		} else if (compliancePercentageOne.compareTo(other.compliancePercentageOne) != 0)
			return false;
		if (compliancePercentageThree == null) {
			if (other.compliancePercentageThree != null)
				return false;
		} else if (compliancePercentageThree.compareTo(other.compliancePercentageThree) != 0)
			return false;
		if (compliancePercentageTwo == null) {
			if (other.compliancePercentageTwo != null)
				return false;
		} else if (compliancePercentageTwo.compareTo(other.compliancePercentageTwo) != 0)
			return false;
		if (currentProjectPhase == null) {
			if (other.currentProjectPhase != null)
				return false;
		} else if (!currentProjectPhase.equals(other.currentProjectPhase))
			return false;
		if (milestoneDateFive == null) {
			if (other.milestoneDateFive != null)
				return false;
		} else if (!milestoneDateFive.equals(other.milestoneDateFive))
			return false;
		if (milestoneDateFour == null) {
			if (other.milestoneDateFour != null)
				return false;
		} else if (!milestoneDateFour.equals(other.milestoneDateFour))
			return false;
		if (milestoneDateOne == null) {
			if (other.milestoneDateOne != null)
				return false;
		} else if (!milestoneDateOne.equals(other.milestoneDateOne))
			return false;
		if (milestoneDateThree == null) {
			if (other.milestoneDateThree != null)
				return false;
		} else if (!milestoneDateThree.equals(other.milestoneDateThree))
			return false;
		if (milestoneDateTwo == null) {
			if (other.milestoneDateTwo != null)
				return false;
		} else if (!milestoneDateTwo.equals(other.milestoneDateTwo))
			return false;
		if (plannedPercentage == null) {
			if (other.plannedPercentage != null)
				return false;
		} else if (plannedPercentage.compareTo(other.plannedPercentage) != 0)
			return false;

		if (relevantMilestoneFive == null) {
			if (other.relevantMilestoneFive != null)
				return false;
		} else if (!relevantMilestoneFive.equals(other.relevantMilestoneFive))
			return false;
		if (relevantMilestoneFour == null) {
			if (other.relevantMilestoneFour != null)
				return false;
		} else if (!relevantMilestoneFour.equals(other.relevantMilestoneFour))
			return false;
		if (relevantMilestoneOne == null) {
			if (other.relevantMilestoneOne != null)
				return false;
		} else if (!relevantMilestoneOne.equals(other.relevantMilestoneOne))
			return false;
		if (relevantMilestoneThree == null) {
			if (other.relevantMilestoneThree != null)
				return false;
		} else if (!relevantMilestoneThree.equals(other.relevantMilestoneThree))
			return false;
		if (relevantMilestoneTwo == null) {
			if (other.relevantMilestoneTwo != null)
				return false;
		} else if (!relevantMilestoneTwo.equals(other.relevantMilestoneTwo))
			return false;

		return true;
	}
}
