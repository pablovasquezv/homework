package com.everis.homework.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.everis.homework.dto.VisualProjectHistory;
import com.everis.homework.dto.VisualProjectTracking;
import com.everis.homework.helper.ProjectTrackingHelper;
import com.everis.homework.mapper.ProjectProfile;
import com.everis.homework.mapper.ProjectTracking;
import com.everis.homework.mapper.RealEndDateLog;
import com.everis.homework.repository.IProjectTrackingRepository;
import com.everis.homework.repository.IRealEndDateRepository;
import com.everis.homework.service.ProjectTrackingService;

/**
 * The Class ProjectTrackingController.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/projectTracking")
public class ProjectTrackingController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	/** The repository. */
	@Autowired
	private IProjectTrackingRepository repository;

	@Autowired
	private IRealEndDateRepository endDateRepository;
	
	@Autowired
	private ProjectTrackingService projectTrackingService;

	/**
	 * Carga masiva.
	 *
	 * @param excel the excel
	 */
	@PostMapping("/massive")
	public void cargaMasiva(@RequestParam("file") MultipartFile excel) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook(excel.getInputStream());
			projectTrackingService.toProjectTracking(workbook);
			return;
		} catch (Exception e) {
			logger.error("Error parsing excel file", e);
			throw new RuntimeException(e.toString());
		}
	}

	@PostMapping("/survaymassive")
	public void cargaMasivaEncuesta(@RequestParam("file") MultipartFile excel) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook(excel.getInputStream());
			projectTrackingService.toSurvay(workbook);
			return;
		} catch (Exception e) {
			logger.error("Error parsing excel file", e);
			throw new RuntimeException(e.toString());
		}
	}

	@PostMapping("/fundingmassive")
	public void cargaMasivaFunding(@RequestParam("file") MultipartFile excel) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook(excel.getInputStream());
			projectTrackingService.toFunding(workbook);
			return;
		} catch (Exception e) {
			logger.error("Error parsing excel file", e);
			throw new RuntimeException(e.toString());
		}
	}

	/**
	 * Creates the project tracking.
	 *
	 * @param usuario         the usuario
	 * @param projectTracking the project tracking
	 * @return the project tracking
	 */
	@PostMapping("/create")
	@ResponseStatus(HttpStatus.CREATED)
	public VisualProjectTracking createProjectTracking(@RequestHeader(name = "usuario") String usuario,
			@RequestBody ProjectTracking projectTracking) {
		ProjectTracking projectTrackingSave = projectTrackingService.create(projectTracking,usuario);
		return ProjectTrackingHelper.toVisual(projectTrackingSave);
	}

	/**
	 * Find all.
	 *
	 * @return the list
	 */
	@GetMapping("/get/all")
	@ResponseStatus(HttpStatus.OK)
	public List<VisualProjectTracking> findAll() {
		List<ProjectTracking> entities = repository.findAll();
		List<VisualProjectTracking> visualList = new ArrayList<VisualProjectTracking>();
		for (ProjectTracking pt : entities)
			visualList.add(ProjectTrackingHelper.toVisual(pt));
		return visualList;
	}

	/**
	 * Find by id.
	 *
	 * @param projectCode the project code
	 * @return the project tracking
	 */
	@GetMapping("/find/{id}")
	@ResponseStatus(HttpStatus.OK)
	public VisualProjectTracking findById(@PathVariable("id") String projectCode) {
		ProjectTracking pt = repository.findById(projectCode).orElse(null);
		return ProjectTrackingHelper.toVisual(pt);
	}

	/**
	 * Update project tracking by project code.
	 *
	 * @param projectId       the project id
	 * @param usuario         the usuario
	 * @param projectTracking the project tracking
	 * @return the project tracking
	 * @throws Exception 
	 */
	@PutMapping("/update/{projectId}")
	@ResponseStatus(HttpStatus.OK)
	public VisualProjectTracking updateProjectTrackingByProjectCode(@PathVariable("projectId") String projectId,
			@RequestHeader(name = "usuario") String usuario, @RequestBody ProjectTracking projectTracking) throws Exception {

		ProjectTracking projectTrackingUpdated = projectTrackingService.updateProjectTrackingByProjectCode(projectId, projectTracking,
				usuario);
		return ProjectTrackingHelper.toVisual(projectTrackingUpdated);
	}

	/**
	 * Delete by id.
	 *
	 * @param id the id
	 * @return true, if successful
	 */
	@DeleteMapping("/delete/{projectId}")
	@ResponseStatus(HttpStatus.OK)
	public boolean deleteById(@PathVariable("projectId") String projectId) {
		repository.findById(projectId).ifPresent(o -> repository.delete(o));
		return true;
	}
	
	/**
	 * Delete by id.
	 *
	 * @param id the id
	 * @return true, if successful
	 */
	@PostMapping("/deleteProfile")
	public boolean deleteProfile(@RequestBody List<ProjectProfile> resources) {
		if (resources == null)
			return true;
		
		return projectTrackingService.deleteProfile(resources);
	}
	
	/**
	 * Find all.
	 *
	 * @return the list
	 */
	@GetMapping("/get/date/logs/{projectId}")
	@ResponseStatus(HttpStatus.OK)
	public List<RealEndDateLog> getAllRealEndDateLogs(@PathVariable("projectId") String projectId) {
		List<RealEndDateLog> entities = endDateRepository.findByProjectCode(projectId).orElse(null);
		return entities;
	}
	
	@GetMapping("/get/history/{projectId}")
	@ResponseStatus(HttpStatus.OK)
	public List<VisualProjectHistory> getProjectHistory(@PathVariable("projectId") String projectId) {
		return projectTrackingService.getHistory(projectId);
	}
}