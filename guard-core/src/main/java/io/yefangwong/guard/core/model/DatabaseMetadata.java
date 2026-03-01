package io.yefangwong.guard.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import java.util.HashMap;

/**
 * 4-Tier Metadata: Database 層
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatabaseMetadata {
    private String name;
    private Map<String, SchemaMetadata> schemas = new HashMap<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Map<String, SchemaMetadata> getSchemas() { return schemas; }
    public void setSchemas(Map<String, SchemaMetadata> schemas) { this.schemas = schemas; }

    public SchemaMetadata getSchema(String schemaName) {
        return schemas.get(schemaName);
    }
}
