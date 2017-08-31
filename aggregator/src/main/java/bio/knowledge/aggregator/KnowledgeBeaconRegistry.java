package bio.knowledge.aggregator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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
	private Map<String, KnowledgeBeacon> beaconById = new HashMap<>();
	
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
	
	public List<KnowledgeBeacon> filterKnowledgeBeaconsById(List<String> ids) {
		
		List<KnowledgeBeacon> beacons = new ArrayList<>();
		for(String id : ids) {
			KnowledgeBeacon beacon = beaconById.get(id);
			if (beacon != null) {
				beacons.add(beacon);
			}
		}
		
		return beacons.isEmpty()? getKnowledgeBeacons() : beacons;
	}
	
	@PostConstruct
	public void init() {
		initKnowledgeBeacons();
//		neo4jInit();

	}
	
	/**
	 * Initiates the registry by grabbing beacons from the official yaml file:
	 * https://raw.githubusercontent.com/NCATS-Tangerine/translator-knowledge-beacon/develop/api/knowledge-beacon-list.yaml
	 * Beacons that are not indicated as "deployed" are not put into the internal list of knowledge beacons.
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void initKnowledgeBeacons() {
		try {
			URL site = new URL(masterKnowledgeBeaconList);
			InputStream inputStream = site.openStream();
			Yaml yaml = new Yaml();
			Map<String, Object> yamlObject = (Map<String, Object>) yaml.load(inputStream);
			ArrayList<Map<String, Object>> beacons = (ArrayList<Map<String, Object>>) yamlObject.get("beacons");
			
			for (int i = 0; i < beacons.size(); i++) {
				
				Map<String, Object> beacon = beacons.get(i);
				String id = Integer.toString(i + 1);
				
				String url = (String) beacon.get("url");
				String name = (String) beacon.get("name");
				String description = (String) beacon.get("description");
				String status = (String) beacon.get("status");
				String contact = (String) beacon.get("contact");
				String wraps = (String) beacon.get("wraps");
				String repo = (String) beacon.get("repo");
				
				boolean isEnabled = "deployed".equals(status);
				
				if (url != null && isEnabled) {
					try {
						
						KnowledgeBeacon kb = new KnowledgeBeacon(id, url, isEnabled);
						kb.setName(name);
						kb.setDescription(description);
						kb.setContact(contact);
						kb.setWraps(wraps);
						kb.setRepo(repo);
						
						this.knowledgeBeacons.add(kb);
						this.beaconById.put(id, kb);
						
					} catch (IllegalArgumentException e) {
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}