package io.yefangwong.guard.core.validation;

import io.yefangwong.guard.core.model.SchemaMetadata;
import io.yefangwong.guard.core.model.TableMetadata;
import io.yefangwong.guard.core.utils.FuzzyMatcher;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.statement.select.*;

import java.util.List;

/**
 * 深度 SQL 訪問器：負責追蹤別名、驗證欄位並提供模糊匹配建議。
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
        }
    }

    private void processPlainSelect(PlainSelect plainSelect) {
        if (plainSelect.getFromItem() instanceof Table) {
            registerTable((Table) plainSelect.getFromItem());
        }
        
        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                if (join.getRightItem() instanceof Table) {
                    registerTable((Table) join.getRightItem());
                }
            }
        }

        if (plainSelect.getSelectItems() != null) {
            for (SelectItem selectItem : plainSelect.getSelectItems()) {
                selectItem.accept(new SelectItemVisitorAdapter() {
                    @Override
                    public void visit(SelectExpressionItem item) {
                        item.getExpression().accept(new ExpressionVisitorAdapter() {
                            @Override
                            public void visit(Column column) {
                                validateColumn(column);
                            }
                        });
                    }
                });
            }
        }
    }

    private void registerTable(Table table) {
        String tableName = table.getName().replace("`", "").replace("\"", "");
        TableMetadata tableMeta = schema.getTables().get(tableName);
        
        if (tableMeta == null) {
            result.addError("Table not found in ERD: " + tableName);
            List<String> suggestions = FuzzyMatcher.findSuggestions(tableName, schema.getTables().keySet());
            if (!suggestions.isEmpty()) {
                result.addSuggestion("Did you mean table: " + suggestions + "?");
            }
            return;
        }

        String alias = (table.getAlias() != null) ? table.getAlias().getName() : tableName;
        context.registerAlias(alias, tableMeta);
    }

    private void validateColumn(Column column) {
        String colName = column.getColumnName().replace("`", "").replace("\"", "");
        Table table = column.getTable();

        if (table != null && table.getName() != null) {
            TableMetadata meta = context.getTableByAlias(table.getName());
            if (meta == null) {
                result.addError("Table or Alias '" + table.getName() + "' not found.");
            } else if (!meta.getColumns().containsKey(colName)) {
                result.addError("Column '" + colName + "' not found in table '" + meta.getName() + "'");
                List<String> suggestions = FuzzyMatcher.findSuggestions(colName, meta.getColumns().keySet());
                if (!suggestions.isEmpty()) {
                    result.addSuggestion("Did you mean column: '" + suggestions + "' in table '" + meta.getName() + "'?");
                }
            }
        } else {
            boolean found = false;
            for (TableMetadata meta : context.getActiveTables().values()) {
                if (meta.getColumns().containsKey(colName)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                result.addError("Column '" + colName + "' not found in any involved tables.");
                // 在所有活動表中搜索相似欄位
                for (TableMetadata meta : context.getActiveTables().values()) {
                    List<String> suggestions = FuzzyMatcher.findSuggestions(colName, meta.getColumns().keySet());
                    if (!suggestions.isEmpty()) {
                        result.addSuggestion("Did you mean column: '" + suggestions + "' in table '" + meta.getName() + "'?");
                    }
                }
            }
        }
    }
}
