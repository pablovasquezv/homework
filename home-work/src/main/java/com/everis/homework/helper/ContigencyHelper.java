package com.everis.homework.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.everis.homework.dto.VisualContigency;
import com.everis.homework.mapper.KeysResources;

/**
 * 
 */
public class ContigencyHelper {

	private static Logger logger = LoggerFactory.getLogger(ContigencyHelper.class);

	public static VisualContigency toVisual(KeysResources kr) {
		if(kr==null ) return null;
		VisualContigency vc=new VisualContigency();
		vc.setCE(kr.getCE());
		vc.setClienteManager(kr.getClienteManager());
		vc.setDetailKeysResourcesList(kr.getDetailKeysResourcesList());
		vc.setGerenteProyecto(kr.getGerenteProyecto());
		vc.setNombreCliente(kr.getNombreCliente());
		vc.setNombreProyecto(kr.getNombreProyecto());
		vc.setProjectCode(kr.getProjectCode());
		vc.setSector(kr.getSector());
		vc.setUN(kr.getUN());
		vc.setPoseePlan(kr.getDocumPlanContingencia()!=null);
		
		return vc;
			
	}
}
