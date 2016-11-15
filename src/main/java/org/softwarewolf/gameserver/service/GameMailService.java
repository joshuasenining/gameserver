package org.softwarewolf.gameserver.service;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.softwarewolf.gameserver.controller.helper.FeFeedback;
import org.softwarewolf.gameserver.domain.EmailSettings;
import org.softwarewolf.gameserver.repository.EmailSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource({"classpath:application.properties"})
public class GameMailService {
	@Autowired
	private EmailSettingsRepository emailSettingsRepository;
	
	@Autowired
	EncodingService encodingService;
	
	public void testMail(FeFeedback feFeedback) {
		Properties props = getEmailProperties();
		String encryptedPassword = props.getProperty("mail.password");
		String decryptedPassword = null;
		try {
			decryptedPassword = encodingService.decrypt(encryptedPassword);
			props.put("mail.password.decrypted", decryptedPassword);
		} catch (Exception e1) {
			feFeedback.setError(e1.getMessage());
			return;
		}
		
		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(props.getProperty("mail.login"), props.getProperty("mail.password.decrypted"));
				}
			});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(props.getProperty("mail.test.from")));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(props.getProperty("mail.test.to")));
			message.setSubject("Testing Subject");
			message.setText("Dear Mail Crawler," +
					"\n\n No spam to my email, please!");

			Transport.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}		
	}
	
	public Properties getEmailProperties() {
		Properties props = new Properties();
		EmailSettings emailSettings = getEmailSettings();
		
		props.put("mail.smtp.host", emailSettings.getSmtpHost() == null ? "" : emailSettings.getSmtpHost());
		props.put("mail.smtp.socketFactory.port", emailSettings.getSocketFactoryPort() == null ? "" : emailSettings.getSocketFactoryPort());
		props.put("mail.smtp.socketFactory.class", emailSettings.getSocketFactoryClass() == null ? "" : emailSettings.getSocketFactoryClass());
		props.put("mail.smtp.auth", emailSettings.getSmtpAuth() ? "true" : "false");
		props.put("mail.smtp.port", emailSettings.getSmtpPort() == null ? "" : emailSettings.getSmtpPort());
		props.put("mail.login", emailSettings.getUserLogin() == null ? "" : emailSettings.getUserLogin());
		props.put("mail.password", emailSettings.getUserPassword() == null ? "" : emailSettings.getUserPassword());
		props.put("mail.test.from", emailSettings.getTestFromAddress() == null ? "" : emailSettings.getTestFromAddress());
		props.put("mail.test.to", emailSettings.getTestToAddress() == null ? "" : emailSettings.getTestToAddress());
		
		return props;
	}

	public EmailSettings initEmailSettings(EmailSettings emailSettings) {
		Properties props = getEmailProperties();

		emailSettings.setDisableEmail(Boolean.valueOf(props.getProperty("mail.disable")));
		emailSettings.setSmtpAuth(Boolean.valueOf(props.getProperty("mail.smtp.auth")));
		emailSettings.setSmtpHost(props.getProperty("mail.smtp.host"));
		emailSettings.setSmtpPort(props.getProperty("mail.smtp.port"));
		emailSettings.setSocketFactoryClass(props.getProperty("mail.smtp.socketFactory.class"));
		emailSettings.setSocketFactoryPort(props.getProperty("mail.smtp.socketFactory.port"));
		emailSettings.setUserLogin(props.getProperty("mail.login"));
		emailSettings.setUserPassword(props.getProperty("mail.password"));
		emailSettings.setTestFromAddress(props.getProperty("mail.test.from"));
		emailSettings.setTestToAddress(props.getProperty("mail.test.to"));
		
		return emailSettings;
	}
	
	public void updateEmailSettings(EmailSettings emailSettings, FeFeedback feFeedback) {
		// Should probably do some validation...
		EmailSettings originalEmailSettings = getEmailSettings();
		String currentPassword = emailSettings.getUserPassword();
		if (emailSettings.getId() == null) {
			if (originalEmailSettings != null) {
				emailSettings.setId(originalEmailSettings.getId());
			}
			if (currentPassword != null) {
				try {
					emailSettings.setUserPassword(encodingService.encrypt(currentPassword));
				} catch (Exception e) {
					feFeedback.setError(e.getMessage());
					return;
				}
			}
		} else {
			// This is an update, check to see if we need to encode a new password
			String originalPassword = originalEmailSettings.getUserPassword();
			if (originalPassword != currentPassword) {
				try {
					emailSettings.setUserPassword(encodingService.encrypt(currentPassword));
				} catch (Exception e) {
					feFeedback.setError(e.getMessage());
					return;
				}
			}
		}
		String info = null;
		String error = null;
		try {
			emailSettingsRepository.save(emailSettings);
			info = "Settings updated";
		} catch (Exception e) {
			error = e.getMessage();
		}
		feFeedback.setInfo(info);
		feFeedback.setError(error);
	}
		
	public EmailSettings getEmailSettings() {
		List<EmailSettings> emailSettingsList = emailSettingsRepository.findAll();
		EmailSettings emailSettings = null;
		if (emailSettingsList.size() >  0) {
			emailSettings = emailSettingsList.get(0);
		} else {
			emailSettings = new EmailSettings();
		}	
		return emailSettings;
	}
}
