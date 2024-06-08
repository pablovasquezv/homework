package com.everis.homework.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.everis.homework.dto.ProjectAlarm;
import com.everis.homework.dto.VisualProjectHistory;
import com.everis.homework.dto.VisualProjectTracking;
import com.everis.homework.dto.VisualProjectTracking.AlertLevel;
import com.everis.homework.dto.VisualProjectTracking.ProjectType;
import com.everis.homework.mapper.Funding;
import com.everis.homework.mapper.Modification;
import com.everis.homework.mapper.ProjectHistory;
import com.everis.homework.mapper.ProjectProfile;
import com.everis.homework.mapper.ProjectTracking;
import com.everis.homework.mapper.Range;
import com.everis.homework.mapper.ServiceFields;
import com.everis.homework.mapper.Survay;
import com.everis.homework.mapper.TraditionalFields;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

/**
 * 
 * @author sgutierc
 *
 */
public class ProjectTrackingHelper {
	private static Logger logger = LoggerFactory.getLogger(ProjectTrackingHelper.class);

	public static enum Case {
		ONE, TWO, THREE, FOUR, FIVE
	};

	private static Mapper dozerMapper;
	static {
		dozerMapper = DozerBeanMapperBuilder.create().withMappingFiles("ProjectHistoryConverter.xml").build();
		
	}

	public ProjectTrackingHelper() {

	}

	public static VisualProjectHistory toVisual(ProjectHistory ph) {
		return dozerMapper.map(ph, VisualProjectHistory.class);
	}
	/**
	 * 
	 * @param vpt
	 * @return
	 */
	private static boolean isDraftProject(VisualProjectTracking vpt) {
		if (vpt == null || vpt.getTraditional() == null)
			return true;
		if (vpt.getTraditional().getDeviationPercentage() == null ||
		// case percentage one
				(vpt.getTraditional().getDeviationPercentageOne() == null && vpt.getTraditional().getDeviationPercentageTwo() != null)
				|| (vpt.getTraditional().getDeviationPercentageOne() == null && vpt.getTraditional().getDeviationPercentageThree() != null)
				|| (vpt.getTraditional().getDeviationPercentageOne() == null && vpt.getTraditional().getDeviationPercentageFour() != null)
				|| (vpt.getTraditional().getDeviationPercentageOne() == null && vpt.getTraditional().getDeviationPercentageFive() != null) ||
				// case percentage two
				(vpt.getTraditional().getDeviationPercentageTwo() == null && vpt.getTraditional().getDeviationPercentageThree() != null)
				|| (vpt.getTraditional().getDeviationPercentageTwo() == null && vpt.getTraditional().getDeviationPercentageFour() != null)
				|| (vpt.getTraditional().getDeviationPercentageTwo() == null && vpt.getTraditional().getDeviationPercentageFive() != null) ||
				// case percentage three
				(vpt.getTraditional().getDeviationPercentageThree() == null && vpt.getTraditional().getDeviationPercentageFour() != null)
				|| (vpt.getTraditional().getDeviationPercentageThree() == null && vpt.getTraditional().getDeviationPercentageFive() != null) ||
				// case percentage four
				(vpt.getTraditional().getDeviationPercentageFour() == null && vpt.getTraditional().getDeviationPercentageFive() != null))
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @param vpt
	 * @return
	 */
	private static boolean isDraftService(VisualProjectTracking vpt) {
		if (vpt == null || vpt.getService() == null)
			return true;
		if (vpt.getService().getPercentageReqResolved() == null && vpt.getService().getPercentageEstDelivered() == null)
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @param vpt
	 * @return
	 */
	private static boolean isDraftAgile(VisualProjectTracking vpt) {
		if (vpt == null)
			return true;
		if (vpt.getNumFinishedSprintStories() == null || vpt.getNumSprintPlannedHistories() == null)
			return true;
		return false;
	}

	/**
	 * Carga la logica de negocio al objeto visual
	 * 
	 * @param vpt
	 * @return
	 */
//	public static VisualProjectTracking loadBusinessLogic(VisualProjectTracking vpt) {
//		VisualProjectTracking.ProjectType type = vpt.getProjectTypeClass();
//
//		BigDecimal ProjectMargin = new BigDecimal("10");// 10%
//		BigDecimal ServiceMargin = new BigDecimal("98");// 98%
//
//		AlertLevel alert = AlertLevel.LOW;
//		switch (type) {
//		case PROJECT: {
//			// Si los campos de proyecto no se han informado, entonces esta en modo Draft.
//			if (isDraftProject(vpt)) {
//				alert = AlertLevel.DRAFT;
//				break;
//			}
//			// Campo % Desvio total del proyecto >=10% ==> semaforo rojo
//			if (vpt.getDeviationPercentage() != null && vpt.getDeviationPercentage().compareTo(ProjectMargin) >= 0)
//				alert = AlertLevel.HIGH;
//			// Si 2 o mas hitos estan con desvio ==> semaforo rojo
//			if (alert != AlertLevel.HIGH) {
//				int cont = 0;
//				if (vpt.getDeviationPercentageOne() != null
//						&& vpt.getDeviationPercentageOne().compareTo(ProjectMargin) >= 0)
//					cont++;
//				if (vpt.getDeviationPercentageTwo() != null
//						&& vpt.getDeviationPercentageTwo().compareTo(ProjectMargin) >= 0)
//					cont++;
//				if (vpt.getDeviationPercentageThree() != null
//						&& vpt.getDeviationPercentageThree().compareTo(ProjectMargin) >= 0)
//					cont++;
//				if (vpt.getDeviationPercentageFour() != null
//						&& vpt.getDeviationPercentageFour().compareTo(ProjectMargin) >= 0)
//					cont++;
//				if (vpt.getDeviationPercentageFive() != null
//						&& vpt.getDeviationPercentageFive().compareTo(ProjectMargin) >= 0)
//					cont++;
//				if (cont >= 2)
//					alert = AlertLevel.HIGH;
//			}
//			if (alert != AlertLevel.HIGH) {
//				// Campo % Desvio total del proyecto >5% y <10% ==> semaforo amarillo
//				if (vpt.getDeviationPercentage() != null
//						&& vpt.getDeviationPercentage().compareTo(new BigDecimal("5")) > 0
//						&& vpt.getDeviationPercentage().compareTo(ProjectMargin) < 0)
//					alert = AlertLevel.MEDIUM;
//				// % de desvio del Hito N >=10% ==> semaforo amarillo
//				if ((vpt.getDeviationPercentageOne() != null
//						&& vpt.getDeviationPercentageOne().compareTo(ProjectMargin) >= 0)
//						|| (vpt.getDeviationPercentageTwo() != null
//								&& vpt.getDeviationPercentageTwo().compareTo(ProjectMargin) >= 0)
//						|| (vpt.getDeviationPercentageThree() != null
//								&& vpt.getDeviationPercentageThree().compareTo(ProjectMargin) >= 0)
//						|| (vpt.getDeviationPercentageFour() != null
//								&& vpt.getDeviationPercentageFour().compareTo(ProjectMargin) >= 0)
//						|| (vpt.getDeviationPercentageFive() != null
//								&& vpt.getDeviationPercentageFive().compareTo(ProjectMargin) >= 0))
//					alert = AlertLevel.MEDIUM;
//			}
//
//			break;
//		}
//		case SERVICE: {
//			// Si los campos del Servicio no se han informado, entonces esta en modo Draft.
//			if (isDraftService(vpt)) {
//				alert = AlertLevel.DRAFT;
//				break;
//			}
//
//			// % de requerimientos resueltos en plazo o % de estimaciones entregadas en
//			// plazo < 98% ==> semaforo rojo
//			if ((vpt.getPercentageReqResolved() != null && vpt.getPercentageReqResolved().compareTo(ServiceMargin) < 0)
//					|| (vpt.getPercentageEstDelivered() != null
//							&& vpt.getPercentageEstDelivered().compareTo(ServiceMargin) < 0))
//				alert = AlertLevel.HIGH;
//
//			if (alert != AlertLevel.HIGH) {
//				// % de requerimientos resueltos en plazo o % de estimaciones entregadas en
//				// plazo < 100% y >= 98%==> semaforo amarillo
//				if ((vpt.getPercentageReqResolved() != null
//						&& vpt.getPercentageReqResolved().compareTo(new BigDecimal("100")) < 0
//						&& vpt.getPercentageReqResolved().compareTo(ServiceMargin) >= 0)
//						|| (vpt.getPercentageEstDelivered() != null
//								&& vpt.getPercentageEstDelivered().compareTo(new BigDecimal("100")) < 0
//								&& vpt.getPercentageEstDelivered().compareTo(ServiceMargin) >= 0)) {
//					alert = AlertLevel.MEDIUM;
//				}
//			}
//			break;
//		}
//		/**
//		 * Proyecto Agile: Campo Indicador de cumplimiento del Sprint: Si num de
//		 * historias terminadas en el Sprint < Num de historias planificadas en el
//		 * Sprint semaforo rojo
//		 */
//		case PROJECT_AGILE: {
//			// Si los campos del proyecto agile no se han informado, entonces esta en modo
//			// Draft.
//			if (isDraftAgile(vpt)) {
//				alert = AlertLevel.DRAFT;
//				break;
//			}
//
//			if (vpt.getNumFinishedSprintStories() != null && vpt.getNumSprintPlannedHistories() != null
//					&& vpt.getNumFinishedSprintStories() < vpt.getNumSprintPlannedHistories())
//				alert = AlertLevel.HIGH;
//			break;
//		}
//		default:
//			alert = AlertLevel.DEFAULT;
//			break;
//		}
//
//		vpt.setAlertLevel(alert.getValue());
//		return vpt;
//	}

	private static ProjectAlarm getAlarm(int order, String name, AlertLevel level, ProjectType type,
			String description) {
		ProjectAlarm alarm = new ProjectAlarm();
		alarm.loadLevel(level);
		alarm.setName(name);
		alarm.setType(type.name());
		alarm.setDescription(description);
		alarm.setOrder(order);
		return alarm;
	}

	/**
	 * 
	 * @param vpt
	 * @return
	 */
	private static ProjectAlarm getServiceAlarm(VisualProjectTracking vpt) {
		
		ProjectType type = vpt.getProjectTypeClass();
		ProjectAlarm firstLevel = getAlarm(0, "ServiceAlarm", AlertLevel.LOW, type, "Sin alertas");
 
		Range redMargin = vpt.getRedMargin();
		Range yellowMargin = vpt.getYellowMargin();

		// Primero revisamos las alarmas de primer nivel:

		// Si los campos del Servicio no se han informado, entonces esta en modo Draft.
		if (isDraftService(vpt)) {
			firstLevel.loadLevel(AlertLevel.DRAFT, "Faltan campos por informar");
			return firstLevel;
		}
		// red
		// red.bottom<=X<red.top => alarma red

		// % de requerimientos resueltos en plazo o % de estimaciones entregadas en
		// plazo < 98% ==> semaforo rojo
		if (vpt.getService().getPercentageReqResolved() != null
				&& (vpt.getService().getPercentageReqResolved().compareTo(redMargin.getBottom()) >= 0
						&& vpt.getService().getPercentageReqResolved().compareTo(redMargin.getTop()) < 0)) {
			firstLevel.loadLevel(AlertLevel.HIGH,
					String.format("%% de requerimientos resueltos en plazo %% %.1f < x <= %% %.1f",
							redMargin.getBottom(), redMargin.getTop()));
			return firstLevel;
		}
		if (vpt.getService().getPercentageEstDelivered() != null
				&& (vpt.getService().getPercentageEstDelivered().compareTo(redMargin.getBottom()) >= 0
						&& vpt.getService().getPercentageEstDelivered().compareTo(redMargin.getTop()) < 0)) {
			firstLevel.loadLevel(AlertLevel.HIGH,
					String.format("%% de estimaciones entregadas en plazo %% %.1f < x <= %% %.1f",
							redMargin.getBottom(), redMargin.getTop()));
			return firstLevel;
		}
		// yellow
		// yellow.bottom<=X<yellow.top => alarma yellow

		// % de requerimientos resueltos en plazo o % de estimaciones entregadas en
		// plazo < 100% y >= 98%==> semaforo amarillo
		if (vpt.getService().getPercentageReqResolved() != null
				&& (vpt.getService().getPercentageReqResolved().compareTo(yellowMargin.getBottom()) >= 0
						&& vpt.getService().getPercentageReqResolved().compareTo(yellowMargin.getTop()) < 0)) {
			firstLevel.loadLevel(AlertLevel.MEDIUM,
					String.format("%% de requerimientos resueltos en plazo %% %.1f < x <= %% %.1f",
							yellowMargin.getBottom(), yellowMargin.getTop()));
			return firstLevel;
		}

		if (vpt.getService().getPercentageEstDelivered() != null
				&& (vpt.getService().getPercentageEstDelivered().compareTo(yellowMargin.getBottom()) >= 0
						&& vpt.getService().getPercentageEstDelivered().compareTo(yellowMargin.getTop()) < 0)) {
			firstLevel.loadLevel(AlertLevel.MEDIUM,
					String.format("%% de estimaciones entregadas en plazo  %%%.1f < x <= %%%.1f",
							yellowMargin.getBottom(), yellowMargin.getTop()));
			return firstLevel;
		}

		return firstLevel;
	}

	/**
	 * 
	 * @param vpt
	 * @return
	 */
	private static ProjectAlarm getProjectAlarms(VisualProjectTracking vpt) {
		
		BigDecimal ProjectMargin = new BigDecimal("10");// 10%
		ProjectType type = vpt.getProjectTypeClass();
		ProjectAlarm firstLevel = getAlarm(0, "ProjectAlarm", AlertLevel.LOW, type, "Sin alertas");
 
		// Primero revisamos las alarmas de primer nivel:

		// Si los campos de proyecto no se han informado, entonces esta en modo Draft.
		if (isDraftProject(vpt)) {
			firstLevel.loadLevel(AlertLevel.DRAFT, "Faltan campos por informar");
		}
		// Campo % Desvio total del proyecto >=10% ==> semaforo rojo
		if (firstLevel.isNot(AlertLevel.DRAFT) && vpt.getTraditional().getDeviationPercentage() != null
				&& vpt.getTraditional().getDeviationPercentage().compareTo(ProjectMargin) >= 0)
			firstLevel.loadLevel(AlertLevel.HIGH, "% Desvio total del proyecto >= 10%");

		// Si 2 o mas hitos estan con desvio ==> semaforo rojo
		if (firstLevel.isNot(AlertLevel.DRAFT) && firstLevel.isNot(AlertLevel.HIGH)) {
			int cont = 0;
			if (vpt.getTraditional().getDeviationPercentageOne() != null
					&& vpt.getTraditional().getDeviationPercentageOne().compareTo(ProjectMargin) >= 0)
				cont++;
			if (vpt.getTraditional().getDeviationPercentageTwo() != null
					&& vpt.getTraditional().getDeviationPercentageTwo().compareTo(ProjectMargin) >= 0)
				cont++;
			if (vpt.getTraditional().getDeviationPercentageThree() != null
					&& vpt.getTraditional().getDeviationPercentageThree().compareTo(ProjectMargin) >= 0)
				cont++;
			if (vpt.getTraditional().getDeviationPercentageFour() != null
					&& vpt.getTraditional().getDeviationPercentageFour().compareTo(ProjectMargin) >= 0)
				cont++;
			if (vpt.getTraditional().getDeviationPercentageFive() != null
					&& vpt.getTraditional().getDeviationPercentageFive().compareTo(ProjectMargin) >= 0)
				cont++;
			if (cont >= 2)
				firstLevel.loadLevel(AlertLevel.HIGH, "Dos o mas hitos estan con desvio");
		}
		// Campo % Desvio total del proyecto >5% y <10% ==> semaforo amarillo
		if ((firstLevel.isNot(AlertLevel.DRAFT) && firstLevel.isNot(AlertLevel.HIGH))
				&& vpt.getTraditional().getDeviationPercentage() != null
				&& vpt.getTraditional().getDeviationPercentage().compareTo(new BigDecimal("5")) > 0
				&& vpt.getTraditional().getDeviationPercentage().compareTo(ProjectMargin) < 0)
			firstLevel.loadLevel(AlertLevel.MEDIUM, "% Desvio total del proyecto > 5% y < 10%");

		// % de desvio del Hito N >=10% ==> semaforo amarillo
		else if ((firstLevel.isNot(AlertLevel.DRAFT) && firstLevel.isNot(AlertLevel.HIGH))
				&& ((vpt.getTraditional().getDeviationPercentageOne() != null
						&& vpt.getTraditional().getDeviationPercentageOne().compareTo(ProjectMargin) >= 0)
						|| (vpt.getTraditional().getDeviationPercentageTwo() != null
								&& vpt.getTraditional().getDeviationPercentageTwo().compareTo(ProjectMargin) >= 0)
						|| (vpt.getTraditional().getDeviationPercentageThree() != null
								&& vpt.getTraditional().getDeviationPercentageThree().compareTo(ProjectMargin) >= 0)
						|| (vpt.getTraditional().getDeviationPercentageFour() != null
								&& vpt.getTraditional().getDeviationPercentageFour().compareTo(ProjectMargin) >= 0)
						|| (vpt.getTraditional().getDeviationPercentageFive() != null
								&& vpt.getTraditional().getDeviationPercentageFive().compareTo(ProjectMargin) >= 0)))
			firstLevel.loadLevel(AlertLevel.MEDIUM, "% de desvio del Hito N >= 10%");

		return firstLevel;
	}

	/**
	 * 
	 * @param vpt
	 * @return
	 */
	private static ProjectAlarm getAgileAlarms(VisualProjectTracking vpt) {
		ProjectType type = vpt.getProjectTypeClass();
		ProjectAlarm firstLevel = getAlarm(0, "AgileAlarm", AlertLevel.LOW, type, "Sin alertas");

		// Primero revisamos las alarmas de primer nivel:

		// Si los campos del proyecto agile no se han informado, entonces esta en modo
		// Draft.
		if (isDraftAgile(vpt)) {
			firstLevel.loadLevel(AlertLevel.DRAFT, "Faltan campos por informar");
		}
		// Si N° de historias terminadas en el Sprint < N° de historias planificadas
		// en
		// el Sprint ==> Semaforo rojo
		if (vpt.getNumFinishedSprintStories() != null && vpt.getNumSprintPlannedHistories() != null
				&& vpt.getNumFinishedSprintStories() < vpt.getNumSprintPlannedHistories()) {
			if (firstLevel.isNot(AlertLevel.DRAFT))
				firstLevel.loadLevel(AlertLevel.HIGH,
						"N° de historias terminadas en el Sprint < N° de historias planificadas en el Sprint");
		}
		return firstLevel;
	}

	private static boolean requireActionPlane(Survay survay) {
		boolean required = false;
		if (survay != null && survay.getActionPlane() != null && survay.getActionPlane().toUpperCase().equals("SI"))
			required = true;
		return required;
	}

	private static ProjectAlarm checkStaffAlarms(VisualProjectTracking vpt) {
		ProjectType type = vpt.getProjectTypeClass();
		ProjectAlarm firstLevel = getAlarm(0, "StaffAlarm", AlertLevel.DRAFT, type, "Sin alertas");

		Survay survay = vpt.getSurvayReport();
		Set<ProjectProfile> profiles = vpt.getStaff().getDetailProjectProfileList();

		if ((profiles == null || profiles.size() == 0) && (requireActionPlane(survay) == false)) {
			firstLevel = getAlarm(0, "StaffAlarm", AlertLevel.DRAFT, type,
					"No tiene datos y no requiere plan de acción");
		}

		if ((profiles == null || profiles.size() == 0) && (requireActionPlane(survay) == true)) {
			firstLevel = getAlarm(0, "StaffAlarm", AlertLevel.HIGH, type, "No tiene datos y requiere plan de acción");
		}

		if ((profiles != null && profiles.size() > 0) && (requireActionPlane(survay) == true)) {
			firstLevel = getAlarm(0, "StaffAlarm", AlertLevel.HIGH, type, "Tiene datos y requiere plan de acción");
		}

		if ((profiles != null && profiles.size() > 0) && (requireActionPlane(survay) == false)) {
			firstLevel = getAlarm(0, "StaffAlarm", AlertLevel.LOW, type, "Tiene datos y no requiere plan de acción");
		}

		return firstLevel;
	}

	/**
	 * Carga la logica de negocio al objeto visual
	 * 
	 * @param vpt
	 * @return
	 */
	public static ProjectAlarm getAlarmsFor(VisualProjectTracking vpt) {
		VisualProjectTracking.ProjectType type = vpt.getProjectTypeClass();
		ProjectAlarm pa = new ProjectAlarm();

		if (vpt.getServiceLine() != null && vpt.getServiceLine().toUpperCase().equals("STAFF AUGMENTATION")) {
			pa = checkStaffAlarms(vpt);
		} else {
			switch (type) {
			case PROJECT: {
				pa = getProjectAlarms(vpt);
				break;
			}
			case SERVICE: {
				pa = getServiceAlarm(vpt);
				break;
			}
			case PROJECT_AGILE: {
				pa = getAgileAlarms(vpt);
				break;
			}
			default:
				break;
			}
		}
		pa.setProjectCode(vpt.getProjectCode());
		return pa;
	}

	/**
	 * =% de Avance Planificado del Proyecto - % de Avance Real del Proyecto
	 * 
	 * @param pt
	 * @return
	 */
	public static BigDecimal calculateDeviationPercentage(ProjectTracking pt) {
		// =% de Avance Planificado del Proyecto - % de Avance Real del Proyecto
		if (pt.getTraditional() == null || pt.getTraditional().getPlannedPercentage() == null
				|| pt.getTraditional().getActualPercentage() == null) {
			logger.error("Error calculating deviation percetage: empty values");
			return null;

		}
		return pt.getTraditional().getPlannedPercentage().subtract(pt.getTraditional().getActualPercentage());
	}

	/**
	 * =% de Avance Planificado del Proyecto - % de Avance Real del Proyecto
	 * 
	 * @param pt
	 * @param opcion
	 * @return
	 */
	public static BigDecimal calculateDeviationPercentage(ProjectTracking pt, Case opcion) {
		BigDecimal value = null;
		TraditionalFields tf = pt.getTraditional();
		if (tf == null)
			return null;

		try {
			switch (opcion) {
			case ONE: {
				// '=% de Avance Planificado del Hito 1 - % de Avance Real del Hito 1
				if (tf.getCompliancePercentageOne() == null || tf.getActualProgressPercentageOne() == null)
					break;
				value = tf.getCompliancePercentageOne().subtract(tf.getActualProgressPercentageOne());
				break;
			}
			case TWO: {
				// '=% de Avance Planificado del Hito 2 - % de Avance Real del Hito 2
				if (tf.getCompliancePercentageTwo() == null || tf.getActualProgressPercentageTwo() == null)
					break;
				value = tf.getCompliancePercentageTwo().subtract(tf.getActualProgressPercentageTwo());
				break;
			}
			case THREE: {
				// '=% de Avance Planificado del Hito 3 - % de Avance Real del Hito 3
				if (tf.getCompliancePercentageThree() == null || tf.getActualProgressPercentageThree() == null)
					break;
				value = tf.getCompliancePercentageThree().subtract(tf.getActualProgressPercentageThree());
				break;
			}
			case FOUR: {
				// '=% de Avance Planificado del Hito 4 - % de Avance Real del Hito 4
				if (tf.getCompliancePercentageFour() == null || tf.getActualProgressPercentageFour() == null)
					break;
				value = tf.getCompliancePercentageFour().subtract(tf.getActualProgressPercentageFour());
				break;
			}
			case FIVE: {
				// '=% de Avance Planificado del Hito 5 - % de Avance Real del Hito 5
				if (tf.getCompliancePercentageFive() == null || tf.getActualProgressPercentageFive() == null)
					break;
				value = tf.getCompliancePercentageFive().subtract(tf.getActualProgressPercentageFive());
				break;
			}
			default: {
				break;
			}
			}
		} catch (Exception e) {
			logger.error("Error calculating deviation percentage", e);
		}
		return value;
	}

	/**
	 * = (num de estimaciones entregadas en plazo / num de estimaciones solicitadas
	 * en el periodo)
	 * 
	 * @param pt
	 * @return
	 */
	public static BigDecimal calculatePercEstimationDelivered(ProjectTracking pt) {
		ServiceFields sf = pt.getService();
		if (sf == null || (sf.getTotalEstRequested() == null || sf.getTotalEstRequested() == 0)
				|| sf.getNumEstDelivered() == null) {
			logger.error("Error calculating percentage estimation delivered");
			return null;
		}
		double value = (double) sf.getNumEstDelivered() / sf.getTotalEstRequested();

		return new BigDecimal(value * 100);

	}

	/**
	 * = num de requerimientos resultos en plazo / num de requerimientos solicitadas
	 * en el periodo
	 * 
	 * @param pt
	 * @return
	 */
	public static BigDecimal calculatePercRequirementResolved(ProjectTracking pt) {
		ServiceFields sf = pt.getService();
		if (sf == null || (sf.getTotalReqRequested() == null || sf.getTotalReqRequested() == 0)
				|| sf.getNumReqDelivered() == null) {
			logger.error("Error calculating percentage requirement resolved");
			return null;
		}
		double value = (double) sf.getNumReqDelivered() / sf.getTotalReqRequested();
		return new BigDecimal(value * 100);
	}

	public static BigDecimal calculateSumTarifas(Set<ProjectProfile> profiles) {
		BigDecimal value = new BigDecimal(0);
		for (ProjectProfile pp : profiles) {
			if (pp.getTarifa() != null)
				value = value.add(pp.getTarifa());
		}
		return value;
	}

//	public static BigDecimal calculateSumTarifas(Set<ProjectProfile> profiles) {
//	    BigDecimal value = BigDecimal.ZERO;
//	    for (ProjectProfile pp: profiles) {
//	        value = value.add(pp.getTarifa() != null ? pp.getTarifa() : new BigDecimal(0));
//	    }
//	    return value;
//	}

	/**
	 * Promedio de velocidad de los equipos = num de historias terminadas en el
	 * Sprint/num de Sprints
	 * 
	 * @param pt
	 * @return
	 */
	public static BigDecimal calculateTeamSpeedAver(ProjectTracking pt) {
		if ((pt.getNumSprints() == null || pt.getNumSprints() == 0) || pt.getNumFinishedSprintStories() == null) {
			logger.error("Error calculating team speed average");
			return null;
		}

		double value = (double) pt.getNumFinishedSprintStories() / pt.getNumSprints();
		return new BigDecimal(value);
	}

	/** calcula lo incurrido vs el fundado **/
	public static BigDecimal calculateIncurredVsFunding(Funding f) {
		BigDecimal value = null;
		value = null;
		if (f.getFunding() == null || f.getNrIncurrido() == null) {
			logger.error("Error calculating versus");
			return null;
		}
		if (f.getFunding().compareTo(new BigDecimal(0)) == 0) {
			logger.error("Invalid Funding value");
			return null;
		}
		value = f.getNrIncurrido().divide(f.getFunding(), 3, RoundingMode.HALF_EVEN);
		return value;
	}

	public static BigDecimal calculateWipVsNrn(Funding f) {
		BigDecimal value = null;
		value = null;
		if (f.getWip() == null || f.getNRN() == null) {
			logger.error("Error calculating wipvsnrn");
			return value;
		}
		if (f.getNRN().compareTo(new BigDecimal(0)) == 0) {
			logger.error("Invalid NRN value");
			return null;
		}
		value = f.getWip().divide(f.getNRN(), 1, RoundingMode.HALF_EVEN);
		return value;
	}

	/**
	 * "SI num de historias terminadas en el Sprint < num de historias planificadas
	 * en el Sprint = CUMPLE SI NO = NO CUMPLE"
	 * 
	 * @param pt
	 * @return
	 */
	public static String calculateSprintComplianceInd(ProjectTracking pt) {
		if (pt.getNumFinishedSprintStories() == null || pt.getNumSprintPlannedHistories() == null) {
			logger.error("Error calculating sprint compliance indicador: empty values");
			return "";
		}

		if (pt.getNumFinishedSprintStories() < pt.getNumSprintPlannedHistories())
			return "NO CUMPLE";
		else
			return "CUMPLE";
	}

	

	/**
	 * Transforma una instancia de ProjectTracking a una de tipo
	 * VisualProjectTracking
	 * 
	 * @param pt
	 * @return
	 */
	public static VisualProjectTracking toVisual(ProjectTracking pt) {
		if (pt == null)
			return new VisualProjectTracking();

		VisualProjectTracking vpt = dozerMapper.map(pt, VisualProjectTracking.class);
		// customs
		vpt.setProjectTypeClass(ProjectType.getType(pt.getProjectTypeCode()));

		if (vpt.getRedMargin() == null && vpt.getYellowMargin() == null) {
			// && vpt.getProjectTypeClass() == ProjectType.SERVICE) {
			// se establecen margenes por defecto para SERVICIOS
			Range redRange = new Range(98, 0);
			Range yellowRange = new Range(100, 98);

			vpt.setRedMargin(redRange);
			vpt.setYellowMargin(yellowRange);
		}

		vpt.setAlarm(getAlarmsFor(vpt));
		vpt.setRealEndDate(pt.getRealEndDate() != null ? pt.getRealEndDate() : pt.getEndDate());

		if (vpt.getModification() != null && vpt.getModification().getUsername() == null) {
			Modification md = vpt.getModification();
			md.setUsername("sistema");
			vpt.setModification(md);
		}
		/*
		 * vpt.setProjectCode(pt.getProjectCode());
		 * vpt.setProjectType(pt.getProjectType());
		 * vpt.setProjectTypeCode(pt.getProjectTypeCode());
		 * vpt.setClient(pt.getClient()); vpt.setSector(pt.getSector());
		 * vpt.setUn(pt.getUn()); vpt.setCe(pt.getCe());
		 * vpt.setServiceLine(pt.getServiceLine());
		 * vpt.setProjectManager(pt.getProjectManager());
		 * vpt.setStartDate(pt.getStartDate());
		 * vpt.setLastModified(pt.getLastModified()); vpt.setEndDate(pt.getEndDate());
		 * vpt.setNumFinishedSprintStories(pt.getNumFinishedSprintStories());
		 * vpt.setNumReleases(pt.getNumReleases());
		 * vpt.setNumSprints(pt.getNumSprints());
		 * vpt.setTeamSpeedAverage(pt.getTeamSpeedAverage());
		 * vpt.setCurrentProjectPhase(pt.getCurrentProjectPhase());
		 * vpt.setActualPercentage(pt.getActualPercentage());
		 * vpt.setPlannedPercentage(pt.getPlannedPercentage());
		 * vpt.setDeviationPercentage(pt.getDeviationPercentage());
		 * vpt.setRelevantMilestoneOne(pt.getRelevantMilestoneOne());
		 * vpt.setRelevantMilestoneTwo(pt.getRelevantMilestoneTwo());
		 * vpt.setRelevantMilestoneThree(pt.getRelevantMilestoneThree());
		 * vpt.setRelevantMilestoneFour(pt.getRelevantMilestoneFour());
		 * vpt.setRelevantMilestoneFive(pt.getRelevantMilestoneFive());
		 * vpt.setMilestoneDateOne(pt.getMilestoneDateOne());
		 * vpt.setMilestoneDateTwo(pt.getMilestoneDateTwo());
		 * vpt.setMilestoneDateThree(pt.getMilestoneDateThree());
		 * vpt.setMilestoneDateFour(pt.getMilestoneDateFour());
		 * vpt.setMilestoneDateFive(pt.getMilestoneDateFive());
		 * vpt.setActualProgressPercentageOne(pt.getActualProgressPercentageOne());
		 * vpt.setActualProgressPercentageTwo(pt.getActualProgressPercentageTwo());
		 * vpt.setActualProgressPercentageThree(pt.getActualProgressPercentageThree());
		 * vpt.setActualProgressPercentageFour(pt.getActualProgressPercentageFour());
		 * vpt.setActualProgressPercentageFive(pt.getActualProgressPercentageFive());
		 * vpt.setCompliancePercentageOne(pt.getCompliancePercentageOne());
		 * vpt.setCompliancePercentageTwo(pt.getCompliancePercentageTwo());
		 * vpt.setCompliancePercentageThree(pt.getCompliancePercentageThree());
		 * vpt.setCompliancePercentageFour(pt.getCompliancePercentageFour());
		 * vpt.setCompliancePercentageFive(pt.getCompliancePercentageFive());
		 * vpt.setDeviationPercentageOne(pt.getDeviationPercentageOne());
		 * vpt.setDeviationPercentageTwo(pt.getDeviationPercentageTwo());
		 * vpt.setDeviationPercentageThree(pt.getDeviationPercentageThree());
		 * vpt.setDeviationPercentageFour(pt.getDeviationPercentageFour());
		 * vpt.setDeviationPercentageFive(pt.getDeviationPercentageFive());
		 * vpt.setPercentageEstDelivered(pt.getPercentageEstDelivered());
		 * vpt.setPercentageReqResolved(pt.getPercentageReqResolved());
		 * vpt.setIncidentsVStotal(pt.getIncidentsVStotal());
		 * vpt.setNumIncidentsRequested(pt.getNumIncidentsRequested());
		 * vpt.setNumEstDelivered(pt.getNumEstDelivered());
		 * vpt.setNumReqDelivered(pt.getNumReqDelivered());
		 * vpt.setTotalEstRequested(pt.getTotalEstRequested());
		 * vpt.setTotalReqRequested(pt.getTotalReqRequested());
		 * vpt.setNumSprintPlannedHistories(pt.getNumSprintPlannedHistories());
		 * vpt.setSprintComplInd(pt.getSprintComplInd());
		 * vpt.setProjectName(pt.getProjectName());
		 * vpt.setOperationalContinuity(pt.getOperationalContinuity());
		 * 
		 * vpt.setDeclaredExtension(pt.getDeclaredExtension());
		 * vpt.setExtAgainstIncome(pt.getExtAgainstIncome());
		 * vpt.setExtensionReason(pt.getExtensionReason());
		 * 
		 * vpt.setExtensionValidated(pt.getExtensionValidated());
		 * 
		 * vpt.setRedMargin(pt.getRedMargin());
		 * vpt.setYellowMargin(pt.getYellowMargin());
		 * vpt.setFundingReport(pt.getFundingReport());
		 * vpt.setSurvayReport(pt.getSurvayReport());
		 * vpt.setNumRollbakReqPer(pt.getNumRollbakReqPer());
		 * vpt.setRollbackProd(pt.getRollbackProd()); //casos de prueba
		 * vpt.setTestCasesIndex(pt.getTestCasesIndex());
		 * vpt.setTestCasesPlanned(pt.getTestCasesPlanned());
		 * vpt.setTestCasesReal(pt.getTestCasesReal()); //errores en prod
		 * vpt.setTotalReqClosedPeriod(pt.getTotalReqClosedPeriod());
		 * vpt.setTotalReqErrorPeriod(pt.getTotalReqErrorPeriod());
		 * vpt.setProdErrorIndex(pt.getProdErrorIndex()); //calidad del codigo
		 * vpt.setPeriodQualityComp(pt.getPeriodQualityComp());
		 */
		return vpt;
	}

	/**
	 * (Porcentage) Resultado del n de Rollbacks/Total de requerimientos cerrados
	 * 
	 * @param pt
	 * @return
	 */
	public static BigDecimal calculateRollbacks(ProjectTracking pt) {
		if (pt.getTechnical().getNumRollbakReqPer() == null || pt.getTechnical().getTotalReqClosedPeriod() == null) {
			logger.error("Error calculating Rollbacks: empty values");
			return null;
		}
		if (pt.getTechnical().getTotalReqClosedPeriod() == 0) {
			logger.error("Error calculating Rollbacks: cero divisor");
			return null;
		}

		double value = (double) pt.getTechnical().getNumRollbakReqPer() / pt.getTechnical().getTotalReqClosedPeriod();
		return new BigDecimal(value * 100);
	}

	/**
	 * (Porcentage) resultado del n de casos reales/casos planificados
	 * 
	 * @param pt
	 * @return
	 */
	public static Float calculateTestCaseIndex(ProjectTracking pt) {
		if (pt.getTechnical().getTestCasesPlanned() == null || pt.getTechnical().getTestCasesReal() == null) {
			logger.error("Error calculating TestCase Index: empty values");
			return null;
		}
		if (pt.getTechnical().getTestCasesPlanned() == 0) {
			logger.error("Error calculating TestCase Index: cero divisor");
			return null;
		}

		float value = (float) pt.getTechnical().getTestCasesReal() / pt.getTechnical().getTestCasesPlanned();
		return new Float(value * 100);
	}

	public static Float calculateRemainingSprints(ProjectTracking pt) {
		if (pt.getClosedStories() == null || pt.getStoriesInBacklog() == null || pt.getCycleTime() == null) {
			logger.error("Error calculating Remaining Sprints Index: empty values");
			return null;
		}
		if (pt.getCycleTime() == 0) {
			logger.error("Error calculating Remaining Sprints Index: cero divisor");
			return null;
		}

		return (float) pt.getStoriesInBacklog() - pt.getClosedStories() / pt.getCycleTime();
	}

	public static Float caculateCompletion(ProjectTracking pt) {
		if (pt.getClosedStories() == null || pt.getStoriesInBacklog() == null) {
			logger.error("Error calculating Completion Index: empty values");
			return null;
		}
		if (pt.getStoriesInBacklog() == 0) {
			logger.error("Error calculating Completion index: cero divisor");
			return null;
		}

		float value = (float) pt.getClosedStories() / pt.getStoriesInBacklog();
		return new Float(value * 100);
	}

	/**
	 ** Requerimientos con error/Total de requerimientos cerrados
	 * 
	 * @param pt
	 * @return
	 */
	public static Float calculateProdErrorIndex(ProjectTracking pt) {
		if (pt.getTechnical().getTotalReqErrorPeriod() == null || pt.getTechnical().getTotalReqClosedPeriod() == null) {
			logger.error("Error calculating TestCase Index: empty values");
			return null;
		}
		if (pt.getTechnical().getTotalReqClosedPeriod() == 0) {
			logger.error("Error calculating TestCase Index: cero divisor");
			return null;
		}

		float value = (float) pt.getTechnical().getTotalReqErrorPeriod() / pt.getTechnical().getTotalReqClosedPeriod();
		return new Float(value * 100);
	}
}
