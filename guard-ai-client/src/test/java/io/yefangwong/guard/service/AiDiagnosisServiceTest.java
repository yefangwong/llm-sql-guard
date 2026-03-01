package io.yefangwong.guard.service;

import io.yefangwong.guard.ai.client.LocalAiClient;
import io.yefangwong.guard.core.model.DatabaseMetadata;
import io.yefangwong.guard.core.model.MetadataLoader;
import io.yefangwong.guard.core.service.AiDiagnosisService;
import io.yefangwong.guard.core.validation.SqlValidator;
import io.yefangwong.guard.core.validation.ValidationResult;
import org.junit.Test;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;

public class AiDiagnosisServiceTest {
    @Test
    public void testFullDiagnosisFlow() throws Exception {
        // 1. 初始化環境
        DatabaseMetadata metadata = MetadataLoader.load("test-metadata.json");
        SqlValidator validator = new SqlValidator(metadata);
        LocalAiClient aiClient = new LocalAiClient();
        AiDiagnosisService diagnosisService = new AiDiagnosisService(aiClient);

        // 2. 準備一個錯誤的 SQL (users 表沒有 email 欄位)
        String sql = "SELECT u.name, u.email FROM users u";
        ValidationResult result = validator.validate(sql);
        
        assertFalse("SQL 驗證應失敗", result.isSuccess());
        System.out.println("❌ 校驗錯誤: " + result.getErrors());

        // 3. 呼叫 AI 進行診斷
        System.out.println("🚀 正在請求 Qwen-2.5-Coder 進行語義診斷...");
        String diagnosis = diagnosisService.diagnose(sql, result).get(30, TimeUnit.SECONDS);
        
        System.out.println("\n🤖 --- AI 診斷建議 ---\n");
        System.out.println(diagnosis);
        System.out.println("\n-----------------------\n");
        
        assertNotNull("診斷結果不應為空", diagnosis);
        assertTrue("診斷應包含關鍵字說明", 
                   diagnosis.contains("email") || diagnosis.contains("欄位") || diagnosis.contains("Column"));
    }
}
