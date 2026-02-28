package io.yefangwong.guard.cli;

import io.yefangwong.guard.cli.shell.InteractiveShell;
import io.yefangwong.guard.core.model.DatabaseMetadata;
import io.yefangwong.guard.core.validation.SqlValidator;
import io.yefangwong.guard.core.validation.ValidationResult;
import io.yefangwong.guard.executor.JdbcSqlExecutor;
import io.yefangwong.guard.executor.SqlExecutor;
import io.yefangwong.guard.ui.model.DataTable;
import io.yefangwong.guard.ui.render.TableRenderer;
import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import com.fasterxml.jackson.databind.ObjectMapper;

@Command(name = "guard", mixinStandardHelpOptions = true, version = "llm-sql-guard 1.0.0",
        description = "Modern Text-to-SQL Firewall & Station.")
public class SqlGuardApp implements Callable<Integer> {

    @Option(names = {"-m", "--metadata"}, description = "Path to the ERD Metadata JSON file.", required = true)
    private String metadataPath;

    @Option(names = {"-s", "--sql"}, description = "SQL query to validate and execute.")
    private String sql;

    @Option(names = {"-d", "--db"}, description = "Path to the SQLite database file (optional).")
    private String dbPath;

    public static void main(String[] args) {
        java.util.logging.Logger.getLogger("org.jline").setLevel(java.util.logging.Level.OFF);
        AnsiConsole.systemInstall();
        try {
            int exitCode = new CommandLine(new SqlGuardApp()).execute(args);
            System.exit(exitCode);
        } finally {
            AnsiConsole.systemUninstall();
        }
    }

    @Override
    public Integer call() throws Exception {
        System.out.println("\u001B[32m[🛡️] LLM-SQL-GUARD Initializing...\u001B[0m");

        DatabaseMetadata metadata = loadMetadata(metadataPath);
        if (metadata == null) return 1;

        SqlValidator validator = new SqlValidator(metadata);
        
        // 初始化執行器 (若提供了資料庫路徑)
        SqlExecutor executor = null;
        if (dbPath != null) {
            String url = "jdbc:sqlite:" + dbPath;
            executor = new JdbcSqlExecutor(url, null, null);
            System.out.println("[info] Database attached: " + dbPath);
        }

        if (sql != null) {
            processSingleSql(sql, validator, executor);
        } else {
            // 進入互動模式
            new InteractiveShell(validator).start(); // 這裡未來也需要傳入 executor
        }

        return 0;
    }

    private void processSingleSql(String sql, SqlValidator validator, SqlExecutor executor) {
        System.out.println("\u001B[90m[ 🔎 ANALYZING SQL ] ...\u001B[0m");
        ValidationResult result = validator.validate(sql);
        
        printResult(result);

        if (result.isValid() && executor != null) {
            try {
                System.out.println("\u001B[90m[ ⚡ EXECUTING SQL ] ...\u001B[0m");
                DataTable data = executor.executeQuery(sql);
                new TableRenderer().render(data);
            } catch (Exception e) {
                System.err.println("\u001B[31m[Error] Execution failed: " + e.getMessage() + "\u001B[0m");
            }
        }
    }

    private DatabaseMetadata loadMetadata(String path) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(new File(path), DatabaseMetadata.class);
        } catch (IOException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            return null;
        }
    }

    private void printResult(ValidationResult result) {
        if (result.isValid()) {
            System.out.println("\u001B[32m[ ✅ VALIDATED ] SQL is compliant.\u001B[0m");
        } else {
            System.out.println("\u001B[31m[ ❌ VIOLATION DETECTED ]\u001B[0m");
            for (String error : result.getErrors()) {
                System.out.println("  ➜ " + error);
            }
            if (!result.getSuggestions().isEmpty()) {
                System.out.println("\n\u001B[33m[ 🤔 DID YOU MEAN? ]\u001B[0m");
                for (String suggestion : result.getSuggestions()) {
                    System.out.println("  ➜ " + suggestion);
                }
            }
        }
    }
}
