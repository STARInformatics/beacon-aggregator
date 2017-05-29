package bio.knowledge.aggregator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import bio.knowledge.config.ConfigLoader;


//@Service
//@PropertySource("classpath:application.properties")
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class KnowledgeBeaconRegistry {
	
	@Value("${aggregator.beaconYamlFilePath}")
	public String beaconYamlFilePath;
	
	private List<KnowledgeBeacon> knowledgeBeacons = new ArrayList<KnowledgeBeacon>();
	
	public List<KnowledgeBeacon> getKnowledgeBeacons() {		
		return this.knowledgeBeacons;
	}
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		try {
			
			//File file = new File(beaconYamlFilePath);
			//InputStream inputStream = new FileInputStream(file);
			
			InputStream inputStream = 
					ConfigLoader.getResourceStream(beaconYamlFilePath);
			
			Yaml yaml = new Yaml();
			
			Map<String, Object> yamlObject = (Map<String, Object>) yaml.load(inputStream);
			ArrayList<Map<String, Object>> beacons = (ArrayList<Map<String, Object>>) yamlObject.get("beacons");
			
			for (Map<String, Object> beacon : beacons) {
				String url = (String) beacon.get("url");
				String name = (String) beacon.get("name");
				String description = (String) beacon.get("description");
				String wraps = (String) beacon.get("wraps");
				String repo = (String) beacon.get("repo");
				
				if (url != null) {
					this.knowledgeBeacons.add(
							new KnowledgeBeacon(url, name, description, wraps, repo)
					);
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	private boolean check(String url) {		
		return true;
	}
	*/
}