package io.yefangwong.guard.core.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yefangwong.guard.core.config.ConfigLoader;
import java.io.File;
import java.io.IOException;

/**
 * Metadata 載入器：負責從 JSON 檔案載入 4-Tier Metadata 模型。
 */
public class MetadataLoader {
    private static DatabaseMetadata currentMetadata;

    /**
     * 從設定檔指定的路徑載入 Metadata。
     */
    public static DatabaseMetadata load() throws IOException {
        String path = ConfigLoader.get().database.metadataPath;
        return load(path);
    }

    /**
     * 從指定路徑載入 Metadata。
     */
    public static DatabaseMetadata load(String path) throws IOException {
        File file = new File(path);
        
        // 如果目前目錄找不到，嘗試往上找一級 (對應 Gradle 子模組測試環境)
        if (!file.exists()) {
            file = new File("../" + path);
        }

        if (!file.exists()) {
            throw new IOException("Metadata file not found: " + path);
        }

        ObjectMapper mapper = new ObjectMapper();
        currentMetadata = mapper.readValue(file, DatabaseMetadata.class);
        return currentMetadata;
    }

    /**
     * 獲取目前已載入的 Metadata。
     */
    public static DatabaseMetadata get() {
        if (currentMetadata == null) {
            try {
                return load();
            } catch (IOException e) {
                throw new RuntimeException("Failed to load database metadata", e);
            }
        }
        return currentMetadata;
    }
}
