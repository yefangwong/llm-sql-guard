package io.yefangwong.guard.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * 4-Tier Metadata: Table 層
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TableMetadata {
    private String name;
    private Map<String, ColumnMetadata> columns = new HashMap<>();
    private List<RelationMetadata> relations = new ArrayList<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Map<String, ColumnMetadata> getColumns() { return columns; }
    public void setColumns(Map<String, ColumnMetadata> columns) { this.columns = columns; }
    public List<RelationMetadata> getRelations() { return relations; }
    public void setRelations(List<RelationMetadata> relations) { this.relations = relations; }

    public ColumnMetadata getColumn(String columnName) {
        return columns.get(columnName);
    }
}
