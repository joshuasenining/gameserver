package org.softwarewolf.gameserver.service;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.softwarewolf.gameserver.domain.ApplicationSettings;
import org.softwarewolf.gameserver.repository.ApplicationSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EncodingService {
	@Autowired
	private ApplicationSettingsRepository applicationSettingsRepository;
	
//	private static final String KEY = "1Hbfh667adfDEJ79";
//	private SecureRandom random = new SecureRandom();
	
	public String encrypt(String value)
			throws Exception {
		Key key = generateKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte [] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
        String encryptedValue64 = Base64.getEncoder().encodeToString(encryptedByteValue);
        return encryptedValue64;
	}

	public String decrypt(String value)
			throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte [] decryptedValue64 = Base64.getDecoder().decode(value);
        byte [] decryptedByteValue = cipher.doFinal(decryptedValue64);
        String decryptedValue = new String(decryptedByteValue,"utf-8");
        return decryptedValue;
	}
	
    private Key generateKey() throws Exception {
//        Key key = new SecretKeySpec(EncodingService.KEY.getBytes(),"AES");
        Key key = new SecretKeySpec(getSalt().getBytes(),"AES");
        return key;
    }
    
    private String getSalt() {
    	List<ApplicationSettings> settingsList = applicationSettingsRepository.findAll();
    	ApplicationSettings settings = null;
    	if (settingsList.isEmpty()) {
    		settings = new ApplicationSettings();
    	} else {
    		settings = settingsList.get(0);
    	}
    	String salt = null;
    	if (settings.getEncryptionSalt() == null ||
    			settingsList.get(0).getEncryptionSalt().isEmpty()) {
    		char[] chars = "abcdefghijklmnopqrstuvwxyz_-+)(*&^%$#@!".toCharArray();
    		StringBuilder sb = new StringBuilder();
    		Random random = new Random();
    		for (int i = 0; i < 16; i++) {
    		    char c = chars[random.nextInt(chars.length)];
    		    sb.append(c);
    		}
    		settings.setEncryptionSalt(sb.toString());
    		applicationSettingsRepository.save(settings);
    		salt = settings.getEncryptionSalt();
    	} else {
    		salt = settings.getEncryptionSalt();
    	}
    	return salt;
    }
}
