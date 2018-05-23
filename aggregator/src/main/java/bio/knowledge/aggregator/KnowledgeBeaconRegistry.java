/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-17 STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
 * Copyright (c) 2017    NIH National Center for Advancing Translational Sciences (NCATS)
 * Copyright (c) 2015-16 Scripps Institute (USA) - Dr. Benjamin Good
 *                       
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *-------------------------------------------------------------------------------
 */
package bio.knowledge.aggregator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.yaml.snakeyaml.Yaml;

/*
 * To allow for initialization of the beacon-yaml-list,
 * this class is explicitly instantiated as a 
 * Spring @Bean in the beacon-aggregator server module
 */
public class KnowledgeBeaconRegistry {

	private static Logger _logger = LoggerFactory.getLogger(KnowledgeBeaconRegistry.class);
	
	@Value( "${beacon-yaml-list}" )
	private String masterKnowledgeBeaconList;
	
	public KnowledgeBeaconRegistry() {}
	
	private List<KnowledgeBeaconImpl> knowledgeBeacons = new ArrayList<KnowledgeBeaconImpl>();
	private Map<Integer, KnowledgeBeaconImpl> beaconById = new HashMap<Integer, KnowledgeBeaconImpl>();
	
	/**
	 * 
	 * @param beaconId
	 * @return
	 */
	public KnowledgeBeaconImpl getBeaconById(Integer beaconId) {
		return beaconById.get(beaconId);
	}
	
	public KnowledgeBeacon getKnowledgeBeaconByUrl(String url) {
		for (KnowledgeBeacon kb : getKnowledgeBeacons()) {
			if (kb.getUrl().equals(url)) {
				return kb;
			}
		}
		
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<KnowledgeBeacon> getKnowledgeBeacons() {		
		return (List<KnowledgeBeacon>)(List)knowledgeBeacons;
	}

	public int countAllBeacons() {
		return this.knowledgeBeacons.size();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<KnowledgeBeacon> filterKnowledgeBeaconsById(List<Integer> ids) {
		
		List<KnowledgeBeaconImpl> beacons = new ArrayList<KnowledgeBeaconImpl>();
		for(Integer id : ids) {
			KnowledgeBeaconImpl beacon = beaconById.get(id);
			if (beacon != null) {
				beacons.add(beacon);
			}
		}
		return beacons.isEmpty()? getKnowledgeBeacons() : (List<KnowledgeBeacon>)(List)beacons;
	}
	
	public List<Integer> getBeaconIds() {
		return new ArrayList<Integer>(beaconById.keySet());
	}
	
	
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
	@PostConstruct
	private void initKnowledgeBeacons() {
		try {

			_logger.info(masterKnowledgeBeaconList);
			
			URL site = new URL(masterKnowledgeBeaconList);
			InputStream inputStream = site.openStream();
			Yaml yaml = new Yaml();
			@SuppressWarnings("unchecked")
			Map<String, Object> yamlObject = (Map<String, Object>) yaml.load(inputStream);
			@SuppressWarnings("unchecked")
			ArrayList<Map<String, Object>> beacons = (ArrayList<Map<String, Object>>) yamlObject.get("beacons");
			
			for (int i = 0; i < beacons.size(); i++) {
				
				Map<String, Object> beacon = beacons.get(i);
				Integer id = i + 1;
				
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
						
						KnowledgeBeaconImpl kb = new KnowledgeBeaconImpl(id, url, isEnabled);
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
			throw new RuntimeException(e);
		}
	}

}