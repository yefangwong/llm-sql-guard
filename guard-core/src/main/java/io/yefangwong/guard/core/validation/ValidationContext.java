package io.yefangwong.guard.core.validation;

import io.yefangwong.guard.core.model.TableMetadata;
import java.util.HashMap;
import java.util.Map;

/**
 * 校驗上下文：追蹤 SQL 執行過程中的表別名與元數據映射。
 */
public class ValidationContext {
    private final Map<String, TableMetadata> aliasToTable = new HashMap<>();
    private final ValidationResult result;

    public ValidationContext(ValidationResult result) {
        this.result = result;
    }

    public void registerAlias(String alias, TableMetadata table) {
        aliasToTable.put(alias, table);
    }

    public TableMetadata getTableByAlias(String alias) {
        return aliasToTable.get(alias);
    }

    public Map<String, TableMetadata> getActiveTables() {
        return aliasToTable;
    }

    public void addError(String error) {
        result.addError(error);
    }
}
