package io.yefangwong.guard.cli.shell;

import io.yefangwong.guard.core.validation.SqlValidator;
import io.yefangwong.guard.core.validation.ValidationResult;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

/**
 * 現代化交互式 Shell (REPL)
 */
public class InteractiveShell {
    private final SqlValidator validator;
    private final String prompt = "\u001B[36msql-guard » \u001B[0m";

    public InteractiveShell(SqlValidator validator) {
        this.validator = validator;
    }

    public void start() {
        try (Terminal terminal = TerminalBuilder.builder().system(true).build()) {
            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .appName("llm-sql-guard")
                    .build();

            System.out.println("\u001B[32m[🛡️] Interactive Mode Started. Type /exit to quit.\u001B[0m");

            while (true) {
                String line;
                try {
                    line = reader.readLine(prompt);
                } catch (UserInterruptException | EndOfFileException e) {
                    break;
                }

                if (line == null || line.trim().isEmpty()) {
                    continue;
                }

                if (line.equalsIgnoreCase("/exit") || line.equalsIgnoreCase("exit")) {
                    break;
                }

                if (line.equalsIgnoreCase("/help")) {
                    printHelp();
                    continue;
                }

                // 執行 SQL 校驗
                processSql(line);
            }
        } catch (IOException e) {
            System.err.println("Error initializing terminal: " + e.getMessage());
        }
    }

    private void processSql(String sql) {
        System.out.println("\u001B[90m[ 🔎 ANALYZING SQL ] ...\u001B[0m");
        ValidationResult result = validator.validate(sql);

        if (result.isValid()) {
            System.out.println("\u001B[32m[ ✅ VALIDATED ] SQL is compliant with ERD.\u001B[0m");
        } else {
            System.out.println("\u001B[31m[ ❌ VIOLATION DETECTED ]\u001B[0m");
            for (String error : result.getErrors()) {
                System.out.println("  ➜ \u001B[31m" + error + "\u001B[0m");
            }
            if (!result.getSuggestions().isEmpty()) {
                System.out.println("\n\u001B[33m[ 🤔 DID YOU MEAN? ]\u001B[0m");
                for (String suggestion : result.getSuggestions()) {
                    System.out.println("  ➜ " + suggestion);
                }
            }
        }
    }

    private void printHelp() {
        System.out.println("\nCommands:");
        System.out.println("  /exit - Exit the shell");
        System.out.println("  /help - Show this help message");
        System.out.println("  [SQL] - Enter any SQL to validate against Metadata\n");
    }
}
