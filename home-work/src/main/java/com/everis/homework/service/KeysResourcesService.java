package com.everis.homework.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.math3.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.everis.homework.mapper.ContigencyDocument;
import com.everis.homework.mapper.DetailKeysResources;
import com.everis.homework.mapper.KeysResources;
import com.everis.homework.repository.IContigencyDocRepository;
import com.everis.homework.repository.IDetailResourcesRepository;
import com.everis.homework.repository.IKeysResourcesRepository;

/**
 * The Class HomeworkService.
 * 
 * @author hvergarc
 */
@Service
public class KeysResourcesService {

	private static final Logger LOGGER = LogManager.getLogger(KeysResourcesService.class);
	private DataFormatter formatter = new DataFormatter();
	private final int WORKBOOK_SHEET = 0;

	/** The repository. */
	@Autowired
	private IKeysResourcesRepository keysResourcesRepository;
	@Autowired
	private IDetailResourcesRepository detailRepository;
	@Autowired
	private IContigencyDocRepository docRepository;

	public List<KeysResources> findAll() {
		List<KeysResources> listUsuarios = null;

		try {
			listUsuarios = keysResourcesRepository.findAll();

		} catch (Exception e) {
			LOGGER.error("No es posible obtener las aplicaciones del usuario :", e);
		}

		return listUsuarios;
	}

	public KeysResources findById(String id) {

		KeysResources keysResources = null;

		keysResources = keysResourcesRepository.findById(id).orElse(null);

		return keysResources;
	}

	public ContigencyDocument getDocument(String id) {
		return docRepository.findById(id).orElse(null);
	}

	public boolean deleteByCodProd(String codProd) {

		if (!(detailRepository.findByProjectCode(codProd) == null)) {
			detailRepository.deleteByProjectCode(codProd);
		}

		keysResourcesRepository.findById(codProd).ifPresent(o -> keysResourcesRepository.delete(o));
		return true;
	}

	public KeysResources loadDocument(ContigencyDocument doc) {
		docRepository.save(doc);
		return this.findById(doc.getProjectCode());
	}

	/**
	 * 
	 * @param kr
	 * @return
	 */
	public KeysResources update(KeysResources kr) {
		// limpiamos los detalles asociados a los codigos de proyecto leidos
		//detailRepository.deleteByProjectCode(kr.getProjectCode());
		//detailRepository.flush();
		// y luego persistimos todas las entidades de tipo Key
		if (kr.getDetailKeysResourcesList() != null || kr.getDetailKeysResourcesList().isEmpty() == false) {
			for (DetailKeysResources det : kr.getDetailKeysResourcesList()) {
				/*
				 * if(det.getIdDetKeyResources()==null) det.setIdDetKeyResources(new
				 * Integer(0));
				 */
				det = detailRepository.save(det);
			}
		}
		detailRepository.flush();

		keysResourcesRepository.save(kr);
		keysResourcesRepository.flush();

		return kr;
	}

	public KeysResources create(KeysResources kr) {
		KeysResources resource = null;
		try {
			if (kr.getDetailKeysResourcesList() != null || kr.getDetailKeysResourcesList().isEmpty() == false) {
				for (DetailKeysResources det : kr.getDetailKeysResourcesList()) {
					// det.setIdDetKeyResources(null);
					det = detailRepository.save(det);
				}
			}
			resource = keysResourcesRepository.save(kr);

			keysResourcesRepository.flush();
			detailRepository.flush();

		} catch (Exception e) {
			LOGGER.error("Error parsing excel file", e);
			throw new RuntimeException(e.toString());
		}

		return resource;

	}

	/**
	 * 
	 * @param workbook
	 * @throws Exception
	 */
	public void loadFromBook(XSSFWorkbook workbookFactory) throws Exception {
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
			HashMap<KeysResources, Set<DetailKeysResources>> mappedRows = new HashMap<KeysResources, Set<DetailKeysResources>>();
			for (XSSFRow row : validRows) {
				// creando una entidad por cada fila
				Pair<KeysResources, DetailKeysResources> rowdata = parseRow(row);
				KeysResources key = rowdata.getFirst();
				Set<DetailKeysResources> resources = mappedRows.get(key);

				if (resources == null)
					resources = new CopyOnWriteArraySet<DetailKeysResources>();
				resources.add(rowdata.getSecond());

				key.setDetailKeysResourcesList(resources);
				mappedRows.put(key, resources);
			}

			// limpiamos los detalles asociados a los codigos de proyecto leidos
			for (KeysResources kr : mappedRows.keySet())
				detailRepository.deleteByProjectCode(kr.getProjectCode());

			// y luego persistimos todas las entidades de tipo Key
			keysResourcesRepository.saveAll(mappedRows.keySet());

		} catch (Exception e) {
			LOGGER.warn("Exception parsing excel row", e);
			throw new Exception("Exception parsing excel file");
		}
	}

	/**
	 * Permite trasformar una fila de excel en un objeto de tipo KeysResources
	 * 
	 * @param row
	 * @return
	 * @throws Exception
	 */
	private Pair<KeysResources, DetailKeysResources> parseRow(XSSFRow row) throws Exception {
		KeysResources kr = new KeysResources();
		DetailKeysResources dr = new DetailKeysResources();

		int lastCell = row.getLastCellNum();
		for (int i = 0; i < lastCell; i++) {
			Cell cell = row.getCell(i);
			switch (i) {
			case 0: {
				kr.setUN(formatter.formatCellValue(cell).trim());
				break;
			} // UN
			case 1: {
				kr.setSector(formatter.formatCellValue(cell).trim());
				break;
			} // SECTOR
			case 2: {
				kr.setCE(formatter.formatCellValue(cell).trim());
				break;
			} // CE
			case 3: {
				kr.setNombreCliente(formatter.formatCellValue(cell).trim());
				break;
			} // NOMBRE CLIENTE
			case 4: {
				String valor = formatter.formatCellValue(cell).trim();
				if (valor == null || valor.isEmpty())
					throw new Exception("Codigo de proyecto invalido");
				kr.setProjectCode(formatter.formatCellValue(cell).trim());
				break;
			} // CODIGO PROYECTO
			case 5: {
				kr.setNombreProyecto(formatter.formatCellValue(cell).trim());
				break;
			} // NOMBRE PROYECTO
			case 6: {
				kr.setClienteManager(formatter.formatCellValue(cell).trim());
				break;
			} // NOMBRE CLIENT MANAGER
			case 7: {
				kr.setGerenteProyecto(formatter.formatCellValue(cell).trim());
				break;
			} // GERENTE PROYECTO
				// CAMPOS DE DETALLE
			case 8: {
				dr.setPerfilClave(formatter.formatCellValue(cell).trim());
				break;
			} // PERFIL CLAVE
			case 9: {
				dr.setNombrePerfilClave(formatter.formatCellValue(cell).trim());
				break;
			} // NOMBRE PERFIL CLAVE
			case 10: {
				dr.setNombreBackup(formatter.formatCellValue(cell).trim());
				break;
			} // NOMBRE DEL BACKUP
			case 11: {
				dr.setPlanContingencia(toThreeLogic(cell));
				break;
			} // Tiene plan de contingencia / continuidad que contenga esta informacion?
			case 12: {
				dr.setBackupActividades(toThreeLogic(cell));
				break;
			} // El backup conoce la actividades que debe realizar?
			case 13: {
				dr.setBackupHerramientas(toThreeLogic(cell));
				break;
			} // El backup conoce los repositorios, herramientas, accesos etc., que se utiliza
				// en el proyectos?
			case 14: {
				dr.setPclaveReunionSeg(toThreeLogic(cell));
				break;
			} // Si el perfil clave es un lider, este ha participado en reunion de
				// seguimiento?"
			case 15: {
				dr.setComunicacionCliEquipo(toThreeLogic(cell));
				break;
			} // Se considero la comunicacion del plan al cliente y al equipo?"
			case 16: {
				dr.setComentarios(formatter.formatCellValue(cell).trim());
				break;
			} // Comentarios Adicionales
			default:
				break;
			}
		}

		keysResourcesRepository.findById(kr.getProjectCode())
				.ifPresent(o -> kr.setDocumPlanContingencia(o.getDocumPlanContingencia()));

		dr.setProjectCode(kr.getProjectCode());
		return new Pair<KeysResources, DetailKeysResources>(kr, dr);
	}
	
	/**
	 * 0<-NO 1<-SI 2<-N/A
	 * 
	 * @param cell
	 * @return
	 */
	private Integer toThreeLogic(Cell cell) {
		String value = formatter.formatCellValue(cell).trim();
		if (value == null || value.isEmpty())
			return new Integer(2);
		if(value.equalsIgnoreCase("N/A") || value.equalsIgnoreCase("NA"))
			return new Integer(2);
		if (value.toUpperCase().equals("SI"))
			return new Integer(1);
		else
			return new Integer(0);
	}

	/**
	 * 
	 * @param cell
	 * @return
	 */
	private Boolean toBoolean(Cell cell) {
		String value = formatter.formatCellValue(cell).trim();
		if (value == null || value.isEmpty())
			return new Boolean(false);
		if (value.toUpperCase().equals("SI"))
			return new Boolean(true);
		else
			return new Boolean(false);
	}

	/**
	 * Retorna un listado de filas validas (no vacias)
	 *
	 * @param hoja       hoja del libro
	 * @param filaInicio the fila inicio
	 * @return the list
	 */
	private List<XSSFRow> getValidRows(XSSFSheet hoja, int filaInicio) {
		List<XSSFRow> filasvalidas = new ArrayList<XSSFRow>();
		int lastRowNum = hoja.getLastRowNum();

		for (int i = filaInicio; i <= lastRowNum; i++) {
			XSSFRow row = hoja.getRow(i);
			if (row != null) {
				Cell celda = row.getCell(6);// Reviso sobre la columna de Project Code
				if (celda != null && celda.getCellType() != CellType.BLANK) {
					filasvalidas.add(row);
				}
			}
		}
		return filasvalidas;
	}

	/**
	 * Elimina un listado de resources desde la BD
	 * @param resources
	 * @return
	 */
	public boolean deleteDetails(List<DetailKeysResources>  resources) {
		try {
			if (resources == null)
				return true;
			detailRepository.deleteAll(resources);

			return true;
		} catch (Exception e) {
			LOGGER.error(e.toString());
		}
		return false;
	}

}