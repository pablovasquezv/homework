package com.everis.homework.service;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 
 * @author sgutierc
 *
 */
@Service
public class MailingService {
	@Value("${smtp.server.url}")
	private String SMTP_SERVER;
	@Value("${smtp.server.auth.user}")
	private String USERNAME;
	@Value("${smtp.server.auth.password}")
	private String PASSWORD;
	@Value("${smtp.server.port}")
	private int SMTP_PORT;

	/**
	 * 
	 * @param subject
	 * @param recipients comma separated
	 * @param body
	 * @throws Exception
	 */
	public void sendMail(String subject, String recipients, String body) throws Exception {
		this.sendMail(subject, InternetAddress.parse(recipients), body);
	}

	/**
	 * 
	 * @param subject
	 * @param recipients a string array of recipients
	 * @param body
	 * @throws Exception
	 */
	public void sendMail(String subject, String[] recipients, String body) throws Exception {
		this.sendMail(subject, parseAddresses(recipients), body);
	}

	/**
	 * 
	 * @param subject
	 * @param recipients an Address Array of recipients
	 * @param body
	 * @throws Exception
	 */
	public void sendMail(String subject, Address[] recipients, String body) throws Exception {
		Properties prop = System.getProperties();
		prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", SMTP_SERVER);
		prop.put("mail.smtp.port", SMTP_PORT);
		prop.put("mail.smtp.ssl.trust", SMTP_SERVER);

		Session session = Session.getInstance(prop, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(USERNAME, PASSWORD);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(USERNAME));
			message.setRecipients(Message.RecipientType.TO, recipients);
			message.setSubject(subject);

			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			mimeBodyPart.setContent(body, "text/html");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(mimeBodyPart);

			message.setContent(multipart);

			Transport.send(message);

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param recipients
	 * @return
	 * @throws Exception
	 */
	private Address[] parseAddresses(String[] recipients) throws Exception {
		if (recipients == null)
			throw new Exception("recipients is null");
		if (recipients.length == 0)
			throw new Exception("recipients list is empty");

		String addresses = "";
		for (String rcp : recipients) {
			addresses += rcp + ",";
		}
		// quito la coma (,) final
		addresses = addresses.substring(0, addresses.length() - 1);

		return InternetAddress.parse(addresses);
	}
}
