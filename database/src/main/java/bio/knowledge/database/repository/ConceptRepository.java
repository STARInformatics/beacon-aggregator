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
package bio.knowledge.database.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import bio.knowledge.model.Concept;
import bio.knowledge.model.neo4j.Neo4jConcept;
import bio.knowledge.model.neo4j.Neo4jConceptCategory;

/**
 * @author Richard
 *
 */
@Repository
public interface ConceptRepository extends Neo4jRepository<Neo4jConcept,Long> {
	@Query("MATCH (concept:Concept {clique: {clique}, queryFoundWith: {queryFoundWith}}) RETURN COUNT(concept) > 0")
	public boolean exists(@Param("clique") String clique, @Param("queryFoundWith") String queryFoundWith);
	
	/**
	 * 
	 * @param cliqueId
	 * @return
	 */
	@Query("MATCH (clique:ConceptClique {accessionId: {cliqueId}})<-[r:MEMBER_OF]-(concept:Concept) RETURN concept, r LIMIT 1")
	public Neo4jConcept getByClique(@Param("cliqueId") String cliqueId);
	
//	@Query("MATCH (clique:ConceptClique {accessionId: {cliqueId}})<-[:MEMBER_OF]-(concept:Concept)-[:HAS_DETAIL] ->(detail:ConceptDetail) RETURN detail")
//	public List<Neo4jConceptDetail> getDetailsByClique(@Param("cliqueId") String cliqueId);
	
	@Query("MATCH (clique:ConceptClique {accessionId: {cliqueId}})<-[:MEMBER_OF]-(concept:Concept)-[:HAS_DETAIL]->"
			+ "(detail:ConceptDetail)-[:SOURCE_BEACON]->(beacon:KnowledgeBeacon) RETURN detail, beacon")
	public List<Map<String, Object>> getDetailsByClique(@Param("cliqueId") String cliqueId);
	
	/**
	 * @return
	 */
	@Query( "MATCH ( concept:Concept ) RETURN concept" )
	public Iterable<Neo4jConcept>  getConcepts();
	
	/**
	 * @param accessionId
	 * @return Concept identified by the accessionId
	 */
	@Query( "MATCH (concept:Concept) WHERE concept.accessionId = {accessionId} RETURN concept")
	public Neo4jConcept findById( @Param("accessionId") String accessionId ) ;
	
	/**
	 * 
	 * @param id of concept 
	 * @return matching Concept
	 */
	@Query( "MATCH (concept:Concept)"
			+ " WHERE concept.semMedDbConceptId = {id}"
		 + " RETURN concept")
	public Neo4jConcept findBySemMedDbConceptId( @Param("id")String id ) ;
	
	/**
	 * 
	 * @param id of implicitome concept
	 * @return matching Concept
	 */
	@Query( "MATCH (concept:Concept) WHERE concept.implicitomeConceptId = {id}"
		 + " RETURN concept")
	public Neo4jConcept findByImplicitomeConceptId( @Param("id") String id ) ;
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	@Query( "MATCH (concept:Concept)"
			+" WHERE concept.semMedDbConceptId = {id}"
			+" RETURN concept" 
			+" UNION"
			+" MATCH ( concept:Concept ) WHERE concept.implicitomeConceptId = {id}"
			+" RETURN concept"
		 )
	public Neo4jConcept findByConceptId(@Param("id") Long id);

	/**
	 * @param filter string to match (as an embedded substring, non-case-sensitive)
	 * @return count of Concepts entries with names matching the filter
	 */
	@Query(
			"MATCH (concept:Concept)"+
			"WHERE "+
			"    LOWER(concept.name)     CONTAINS LOWER({filter}) OR"+
			"    LOWER(concept.synonyms) CONTAINS LOWER({filter})"+
		    " RETURN count(concept)"
		)
	public long countByNameLikeIgnoreCase( @Param("filter") String filter);
	
	/**
	 * @param filter string to match (as an embedded substring, non-case-sensitive)
	 * @param pageable specification of what page and page size of Concept data entries to return
	 * @return
	 */
	@Query(
			 "MATCH (concept:Concept)"+
			" WHERE "+
			"    LOWER(concept.name)     CONTAINS LOWER({filter}) OR"+
			"    LOWER(concept.synonyms) CONTAINS LOWER({filter})"+
			" RETURN concept"+
			" SKIP  {1}.pageNumber*{1}.pageSize"+
			" LIMIT {1}.pageSize"
	)
	public List<Neo4jConcept> findByNameLikeIgnoreCase( @Param("filter") String filter, Pageable pageable );
	
	@Query("MATCH (concept:Concept) WHERE concept.accessionId = {curieId} RETURN concept;")
	public Concept apiGetConceptById(@Param("curieId") String curieId);
	
	@Query(
			
			" MATCH path=(clique:ConceptClique)<-[:MEMBER_OF]-(concept:Concept) "+
		    " WITH " +
			" 	SIZE(FILTER(x IN {filter} WHERE REPLACE(LOWER(concept.name),'-',' ') CONTAINS LOWER(x))) AS name_match, " +  
			" 	SIZE(FILTER(x IN {filter} WHERE REPLACE(LOWER(concept.definition),'-',' ') CONTAINS LOWER(x))) AS def_match, " +  
			" 	SIZE(FILTER(x IN {filter} WHERE ANY(s IN concept.synonyms WHERE REPLACE(LOWER(s),'-',' ') CONTAINS LOWER(x)))) AS syn_match, " +
			"	path AS path " +
			" WHERE name_match > 0 OR def_match > 0 OR syn_match > 0 AND "+
			" ( " +
			" 	{categories} IS NULL OR SIZE({categories}) = 0 OR " +
			"	ANY(a IN {categories} WHERE ANY(b IN concept.categories WHERE TOLOWER(a) = TOLOWER(b)))" +
			" ) " +
			" RETURN path " +
			" ORDER BY name_match DESC, syn_match DESC " +
			" SKIP  ({pageNumber} - 1) * {pageSize} " +
			" LIMIT {pageSize} "
	)
	public List<Neo4jConcept> getConceptsByKeywordsAndCategories(
			@Param("filter") List<String> filter,
			@Param("categories") List<String> categories,
			@Param("pageNumber") long pageNumber,
			@Param("pageSize") long pageSize
	);

	@Query(
			" MATCH (clique:ConceptClique)<-[:MEMBER_OF]-(concept:Concept)<-[:QUERY]-(query:Query)<-[:QUERY]-(queryTracker:QueryTracker {queryString:{queryString}})"+
			" WHERE {beaconIds} IS NULL OR query.beaconId IN {beaconIds}" +
			" RETURN concept, clique " +
			" SKIP  ({pageNumber} - 1) * {pageSize} " +
			" LIMIT {pageSize} "
	)
	public List<LinkedHashMap<String, Object>> getConceptsByQueryId(
			@Param("queryString") String queryId,
			@Param("beaconIds") List<Integer> beaconIds,
			@Param("pageNumber") long pageNumber,
			@Param("pageSize") long pageSize
	);
	
	/**
	 * 
	 * @param clique
	 * @return
	 */
	@Query( "MATCH (concept:Concept) "+
			"WHERE concept.clique = {clique} RETURN concept.categories")
	public List<Neo4jConceptCategory> getConceptCategories(@Param("clique") String clique);
	
	/**
	 * @param filter
	 * @return
	 */
	@Query( "MATCH (concept:Concept)"+
			"WHERE "+
			"   concept.usage > 0 AND"+
			"   ( "+
			"     ALL (x IN {filter} WHERE LOWER(concept.name)     CONTAINS LOWER(x)) OR "+
			"     ALL (x IN {filter} WHERE LOWER(concept.synonyms) CONTAINS LOWER(x)) "+
			"   ) "+
			"RETURN count(concept)" 
		)
	public long countByInitialSearch(@Param("filter") String[] filter);


	/**
	 * 
	 */
	@Query( "MATCH (concept:Concept) "+
			"  WHERE concept.usage > 0 "+
			"RETURN count(concept)")
	public long countAll();
	
	/**
	 * Right now accountId and groupId are only being used to count the number
	 * of concept maps attached to the library that are visible to the user
	 * (i.e., are public, created by the user, or shared with a group that
	 * the user belongs to).
	 * 
	 * @param filter
	 *            string to match (as an embedded substring, non-case-sensitive)
	 * @param pageable
	 *            specification of what page and page size of data to return
	 * @return
	 */
	@Query( "MATCH path = (concept:Concept)-[:LIBRARY]->(library:Library)"+
			" WHERE"+
			"    concept.usage > 0 AND"+
			"   ( "+
			"     ALL (x IN {filter} WHERE LOWER(concept.name)     CONTAINS LOWER(x)) OR"+
			"     ALL (x IN {filter} WHERE LOWER(concept.synonyms) CONTAINS LOWER(x))"+
			"   )"+
			" OPTIONAL MATCH (library:Library)-[:ASSOCIATED_MAP]->(cm:ConceptMap)"+
			" WHERE "+ ConceptMapArchiveRepository.conceptMapIsPermitted +
			" WITH COUNT(cm) as c, path AS path, concept AS concept, library AS library" +
			" SET library.numberOfVisibleMaps = c" +
			
			" RETURN path"+			
			"    ORDER BY concept.usage DESC"+
			"    SKIP  {1}.pageNumber*{1}.pageSize"+
			"    LIMIT {1}.pageSize"
		)
	public List<Neo4jConcept> findByInitialSearch(
			@Param("filter") String[] filter,
			Pageable pageable,
			@Param("accountId") String accountId,
			@Param("groupIds") String[] groupIds
	);
	
	/**
	 * 
	 * @param categories
	 * @param filter string to match (as an embedded substring, non-case-sensitive)
	 * @param pageable specification of what page and page size of data to return
	 * @return
	 */
	@Query( " MATCH path = (concept:Concept)-[:LIBRARY]->(library:Library)"+
			" WHERE"+
			"   concept.usage > 0 AND"+
			"   ( size({categories}) = 0"+
			"     OR ANY ( x IN {categories} WHERE LOWER(concept.semanticGroup) CONTAINS LOWER(x) )"+
			"   ) AND"+
			"   ( "+
			"     ALL (x IN {filter} WHERE LOWER(concept.name)     CONTAINS LOWER(x)) OR"+
			"     ALL (x IN {filter} WHERE LOWER(concept.synonyms) CONTAINS LOWER(x))"+
			"   )"+
			" OPTIONAL MATCH (library:Library)-[:ASSOCIATED_MAP]->(cm:ConceptMap)"+
			" WHERE "+ ConceptMapArchiveRepository.conceptMapIsPermitted +
			" WITH COUNT(cm) as c, path AS path, concept AS concept, library AS library" +
			" SET library.numberOfVisibleMaps = c" +
			" RETURN path"+
			" ORDER BY concept.usage DESC"+
			" SKIP  {2}.pageNumber*{2}.pageSize"+
			" LIMIT {2}.pageSize"
		)
	public List<Neo4jConcept> findByNameLikeIgnoreCase(
			@Param("categories") ArrayList<String> categories, 
			@Param("filter") String[] filter, 
			Pageable pageable,
			@Param("accountId") String accountId,
			@Param("groupIds") String[] groupIds
	);

	/**
	 * @param pageable
	 * @param userId
	 * @param groupIds
	 * @return
	 */
	@Query( "MATCH path = (concept:Concept)-[:LIBRARY]->(library:Library)"+
			" WHERE concept.usage > 0"+
			" OPTIONAL MATCH (library:Library)-[:ASSOCIATED_MAP]->(cm:ConceptMap)"+
			" WHERE "+ ConceptMapArchiveRepository.conceptMapIsPermitted +
			" WITH COUNT(cm) as c, path AS path, concept AS concept, library AS library" +
			" SET library.numberOfVisibleMaps = c" +
			" RETURN path"+			
			"    ORDER BY concept.usage DESC"+
			"    SKIP  {0}.pageNumber*{0}.pageSize"+
			"    LIMIT {0}.pageSize"
		)
	public List<Neo4jConcept> findAllByPage(
			Pageable pageable,
			@Param("accountId") String accountId,
			@Param("groupIds") String[] groupIds
	);
	
	@Query(
			" MATCH (q:QueryTracker)-[:QUERY]->(concept:Concept)-[:BEACON_CITATION]->(c:BeaconCitation)-[:SOURCE_BEACON]->(b:KnowledgeBeacon) " +
			" WHERE b.beaconId = {beaconId} AND q.queryString = {queryString} " +
			" RETURN COUNT(concept);"
	)
	public Integer countQueryResults(@Param("queryString") String queryString, @Param("beaconId") Integer beaconId);

	@Query("match (q:QueryTracker {queryString:{queryString}})-[:QUERY]->(c:Concept) return c skip {pageSize} * ({pageNumber} - 1) limit {pageSize}")
	List<Neo4jConcept> getConcepts(
			@Param("queryString") String queryString,
			@Param("pageNumber") long pageNumber,
			@Param("pageSize") long pageSize
	);


}
