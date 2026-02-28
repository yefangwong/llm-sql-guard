package io.yefangwong.guard.ui.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 中性的資料表格模型，用於 UI 渲染。
 */
public class DataTable {
    private List<String> columns = new ArrayList<>();
    private List<List<Object>> rows = new ArrayList<>();

    public DataTable() {}

    public void addColumn(String name) { columns.add(name); }
    public void addRow(List<Object> row) { rows.add(row); }

    public List<String> getColumns() { return columns; }
    public List<List<Object>> getRows() { return rows; }
}
