package io.yefangwong.guard.core.validation;

import io.yefangwong.guard.core.model.DatabaseMetadata;
import io.yefangwong.guard.core.model.SchemaMetadata;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;

/**
 * 核心 SQL 校驗引擎。
 */
public class SqlValidator {
    private final DatabaseMetadata metadata;

    public SqlValidator(DatabaseMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * 校驗 SQL 是否符合 ERD。
     */
    public ValidationResult validate(String sql) {
        ValidationResult result = new ValidationResult();
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            
            if (metadata.getSchemas() == null || metadata.getSchemas().isEmpty()) {
                result.addError("No schema defined in Metadata.");
                return result;
            }
            
            // 簡化邏輯：獲取第一個 Schema 進行校驗
            SchemaMetadata defaultSchema = metadata.getSchemas().values().iterator().next();
            
            // 啟動深度校驗訪問器
            ValidationContext context = new ValidationContext(result);
            SqlValidationVisitor visitor = new SqlValidationVisitor(defaultSchema, context, result);
            statement.accept(visitor);
            
        } catch (JSQLParserException e) {
            result.addError("SQL Syntax Error: " + e.getMessage());
        }
        return result;
    }
}
