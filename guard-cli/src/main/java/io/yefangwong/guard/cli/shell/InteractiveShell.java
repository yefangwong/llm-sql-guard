package io.yefangwong.guard.cli.shell;

import io.yefangwong.guard.ai.client.LocalAiClient;
import io.yefangwong.guard.core.model.DatabaseMetadata;
import io.yefangwong.guard.core.model.MetadataLoader;
import io.yefangwong.guard.core.service.AiDiagnosisService;
import io.yefangwong.guard.core.validation.SqlValidator;
import io.yefangwong.guard.core.validation.ValidationResult;
import io.yefangwong.guard.executor.JdbcSqlExecutor;
import io.yefangwong.guard.ui.model.DataTable;
import io.yefangwong.guard.ui.render.TableRenderer;
import org.jline.reader.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.fusesource.jansi.Ansi;
import java.io.PrintWriter;
import static org.fusesource.jansi.Ansi.ansi;

public class InteractiveShell {
    private final SqlValidator validator;
    private final AiDiagnosisService diagnosisService;
    private final JdbcSqlExecutor executor;
    private final TableRenderer renderer = new TableRenderer();

    public InteractiveShell() throws Exception {
        DatabaseMetadata metadata = MetadataLoader.load();
        this.validator = new SqlValidator(metadata);
        this.diagnosisService = new AiDiagnosisService(new LocalAiClient());
        this.executor = new JdbcSqlExecutor(); 
    }

    public void start() throws Exception {
        Terminal terminal = TerminalBuilder.builder().system(true).build();
        LineReader reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .variable(LineReader.SECONDARY_PROMPT_PATTERN, "%P > ")
                .build();

        printWelcome(terminal);

        String prompt = "sql-guard> ";
        while (true) {
            String line;
            try {
                line = reader.readLine(prompt);
            } catch (UserInterruptException e) {
                break;
            } catch (EndOfFileException e) {
                break;
            }

            if (line == null || line.trim().equalsIgnoreCase("exit") || line.trim().equalsIgnoreCase("quit")) {
                break;
            }

            if (line.trim().isEmpty()) continue;

            processCommand(line, terminal);
        }
        
        terminal.writer().println("Goodbye!");
        terminal.flush();
    }

    private void processCommand(String sql, Terminal terminal) {
        PrintWriter writer = terminal.writer();
        writer.println(ansi().fgCyan().a("Validating SQL...").reset());
        terminal.flush();

        ValidationResult result = validator.validate(sql);

        if (result.isSuccess()) {
            writer.println(ansi().fgGreen().a("✓ Validation Passed. Executing safely...").reset());
            terminal.flush();
            try {
                DataTable table = executor.executeQuery(sql);
                renderer.render(table, writer);
            } catch (Exception e) {
                writer.println(ansi().fgRed().a("Execution Error: " + e.getMessage()).reset());
            }
        } else {
            writer.println(ansi().fgRed().a("✗ Validation Failed:").reset());
            for (String error : result.getErrors()) {
                writer.println(ansi().fgRed().a("  - " + error).reset());
            }

            writer.println(ansi().fgYellow().a("🤖 Requesting AI Diagnosis...").reset());
            terminal.flush();

            try {
                String diagnosis = diagnosisService.diagnose(sql, result).get();
                writer.println(ansi().fgYellow().a("\n--- AI Diagnosis ---").reset());
                writer.println(diagnosis);
                writer.println(ansi().fgYellow().a("--------------------\n").reset());
            } catch (Exception e) {
                writer.println(ansi().fgRed().a("AI Diagnosis failed: " + e.getMessage()).reset());
            }
        }
        terminal.flush();
    }

    private void printWelcome(Terminal terminal) {
        terminal.writer().println(ansi().eraseScreen());
        terminal.writer().println(ansi().fgBlue().bold().a("🛡️ llm-sql-guard Interactive Shell v1.0.0-alpha").reset());
        terminal.writer().println("Type 'exit' to quit. Multiline SQL is supported.");
        terminal.writer().println();
        terminal.flush();
    }
}
