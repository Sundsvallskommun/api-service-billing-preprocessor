package se.sundsvall.billingpreprocessor.integration.sftp;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("integration.sftp")
@Component
public class SftpPropertiesConfig {

	private Map<String, SftpProperties> municipalityIds = new HashMap<>();

	public Map<String, SftpProperties> getMap() {
		return municipalityIds;
	}

	public void setMunicipalityIds(Map<String, SftpProperties> municipalityId) {
		this.municipalityIds = municipalityId;
	}
}
