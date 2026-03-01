package io.yefangwong.guard.core.validation;

import io.yefangwong.guard.core.model.SchemaMetadata;
import io.yefangwong.guard.core.model.TableMetadata;
import io.yefangwong.guard.core.utils.FuzzyMatcher;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.statement.select.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 專業 SQL 語義訪問器：實作深度別名追蹤與全方位欄位驗證。
 */
public class SqlValidationVisitor extends StatementVisitorAdapter {
    private final SchemaMetadata schema;
    private final ValidationContext context;
    private final ValidationResult result;

    public SqlValidationVisitor(SchemaMetadata schema, ValidationContext context, ValidationResult result) {
        this.schema = schema;
        this.context = context;
        this.result = result;
    }

    @Override
    public void visit(Select select) {
        if (select.getSelectBody() instanceof PlainSelect) {
            processPlainSelect((PlainSelect) select.getSelectBody());
        } else if (select.getSelectBody() instanceof SetOperationList) {
            // 處理 UNION/INTERSECT 等操作
            SetOperationList list = (SetOperationList) select.getSelectBody();
            for (SelectBody body : list.getSelects()) {
                if (body instanceof PlainSelect) {
                    processPlainSelect((PlainSelect) body);
                }
            }
        }
    }

    private void processPlainSelect(PlainSelect plainSelect) {
        // 1. 先註冊 FROM 與 JOIN 表 (建立 Alias Map)
        if (plainSelect.getFromItem() != null) {
            plainSelect.getFromItem().accept(new FromItemVisitorAdapter() {
                @Override
                public void visit(Table table) {
                    registerTable(table);
                }
            });
        }
        
        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                join.getRightItem().accept(new FromItemVisitorAdapter() {
                    @Override
                    public void visit(Table table) {
                        registerTable(table);
                    }
                });
            }
        }

        // 2. 驗證 SELECT 欄位
        if (plainSelect.getSelectItems() != null) {
            for (SelectItem selectItem : plainSelect.getSelectItems()) {
                validateSelectItem(selectItem);
            }
        }

        // 3. 驗證 WHERE 子句
        if (plainSelect.getWhere() != null) {
            validateExpression(plainSelect.getWhere());
        }

        // 4. 驗證 ORDER BY
        if (plainSelect.getOrderByElements() != null) {
            for (OrderByElement element : plainSelect.getOrderByElements()) {
                validateExpression(element.getExpression());
            }
        }
    }

    private void registerTable(Table table) {
        String tableName = normalize(table.getName());
        // 大小寫不敏感搜尋
        TableMetadata tableMeta = findTableCaseInsensitive(tableName);
        
        if (tableMeta == null) {
            result.addError("Table not found in Metadata: " + tableName);
            List<String> suggestions = FuzzyMatcher.findSuggestions(tableName, schema.getTables().keySet());
            if (!suggestions.isEmpty()) {
                result.addSuggestion("Did you mean table: " + suggestions + "?");
            }
            return;
        }

        String alias = (table.getAlias() != null) ? normalize(table.getAlias().getName()) : tableName;
        context.registerAlias(alias, tableMeta);
    }

    private void validateSelectItem(SelectItem selectItem) {
        selectItem.accept(new SelectItemVisitorAdapter() {
            @Override
            public void visit(SelectExpressionItem item) {
                validateExpression(item.getExpression());
            }
            @Override
            public void visit(AllColumns columns) {
                // SELECT * 不需要個別驗證欄位，但在 registerTable 時已驗證過表存在
            }
        });
    }

    private void validateExpression(Expression expression) {
        expression.accept(new ExpressionVisitorAdapter() {
            @Override
            public void visit(Column column) {
                validateColumn(column);
            }
        });
    }

    private void validateColumn(Column column) {
        String colName = normalize(column.getColumnName());
        Table table = column.getTable();

        if (table != null && table.getName() != null) {
            String aliasOrName = normalize(table.getName());
            TableMetadata meta = context.getTableByAlias(aliasOrName);
            
            if (meta == null) {
                result.addError("Table or Alias '" + aliasOrName + "' not found in current scope.");
            } else {
                checkColumnInTable(colName, meta);
            }
        } else {
            // 無別名引用，在所有已註冊的活動表中搜索
            boolean found = false;
            for (TableMetadata meta : context.getActiveTables().values()) {
                if (findColumnCaseInsensitive(colName, meta)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                result.addError("Column '" + colName + "' not found in any involved tables.");
            }
        }
    }

    private void checkColumnInTable(String colName, TableMetadata meta) {
        if (!findColumnCaseInsensitive(colName, meta)) {
            result.addError("Column '" + colName + "' not found in table '" + meta.getName() + "'");
            List<String> suggestions = FuzzyMatcher.findSuggestions(colName, meta.getColumns().keySet());
            if (!suggestions.isEmpty()) {
                result.addSuggestion("Did you mean column: '" + suggestions + "' in table '" + meta.getName() + "'?");
            }
        }
    }

    private String normalize(String input) {
        if (input == null) return null;
        return input.replace("`", "").replace("\"", "").replace("[", "").replace("]", "");
    }

    private TableMetadata findTableCaseInsensitive(String name) {
        for (Map.Entry<String, TableMetadata> entry : schema.getTables().entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) return entry.getValue();
        }
        return null;
    }

    private boolean findColumnCaseInsensitive(String name, TableMetadata meta) {
        for (String col : meta.getColumns().keySet()) {
            if (col.equalsIgnoreCase(name)) return true;
        }
        return false;
    }
}
