package io.yefangwong.guard.ai.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import io.yefangwong.guard.ai.model.AiRequest;
import io.yefangwong.guard.ai.model.AiResponse;
import io.yefangwong.guard.core.ai.AiClient;
import io.yefangwong.guard.core.config.ConfigLoader;
import io.yefangwong.guard.core.config.GuardConfig;
import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 本地 AI 客戶端：支援 OpenAI 相容協定與自動簡繁轉換。
 */
public class LocalAiClient implements AiClient {
    private final OkHttpClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private final GuardConfig.AiConfig config;

    public LocalAiClient() {
        this.config = ConfigLoader.get().ai;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(config.timeoutMs, TimeUnit.MILLISECONDS)
                .readTimeout(config.timeoutMs * 2, TimeUnit.MILLISECONDS)
                .build();
    }

    @Override
    public CompletableFuture<String> ask(String prompt) {
        CompletableFuture<String> future = new CompletableFuture<>();
        
        if (!config.enabled) {
            future.complete("AI 診斷已停用。");
            return future;
        }

        AiRequest requestObj = new AiRequest(config.model, prompt);
        try {
            String json = mapper.writeValueAsString(requestObj);
            RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(config.endpoint)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    future.completeExceptionally(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        String bodyString = responseBody.string();
                        if (!response.isSuccessful()) {
                            future.complete("AI 服務暫時無法回應 (" + response.code() + "): " + bodyString);
                            return;
                        }
                        
                        AiResponse aiResp = mapper.readValue(bodyString, AiResponse.class);
                        String content = aiResp.getContent();
                        
                        // 執行簡繁轉換 (S2T)
                        String traditionalContent = ZhConverterUtil.toTraditional(content);
                        future.complete(traditionalContent);
                    } catch (Exception e) {
                        future.completeExceptionally(e);
                    }
                }
            });
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }

    @Override
    public boolean isAvailable() {
        return config.enabled && config.endpoint != null;
    }
}
