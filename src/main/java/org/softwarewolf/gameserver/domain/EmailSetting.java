package org.softwarewolf.gameserver.domain;

import java.io.Serializable;

public class EmailSetting implements Serializable {
	private static final long serialVersionUID = 1L;
	private String smtpHost;
	private String socketFactoryPort;
	private String socketFactoryClass;
	private Boolean smtpAuth;
	private String smtpPort;
	private Boolean disableEmail;
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
	public Boolean getSmtpAuth() {
		return smtpAuth;
	}
	public void setSmtpAuth(Boolean smtpAuth) {
		this.smtpAuth = smtpAuth;
	}
	public String getSmtpPort() {
		return smtpPort;
	}
	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
	}
	public Boolean getDisableEmail() {
		return disableEmail;
	}
	public void setDisableEmail(Boolean disableEmail) {
		this.disableEmail = disableEmail;
	}
	
}
