package io.yefangwong.guard.core.utils;

import io.yefangwong.guard.core.model.DatabaseMetadata;
import io.yefangwong.guard.core.model.SchemaMetadata;
import io.yefangwong.guard.core.model.TableMetadata;
import io.yefangwong.guard.core.model.ColumnMetadata;

import java.util.Map;

/**
 * Prompt 生成器：將 JSON Metadata 轉化為 AI 友好的文字描述。
 */
public class PromptGenerator {

    /**
     * 生成 Markdown 格式的 ERD 描述，用於貼入 LLM Prompt。
     */
    public String generateMarkdownPrompt(DatabaseMetadata metadata) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Database ERD: ").append(metadata.getName()).append("\n\n");

        for (SchemaMetadata schema : metadata.getSchemas().values()) {
            sb.append("## Schema: ").append(schema.getName()).append("\n");
            for (TableMetadata table : schema.getTables().values()) {
                sb.append("### Table: ").append(table.getName()).append("\n");
                sb.append("| Column | Type |\n| --- | --- |\n");
                for (Map.Entry<String, ColumnMetadata> col : table.getColumns().entrySet()) {
                    sb.append("| ").append(col.getKey()).append(" | ")
                      .append(col.getValue().getType()).append(" |\n");
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
