package com.everis.homework.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.everis.homework.mapper.ProjectTracking;
import com.everis.homework.mapper.ProjectTrackingLog;
import com.everis.homework.mapper.RealEndDateLog;
import com.everis.homework.repository.IRealEndDateRepository;
import com.everis.homework.repository.IProjectTrackingLogRepository;
import com.everis.homework.repository.IProjectTrackingRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * @author sgutierc
 *
 */
@Service
public class ProjectTrackingLogService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	IRealEndDateRepository endDateRepository;

	@Autowired
	IProjectTrackingRepository repository;

	@Autowired
	IProjectTrackingLogRepository logRepository;

	private Gson gsonBuilder;

	public ProjectTrackingLogService() {
		gsonBuilder = new GsonBuilder().create();
	}

	/**
	 * 
	 * @param modified
	 * @param username
	 */
	public void createLog(ProjectTracking modified, String username) {
		ProjectTracking original = repository.findById(modified.getProjectCode()).orElse(null);

		if (original == null) {
			logger.info("ProjectTracking not found in repository " + modified.getProjectCode());
			return;
		}

		// buscamos cambios sobre campo real end date y guardamos un log si existe
		// diferencia
		if (original.getRealEndDate()!=null &&
			original.getRealEndDate().isEqual(modified.getRealEndDate()) == false) {
			RealEndDateLog log = createRealEndDateLog(original, modified, username);
			if (log != null) {
				endDateRepository.save(log);
				endDateRepository.flush();
			}
		}

		ProjectTrackingLog safeLog = createSafeLog(original, modified, username);
		if (safeLog != null) {
			logRepository.save(safeLog);
			logRepository.flush();
		}
	}

	/**
	 * 
	 * @param original
	 * @param modified
	 * @param username
	 * @return
	 */
	private RealEndDateLog createRealEndDateLog(ProjectTracking original, ProjectTracking modified, String username) {
		RealEndDateLog log = new RealEndDateLog();
		if (original == null || modified == null)
			return null;

		log.setProjectCode(original.getProjectCode());
		log.setUsername(username);
		log.setRealEndDateInitial(original.getRealEndDate());
		log.setRealEndDateModified(modified.getRealEndDate());
		log.setExtAgainstIncome(modified.getExtAgainstIncome());
		log.setExtensionReason(modified.getExtensionReason());
		log.setModificationDate(LocalDateTime.now().toLocalDate());
		return log;
	}

	private ProjectTrackingLog createSafeLog(ProjectTracking original, ProjectTracking modified, String username) {
		if (original == null || modified == null)
			return null;
		ProjectTrackingLog log = null;
		try {
			log = new ProjectTrackingLog();
			log.setUsername(username);
			log.setProjectCode(original.getProjectCode());
			log.setOldInformation(gsonBuilder.toJson(original));
			log.setUpdatedInformation(gsonBuilder.toJson(modified));
			log.setUpdateDate(LocalDateTime.now().toLocalDate());
		} catch (Exception e) {
			log = null;
			e.printStackTrace();
		}

		return log;
	}
}
