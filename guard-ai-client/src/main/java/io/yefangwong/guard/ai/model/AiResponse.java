package io.yefangwong.guard.ai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * OpenAI 相容的 Chat Completion 響應模型
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiResponse {
    public List<Choice> choices;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice {
        public Message message;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        public String content;
    }

    public String getContent() {
        if (choices != null && !choices.isEmpty()) {
            return choices.get(0).message.content;
        }
        return "AI 無法提供有效建議。";
    }
}
