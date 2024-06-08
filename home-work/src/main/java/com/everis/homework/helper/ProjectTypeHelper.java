package com.everis.homework.helper;

import java.util.HashMap;

/**
 * 
 * @author sgutierc
 *
 */
public class ProjectTypeHelper {

	private static final String PROJECT_AGILE_TEXT = String.format("Proyecto %sgil", '\u00C1');

	/**
	 * Servicios
	 */
	public static final int SERVICE_CODE = 1;
	/**
	 * Projecto Agile
	 */
	public static final int PROJECT_AGILE_CODE = 2;
	/**
	 * Projecto Tradicional
	 */
	public static final int PROJECT_TRAD_CODE = 3;
	private static final int DEFAULT = PROJECT_TRAD_CODE;

	private static final HashMap<String, Integer> TextToCode = new HashMap<String, Integer>();
	private static final HashMap<Integer, String> CodeToText = new HashMap<Integer, String>();

	static {
		TextToCode.put("Servicios", SERVICE_CODE);
		TextToCode.put("Proyecto Agil", PROJECT_AGILE_CODE);
		TextToCode.put(PROJECT_AGILE_TEXT, PROJECT_AGILE_CODE);
		TextToCode.put("Proyecto", PROJECT_TRAD_CODE);

		CodeToText.put(SERVICE_CODE, "Servicios");
		CodeToText.put(PROJECT_AGILE_CODE, PROJECT_AGILE_TEXT);
		CodeToText.put(PROJECT_TRAD_CODE, "Proyecto");
	}

	/**
	 * Retorna el codigo de tipo de proyecto dado su nombre
	 * 
	 * @param type
	 * @return
	 */
	public static Integer textToCode(String type) {
		if (TextToCode.containsKey(type))
			return TextToCode.get(type);
		else
			return DEFAULT;
	}

	/**
	 * Retorna el nombre del tipo de proyecto dado su codigo
	 * 
	 * @param code
	 * @return
	 */
	public static String codeToText(Integer code) {
		return CodeToText.get(code);

	}

}
