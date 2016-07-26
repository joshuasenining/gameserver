package org.softwarewolf.gameserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({"classpath*:/db.xml"})
public class DatasourceConfig {

}
