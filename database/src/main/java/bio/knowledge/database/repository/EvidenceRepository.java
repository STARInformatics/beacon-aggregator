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

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import bio.knowledge.model.neo4j.Neo4jEvidence;

/**
 * @author Richard
 *
 */
public interface EvidenceRepository extends Neo4jRepository<Neo4jEvidence,Long> {
	
	/**
	 * 
	 * @param evidenceId
	 * @return
	 */
	@Query("MERGE (evidence:Evidence:IdentifiedEntity:DatabaseEntity { accessionId : {evidenceId} }) "
			+"ON CREATE SET "
			+"evidence.uri = \"http://knowledge.bio/evidence/\"+{evidenceId}, "
			+"evidence.accessionId = {evidenceId}, "
			+"evidence.name = \"\","
			+"evidence.description = \"\","
			+"evidence.count = TOINT(\"0\"), "
			+"evidence.versionDate = timestamp(), "
			+"evidence.version = TOINT(\"1\")"
			+"RETURN evidence")
	public Neo4jEvidence createByEvidenceId(@Param("evidenceId") String evidenceId);
	
	/**
	 * 
	 * @param evidenceId
	 * @return
	 */
	@Query("MATCH (evidence:Evidence:IdentifiedEntity:DatabaseEntity { accessionId : {evidenceId} }) RETURN evidence")
	public Neo4jEvidence findByEvidenceId(@Param("evidenceId") String evidenceId);
	
	/**
	 * 
	 * @param statementId
	 * @param filter
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@Query(	" MATCH (statement:Statement {accessionId : {statementId}})-[:EVIDENCE]->(evidence:Evidence)-[:ANNOTATION]->(annotation:Annotation)-[:REFERENCE]->(reference:Reference) " +
			" WHERE " +
			"    {filter} IS NULL OR SIZE({filter}) = 0 OR " +
			"    ANY (x IN {filter} WHERE LOWER(annotation.name) CONTAINS LOWER(x)) " +
			" RETURN annotation, reference.pmid as pmid, reference.year as year, reference.month as month, reference.day as day " +
			" ORDER BY reference.year DESC, reference.month DESC, reference.day DESC " +
			" LIMIT {pageSize} "
	)
	public List<Map<String, Object>> getEvidenceByIdAndKeywords(
		@Param("statementId") String statementId,
		@Param("filter") List<String> filter,
		@Param("pageSize") Integer pageSize
	);
	
}
