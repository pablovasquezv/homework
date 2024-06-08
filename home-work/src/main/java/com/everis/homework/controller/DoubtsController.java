package com.everis.homework.controller;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.everis.homework.dto.Doubt;
import com.everis.homework.service.MailingService;

/**
 * 
 * @author sgutierc
 *
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/doubts")
public class DoubtsController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Value("${doubts.mail.production.inbox}")
	private String PRODUCTION_MAIL;
	@Value("${doubts.mail.production.subject}")
	private String SUBJECT_PROD;
	@Value("${doubts.mail.user.subject}")
	private String SUBJECT_USER;
	@Value("${doubts.mail.production.template}")
	private String BODY_PROD_TEMPLATE;
	@Value("${doubts.mail.user.template}")
	private String BODY_USER_TEMPLATE;

	@Value("${mail.template.realenddate}")
	private String REAL_END_DATE_TEMPLATE;
	
	private static final DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	/** The service */
	@Autowired
	MailingService mailingService;

	@Autowired
	ResourceLoader resourceLoader;

	@PostMapping("/send")
	public void sendDoubt(@RequestBody Doubt doubt) throws Exception {

		if (doubt.getFrom() == null || doubt.getFrom().isEmpty())
			throw new Exception("Invalid email from atrribute");
		if (doubt.getFrom().contains("@") == false)
			doubt.setFrom(doubt.getFrom() + "@everis.com");

		// Envio correo a produccion
		mailingService.sendMail(String.format(SUBJECT_PROD, doubt.getFrom()), PRODUCTION_MAIL,
				prepareBody(doubt.getProjectCode(), doubt.getDoubt(), doubt.getFrom(), BODY_PROD_TEMPLATE));
		// y luego informo al usuario
		mailingService.sendMail(SUBJECT_USER, doubt.getFrom(),
				prepareBody(doubt.getProjectCode(), doubt.getDoubt(), doubt.getFrom(), BODY_USER_TEMPLATE));
	}
	
	/**
	 * 
	 * @param usuario
	 * @param projectKey
	 * @param original
	 * @param updated
	 * @param modification
	 * @throws Exception
	 */
	public void realEndDateMail(String usuario,String projectKey,Date original, Date updated,Date modification) throws Exception {
		// Envio correo a produccion
		
		String body=getTemplate(REAL_END_DATE_TEMPLATE);
		body=body.replace("PROJECT_KEY",projectKey);//formatter.format(
		body=body.replace("ORIGINAL_DATE_KEY",formatter.format(original));
		body=body.replace("UPDATED_DATE_KEY",formatter.format(updated));
		body=body.replace("MOD_DATE_KEY",formatter.format(modification));
		body=body.replace("USER_KEY",usuario);		
		
		mailingService.sendMail("Actualización Fecha Real Fin",PRODUCTION_MAIL,body);
	}
	
	/**
	 * 
	 * @param doubt
	 * @param from
	 * @return
	 */
	private String prepareBody(String projectCode, String doubt, String from, String template) {
		String body = "";

		try {
			body = getTemplate(template);
			body = body.replace("PROJECT_KEY", projectCode);
			body = body.replace("DOUBT_KEY", doubt);
			body = body.replace("FROM_KEY", from);
			body = body.replace("DATE_KEY", formatter.format(new Date()));
		} catch (Exception e) {
			logger.error(e.toString());
		}

		return body;
	}

	/**
	 * 
	 * @return
	 */
	private String getTemplate(String templatePath) {
		Resource resource = resourceLoader.getResource(templatePath);
		String template = "";
		try {
			BufferedInputStream bs = new BufferedInputStream(resource.getInputStream());
			byte[] buffer = new byte[1024];
			int read = 0;
			while ((read = bs.read(buffer, 0, buffer.length)) != -1) {
				template += new String(buffer, 0, read, "UTF-8");
			}
		} catch (IOException e) {
			logger.error(e.toString());
		}
		return template;
	}

}
