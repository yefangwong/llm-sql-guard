package io.yefangwong.guard.ui.render;

import io.yefangwong.guard.ui.model.DataTable;
import java.util.ArrayList;
import java.util.List;

/**
 * 終端表格渲染引擎：負責將數據轉化為美觀的 ANSI 表格。
 */
public class TableRenderer {
    private static final String CYAN = "\u001B[36m";
    private static final String RESET = "\u001B[0m";
    private static final String GRAY = "\u001B[90m";

    public void render(DataTable table) {
        if (table == null || table.getColumns().isEmpty()) {
            System.out.println(GRAY + "(Empty result set)" + RESET);
            return;
        }

        List<String> columns = table.getColumns();
        List<List<Object>> rows = table.getRows();

        // 1. 計算每欄的最大寬度
        int[] columnWidths = new int[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            columnWidths[i] = columns.get(i).length();
        }

        for (List<Object> row : rows) {
            for (int i = 0; i < row.size(); i++) {
                String val = String.valueOf(row.get(i));
                columnWidths[i] = Math.max(columnWidths[i], val.length());
            }
        }

        // 2. 渲染表頭
        printDivider(columnWidths, "┌", "┬", "┐");
        printRow(columns, columnWidths, true);
        printDivider(columnWidths, "├", "┼", "┤");

        // 3. 渲染數據行
        if (rows.isEmpty()) {
            List<String> emptyMsg = new ArrayList<>();
            for (int i = 0; i < columns.size(); i++) emptyMsg.add("-");
            printRow(emptyMsg, columnWidths, false);
        } else {
            for (List<Object> row : rows) {
                List<String> stringRow = new ArrayList<>();
                for (Object o : row) stringRow.add(String.valueOf(o));
                printRow(stringRow, columnWidths, false);
            }
        }

        // 4. 渲染表底
        printDivider(columnWidths, "└", "┴", "┘");
        System.out.println(GRAY + " Total rows: " + rows.size() + RESET + "\n");
    }

    private void printDivider(int[] widths, String left, String middle, String right) {
        StringBuilder sb = new StringBuilder(GRAY).append(left);
        for (int i = 0; i < widths.length; i++) {
            for (int j = 0; j < widths[i] + 2; j++) sb.append("─");
            if (i < widths.length - 1) sb.append(middle);
        }
        sb.append(right).append(RESET);
        System.out.println(sb.toString());
    }

    private void printRow(List<String> row, int[] widths, boolean isHeader) {
        StringBuilder sb = new StringBuilder(GRAY).append("│").append(RESET);
        for (int i = 0; i < row.size(); i++) {
            String val = row.get(i);
            String color = isHeader ? CYAN : "";
            sb.append(" ").append(color).append(padString(val, widths[i])).append(RESET).append(GRAY).append(" │").append(RESET);
        }
        System.out.println(sb.toString());
    }

    private String padString(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}
