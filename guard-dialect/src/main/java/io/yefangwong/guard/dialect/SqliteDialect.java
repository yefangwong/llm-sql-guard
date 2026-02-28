package io.yefangwong.guard.dialect;

import java.util.Collections;
import java.util.Set;

/**
 * SQLite 方言實作，支援 Spider 資料集驗證。
 */
public class SqliteDialect implements SqlDialect {
    private static final Set<String> RESERVED_WORDS = Collections.emptySet(); // 簡化版

    @Override
    public String getName() {
        return "sqlite";
    }

    @Override
    public String getIdentifierQuote() {
        return "`";
    }

    @Override
    public boolean isReservedWord(String word) {
        return RESERVED_WORDS.contains(word.toUpperCase());
    }
}
