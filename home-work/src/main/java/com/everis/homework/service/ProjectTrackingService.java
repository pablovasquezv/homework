package com.everis.homework.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.everis.homework.controller.DoubtsController;
import com.everis.homework.dto.ProjectAlarm;
import com.everis.homework.dto.VisualProjectHistory;
import com.everis.homework.dto.VisualProjectTracking.ProjectType;
import com.everis.homework.helper.ProjectTrackingHelper;
import com.everis.homework.helper.ProjectTypeHelper;
import com.everis.homework.mapper.Funding;
import com.everis.homework.mapper.Modification;
import com.everis.homework.mapper.Profile;
import com.everis.homework.mapper.ProjectHistory;
import com.everis.homework.mapper.ProjectProfile;
import com.everis.homework.mapper.ProjectTracking;
import com.everis.homework.mapper.Range;
import com.everis.homework.mapper.Rol;
import com.everis.homework.mapper.ServiceFields;
import com.everis.homework.mapper.Survay;
import com.everis.homework.mapper.TechnicalFields;
import com.everis.homework.mapper.TraditionalFields;
import com.everis.homework.repository.IProfileRepository;
import com.everis.homework.repository.IProjectHistoryRepository;
import com.everis.homework.repository.IProjectProfileRepository;
import com.everis.homework.repository.IProjectTrackingRepository;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * The Class ProjectTrackingService.
 */
@Service
public class ProjectTrackingService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private DataFormatter formatter = new DataFormatter();
	private final int WORKBOOK_SHEET = 0;

	/** The repository. */
	@Autowired
	private IProjectTrackingRepository repository;

	@Autowired
	private IProjectHistoryRepository historyRepository;
	
	@Autowired
	private ProjectTrackingLogService logService;

	@Autowired
	private IProjectProfileRepository repositoryProfile;

	@Autowired
	private IProfileRepository repositoryUsers;

	@Autowired
	DoubtsController doubtController;

	private static final Integer ADMIN_ROL = 1;

	private Gson gsonBuilder;

	public ProjectTrackingService() {
		gsonBuilder = new GsonBuilder().create();
	}
	
	/**
	 * Update project tracking by project code.
	 *
	 * @param projectCode     the project code
	 * @param projectTracking the project tracking
	 * @return the project tracking
	 * @throws Exception
	 */
	public ProjectTracking updateProjectTrackingByProjectCode(String projectCode, ProjectTracking projectTracking,
			String username) throws Exception {

		boolean adminMode = false;
		Profile profile = repositoryUsers.findById(username).orElse(null);
		if (profile != null) {
			for (Rol rol : profile.getRoles()) {
				if (rol.getId().equals(ADMIN_ROL)) {
					adminMode = true;
					break;
				}
			}
		}

		if (repository.existsByProjectCode(projectCode)) {
			projectTracking.setProjectCode(projectCode);
			projectTracking = loadExtensionRules(projectTracking);

			ProjectTracking old = repository.findById(projectTracking.getProjectCode()).orElse(null);

			projectTracking.getTechnical().setRollbackProd(ProjectTrackingHelper.calculateRollbacks(projectTracking));
			projectTracking.setPercentageCompletion(ProjectTrackingHelper.caculateCompletion(projectTracking));
			projectTracking.getTechnical().setTestCasesIndex(ProjectTrackingHelper.calculateTestCaseIndex(projectTracking));

			if (projectTracking.getStaff().getDetailProjectProfileList() != null
					&& projectTracking.getStaff().getDetailProjectProfileList().size() > 0)
				projectTracking.getStaff().setSumTarifas(ProjectTrackingHelper
						.calculateSumTarifas(projectTracking.getStaff().getDetailProjectProfileList()));
			else
				projectTracking.getStaff().setSumTarifas(new BigDecimal(0));
			// SI perfil no es administrador, debemos validar maximo de modificaciones, Si
			// es administrador se permite N modificaciones
			if (adminMode == false) {
				projectTracking.setAttempts(countModifications(projectTracking, projectCode));
			}

			checkRealEndDateLogic(projectTracking, username);
			if (old != null)
				checkHistoryLogic(projectTracking, old);

			if (old != null && old.matches(projectTracking) == false)
				projectTracking.setModification(new Modification(new Date(), username));
			
			// salvamos un log de los cambios
			logService.createLog(projectTracking, username);
			return repository.save(projectTracking);
		} else {
			return null;
		}
	}

	private void checkHistoryLogic(ProjectTracking pt, ProjectTracking old) {
		List<ProjectHistory> phl = historyRepository.findByProjectCode(pt.getProjectCode()).orElse(null);
		if (phl == null)
			phl = new CopyOnWriteArrayList<ProjectHistory>();
		
		ProjectHistory ph = new ProjectHistory();
		ph.setProjectCode(pt.getProjectCode());

		ProjectType type = ProjectType.getType(pt.getProjectTypeCode());

		if (pt.getServiceLine().equalsIgnoreCase("STAFF AUGMENTATION")) {// CHCOVID-74
			if (pt.getStaff().matches(old.getStaff()) == false
					|| pt.getTechnical().matches(old.getTechnical()) == false) {
				ph.setStaff(gsonBuilder.toJson(old.getStaff()));
				ph.setTechnical(gsonBuilder.toJson(old.getTechnical()));
				ph.loadModification(old.getModification());
				phl.add(ph);
			}
		}

		else if (type == ProjectType.PROJECT) {/// CHCOVID-72
			if (pt.getTraditional().matches(old.getTraditional()) == false
					|| pt.getTechnical().matches(old.getTechnical()) == false) {
				ph.setTraditional(gsonBuilder.toJson(old.getTraditional()));
				ph.setTechnical(gsonBuilder.toJson(old.getTechnical()));
				ph.loadModification(old.getModification());
				phl.add(ph);
			}
		} else if (type == ProjectType.SERVICE) {/// CHCOVID-73
			if (pt.getService().matches(old.getService()) == false
					|| pt.getTechnical().matches(old.getTechnical()) == false) {
				ph.setService(gsonBuilder.toJson(old.getService()));
				ph.setTechnical(gsonBuilder.toJson(old.getTechnical()));
				ph.loadModification(old.getModification());
				phl.add(ph);
			}

		} else if (type == ProjectType.PROJECT_AGILE) {
			if (pt.getTechnical().matches(old.getTechnical()) == false) {
				ph.setTechnical(gsonBuilder.toJson(old.getTechnical()));
				ph.loadModification(old.getModification());
				phl.add(ph);
			}
		}

		//historyRepository.saveAll(phl);
		if(phl.size()>5) {
			ProjectHistory lastPh=phl.iterator().next();
			for(ProjectHistory hh:phl) {
				if(hh.getLastModified().before(lastPh.getLastModified()))
					lastPh=hh;
			}
			
			historyRepository.delete(lastPh);//eliminamos el valor mas antiguo en la bd.
		}
		
		historyRepository.saveAll(phl);
	}

	/**
	 * Ejecutamos logica de negocio asociada al campo RealEndDate
	 * 
	 * @param pt
	 * @throws Exception
	 */
	private void checkRealEndDateLogic(ProjectTracking pt, String usuario) throws Exception {
		if (pt == null)
			return;

		ProjectTracking old = repository.findById(pt.getProjectCode()).orElse(null);

		if (old == null)
			return;
		// Si hay cambios en la fecha real de fin, entonces enviamos mail informativo.
		{
			boolean equals = true;
			if (pt.getRealEndDate() == null) {
				if (old.getRealEndDate() != null)
					equals = false;
			} else if (!pt.getRealEndDate().equals(old.getRealEndDate()))
				equals = false;

			// enviar mensaje
			if (equals == false) {
				doubtController.realEndDateMail(usuario, pt.getProjectCode(), localDateToDate(old.getRealEndDate()),
						localDateToDate(pt.getRealEndDate()), new Date());
			}

		}

	}

	private static final ZoneOffset zoneOffset = OffsetDateTime.now(ZoneId.systemDefault()).getOffset();

	private Date localDateToDate(LocalDate ldate) {
		return Date.from(ldate.atStartOfDay().toInstant(zoneOffset));
	}

	public Integer countModifications(ProjectTracking modified, String projectCode) throws Exception {
		ProjectTracking original = repository.findById(modified.getProjectCode()).orElse(null);
		if (original == null || original.getProjectType() == null)
			return null;

		int nroMod = original.getAttempts() == null ? 0 : original.getAttempts().intValue();
		if (original.getProjectType().equals(modified.getProjectType()) == false
				|| original.getServiceLine().equals(modified.getServiceLine()) == false) {
			// tipo proyecto o linea de servicio modificadas
			nroMod++;
			if (nroMod > 3)
				throw new Exception("Invalid Update - max attemps");
		}

		return nroMod;
	}

	/**
	 * 
	 * @param pt
	 * @return
	 */
	private ProjectTracking loadExtensionRules(ProjectTracking pt) {
		/**
		 * Si la extension fue validada entonces no hay que modificar nada.
		 */
		if (pt.getExtensionValidated() != null && pt.getExtensionValidated().booleanValue() == true)
			return pt;
		/**
		 * si la extension no posee validacion, entonces la establecemos en falso
		 */
		if (pt.getExtensionValidated() == null)
			pt.setExtensionValidated(new Boolean(false));

		return pt;
	}

	/**
	 * Creates the.
	 *
	 * @param projectTracking the project tracking
	 * @return the project tracking
	 */
	public ProjectTracking create(ProjectTracking projectTracking, String usuario) {
		projectTracking = loadExtensionRules(projectTracking);
		if (repository.existsByProjectCode(projectTracking.getProjectCode())) {
			return null;
		} else {
			if(projectTracking.getTechnical()==null)
				projectTracking.setTechnical(new TechnicalFields());
			projectTracking.setModification(new Modification(new Date(), usuario));
			projectTracking.getTechnical().setRollbackProd(ProjectTrackingHelper.calculateRollbacks(projectTracking));
			projectTracking.getTechnical().setTestCasesIndex(ProjectTrackingHelper.calculateTestCaseIndex(projectTracking));
			projectTracking.getTechnical().setProdErrorIndex(ProjectTrackingHelper.calculateProdErrorIndex(projectTracking));

			return repository.save(projectTracking);
		}
	}

//	public ProjectTracking createProfile(ProjectTracking pt) {
//		ProjectTracking profile = null;
//		try {
//			if (pt.getDetailProjectProfileList() != null || pt.getDetailProjectProfileList().isEmpty() == false) {
//				for (ProjectProfile ppfl : pt.getDetailProjectProfileList()) {
//					ppfl = profileRepository.save(ppfl);
//				}
//			}
//			profile = repository.save(pt);
//1-a�adir perfil
//1.1-generar un nuevo perfil y cargarlo al listado del proyect tracking en  modelo
//2-guardar enviarndo el project tracking
//3-update/create servicio
//
//
////			detailRepository.flush();
//
//		} catch (Exception e) {
//			logger.error("Error parsing excel file", e);
//			throw new RuntimeException(e.toString());
//		}
//
//		return profile;
//
//	}

	/**
	 * Persiste desde un archivo excel entidades de tipo Project Tracking
	 * 
	 * @param workbookFactory archivo excel
	 */
	public void toProjectTracking(XSSFWorkbook workbookFactory) throws Exception {
		if (workbookFactory == null)
			throw new Exception("Invalid XSSFWorkbook input");

		XSSFSheet hoja = workbookFactory.getSheetAt(WORKBOOK_SHEET);
		List<XSSFRow> validRows = null;
		validRows = getValidRows(hoja, 1);
		int totalFilas = validRows.size();
		if (totalFilas == 0)
			throw new Exception("Empty workbook");

		try {
			// vamos a iterar sobre las filas validas
			List<ProjectTracking> mappedRows = new ArrayList<ProjectTracking>();
			for (XSSFRow row : validRows) {
				// creando una entidad por cada fila
				mappedRows.add(parseRow(row));
			}
			// luego persistimos todas las entidades
			repository.saveAll(mappedRows);
		} catch (Exception e) {
			logger.warn("Exception parsing excel row", e);
			throw new Exception("Exception parsing excel file");
		}
	}

	private final int FUNDING_KEY_COL = 0;

	public void toFunding(XSSFWorkbook workbookFactory) throws Exception {
		if (workbookFactory == null)
			throw new Exception("Invalid XSSFWorkbook input");

		XSSFSheet hoja = workbookFactory.getSheetAt(WORKBOOK_SHEET);
		List<XSSFRow> validRows = null;
		validRows = getValidRows(hoja, 1, FUNDING_KEY_COL);
		int totalFilas = validRows.size();
		if (totalFilas == 0)
			throw new Exception("Empty workbook");
		try {
			// vamos a iterar sobre las filas validas
			List<Funding> mappedRows = new ArrayList<Funding>();
			List<ProjectTracking> relatedProjects = new ArrayList<ProjectTracking>();
			for (XSSFRow row : validRows) {
				// creando una entidad por cada fila
				Pair<ProjectTracking, Funding> result = parseFunding(row);
				relatedProjects.add(result.getFirst());
				mappedRows.add(result.getSecond());
			}
			// luego persistimos todas las entidades
			// fundingRepository.saveAll(mappedRows);
			repository.saveAll(relatedProjects);

			// fundingRepository.flush();
			repository.flush();

		} catch (Exception e) {
			logger.warn("Exception parsing excel row", e);
			throw new Exception("Exception parsing excel file");
		}
	}

	private final int EVALUATION_KEY_COL = 0;

	public void toSurvay(XSSFWorkbook workbookFactory) throws Exception {
		if (workbookFactory == null)
			throw new Exception("Invalid XSSFWorkbook input");

		XSSFSheet hoja = workbookFactory.getSheetAt(WORKBOOK_SHEET);
		List<XSSFRow> validRows = null;
		validRows = getValidRows(hoja, 1, EVALUATION_KEY_COL);
		int totalFilas = validRows.size();
		if (totalFilas == 0)
			throw new Exception("Empty workbook");
		try {
			// vamos a iterar sobre las filas validas
			List<Survay> mappedRows = new ArrayList<Survay>();
			List<ProjectTracking> relatedProjects = new ArrayList<ProjectTracking>();

			for (XSSFRow row : validRows) {
				// creando una entidad por cada fila
				Pair<ProjectTracking, Survay> result = parseEvaluation(row);
				relatedProjects.add(result.getFirst());
				mappedRows.add(result.getSecond());
			}
			// luego persistimos todas las entidades
			// survayRepository.saveAll(mappedRows);
			repository.saveAll(relatedProjects);
			repository.flush();
		} catch (Exception e) {
			logger.warn("Exception parsing excel row", e);
			throw new Exception("Exception parsing excel file");
		}
	}

	private Pair<ProjectTracking, Survay> parseEvaluation(XSSFRow row) throws Exception {
		Survay s = new Survay();
		int lastCell = row.getLastCellNum();
		for (int i = 0; i < lastCell; i++) {
			Cell cell = row.getCell(i);
			switch (i) {
			case 0: {
				String value = formatter.formatCellValue(cell).trim();
				if (value == null || value.isEmpty())
					throw new Exception("Invalid Project Code");
				s.setProjectCode(value);
				break;
			} // Codigo proyecto
			case 1: {
				s.setProjectName(formatter.formatCellValue(cell).trim());
				break;
			} // Nombre proyecto
			case 2: {
				s.setGrade(convertToFloat(cell));
				break;
			} // Nota
			case 3: {
				s.setSurvayDate(convertToLocalDate(cell));
				break;
			} // Fecha de la encuesta
			case 4: {
				s.setActionPlane(formatter.formatCellValue(cell).trim());
				break;
			} // Plan de Accion Solicitado
			case 5: {
				s.setComments(formatter.formatCellValue(cell).trim());
				break;
			} // Comentarios
			default:
				break;
			}
		}

		ProjectTracking pt = repository.findById(s.getProjectCode()).orElse(null);
		if (pt == null)
			throw new Exception("Project code not exists " + s.getProjectCode());

		pt.setSurvayReport(s);
		// repository.save(pt);

		return Pair.of(pt, s);
	}

	private Pair<ProjectTracking, Funding> parseFunding(XSSFRow row) throws Exception {
		Funding f = new Funding();
		int lastCell = row.getLastCellNum();
		for (int i = 0; i < lastCell; i++) {
			Cell cell = row.getCell(i);
			switch (i) {
			case 0: {
				String value = formatter.formatCellValue(cell).trim();
				if (value == null || value.isEmpty())
					throw new Exception("Invalid Project Code");

				f.setProjectCode(value);
				break;
			} // Codigo proyecto
			case 1: {
				f.setProjectName(formatter.formatCellValue(cell).trim());
				break;
			} // Nombre proyecto
			case 2: {
				f.setNRN(convertToBigDecimal(cell));
				break;
			} // NR N
			case 3: {
				f.setFunding(convertToBigDecimal(cell));
				break;
			} // Funding
			case 4: {
				f.setNrIncurrido(convertToBigDecimal(cell));
				break;
			} // NR Incurrido
			case 5: {
				f.setWip(convertToBigDecimal(cell));
				break;
			} // WIP
			case 6: {
				f.setFacturado(convertToBigDecimal(cell));
				break;
			} // Facturado
			case 7: {
				f.setDiasWIP(convertToBigDecimal(cell));
				break;
			} // Días de WIP
			default:
				break;
			}
		}
		f.setIncurredVsFunding(ProjectTrackingHelper.calculateIncurredVsFunding(f));
		f.setWipconNRN(ProjectTrackingHelper.calculateWipVsNrn(f));

		ProjectTracking pt = repository.findById(f.getProjectCode()).orElse(null);
		if (pt == null)
			throw new Exception("Project code not exists " + f.getProjectCode());

		pt.setFundingReport(f);
		// repository.save(pt);

		return Pair.of(pt, f);
	}

	private final BigDecimal PERCENT = new BigDecimal(100);

	/**
	 * Permite trasformar una fila de excel en un objeto de tipo ProjectTracking
	 * 
	 * @param row
	 * @return
	 * @throws Exception
	 */
	private ProjectTracking parseRow(XSSFRow row) throws Exception {
		ProjectTracking pt = new ProjectTracking();

		TraditionalFields tf = new TraditionalFields();
		ServiceFields sf = new ServiceFields();

		int lastCell = row.getLastCellNum();
		for (int i = 0; i < lastCell; i++) {
			Cell cell = row.getCell(i);
			switch (i) {
			case 0: {
				pt.setSector(formatter.formatCellValue(cell).trim());
				break;
			} // Sector
			case 1: {
				pt.setUn(formatter.formatCellValue(cell).trim());
				break;
			} // UN
			case 2: {
				pt.setCe(formatter.formatCellValue(cell).trim());
				break;
			} // CE
			case 3: {
				pt.setClient(formatter.formatCellValue(cell).trim());
				break;
			} // Cliente
			case 4: {
				pt.setServiceLine(formatter.formatCellValue(cell).trim());
				break;
			} // Linea Servicio
			case 5: {
				pt.setProjectManager(formatter.formatCellValue(cell).trim());
				break;
			} // Gerente de Proyecto
			case 6: {
				String value = formatter.formatCellValue(cell).trim();
				if (value == null || value.isEmpty())
					throw new Exception("Invalid Project Code");

				pt.setProjectCode(value);
				break;
			} // Codigo proyecto
			case 7: {
				pt.setProjectName(formatter.formatCellValue(cell).trim());
				break;
			} // Nombre proyecto
			case 8: {
				pt.setProjectType(formatter.formatCellValue(cell).trim());
				pt.setProjectTypeCode(ProjectTypeHelper.textToCode(formatter.formatCellValue(cell).trim()));
				break;
			} // Tipologia de Proyecto
			case 9: {
				pt.setStartDate(convertToLocalDate(cell));
				break;
			} // Fecha inicio Proyecto
			case 10: {
				pt.setEndDate(convertToLocalDate(cell));
				break;
			} // Fecha fin proyecto
			case 11: {
				pt.setRealEndDate(convertToLocalDate(cell));
				break;
			} // Fecha real fin proyecto
			case 12: {
				tf.setCurrentProjectPhase(formatter.formatCellValue(cell).trim());

				break;
			} // Fase del proyecto en el que se encuentra
			case 13: {
				tf.setActualPercentage(convertToBigDecimal(cell, PERCENT));

				break;
			} // % de Avance Real del Proyecto
			case 14: {
				tf.setPlannedPercentage(convertToBigDecimal(cell, PERCENT));

				break;
			} // % de Avance Planificado del Proyecto

			// HITO 1
			case 15: {
				tf.setRelevantMilestoneOne(formatter.formatCellValue(cell).trim());

				break;
			} // Hito Relevante 1
			case 16: {
				tf.setMilestoneDateOne(convertToLocalDate(cell));

				break;
			} // Fecha de Hito 1
			case 17: {
				tf.setActualProgressPercentageOne(convertToBigDecimal(cell, PERCENT));

				break;
			} // % de Avance Real Hito 1
			case 18: {
				tf.setCompliancePercentageOne(convertToBigDecimal(cell, PERCENT));

				break;
			} // % de Avance Planificado Hito 1

			// HITO 2
			case 19: {
				tf.setRelevantMilestoneTwo(formatter.formatCellValue(cell).trim());

				break;
			} // Hito Relevante 2
			case 20: {
				tf.setMilestoneDateTwo(convertToLocalDate(cell));

				break;
			} // Fecha de Hito 2
			case 21: {
				tf.setActualProgressPercentageTwo(convertToBigDecimal(cell, PERCENT));

				break;
			} // % de Avance Real Hito 2
			case 22: {
				tf.setCompliancePercentageTwo(convertToBigDecimal(cell, PERCENT));

				break;
			} // % de Avance Planificado Hito 2

			// HITO 3
			case 23: {
				tf.setRelevantMilestoneThree(formatter.formatCellValue(cell).trim());

				break;
			} // Hito Relevante 3
			case 24: {
				tf.setMilestoneDateThree(convertToLocalDate(cell));

				break;
			} // Fecha de Hito 3
			case 25: {
				tf.setActualProgressPercentageThree(convertToBigDecimal(cell, PERCENT));

				break;
			} // % de Avance Real Hito 3
			case 26: {
				tf.setCompliancePercentageThree(convertToBigDecimal(cell, PERCENT));

				break;
			} // % de Avance Planificado Hito 3

			// HITO 4
			case 27: {
				tf.setRelevantMilestoneFour(formatter.formatCellValue(cell).trim());

				break;
			} // Hito Relevante 4
			case 28: {
				tf.setMilestoneDateFour(convertToLocalDate(cell));

				break;
			} // Fecha de Hito 4
			case 29: {
				tf.setActualProgressPercentageFour(convertToBigDecimal(cell, PERCENT));

				break;
			} // % de Avance Real Hito 4
			case 30: {
				tf.setCompliancePercentageFour(convertToBigDecimal(cell, PERCENT));

				break;
			} // % de Avance Planificado Hito 4

			// HITO 5
			case 31: {
				tf.setRelevantMilestoneFive(formatter.formatCellValue(cell).trim());

				break;
			} // Hito Relevante 5
			case 32: {
				tf.setMilestoneDateFive(convertToLocalDate(cell));

				break;
			} // Fecha de Hito 5
			case 33: {
				tf.setActualProgressPercentageFive(convertToBigDecimal(cell, PERCENT));

				break;
			} // % de Avance Real Hito 5
			case 34: {
				tf.setCompliancePercentageFive(convertToBigDecimal(cell, PERCENT));

				break;
			} // % de Avance Planificado Hito 5
			case 35: {
				sf.setTotalEstRequested(convertToInteger(cell));
				break;
			} // Total de estimaciones solicitadas en el periodo
			case 36: {
				sf.setTotalReqRequested(convertToInteger(cell));
				break;
			} // Total de requerimientos solicitados en el periodo
			case 37: {
				sf.setNumEstDelivered(convertToInteger(cell));
				break;
			} // No de estimaciones en plazo en el periodo
			case 38: {
				sf.setNumReqDelivered(convertToInteger(cell));
				break;
			} // No de requerimiento en plazo en el periodo
			case 39: {
				// TODO pt.setNumIncidentsRequested(convertToInteger(cell));
				break;
			} // No de incidentes solicitados en el periodo
			case 40: {
				pt.setNumFinishedSprintStories(convertToInteger(cell));
				break;
			} // No de historias terminadas en el Sprint
			case 41: {
				pt.setNumReleases(convertToInteger(cell));
				break;
			} // No de Releases
			case 42: {
				pt.setNumSprints(convertToInteger(cell));
				break;
			} // No de Sprints
			case 43: {
				pt.setNumSprintPlannedHistories(convertToInteger(cell));
				break;
			} // No de historias planificadas en el Sprint

			default:
				break;
			}
		}

		Range redRange = new Range(98, 0);
		Range yellowRange = new Range(100, 98);

		pt.setRedMargin(redRange);
		pt.setYellowMargin(yellowRange);

		// campos calculados
		pt.setModification(new Modification(new Date(), "Carga Masiva"));
		tf.setDeviationPercentage(ProjectTrackingHelper.calculateDeviationPercentage(pt));

		tf.setDeviationPercentageOne(
				ProjectTrackingHelper.calculateDeviationPercentage(pt, ProjectTrackingHelper.Case.ONE));
		tf.setDeviationPercentageTwo(
				ProjectTrackingHelper.calculateDeviationPercentage(pt, ProjectTrackingHelper.Case.TWO));
		tf.setDeviationPercentageThree(
				ProjectTrackingHelper.calculateDeviationPercentage(pt, ProjectTrackingHelper.Case.THREE));
		tf.setDeviationPercentageFour(
				ProjectTrackingHelper.calculateDeviationPercentage(pt, ProjectTrackingHelper.Case.FOUR));
		tf.setDeviationPercentageFive(
				ProjectTrackingHelper.calculateDeviationPercentage(pt, ProjectTrackingHelper.Case.FIVE));
		sf.setPercentageEstDelivered(ProjectTrackingHelper.calculatePercEstimationDelivered(pt));
		sf.setPercentageReqResolved(ProjectTrackingHelper.calculatePercRequirementResolved(pt));
		pt.setTeamSpeedAverage(ProjectTrackingHelper.calculateTeamSpeedAver(pt));
		pt.setSprintComplInd(ProjectTrackingHelper.calculateSprintComplianceInd(pt));

		pt.setTraditional(tf);
		pt.setService(sf);

		// Extension sobre ingresos
		if (repository.existsByProjectCode(pt.getProjectCode())) {
			repository.findById(pt.getProjectCode()).ifPresent(o -> {
				// campos fecha inicio, fin y real fin de proyecto no podran ser modificadas
				// mediante carga masiva si el proyecto ya existe
				// CHCOVID-17
				pt.setEndDate(o.getEndDate());
				pt.setStartDate(o.getStartDate());
				pt.setRealEndDate(o.getRealEndDate());

				pt.setExtAgainstIncome(o.getExtAgainstIncome());
				pt.setExtensionReason(o.getExtensionReason());
				pt.setExtensionValidated(o.getExtensionValidated());
				pt.setDeclaredExtension(o.getDeclaredExtension());
			});
		} else {
			if ((pt.getEndDate() != null && pt.getRealEndDate() != null)
					&& (pt.getRealEndDate().isAfter(pt.getEndDate()))) {
				// establecemos declarada la extension, luego el ejecutivo completara la
				// informacion faltante
				pt.setExtAgainstIncome(null);
				pt.setExtensionReason(null);
				pt.setExtensionValidated(new Boolean(false));
				pt.setDeclaredExtension(new Boolean(true));
			} else {
				pt.setDeclaredExtension(new Boolean(false));
				pt.setExtAgainstIncome(null);
				pt.setExtensionReason(null);
				pt.setExtensionValidated(null);
			}
		}
		return pt;
	}

	private BigDecimal convertToBigDecimal(Cell cell, BigDecimal percent) throws Exception {
		BigDecimal value = convertToBigDecimal(cell);
		if (value == null)
			return null;
		return value.multiply(PERCENT);
	}

	public boolean deleteProfile(List<ProjectProfile> resources) {
		try {
			if (resources == null)
				return true;
			repositoryProfile.deleteAll(resources);

			return true;
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return false;
	}

	/**
	 * Convierte una celda a dato de tipo BigDecimal
	 * 
	 * @param cell
	 * @return
	 * @throws Exception
	 */
	private BigDecimal convertToBigDecimal(Cell cell) throws Exception {
		if (cell == null) {
			logger.warn("Invalid cell value - null");
			return null;
		}

		// DecimalFormatSymbols currentLocaleSymbols =
		// DecimalFormatSymbols.getInstance();
		// char decimalSep=currentLocaleSymbols.getDecimalSeparator();

		// String rawValue = formatter.formatCellValue(cell).trim().replace(decimalSep,
		// Character.MIN_VALUE);
		String rawValue = cell.toString().trim();
		if (StringUtils.isEmpty(rawValue) || "-".equals(rawValue))
			return null;
		logger.info("Readed cell value :" + rawValue);
		// rawValue = rawValue.replace(',', '.');
		BigDecimal value = null;
		try {
			if (rawValue.indexOf("%") > 0)
				value = new BigDecimal(rawValue.trim().replace("%", "")).divide(BigDecimal.valueOf(100));
			else
				value = new BigDecimal(rawValue);
		} catch (Exception e) {
			logger.error(String.format("Error with cell value (%s) -> [%s]", rawValue, e.toString()));
		}

		return value;
	}

	private Float convertToFloat(Cell cell) throws Exception {
		if (cell == null) {
			logger.warn("Invalid cell value - null");
			return null;

		}

		String rawValue = formatter.formatCellValue(cell).trim();
		if (StringUtils.isEmpty(rawValue))
			return null;

		rawValue = rawValue.replace(',', '.');

		return Float.valueOf(rawValue);
	}

	/**
	 * Convierte una celda a dato de tipo Integer
	 * 
	 * @param cell
	 * @return
	 * @throws Exception
	 */
	private Integer convertToInteger(Cell cell) throws Exception {
		if (cell == null) {
			logger.warn("Invalid cell value - null");
			return null;

		}

		String rawValue = formatter.formatCellValue(cell).trim();
		if (StringUtils.isEmpty(rawValue))
			return null;

		rawValue = rawValue.replace(',', '.');

		return Double.valueOf(rawValue).intValue();
	}

	/**
	 * Convierte una celda a fecha local
	 * 
	 * @param cell
	 * @return
	 * @throws Exception
	 */
	private LocalDate convertToLocalDate(Cell dateCell) throws Exception {
		if (dateCell == null) {
			logger.warn("Invalid cell value - null");
			return null;

		}
		LocalDate date = null;
		if (isValidDate(dateCell)) {
			date = convertToLocalDateViaInstant(dateCell.getDateCellValue());
		} else if (!StringUtils.isEmpty(formatter.formatCellValue(dateCell))) {
			date = convertToLocalDateViaInstant(formatter.formatCellValue(dateCell));
		}
		return date;
	}

	/**
	 * Checks if is valid date.
	 *
	 * @param dateString the date string
	 * @return true, if is valid date
	 */
	public static boolean isValidDate(Cell dateString) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			sdf.format(dateString.getDateCellValue());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Convert to local date via instant.
	 *
	 * @param dateToConvert the date to convert
	 * @return the local date
	 */
	private static LocalDate convertToLocalDateViaInstant(java.util.Date dateToConvert) {
		if (dateToConvert != null) {
			return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		}
		return null;
	}

	/**
	 * Convert to local date via instant.
	 *
	 * @param formatCellValue the format cell value
	 * @return the local date
	 */
	private LocalDate convertToLocalDateViaInstant(String formatCellValue) {
		try {
			return new SimpleDateFormat("dd/MM/yyyy").parse(formatCellValue).toInstant().atZone(ZoneId.systemDefault())
					.toLocalDate();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Retorna un listado de filas validas (no vacias)
	 *
	 * @param hoja       hoja del libro
	 * @param filaInicio the fila inicio
	 * @return the list
	 */
	private List<XSSFRow> getValidRows(XSSFSheet hoja, int filaInicio) {
		return this.getValidRows(hoja, filaInicio, 6);// revisa sobre fila de cod_proyecto
	}

	private List<XSSFRow> getValidRows(XSSFSheet hoja, int filaInicio, int keycolumn) {
		List<XSSFRow> filasvalidas = new ArrayList<XSSFRow>();
		int lastRowNum = hoja.getLastRowNum();

		for (int i = filaInicio; i <= lastRowNum; i++) {
			XSSFRow row = hoja.getRow(i);
			if (row != null) {
				Cell celda = row.getCell(keycolumn);// Reviso sobre la columna clave
				if (celda != null && celda.getCellType() != CellType.BLANK) {
					filasvalidas.add(row);
				}
			}
		}
		return filasvalidas;
	}

	/**
	 * 
	 * @param pt
	 * @return
	 * @throws Exception
	 */
	public ProjectAlarm getAlarmsFor(ProjectTracking pt) throws Exception {
		if (pt == null)
			throw new Exception("Invalid Project Tracking object");

		ProjectAlarm alarm = ProjectTrackingHelper.toVisual(pt).getAlarm();

		return alarm;
	}
	
	public List<VisualProjectHistory> getHistory(String projectCode){
		List<ProjectHistory> raw=historyRepository.findByProjectCode(projectCode).orElse(null);
		List<VisualProjectHistory> entities=new ArrayList<VisualProjectHistory>();

		if(raw!=null) {
			for(ProjectHistory ph:raw) {
				entities.add(ProjectTrackingHelper.toVisual(ph));
			}
		}
		
		return entities;
	}
}