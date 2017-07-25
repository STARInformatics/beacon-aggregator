package bio.knowledge.aggregator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

//@Service
//@PropertySource("classpath:application.properties")
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class KnowledgeBeaconRegistry {
	
	private static String masterKnowledgeBeaconList = "https://raw.githubusercontent.com/"
			+ "NCATS-Tangerine/translator-knowledge-beacon/"
			+ "develop/api/knowledge-beacon-list.yaml";
	
	private List<KnowledgeBeacon> knowledgeBeacons = new ArrayList<KnowledgeBeacon>();
	
	public KnowledgeBeacon getKnowledgeBeaconByUrl(String url) {
		for (KnowledgeBeacon kb : getKnowledgeBeacons()) {
			if (kb.getUrl().equals(url)) {
				return kb;
			}
		}
		
		return null;
	}
	
	public List<KnowledgeBeacon> getKnowledgeBeacons() {		
		return this.knowledgeBeacons;
	}
	
	@PostConstruct
	public void init() {
		initKnowledgeBeacons();
//		neo4jInit();

	}
	
	/**
	 * Initiates the registry by grabbing beacons from the official yaml file:
	 * https://raw.githubusercontent.com/NCATS-Tangerine/translator-knowledge-beacon/develop/api/knowledge-beacon-list.yaml
	 */
	@SuppressWarnings("unchecked")
	private void initKnowledgeBeacons() {
		try {
			URL site = new URL(masterKnowledgeBeaconList);
			InputStream inputStream = site.openStream();
			Yaml yaml = new Yaml();
			Map<String, Object> yamlObject = (Map<String, Object>) yaml.load(inputStream);
			ArrayList<Map<String, Object>> beacons = (ArrayList<Map<String, Object>>) yamlObject.get("beacons");
			
			for (Map<String, Object> beacon : beacons) {
				String url = (String) beacon.get("url");
				String name = (String) beacon.get("name");
				String description = (String) beacon.get("description");
				String status = (String) beacon.get("status");
				boolean isEnabled = !"in_progress".equals(status);
				
				if (url != null) {
					this.knowledgeBeacons.add(
							new KnowledgeBeacon(url, name, description, isEnabled)
					);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	private boolean check(String url) {		
		return true;
	}
	*/

	/**
	 * Adds a knowledge source with the given URL to the knowledge source pool
	 * that will be queried by the methods in {@code KnowledgeBeaconService}.
	 * 
	 * @param url
	 */
	public void addKnowledgeSource(String url, String name, String description) {
		KnowledgeBeacon kb = new KnowledgeBeacon(url, name, description);
		if (!knowledgeBeacons.contains(kb)) {
			knowledgeBeacons.add(kb);
		}
	}
}