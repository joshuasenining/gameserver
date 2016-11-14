package org.softwarewolf.gameserver.service;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.softwarewolf.gameserver.controller.helper.FeFeedback;
import org.softwarewolf.gameserver.domain.dto.EmailSettingsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource({"classpath:application.properties"})
public class GameMailService {
	@Value("${mail.smtp.host}")
	private String smtpHost;
	
	@Value("${mail.smpt.socketFactory.port}")
	private String socketFactoryPort;

	@Value("${mail.smtp.socketFactory.class}")
	private String socketFactoryClass;

	@Value("${mail.smtp.auth}")
	private String auth;

	@Value("${mail.smtp.port}")
	private String port;

	public void testMail() {
		Properties props = getEmailProperties();
		
		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("dirkwiggley@gmail.com","Gm@ilS0cks");
				}
			});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("dirkwiggley@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse("dm_tim@yahoo.com"));
			message.setSubject("Testing Subject");
			message.setText("Dear Mail Crawler," +
					"\n\n No spam to my email, please!");

			Transport.send(message);

			System.out.println("Done");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}		
	}
	
	public Properties getEmailProperties() {
		Properties props = new Properties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.socketFactory.port", socketFactoryPort);
		props.put("mail.smtp.socketFactory.class",
				socketFactoryClass);
		props.put("mail.smtp.auth", auth);
		props.put("mail.smtp.port", port);
		
		return props;
	}

	public EmailSettingsDto initEmailSettignsDto(EmailSettingsDto emailSettingsDto) {
		Properties props = getEmailProperties();
		emailSettingsDto.setDisableEmail(props.getProperty("mail.disable"));
		emailSettingsDto.setSmtpAuth(props.getProperty("mail.smtp.auth"));
		emailSettingsDto.setSmtpHost(props.getProperty("mail.smtp.host"));
		emailSettingsDto.setSmtpPort(props.getProperty("mail.smtp.port"));
		emailSettingsDto.setSocketFactoryClass(props.getProperty("mail.smtp.socketFactory.class"));
		emailSettingsDto.setSocketFactoryPort(props.getProperty("mail.smtp.socketFactory.port"));
		return emailSettingsDto;
	}
	
	public void changeEmailSettings(EmailSettingsDto emailSettingsDto, FeFeedback feFeedback) {
		feFeedback.setInfo("Settings updated");
	}
}
