package io.yefangwong.guard.core.ai;

import java.util.concurrent.CompletableFuture;

/**
 * AI 客戶端介面，定義與 LLM 通訊的標準。
 */
public interface AiClient {
    /**
     * 發送 Prompt 並獲取回覆。
     * @param prompt 提示詞
     * @return 異步回傳 AI 的回覆內容
     */
    CompletableFuture<String> ask(String prompt);
    
    /**
     * 檢查 AI 服務是否可用。
     * @return 是否連通
     */
    boolean isAvailable();
}
