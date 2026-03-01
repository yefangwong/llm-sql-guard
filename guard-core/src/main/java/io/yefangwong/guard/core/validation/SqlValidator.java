package io.yefangwong.guard.core.validation;

import io.yefangwong.guard.core.model.DatabaseMetadata;
import io.yefangwong.guard.core.model.SchemaMetadata;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;

/**
 * 核心 SQL 校驗引擎。
 */
public class SqlValidator {
    private final DatabaseMetadata metadata;

    public SqlValidator(DatabaseMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * 校驗 SQL 是否符合 ERD 且具備唯讀安全性。
     */
    public ValidationResult validate(String sql) {
        ValidationResult result = new ValidationResult();
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            
            // 安全性檢查：僅允許 Select 語句 (Read-Only Guard)
            if (!(statement instanceof Select)) {
                result.addError("Security Violation: Only SELECT statements are allowed in this environment.");
                return result;
            }

            if (metadata.getSchemas() == null || metadata.getSchemas().isEmpty()) {
                result.addError("Architecture Error: No database schema loaded in Metadata.");
                return result;
            }
            
            // 優先尋找 PUBLIC 或 預設 Schema，若無則取第一個
            SchemaMetadata targetSchema = metadata.getSchema("PUBLIC");
            if (targetSchema == null) {
                targetSchema = metadata.getSchemas().values().iterator().next();
            }
            
            // 啟動深度語義校驗
            ValidationContext context = new ValidationContext(result);
            SqlValidationVisitor visitor = new SqlValidationVisitor(targetSchema, context, result);
            statement.accept(visitor);
            
        } catch (JSQLParserException e) {
            result.addError("SQL Syntax Error: " + e.getMessage());
        } catch (Exception e) {
            result.addError("Internal Validation Error: " + e.getMessage());
        }
        return result;
    }
}
