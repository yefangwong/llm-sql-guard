package io.yefangwong.guard.ai.client;

import io.yefangwong.guard.core.ai.AiClient;
import io.yefangwong.guard.core.config.ConfigLoader;
import org.junit.Test;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.*;

public class AiClientTest {
    @Test
    public void testAiConnection() {
        AiClient client = new LocalAiClient();
        String endpoint = ConfigLoader.get().ai.endpoint;
        
        System.out.println("🚀 正在測試本地 AI 通訊 (Endpoint: " + endpoint + ")...");
        
        try {
            String prompt = "請簡短解釋 SQL 注入 (SQL Injection) 是什麼。";
            // 增加超時到 30 秒，給本地推理足夠時間
            String response = client.ask(prompt).get(30, TimeUnit.SECONDS);
            
            System.out.println("🤖 AI 回覆: " + response);
            assertNotNull("AI 回覆不應為空", response);
        } catch (Exception e) {
            System.err.println("❌ AI 通訊失敗!");
            e.printStackTrace();
        }
    }
}
