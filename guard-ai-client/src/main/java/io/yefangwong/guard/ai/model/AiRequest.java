package io.yefangwong.guard.ai.model;

import java.util.ArrayList;
import java.util.List;

/**
 * OpenAI 相容的 Chat Completion 請求模型
 */
public class AiRequest {
    public String model;
    public List<Message> messages = new ArrayList<>();
    public boolean stream = false;

    public AiRequest(String model, String prompt) {
        this.model = model;
        this.messages.add(new Message("user", prompt));
    }

    public static class Message {
        public String role;
        public String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
