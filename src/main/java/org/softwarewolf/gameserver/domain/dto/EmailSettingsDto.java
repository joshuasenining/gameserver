package org.softwarewolf.gameserver.domain.dto;

import java.io.Serializable;

public class EmailSettingsDto implements Serializable {
	private static final long serialVersionUID = 1L;
	private String smtpHost;
	private String socketFactoryPort;
	private String socketFactoryClass;
	private String smtpAuth;
	private String smtpPort;
	private String disableEmail;
	private String userLogin;
	private String userPassword;
	
	public String getUserLogin() {
		return userLogin;
	}
	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getSmtpHost() {
		return smtpHost;
	}
	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}
	public String getSocketFactoryPort() {
		return socketFactoryPort;
	}
	public void setSocketFactoryPort(String socketFactoryPort) {
		this.socketFactoryPort = socketFactoryPort;
	}
	public String getSocketFactoryClass() {
		return socketFactoryClass;
	}
	public void setSocketFactoryClass(String socketFactoryClass) {
		this.socketFactoryClass = socketFactoryClass;
	}
	public String getSmtpAuth() {
		return smtpAuth;
	}
	public void setSmtpAuth(String smtpAuth) {
		this.smtpAuth = smtpAuth;
	}
	public String getSmtpPort() {
		return smtpPort;
	}
	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
	}
	public String getDisableEmail() {
		return disableEmail;
	}
	public void setDisableEmail(String disableEmail) {
		this.disableEmail = disableEmail;
	}
	
}
