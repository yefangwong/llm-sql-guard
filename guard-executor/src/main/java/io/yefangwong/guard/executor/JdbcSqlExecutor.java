package io.yefangwong.guard.executor;

import io.yefangwong.guard.core.config.ConfigLoader;
import io.yefangwong.guard.core.config.GuardConfig;
import io.yefangwong.guard.ui.model.DataTable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 基於 JDBC 的 SQL 執行器實作。
 */
public class JdbcSqlExecutor implements SqlExecutor {
    private final String url;
    private final String user;
    private final String password;

    public JdbcSqlExecutor() {
        GuardConfig.DatabaseConfig config = ConfigLoader.get().database;
        this.url = config.getUrl();
        this.user = null;
        this.password = null;
    }

    public JdbcSqlExecutor(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public DataTable executeQuery(String sql) throws SQLException {
        // 安全檢查：僅允許 SELECT 語句
        if (!sql.trim().toUpperCase().startsWith("SELECT")) {
            throw new SQLException("Security Block: Only SELECT statements are allowed in read-only mode.");
        }

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            DataTable table = new DataTable();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // 1. 提取表頭
            for (int i = 1; i <= columnCount; i++) {
                table.addColumn(metaData.getColumnName(i));
            }

            // 2. 提取數據行
            while (rs.next()) {
                List<Object> row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                }
                table.addRow(row);
            }

            return table;
        }
    }

    @Override
    public boolean testConnection() {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            return !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
