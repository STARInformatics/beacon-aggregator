package bio.knowledge.server.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import bio.knowledge.aggregator.KnowledgeBeaconService;
import bio.knowledge.database.repository.ConceptCliqueRepository;
import bio.knowledge.model.ConceptClique;

@Service
public class ExactMatchesHandler {
	@Autowired private ConceptCliqueRepository conceptCliqueRepository;
	@Autowired private KnowledgeBeaconService kbs;
	
	/**
	 * Builds up concept cliques for each conceptId in {@code c}, and then merges them into a single
	 * set of conceptIds and returns this set.
	 * @param c
	 * @param sources 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ResponseEntity<List<String>> getExactMatchesSafe(List<String> c, String sessionId) {
		List<Map<String, Object>> l = conceptCliqueRepository.getConceptCliques(c);
		
		List<ConceptClique> cliques = new ArrayList<ConceptClique>();
		List<String> unmatchedConceptIds = new ArrayList<String>(c);
		
		for (Map<String, Object> m : l) {
			List<String> matchedConceptIds = Arrays.asList((String[]) m.get("matchedConceptIds"));
			ConceptClique clique = (ConceptClique) m.get("clique");
			
			unmatchedConceptIds.removeAll(matchedConceptIds);
			cliques.add(clique);
		}
		
		if (unmatchedConceptIds.isEmpty()) {
			Set<String> union = ConceptClique.unionOfConceptIds(cliques);
			return ResponseEntity.ok(new ArrayList<String>(union));
			
		} else {
			List<ConceptClique> foundCliques = unmatchedConceptIds.stream().map(
					conceptId -> new ConceptClique(findAggregatedExactMatches(conceptId, sessionId))
			).collect(Collectors.toList());
			
			foundCliques.forEach(clique -> conceptCliqueRepository.save(clique));
			
			cliques.addAll(foundCliques);
			
			Set<String> union = ConceptClique.unionOfConceptIds(cliques);
			
			int sumOfCliqueSizes = cliques.stream().mapToInt(clique -> clique.size()).sum();
			
			//TODO: This can be put inside a thread to speed things up.
			//		All we're doing here is cleaning up the database
			//		without changing what this method will return.
			if (sumOfCliqueSizes != union.size()) {
				for (ConceptClique clique1 : cliques) {
					for (ConceptClique clique2 : cliques) {
						if (clique1 != clique2) {
							if (ConceptClique.notDisjoint(clique1, clique2)) {
								clique1.addAll(clique2);
								conceptCliqueRepository.save(clique1);
								conceptCliqueRepository.delete(clique2);
							}
						}
					}
				}
			}
			
			return ResponseEntity.ok(new ArrayList<String>(union));
		}
	}
	
	/**
	 * Performs a similar function as {@code getExactMatchesSafe(List<String>)}, but
	 * <b>assumes that {@code c} is a list of exact matches</b>. If this assumption fails to
	 * hold true, the database could be populated with faulty concept cliques. Because of this,
	 * this method is much faster than {@code getExactMatchesSafe}. Note that if {@code c.size() == 1},
	 * then the assumption is trivially satisfied and this method can be used safely.
	 * 
	 * @param c
	 * 		A list of <b>exactly matching</b> concept ID's
	 * @return
	 */
	public ResponseEntity<List<String>> getExactMatchesUnsafe(List<String> c, String sessionId) {
		ConceptClique conceptClique = conceptCliqueRepository.getConceptClique(c);
		
		if (conceptClique != null) {
			return ResponseEntity.ok(conceptClique.getConceptIds());
		} else {
			
			ConceptClique clique = new ConceptClique(findAggregatedExactMatches(c, sessionId));
			
			conceptCliqueRepository.save(clique);
			
			return ResponseEntity.ok(clique.getConceptIds());
		}
	}
	
	private Set<String> findAggregatedExactMatches(List<String> c, String sessionId) {
		Set<String> matches = new HashSet<String>(c);
		int size;
		
		do {
			size = matches.size();
			CompletableFuture<List<String>> future = kbs.getExactMatchesToConceptList(new ArrayList<String>(matches), sessionId);
			
			try {
				List<String> aggregatedMatches = future.get(ControllerImpl.TIMEOUT, ControllerImpl.TIMEUNIT);
				matches.addAll(aggregatedMatches);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				e.printStackTrace();
			}
			
		} while (size < matches.size());
		
		return matches;
	}
	
	private Set<String> findAggregatedExactMatches(String conceptId, String sessionId) {
		return findAggregatedExactMatches(Arrays.asList(new String[]{conceptId}), sessionId);
	}
}
