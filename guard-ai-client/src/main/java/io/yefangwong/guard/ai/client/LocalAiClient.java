package io.yefangwong.guard.ai.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yefangwong.guard.ai.model.AiRequest;
import io.yefangwong.guard.ai.model.AiResponse;
import io.yefangwong.guard.core.ai.AiClient;
import io.yefangwong.guard.core.config.ConfigLoader;
import io.yefangwong.guard.core.config.GuardConfig;
import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
                    String responseBody = response.body().string();
                    if (!response.isSuccessful()) {
                        future.complete("AI 服務暫時無法回應 (" + response.code() + "): " + responseBody);
                        return;
                    }
                    AiResponse aiResp = mapper.readValue(responseBody, AiResponse.class);
                    future.complete(aiResp.getContent());
                }
            });
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }

    @Override
    public boolean isAvailable() {
        // 簡單的心跳測試，這裡暫時檢查配置
        return config.enabled && config.endpoint != null;
    }
}
