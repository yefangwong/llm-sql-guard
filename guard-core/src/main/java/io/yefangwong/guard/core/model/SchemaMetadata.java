package io.yefangwong.guard.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import java.util.HashMap;

/**
 * 4-Tier Metadata: Schema 層
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchemaMetadata {
    private String name;
    private Map<String, TableMetadata> tables = new HashMap<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Map<String, TableMetadata> getTables() { return tables; }
    public void setTables(Map<String, TableMetadata> tables) { this.tables = tables; }

    public TableMetadata getTable(String tableName) {
        return tables.get(tableName);
    }
}
