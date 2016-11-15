package org.softwarewolf.gameserver.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

public class EmailSettings implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String smtpHost;
	private String socketFactoryPort;
	private String socketFactoryClass;
	private boolean smtpAuth;
	private String smtpPort;
	private boolean disableEmail;
	private String userLogin;
	private String userPassword;
	private String testFromAddress;
	private String testToAddress;
	
	public EmailSettings() {
		smtpAuth = false;
		disableEmail = true;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
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

	public String getTestFromAddress() {
		return testFromAddress;
	}

	public void setTestFromAddress(String testFromAddress) {
		this.testFromAddress = testFromAddress;
	}

	public String getTestToAddress() {
		return testToAddress;
	}

	public void setTestToAddress(String testToAddress) {
		this.testToAddress = testToAddress;
	}

	public void setSmtpAuth(boolean smtpAuth) {
		this.smtpAuth = smtpAuth;
	}

	public void setDisableEmail(boolean disableEmail) {
		this.disableEmail = disableEmail;
	}
	
}
