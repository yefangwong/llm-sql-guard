package io.yefangwong.guard.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;

public class ConfigLoader {
    private static final String DEFAULT_CONFIG_PATH = "guard.yaml";
    private static GuardConfig currentConfig;

    public static GuardConfig load() throws IOException {
        return load(DEFAULT_CONFIG_PATH);
    }

    public static GuardConfig load(String path) throws IOException {
        File configFile = new File(path);
        // 如果目前目錄找不到，嘗試往上找一級 (為了 Gradle 子模組測試)
        if (!configFile.exists()) {
            configFile = new File("../" + path);
        }
        
        if (!configFile.exists()) {
            throw new IOException("Configuration file not found: " + path);
        }

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        currentConfig = mapper.readValue(configFile, GuardConfig.class);
        return currentConfig;
    }

    public static GuardConfig get() {
        if (currentConfig == null) {
            try {
                return load();
            } catch (IOException e) {
                throw new RuntimeException("Failed to load configuration from " + DEFAULT_CONFIG_PATH, e);
            }
        }
        return currentConfig;
    }
}
