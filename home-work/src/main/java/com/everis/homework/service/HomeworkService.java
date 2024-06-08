package com.everis.homework.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.everis.homework.mapper.Homework;
import com.everis.homework.repository.IHomeworkRepository;

/**
 * The Class HomeworkService.
 * 
 * @author gsaravia
 */
@Service
public class HomeworkService {

	/** The repository. */
	@Autowired
	private IHomeworkRepository repository;

	/**
	 * Editar homework por su Id.
	 *
	 * @param id       the id
	 * @param homework the homework
	 * @return Mono<ResponseEntity<Homework> >
	 */
	public Homework updateHomeworkById(Integer id, Homework homework) {
		if (repository.existsById(id)) {
			homework.setNroEmpleado(id);
			return repository.save(homework);
		} else {
			return null;
		}
	}

	/**
	 * Creates the.
	 *
	 * @param homework the homework
	 * @return the homework
	 */
	public Homework create(Homework homework) {
		if (repository.existsById(homework.getNroEmpleado())) {
			return null;
		} else {
			return repository.save(homework);
		}
	}

	private DataFormatter formatter = new DataFormatter();

	/**
	 * To homework.
	 *
	 * @param workbookFactory the workbook factory
	 * @throws Exception
	 */
	public void toHomework(XSSFWorkbook workbookFactory) throws Exception {
		if (workbookFactory == null)
			throw new Exception("Invalid XSSFWorkbook input");

		XSSFSheet hoja = workbookFactory.getSheetAt(0);
		if (workbookFactory.getNumberOfSheets() == 2) {
			hoja = workbookFactory.getSheetAt(1);
		}
		XSSFRow fila = null;
		List<XSSFRow> filasvalidas = null;
		filasvalidas = validaCellBlankExcel(hoja);
		Integer totalFilas = filasvalidas.size();

		if (totalFilas == 0)
			throw new Exception("Empty or invalid file");

		List<Homework> parsedRows = new ArrayList<Homework>();

		for (int i = 3; i <= totalFilas; i++) {
			fila = hoja.getRow(i);
			if (fila != null) {
				Homework homework = parseRow(fila);
				parsedRows.add(homework);
			}
		}

		if (parsedRows.size() > 0)
			repository.saveAll(parsedRows);
		else
			throw new Exception("Invalid data");
	}

	/**
	 * Transforma una fila del libro excel en una entidad de tipo Homework
	 * 
	 * @param fila
	 * @return
	 * @throws Exception error si la data no cumple con los formatos esperados
	 */
	private Homework parseRow(XSSFRow fila) throws Exception {
		int x = 0;
		String nombreCliente = formatter.formatCellValue(fila.getCell(x++));
		Cell nroEmpleado = fila.getCell(x++);

		if (nroEmpleado == null || isNumeric(formatter.formatCellValue(nroEmpleado)) == false)
			throw new Exception("Invalid employee number");

//		if (nroEmpleado != null && isNumeric(formatter.formatCellValue(nroEmpleado))) {
		Homework homework = new Homework();
		Integer usuario = Integer.parseInt(formatter.formatCellValue(nroEmpleado));
		Optional<Homework> op = repository.findById(usuario);

		if (op.isPresent()) {
			homework = op.get();
		} else {
			homework = new Homework();
		}

		homework.setCliente(nombreCliente);
		homework.setNroEmpleado(usuario);
		String username = formatter.formatCellValue(fila.getCell(x++));
		if (username == null || username.isEmpty())
			throw new Exception("Invalid username");
		homework.setUserName(username);
		homework.setNombre(formatter.formatCellValue(fila.getCell(x++)));
		homework.setApellido(formatter.formatCellValue(fila.getCell(x++)));
		homework.setDonde(formatter.formatCellValue(fila.getCell(x++)));
		homework.setSector(formatter.formatCellValue(fila.getCell(x++)));
		homework.setProyect(formatter.formatCellValue(fila.getCell(x++)));
		homework.setProyect2(formatter.formatCellValue(fila.getCell(x++)));
		homework.setComentario(formatter.formatCellValue(fila.getCell(x++)));
		homework.setUnidad(formatter.formatCellValue(fila.getCell(x++)));
		String cuarentena = formatter.formatCellValue(fila.getCell(x++));

		if (cuarentena != null && cuarentena.equalsIgnoreCase("SI")) {
			homework.setCuarentena(true);
		} else {
			homework.setCuarentena(false);
		}

		Cell filaInicioCuarentena = fila.getCell(x++);
		if (filaInicioCuarentena != null) {
			if (isValidDate(filaInicioCuarentena)) {
				homework.setInicioCuarentena(convertToLocalDateViaInstant(filaInicioCuarentena.getDateCellValue()));
			} else if (!StringUtils.isEmpty(formatter.formatCellValue(filaInicioCuarentena))) {
				homework.setInicioCuarentena(
						convertToLocalDateViaInstant(formatter.formatCellValue(filaInicioCuarentena)));
			}
		}
		String isCovid19 = formatter.formatCellValue(fila.getCell(x++));
		if (isCovid19 != null && isCovid19.equalsIgnoreCase("SI")) {
			homework.setCovid19(true);
		} else {
			homework.setCovid19(false);
		}
		Cell fechaDiagnostico = fila.getCell(x++);
		if (fechaDiagnostico != null) {
			if (isValidDate(fechaDiagnostico)) {
				homework.setFechaDiagnostico(convertToLocalDateViaInstant(fechaDiagnostico.getDateCellValue()));
			} else if (!StringUtils.isEmpty(formatter.formatCellValue(fechaDiagnostico))) {
				homework.setFechaDiagnostico(convertToLocalDateViaInstant(formatter.formatCellValue(fechaDiagnostico)));
			}
		}
		homework.setTelefono(formatter.formatCellValue(fila.getCell(x++)));
		// Donde > Direccion
		homework.setDireccion(formatter.formatCellValue(fila.getCell(x++)));
		// Donde > Comuna
		homework.setComuna(formatter.formatCellValue(fila.getCell(x++)));
		// Donde > Region
		homework.setRegion(formatter.formatCellValue(fila.getCell(x++)));
		// Donde > Oficina
		homework.setOficinaEveris(formatter.formatCellValue(fila.getCell(x++)));

		// Fecha de inicio [Vacaciones o Licencia]
		Cell filaFechaInicio = fila.getCell(x++);
		if (filaFechaInicio != null) {
			if (isValidDate(filaFechaInicio)) {
				homework.setFechaInicio(convertToLocalDateViaInstant(filaFechaInicio.getDateCellValue()));
			} else if (!StringUtils.isEmpty(formatter.formatCellValue(filaFechaInicio))) {
				homework.setFechaInicio(convertToLocalDateViaInstant(formatter.formatCellValue(filaFechaInicio)));
			}
		}
		// Fecha de fin [Vacaciones o Licencia]
		Cell filaFechaFin = fila.getCell(x++);
		if (filaFechaFin != null) {
			if (isValidDate(filaFechaFin)) {
				homework.setFechaFin(convertToLocalDateViaInstant(filaFechaFin.getDateCellValue()));
			} else if (!StringUtils.isEmpty(formatter.formatCellValue(filaFechaFin))) {
				homework.setFechaFin(convertToLocalDateViaInstant(formatter.formatCellValue(filaFechaFin)));
			}
		}

//		}
		return homework;
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
	 * Checks if is numeric.
	 *
	 * @param cadena the cadena
	 * @return true, if is numeric
	 */
	private static boolean isNumeric(String cadena) {
		try {
			Integer.parseInt(cadena);
			return true;
		} catch (NumberFormatException nfe) {
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

	/*
	 * Metodo que realiza las validaciones de celdas vacias dentro de una hoja
	 * Excel. Retorna lista de filas validadas
	 */
	public static List<XSSFRow> validaCellBlankExcel(XSSFSheet hoja) {
		List<XSSFRow> filasvalidas = new ArrayList<XSSFRow>();
		filasvalidas = validaCellBlankExcelFilaInicio(hoja, 1);
		return filasvalidas;
	}

	/**
	 * Valida cell blank excel fila inicio.
	 *
	 * @param hoja       the hoja
	 * @param filaInicio the fila inicio
	 * @return the list
	 */
	public static List<XSSFRow> validaCellBlankExcelFilaInicio(XSSFSheet hoja, int filaInicio) {
		List<XSSFRow> filasvalidas = new ArrayList<XSSFRow>();
		Boolean filaNoVacia = false;
		int lastRowNum = hoja.getLastRowNum();

		for (int i = filaInicio; i <= lastRowNum; i++) {
			filaNoVacia = false;
			if (hoja.getRow(i) != null) {
				int firstCell = hoja.getRow(i).getFirstCellNum();
				int lastCell = hoja.getRow(i).getLastCellNum();
				for (int j = firstCell; j < lastCell; j++) {
					Cell celda = (hoja.getRow(i) != null && hoja.getRow(i).getCell(j) != null)
							? hoja.getRow(i).getCell(j)
							: null;
					if (celda != null && celda.getCellType() != CellType.BLANK) {
						filaNoVacia = true;
					}
				}
				if (filaNoVacia) {
					filasvalidas.add(hoja.getRow(i));
				}
			}
		}
		return filasvalidas;
	}
}