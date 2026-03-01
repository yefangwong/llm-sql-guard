package io.yefangwong.guard.ui;

import io.yefangwong.guard.ui.model.DataTable;
import io.yefangwong.guard.ui.render.TableRenderer;
import java.io.PrintWriter;
import java.util.Arrays;

public class UiTest {
    public static void main(String[] args) {
        System.out.println("Testing Table Rendering Engine...\n");

        DataTable table = new DataTable();
        table.addColumn("ID");
        table.addColumn("USER_NAME");
        table.addColumn("STATUS");
        table.addColumn("LAST_LOGIN");

        table.addRow(Arrays.asList(1, "alice", "ACTIVE", "2026-02-27 09:00"));
        table.addRow(Arrays.asList(2, "bob", "SUSPENDED", "2026-02-26 14:30"));
        table.addRow(Arrays.asList(105, "charlie_long_name", "ACTIVE", "2026-02-27 10:15"));

        TableRenderer renderer = new TableRenderer();
        renderer.render(table, new PrintWriter(System.out, true));
    }
}
