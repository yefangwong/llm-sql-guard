package io.yefangwong.guard.core.model;

import java.util.List;
import java.util.Map;

public class TableMetadata {
    private String name;
    private Map<String, ColumnMetadata> columns;
    private List<RelationMetadata> relations;

    public TableMetadata() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Map<String, ColumnMetadata> getColumns() { return columns; }
    public void setColumns(Map<String, ColumnMetadata> columns) { this.columns = columns; }
    public List<RelationMetadata> getRelations() { return relations; }
    public void setRelations(List<RelationMetadata> relations) { this.relations = relations; }
}
