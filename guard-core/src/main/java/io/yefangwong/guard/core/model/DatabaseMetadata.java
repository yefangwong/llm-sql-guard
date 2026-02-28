package io.yefangwong.guard.core.model;

import java.util.Map;

/**
 * 企業 ERD 的 Metadata 模型 (4-Tier)
 */
public class DatabaseMetadata {
    private String name;
    private Map<String, SchemaMetadata> schemas;

    public DatabaseMetadata() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Map<String, SchemaMetadata> getSchemas() { return schemas; }
    public void setSchemas(Map<String, SchemaMetadata> schemas) { this.schemas = schemas; }
}
