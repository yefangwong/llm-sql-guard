package io.yefangwong.guard.core.model;

import java.util.Map;

public class SchemaMetadata {
    private String name;
    private Map<String, TableMetadata> tables;

    public SchemaMetadata() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Map<String, TableMetadata> getTables() { return tables; }
    public void setTables(Map<String, TableMetadata> tables) { this.tables = tables; }
}
