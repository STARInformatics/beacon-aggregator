/**
 * 
 */
package bio.knowledge.server.blackboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bio.knowledge.aggregator.ConceptCategoryService;
import bio.knowledge.aggregator.QuerySession;
import bio.knowledge.aggregator.StatementsQueryInterface;
import bio.knowledge.aggregator.ontology.Ontology;
import bio.knowledge.client.model.BeaconStatement;
import bio.knowledge.client.model.BeaconStatementObject;
import bio.knowledge.client.model.BeaconStatementPredicate;
import bio.knowledge.client.model.BeaconStatementSubject;
import bio.knowledge.database.repository.ConceptRepository;
import bio.knowledge.database.repository.EvidenceRepository;
import bio.knowledge.database.repository.TkgNodeRepository;
import bio.knowledge.database.repository.PredicateRepository;
import bio.knowledge.database.repository.StatementRepository;
import bio.knowledge.database.repository.aggregator.BeaconCitationRepository;
import bio.knowledge.database.repository.aggregator.ConceptCliqueRepository;
import bio.knowledge.database.repository.beacon.BeaconRepository;
import bio.knowledge.model.SimpleConcept;
import bio.knowledge.model.aggregator.neo4j.Neo4jConceptClique;
import bio.knowledge.model.aggregator.neo4j.Neo4jBeaconCitation;
import bio.knowledge.model.aggregator.neo4j.Neo4jKnowledgeBeacon;
import bio.knowledge.model.neo4j.Neo4jConceptCategory;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.model.neo4j.Neo4jEvidence;
import bio.knowledge.model.neo4j.Neo4jPredicate;
import bio.knowledge.model.neo4j.Neo4jStatement;
import bio.knowledge.model.neo4j.TkgNode;
import bio.knowledge.ontology.BiolinkClass;
import bio.knowledge.ontology.BiolinkSlot;
import bio.knowledge.server.controller.ExactMatchesHandler;
import bio.knowledge.server.model.ServerStatement;
import bio.knowledge.server.model.ServerStatementObject;
import bio.knowledge.server.model.ServerStatementPredicate;
import bio.knowledge.server.model.ServerStatementSubject;
import bio.knowledge.server.tkg.Property;
import bio.knowledge.server.tkg.TKG;

/**
 * @author richard
 *
 */
@Component
public class StatementsDatabaseInterface 
		extends CoreDatabaseInterface<
					StatementsQueryInterface,
					BeaconStatement,
					ServerStatement
				> 
{
	@Autowired private BeaconRepository beaconRepository;

	@Autowired private ConceptRepository  conceptRepository;
	
	@Autowired private ExactMatchesHandler     exactMatchesHandler;
	@Autowired private ConceptCliqueRepository conceptCliqueRepository;
	
	@Autowired private StatementRepository statementRepository;
	@Autowired private PredicateRepository predicateRepository;
	@Autowired private BeaconCitationRepository beaconCitationRepository;

	@Autowired private TKG tkg;
	@Autowired private TkgNodeRepository nodeRepository;
	@Autowired private Ontology ontology;

	/*
	 * (non-Javadoc)
	 * @see bio.knowledge.aggregator.DatabaseInterface#loadData(java.lang.Object, java.util.List, java.lang.Integer)
	 */
	@Override
	public void loadData(QuerySession<StatementsQueryInterface> query, List<BeaconStatement> results, Integer beaconId) {
		Set<String> conceptIds = new HashSet<>();
		for (BeaconStatement statement : results) {
			conceptIds.add(statement.getObject().getId());
			conceptIds.add(statement.getSubject().getId());
		}
		
		exactMatchesHandler.createAndGetConceptCliques(new ArrayList<>(conceptIds));
		
		Neo4jKnowledgeBeacon beacon = beaconRepository.getBeacon(beaconId);

		for (BeaconStatement beaconStatement : results) {
			
			try {
				
				BeaconStatementSubject beaconSubject    = beaconStatement.getSubject();
				BeaconStatementPredicate beaconRelation = beaconStatement.getPredicate();
				BeaconStatementObject beaconObject      = beaconStatement.getObject();
				
				Map<String,Object> sMap = statementRepository.findById(beaconStatement.getId());
				
				Neo4jStatement statement;
				
				if (sMap != null && !sMap.isEmpty()) {
					statement = (Neo4jStatement)sMap.get("statement");
					statement.setSubject((Neo4jConcept)sMap.get("subject"));
					statement.setObject((Neo4jConcept)sMap.get("object"));
				} else {
					// Create a new empty statement
					statement = new Neo4jStatement();
					statement.setId(beaconStatement.getId());
				}
				
				Neo4jConcept neo4jSubject = statement.getSubject();
				if (neo4jSubject == null) {
					neo4jSubject = getConcept((SimpleConcept)beaconSubject, beacon);
				}
				
				Neo4jPredicate neo4jPredicate = statement.getRelation();
				if (neo4jPredicate == null) {
					
					neo4jPredicate = predicateRepository.findPredicateById(beaconRelation.getRelation());
					
					if (neo4jPredicate == null) {
						neo4jPredicate = new Neo4jPredicate();
						neo4jPredicate.setEdgeLabel(beaconRelation.getEdgeLabel());
						neo4jPredicate.setRelation(beaconRelation.getRelation());
						neo4jPredicate = predicateRepository.save(neo4jPredicate);
					}
				}
				
				Neo4jConcept neo4jObject = (Neo4jConcept)statement.getObject();
				if (neo4jObject == null) {
					neo4jObject = getConcept((SimpleConcept)beaconObject, beacon);
				}
				
				statement.setId(beaconStatement.getId());
				statement.setSubject(neo4jSubject);
				statement.setRelation(neo4jPredicate);
				statement.setObject(neo4jObject);
				
				Neo4jBeaconCitation citation = 
						beaconCitationRepository.findByBeaconAndObjectId(
													beacon.getBeaconId(),
													beaconStatement.getId()
												);
				if(citation==null) {
					citation = new Neo4jBeaconCitation(beacon,beaconStatement.getId());
					citation = beaconCitationRepository.save(citation);
				}
				statement.setBeaconCitation(citation);
				
				statement.addQuery(query.getQueryTracker());
				
				statementRepository.save(statement);

				buildTKGData(statement);
				
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void buildTKGData(Neo4jStatement statement) {
		String subjectCliqueId = statement.getSubject().getClique().getId();
		String objectCliqueId = statement.getObject().getClique().getId();
		String edgeLabel = statement.getRelation().getEdgeLabel();
		Integer beaconId = statement.getBeaconCitation().getBeacon().getBeaconId();

		String slotName = edgeLabel.toLowerCase().replace("_", " ");
		Optional<BiolinkSlot> optionalSlot = ontology.getSlotByName(slotName);
		if (!optionalSlot.isPresent()) {
			edgeLabel = "related_to";
		}

		tkg.mergeEdge(
				subjectCliqueId,
				objectCliqueId,
				edgeLabel,
				new Property("is_defined_by", "KBA"),
				new Property("provided_by", "beacon " + String.valueOf(beaconId)),
				new Property("relation", statement.getRelation().getEdgeLabel())
		);

		TkgNode subject = nodeRepository.getNode(subjectCliqueId);
		TkgNode object = nodeRepository.getNode(objectCliqueId);

		setNodeCategory(subject, statement.getSubject().getType());
		setNodeCategory(object, statement.getObject().getType());

		subject.setName(statement.getSubject().getName());
		object.setName(statement.getObject().getName());

		nodeRepository.saveAll(Arrays.asList(new TkgNode[] {subject, object}));
	}

	private void setNodeCategory(TkgNode node, String category) {
		Optional<BiolinkClass> optional = ontology.getClassByName(category);

		if (optional.isPresent()) {
			node.setCategory(category);
		} else {
			node.setCategory("named thing");
			node.setNonBiolinkCategory(category);
		}
	}
	
	private Neo4jConcept getConcept(SimpleConcept concept, Neo4jKnowledgeBeacon beacon) {
		
		Neo4jConceptClique clique = exactMatchesHandler.getConceptCliqueFromDb(concept.getId());
		
		Set<Neo4jConceptCategory> categories = new HashSet<>();
		for (String category : concept.getCategories()) {
			//categories.add(conceptTypeService.lookUp(beacon.getBeaconId(), category));
			Neo4jConceptCategory dbCategory = new Neo4jConceptCategory();
			dbCategory.setName(category);
			categories.add(dbCategory);
		}
		
		if (clique == null) {
			clique = exactMatchesHandler.createConceptClique(
					concept.getId(), 
					beacon.getBeaconId(),
					categories
			);
			clique = conceptCliqueRepository.save(clique);
		}
		
		String cliqueId = clique.getId();
		
		Neo4jConcept neo4jConcept = conceptRepository.getByClique(cliqueId);
		
		if (neo4jConcept == null) {
			/*
			 * TODO: This may not be adequate: we may wish to trigger a full beacon harvesting of this concept, if not here.
			 */
			neo4jConcept = new Neo4jConcept(clique, categories, concept.getName());
		}
		
		/*
		 * Add this beacon to the set of beacons 
		 * which have cited this concept
		 */
		Neo4jBeaconCitation citation = 
				beaconCitationRepository.findByBeaconAndObjectId(
											beacon.getBeaconId(),
											concept.getId()
										);
		if(citation==null) {
			citation = new Neo4jBeaconCitation(beacon,concept.getId());
			citation = beaconCitationRepository.save(citation);
		}
		neo4jConcept.addBeaconCitation(citation);
		
		// (re)save the (re)constituted Neo4jConcept node
		neo4jConcept = conceptRepository.save(neo4jConcept);
		
		return neo4jConcept;
	}

	/*
	 * (non-Javadoc)
	 * @see bio.knowledge.aggregator.DatabaseInterface#getDataPage(bio.knowledge.aggregator.QuerySession, java.util.List)
	 */
	@Override
	public List<ServerStatement> getDataPage(
				QuerySession<StatementsQueryInterface> query, 
				List<Integer> beacons
	) {
		StatementsQueryInterface statementQuery = query.getQuery();
		
		/*
		 * TODO: review this legacy code to clarify whether or not it is truly needed... or if the 'queryString' mechanism suffices
		 * TODO: review 'queryString' to see if we need to harmonize with the QueryTracker mechanism in /concepts
		 * 
		String[] sources = split(statementQuery.getSource());
		String[] relationIds = statementQuery.getRelations().toArray(new String[0]);
		String[] targets = split(statementQuery.getTarget());
		String[] semanticGroups = statementQuery.getConceptTypes().toArray(new String[0]);
		String[] filter = split(statementQuery.getKeywords());
		*/
		
		String queryString = query.makeQueryString();
		
		beacons = beacons.isEmpty() ? 
				  beaconRepository.
				  	findAllBeacons().
				  		stream().
				  			map(b -> b.getBeaconId()).
				  				collect(Collectors.toList()) : 
				  beacons;
				  				
		int pageNumber = statementQuery.getPageNumber();
		int pageSize   = statementQuery.getPageSize();
		
		List<Neo4jStatement> results = statementRepository.getQueryResults(
				queryString,
				beacons,
				pageNumber,
				pageSize
		);

		List<ServerStatement> serverStatements = new ArrayList<ServerStatement>();
		for (Neo4jStatement statement : results) {

//			Neo4jGeneralStatement statement = (Neo4jGeneralStatement) result.get("statement");

			Neo4jConcept neo4jSubject = statement.getSubject();
			ServerStatementSubject serverSubject = new ServerStatementSubject();
			
			serverSubject.setClique(neo4jSubject.getClique().getId());
			
			Neo4jBeaconCitation subjCitation = statement.getBeaconCitation();
			serverSubject.setId(subjCitation.getObjectId());
			
			serverSubject.setName(neo4jSubject.getName());
			
			/*
			Optional<ConceptTypeEntry> subjectTypeOpt = neo4jSubject.getType();
			ConceptTypeEntry subjectType;
			if(subjectTypeOpt.isPresent())
				subjectType = subjectTypeOpt.get();
			else
			 */
			serverSubject.setCategories(new ArrayList<>(neo4jSubject.getTypes()));
			
			Neo4jPredicate neo4jPredicate = statement.getRelation();
			ServerStatementPredicate serverPredicate = new ServerStatementPredicate();
			serverPredicate.setEdgeLabel(neo4jPredicate.getEdgeLabel());
			serverPredicate.setRelation(neo4jPredicate.getRelation());

			Neo4jConcept neo4jObject = statement.getObject();
			ServerStatementObject serverObject = new ServerStatementObject();
			
			serverObject.setClique(neo4jObject.getClique().getId());
			
			Neo4jBeaconCitation objCitation = statement.getBeaconCitation();
			serverObject.setId(objCitation.getObjectId());
			
			serverObject.setName(neo4jObject.getName());
			
			/*
			Optional<ConceptTypeEntry> objectTypeOpt = neo4jObject.getType();
			ConceptTypeEntry objectType;
			if(objectTypeOpt.isPresent())
				objectType = objectTypeOpt.get();
			else
			*/
			serverObject.setCategories(new ArrayList<>(neo4jObject.getTypes()));
			
			ServerStatement serverStatement = new ServerStatement();
			serverStatement.setId(statement.getId());

			serverStatement.setObject(serverObject);
			serverStatement.setSubject(serverSubject);
			serverStatement.setPredicate(serverPredicate);
			
			Neo4jKnowledgeBeacon neo4jBeacon = statement.getBeaconCitation().getBeacon();
			serverStatement.setBeacon(neo4jBeacon.getBeaconId());
			
			serverStatements.add(serverStatement);
		}
		return serverStatements;
	}

	@Override
	public Integer getDataCount(QuerySession<StatementsQueryInterface> query, int beaconId) {
		return statementRepository.countQueryResults(query.makeQueryString(), beaconId);
	}
}

