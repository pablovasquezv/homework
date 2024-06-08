package com.everis.homework.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.everis.homework.mapper.HomeworkLog;
import com.everis.homework.repository.IHomeworkLogRepository;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/homeworklog")
public class HomeworkLogController {

	@Autowired
	private IHomeworkLogRepository repositoryLog;

	/**
	 * Obtener todos los logs
	 * @return
	 */
	@GetMapping("/get/all")
	@ResponseStatus(HttpStatus.OK)
	public List<HomeworkLog> findAll() {
		return repositoryLog.findAll();
	}
}