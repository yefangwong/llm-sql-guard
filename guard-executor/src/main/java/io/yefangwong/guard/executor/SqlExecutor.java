package io.yefangwong.guard.executor;

import io.yefangwong.guard.ui.model.DataTable;
import java.sql.SQLException;

/**
 * 安全 SQL 執行器介面。
 */
public interface SqlExecutor {
    /**
     * 執行查詢並返回渲染用的 DataTable。
     */
    DataTable executeQuery(String sql) throws SQLException;

    /**
     * 測試連接。
     */
    boolean testConnection();
}
