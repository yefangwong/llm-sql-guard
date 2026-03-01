package io.yefangwong.guard.core.service;

import io.yefangwong.guard.core.ai.AiClient;
import io.yefangwong.guard.core.model.DatabaseMetadata;
import io.yefangwong.guard.core.model.MetadataLoader;
import io.yefangwong.guard.core.utils.PromptGenerator;
import io.yefangwong.guard.core.validation.ValidationResult;

import java.util.concurrent.CompletableFuture;

/**
 * AI 診斷服務：負責將 SQL 錯誤與 ERD 元數據結合，生成並發送診斷請求。
 */
public class AiDiagnosisService {
    private final AiClient aiClient;
    private final PromptGenerator promptGenerator = new PromptGenerator();

    public AiDiagnosisService(AiClient aiClient) {
        this.aiClient = aiClient;
    }

    /**
     * 針對 SQL 驗證失敗的結果進行 AI 診斷。
     */
    public CompletableFuture<String> diagnose(String sql, ValidationResult result) {
        if (result.isSuccess()) {
            return CompletableFuture.completedFuture("SQL 驗證通過，無需診斷。");
        }

        DatabaseMetadata metadata = MetadataLoader.get();
        String erdMarkdown = promptGenerator.generateMarkdownPrompt(metadata);
        
        // 建立專業診斷 Prompt
        String diagnosisPrompt = createProfessionalPrompt(sql, result, erdMarkdown);
        
        return aiClient.ask(diagnosisPrompt);
    }

    private String createProfessionalPrompt(String sql, ValidationResult result, String erd) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an expert SQL DBA and Security Auditor. You MUST always respond in Traditional Chinese (繁體中文).\n");
        sb.append("Task: Diagnose the following SQL query error based on the provided Database ERD.\n\n");
        
        sb.append("### User SQL Query:\n").append("```sql\n").append(sql).append("\n```\n\n");
        
        sb.append("### Validation Errors Found:\n");
        for (String error : result.getErrors()) {
            sb.append("- ").append(error).append("\n");
        }
        
        if (!result.getSuggestions().isEmpty()) {
            sb.append("\n### System Suggestions:\n");
            for (String suggestion : result.getSuggestions()) {
                sb.append("- ").append(suggestion).append("\n");
            }
        }

        sb.append("\n### Database Context (ERD):\n").append(erd).append("\n");
        
        sb.append("\n### Output Requirement (Mandatory Traditional Chinese):\n");
        sb.append("1. 請務必使用 **繁體中文 (Traditional Chinese)** 進行回答。\n");
        sb.append("2. 清楚解釋為什麼會發生錯誤 (Why)。\n");
        sb.append("3. 提供修正後的 SQL 查詢語句 (Corrected SQL)。\n");
        sb.append("4. 保持語氣專業。回答開頭請先確認收到繁體中文指令。\n");

        return sb.toString();
    }
}
