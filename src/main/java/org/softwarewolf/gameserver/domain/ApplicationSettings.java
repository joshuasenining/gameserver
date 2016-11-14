package org.softwarewolf.gameserver.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ApplicationSettings {
	String encryptionSalt;

	public String getEncryptionSalt() {
		return encryptionSalt;
	}

	public void setEncryptionSalt(String encryptionSalt) {
		this.encryptionSalt = encryptionSalt;
	}	
}
