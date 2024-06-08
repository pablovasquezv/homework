package com.everis.homework.controller;

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

import com.everis.homework.mapper.Homework;
import com.everis.homework.repository.IHomeworkRepository;
import com.everis.homework.service.HomeworkLogService;
import com.everis.homework.service.HomeworkService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author gsaravia
 *
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/homework")
public class HomeworkController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/** The repository. */
	@Autowired
	private IHomeworkRepository repository;

	/** The service. */
	@Autowired
	private HomeworkService service;

	/** The service log. */
	@Autowired
	private HomeworkLogService serviceLog;

	@PostMapping("/massive")
	public void cargaMasiva(@RequestParam("file") MultipartFile excel) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook(excel.getInputStream());
			service.toHomework(workbook);
		} catch (Exception e) {
			logger.error("Error parsing excel file", e);
			throw new RuntimeException(e.toString());
		}
	}

	/**
	 * Crea una nueva homework
	 * 
	 * @param homework
	 * @return
	 */
	@PostMapping("/create")
	@ResponseStatus(HttpStatus.CREATED)
	public Homework createHomework(@RequestHeader(name = "usuario") String usuario, @RequestBody Homework homework) {
		Homework homeworkSave = service.create(homework);
		// Insert Log
		if (homeworkSave != null) {
			serviceLog.createLog(usuario, null, homeworkSave);
		}
		return homeworkSave;
	}

	/**
	 * Obtener todas las homework
	 * 
	 * @return
	 */
	@GetMapping("/get/all")
	@ResponseStatus(HttpStatus.OK)
	public List<Homework> findAll() {
		return repository.findAll();
	}
	
	/**
	 * Obtener una homework por su id
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/find/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Homework findById(@PathVariable("id") Integer id) {
		return repository.findById(id).orElse(null);
	}
	
	/**
	 * Obtener una homework por su id
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/findByUser/{username}")
	@ResponseStatus(HttpStatus.OK)
	public Homework findByUser(@PathVariable("username") String username) {
		return repository.findByUser(username).orElse(null);
	}

	/**
	 * Modifica una homework por su id
	 * 
	 * @param id
	 * @param med
	 * @return
	 */
	@PutMapping("/update/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Homework updateHomeworkById(@PathVariable("id") Integer id, @RequestHeader(name = "usuario") String usuario,
			@RequestBody Homework homework) {
		Homework homeworkPersisted = repository.findById(id).orElse(null);
		// Informacion antigua
		Gson gsonBuilder = new GsonBuilder().create();
		String dataPersisted = gsonBuilder.toJson(homeworkPersisted);
		Homework homeworkUpdated = service.updateHomeworkById(id, homework);
		// Insert Log
		if (homeworkUpdated != null) {
			serviceLog.createLog(usuario, dataPersisted, homeworkUpdated);
		}
		return homeworkUpdated;
	}

	/**
	 * Elimina una homework por su id
	 * 
	 * @param id
	 * @return null
	 */
	@DeleteMapping("/delete/{id}")
	@ResponseStatus(HttpStatus.OK)
	public boolean deleteById(@PathVariable("id") Integer id) {
		repository.findById(id).ifPresent(o -> repository.delete(o));
		return true;
	}

}