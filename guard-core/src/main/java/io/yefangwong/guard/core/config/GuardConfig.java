package io.yefangwong.guard.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class GuardConfig {
    public ServerConfig server;
    public AiConfig ai;
    public DatabaseConfig database;
    public UiConfig ui;

    public static class ServerConfig {
        public int port;
    }

    public static class AiConfig {
        public String endpoint;
        public String model;
        @JsonProperty("timeout_ms")
        public int timeoutMs;
        public boolean enabled;
    }

    public static class DatabaseConfig {
        public String path;
        @JsonProperty("metadata_path")
        public String metadataPath;
        @JsonProperty("read_only")
        public boolean readOnly;

        public String getUrl() {
            return "jdbc:sqlite:" + path;
        }
    }

    public static class UiConfig {
        public String theme;
        @JsonProperty("table_limit")
        public int tableLimit;
        @JsonProperty("show_execution_time")
        public boolean showExecutionTime;
    }
}
