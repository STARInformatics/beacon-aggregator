package bio.knowledge.model.aggregator.neo4j;

import bio.knowledge.model.aggregator.QueryTracker;
import bio.knowledge.model.core.neo4j.Neo4jAbstractDatabaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.QueryResult;

@NodeEntity(label="Query")
@QueryResult
public class Neo4jQuery extends Neo4jAbstractDatabaseEntity {
    private Integer status;
    private Integer beaconId;
    private Integer discovered;
    private Integer processed;
    private Integer count;

    public Integer getBeaconId() {
        return beaconId;
    }

    public Integer getDiscovered() {
        return discovered;
    }

    public void setDiscovered(Integer discovered) {
        this.discovered = discovered;
    }

    public Integer getProcessed() {
        return processed;
    }

    public void setProcessed(Integer processed) {
        this.processed = processed;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setBeaconId(Integer beaconId) {
        this.beaconId = beaconId;
    }
}
