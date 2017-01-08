package org.softwarewolf.gameserver.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ApplicationSettings {
	@Id
	String id;
	String encryptionSalt;
	String exportDir;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getEncryptionSalt() {
		return encryptionSalt;
	}
	public void setEncryptionSalt(String encryptionSalt) {
		this.encryptionSalt = encryptionSalt;
	}
	
	public String getExportDir() {
		return exportDir;
	}
	public void setExportDir(String exportDir) {
		this.exportDir = exportDir;
	}
}
