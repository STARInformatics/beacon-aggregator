/**
 * 
 */
package bio.knowledge.server.blackboard;

import java.util.List;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//import bio.knowledge.aggregator.BeaconStatementWrapper;
import bio.knowledge.aggregator.BeaconItemWrapper;
//import bio.knowledge.aggregator.ConceptTypeService;
import bio.knowledge.aggregator.StatementsQueryInterface;
import bio.knowledge.aggregator.DatabaseInterface;
import bio.knowledge.aggregator.KnowledgeBeacon;
import bio.knowledge.aggregator.Query;
import bio.knowledge.client.model.BeaconStatement;
//import bio.knowledge.database.repository.StatementRepository;
//import bio.knowledge.model.StatementTypeEntry;
//import bio.knowledge.model.neo4j.Neo4jStatement;
import bio.knowledge.server.model.ServerStatement;

/**
 * @author richard
 *
 */
@Component
public class StatementsDatabaseInterface 
		implements DatabaseInterface<
						BeaconStatement,
						ServerStatement,
						StatementsQueryInterface
					> {
	
	//@Autowired private ConceptTypeService StatementTypeService;
	//@Autowired private StatementRepository  StatementRepository;

	@Override
	public boolean cacheData(
			KnowledgeBeacon kb, 
			BeaconItemWrapper<BeaconStatement> beaconItemWrapper, 
			String queryString
	) {
		/*
		BeaconStatementWrapper StatementWrapper = (BeaconStatementWrapper) beaconItemWrapper;
		BeaconStatement Statement = StatementWrapper.getItem();

		StatementTypeEntry StatementType = StatementTypeService.lookUp(Statement.getType());
		Neo4jStatement neo4jStatement = new Neo4jStatement();
		
		neo4jStatement.setClique(StatementWrapper.getClique());
		neo4jStatement.setName(Statement.getName());
		if(StatementType!=null) {
			List<StatementTypeEntry> types = new ArrayList<StatementTypeEntry>();
			types.add(StatementType);
			neo4jStatement.setTypes(types);
		}

		neo4jStatement.setQueryFoundWith(queryString);
		neo4jStatement.setSynonyms(Statement.getSynonyms());
		neo4jStatement.setDefinition(Statement.getDefinition());

		if (!StatementRepository.exists(neo4jStatement.getClique(), queryString)) {
			StatementRepository.save(neo4jStatement);
			return true;
		} else {
			return false;
		}
		*/
		return false;
	}

	@Override
	public List<ServerStatement> getDataPage(
				Query<StatementsQueryInterface> query, 
				List<Integer> beacons
	) {

		//return getStatementsFromDatabase(
		//		keywords, StatementTypes, 
		//		pageNumber, pageSize,
		//		beacons, queryString
		//);
		return null;
	}
}