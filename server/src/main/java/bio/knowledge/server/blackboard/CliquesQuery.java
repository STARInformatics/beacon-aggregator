/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-18 STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
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
package bio.knowledge.server.blackboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bio.knowledge.aggregator.CliquesQueryInterface;
import bio.knowledge.client.model.ExactMatchResponse;
import bio.knowledge.model.aggregator.neo4j.Neo4jConceptClique;
import bio.knowledge.ontology.BiolinkTerm;
import bio.knowledge.server.controller.ExactMatchesHandler;
import bio.knowledge.server.model.ServerClique;
import bio.knowledge.server.model.ServerCliquesQuery;
import bio.knowledge.server.model.ServerCliquesQueryBeaconStatus;
import bio.knowledge.server.model.ServerCliquesQueryResult;
import bio.knowledge.server.model.ServerCliquesQueryStatus;

public class CliquesQuery extends 
	AbstractQuery<CliquesQueryInterface, Neo4jConceptClique, ServerClique>
	implements CliquesQueryInterface
{

	//TODO: CANNOT do this this for some reason??
	//@Autowired private ExactMatchesHandler exactMatchesHandler;
	//@Autowired private ConceptCliqueRepository cliqueRepository;
	
	private final ServerCliquesQuery query;
	private final ServerCliquesQueryStatus status;
	private final ServerCliquesQueryResult results;
	
	public CliquesQuery(BeaconHarvestService beaconHarvestService,
			CliquesDatabaseInterface databaseInterface) {
		super(beaconHarvestService, databaseInterface);

		query = new ServerCliquesQuery();
		query.setQueryId(getQueryId());
		
		status = new ServerCliquesQueryStatus(); 
		status.setQueryId(getQueryId());
		
		results = new ServerCliquesQueryResult();
		results.setQueryId(getQueryId());
		
		
	}

	@Override
	public String makeQueryString() {
		return makeQueryString("cliques",getKeywords());
	}

	@Override
	public CliquesQueryInterface getQuery() {
		return this;
	}
	
	/**
	 * Initiates clique query and returns the query
	 * @param identifiers
	 * @param beacons
	 * @return
	 */
	public ServerCliquesQuery getQuery(List<String> identifiers, List<Integer> beacons) {
		query.setIdentifiers(identifiers);
		setQueryBeacons(beacons);
		
		getHarvestService().initiateHarvestOnAllQueriedBeacons(this);
		
		return query;
	}

	@Override
	public Supplier<Integer> getQueryResultSupplier(Integer beacon) {
		return () -> {
			try {
				return queryBeaconForExactMatches();
			} catch (Exception e) {
				throw e;
			}
				
		};
	}
	
	private Integer queryBeaconForExactMatches() {
		List<String> identifiers   = getKeywords();
		
		CliquesDatabaseInterface dbi = 
				(CliquesDatabaseInterface)getDatabaseInterface();
		
		List<Neo4jConceptClique> results = dbi.harvestAndSaveData(identifiers);
		
		dbi.loadData(this,results,null);
		
		return results.size();
	}

	@Override
	protected BeaconStatusInterface createBeaconStatus(Integer beacon) {
		return new ServerCliquesQueryBeaconStatus();
	}

	@Override
	public List<String> getKeywords() {
		return query.getIdentifiers(); 
	}

	public ServerCliquesQueryStatus getQueryStatus() {
		List<Integer> beacons = getQueryBeacons();
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		List<BeaconStatusInterface> bsList = 
				(List<BeaconStatusInterface>)(List)status.getStatus();
		bsList.clear();
		
		for( Integer beacon : beacons ) {
			Optional<BeaconStatusInterface> beaconStatus = getBeaconStatus(beacon);
			if(beaconStatus.isPresent())
				bsList.add(beaconStatus.get());
		}
		
		return status;
	}
	
	public ServerCliquesQueryResult getQueryResults() {
		List<Integer> beacons = getQueryBeacons();
		
		List<ServerClique> cliqueIds = getDatabaseInterface().getDataPage(this, beacons);
		
		results.setResults(cliqueIds);
		return results;
	}

	
}
