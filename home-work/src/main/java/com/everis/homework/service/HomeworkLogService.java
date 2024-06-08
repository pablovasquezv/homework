package com.everis.homework.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.everis.homework.mapper.Homework;
import com.everis.homework.mapper.HomeworkLog;
import com.everis.homework.repository.IHomeworkLogRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * The Class HomeworkLogService.
 */
@Service
public class HomeworkLogService {

	/** The repository log. */
	@Autowired
	private IHomeworkLogRepository repositoryLog;

	public HomeworkLog createLog(String userId, String dataPersisted, Homework homeworkUpdated){
		HomeworkLog homeworkLog = new HomeworkLog();
		homeworkLog.setUsersadAction(userId);
		homeworkLog.setUpdateDate(LocalDateTime.now());
		homeworkLog.setUpdatedUsersad(homeworkUpdated.getNroEmpleado());
		// Informacion antigua
		if(dataPersisted != null) {
			homeworkLog.setOldInformation(dataPersisted);
		}
		// Informacion actualizada
		if(homeworkUpdated != null) {
			Gson gsonBuilder = new GsonBuilder().create();
			homeworkLog.setUpdateInformation(gsonBuilder.toJson(homeworkUpdated));
		}
		return repositoryLog.save(homeworkLog);
	}
}