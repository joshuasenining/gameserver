package org.softwarewolf.gameserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

@SpringBootApplication
@Service
public class GameserverApplication {	
	public static void main(String[] args) {
		SpringApplication.run(GameserverApplication.class, args);
	}
}
