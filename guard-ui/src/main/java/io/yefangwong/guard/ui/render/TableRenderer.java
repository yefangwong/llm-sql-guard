package io.yefangwong.guard.ui.render;

import io.yefangwong.guard.ui.model.DataTable;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 專業終端表格渲染引擎：負責將數據轉化為美觀的 ANSI 表格。
 * 支援自定義輸出流，完美整合 JLine Terminal。
 */
public class TableRenderer {
    private static final String CYAN = "\u001B[36m";
    private static final String RESET = "\u001B[0m";
    private static final String GRAY = "\u001B[90m";
    private static final String WHITE_BOLD = "\u001B[1;37m";

    public void render(DataTable table, PrintWriter writer) {
        if (table == null || table.getColumns().isEmpty()) {
            writer.println(GRAY + "(Empty result set)" + RESET);
            writer.flush();
            return;
        }

        List<String> columns = table.getColumns();
        List<List<Object>> rows = table.getRows();

        // 1. 計算每欄的最大寬度 (限制最大寬度為 50，防止溢出)
        int[] columnWidths = new int[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            columnWidths[i] = Math.min(50, columns.get(i).length());
        }

        for (List<Object> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                String val = (row.get(i) == null) ? "NULL" : String.valueOf(row.get(i));
                columnWidths[i] = Math.max(columnWidths[i], Math.min(50, val.length()));
            }
        }

        // 2. 渲染表頭
        printDivider(columnWidths, "┌", "┬", "┐", writer);
        printRow(columns, columnWidths, true, writer);
        printDivider(columnWidths, "├", "┼", "┤", writer);

        // 3. 渲染數據行
        if (rows.isEmpty()) {
            writer.println(GRAY + "│ (No data found) " + RESET);
        } else {
            for (List<Object> row : rows) {
                List<String> stringRow = new ArrayList<>();
                for (Object o : row) {
                    String s = (o == null) ? "NULL" : String.valueOf(o);
                    if (s.length() > 50) s = s.substring(0, 47) + "...";
                    stringRow.add(s);
                }
                printRow(stringRow, columnWidths, false, writer);
            }
        }

        // 4. 渲染表底
        printDivider(columnWidths, "└", "┴", "┘", writer);
        writer.println(GRAY + " Total: " + rows.size() + " rows" + RESET + "\n");
        writer.flush();
    }

    private void printDivider(int[] widths, String left, String middle, String right, PrintWriter writer) {
        StringBuilder sb = new StringBuilder(GRAY).append(left);
        for (int i = 0; i < widths.length; i++) {
            for (int j = 0; j < widths[i] + 2; j++) sb.append("─");
            if (i < widths.length - 1) sb.append(middle);
        }
        sb.append(right).append(RESET);
        writer.println(sb.toString());
    }

    private void printRow(List<String> row, int[] widths, boolean isHeader, PrintWriter writer) {
        StringBuilder sb = new StringBuilder(GRAY).append("│").append(RESET);
        for (int i = 0; i < row.size(); i++) {
            String val = row.get(i);
            String color = isHeader ? WHITE_BOLD : "";
            sb.append(" ").append(color).append(padString(val, widths[i])).append(RESET).append(GRAY).append(" │").append(RESET);
        }
        writer.println(sb.toString());
    }

    private String padString(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}
