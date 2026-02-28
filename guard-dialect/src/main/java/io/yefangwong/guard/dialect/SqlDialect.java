package io.yefangwong.guard.dialect;

/**
 * 抽象方言介面，用於支援不同資料庫的 SQL 語法與標識符引用規則。
 */
public interface SqlDialect {
    /**
     * @return 資料庫類型名稱 (例如: "sqlite", "oracle")
     */
    String getName();

    /**
     * 獲取標識符引用符號 (例如 SQLite 為 `, Oracle 為 ")
     */
    String getIdentifierQuote();

    /**
     * 檢查是否為系統保留字。
     */
    boolean isReservedWord(String word);
}
