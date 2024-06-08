package com.everis.homework.controller;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.everis.homework.dto.VisualContigency;
import com.everis.homework.helper.ContigencyHelper;
import com.everis.homework.mapper.ContigencyDocument;
import com.everis.homework.mapper.DetailKeysResources;
import com.everis.homework.mapper.KeysResources;
import com.everis.homework.service.KeysResourcesService;

/**
 * @author gsaravia
 *
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/keysresources")
public class KeysResourcesController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/** The service. */
	@Autowired
	private KeysResourcesService keysResourcesService;

//	/** The service log. */
//	@Autowired
//	private HomeworkLogService serviceLog;

	@PostMapping("/massive")
	public void cargaMasiva(@RequestParam("file") MultipartFile excel) {
		try {
			XSSFWorkbook workbook = new XSSFWorkbook(excel.getInputStream());
			keysResourcesService.loadFromBook(workbook);
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
	 * @throws Exception
	 */
	@PostMapping("/create")
	@ResponseStatus(HttpStatus.CREATED)
	public VisualContigency createKeysResources(@RequestBody KeysResources keysResources) throws Exception {

		if (keysResourcesService.findById(keysResources.getProjectCode()) != null)
			return update(keysResources.getProjectCode(), keysResources);
		else
			return ContigencyHelper.toVisual(keysResourcesService.create(keysResources));
	}

	@PostMapping("/deleteDetail")
	public boolean deleteDetail(@RequestBody List<DetailKeysResources> resources) {
		if (resources == null)
			return true;
		
		return keysResourcesService.deleteDetails(resources);
	}

	/**
	 * Obtener todas las homework
	 * 
	 * @return
	 */
	@GetMapping("/get/all")
	@ResponseStatus(HttpStatus.OK)
	public List<VisualContigency> findAll() {
		List<VisualContigency> all = new CopyOnWriteArrayList<VisualContigency>();
		for (KeysResources kr : keysResourcesService.findAll())
			all.add(ContigencyHelper.toVisual(kr));

		return all;
	}

	/**
	 * Obtener una homework por su id
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/find/{id}")
	@ResponseStatus(HttpStatus.OK)
	public VisualContigency findById(@PathVariable("id") String id) {
		return ContigencyHelper.toVisual(keysResourcesService.findById(id));
	}

	/**
	 * Modifica una homework por su id
	 * 
	 * @param id
	 * @param med
	 * @return
	 * @throws Exception
	 */
	@PutMapping("/update/{id}")
	@ResponseStatus(HttpStatus.OK)
	public VisualContigency update(@PathVariable("id") String id, @RequestBody KeysResources keysResources)
			throws Exception {
		if (keysResourcesService.findById(id) == null)
			throw new Exception("Not able to find resource with id " + id);

		return ContigencyHelper.toVisual(keysResourcesService.update(keysResources));
	}

	@PostMapping("/loadDocument/{id}")
	public VisualContigency loadPlan(@PathVariable("id") String id, @RequestBody ContigencyDocument document)
			throws Exception {
		KeysResources kr = keysResourcesService.findById(id);
		if (kr == null)
			throw new Exception("Not able to find resource with id " + id);

		return ContigencyHelper.toVisual(keysResourcesService.loadDocument(document));
	}

	@GetMapping("/getDocument/{id}")
	public ContigencyDocument getDocument(@PathVariable("id") String id) {
		ContigencyDocument document = keysResourcesService.getDocument(id);
		return document;
	}

	/**
	 * Elimina una homework por su id
	 * 
	 * @param id
	 * @return null
	 */
	@DeleteMapping("/delete/{codProd}")
	@ResponseStatus(HttpStatus.OK)
	public boolean deleteById(@PathVariable("codProd") String codProd) {
		return keysResourcesService.deleteByCodProd(codProd);
	}

}