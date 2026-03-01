package io.yefangwong.guard.cli;

import io.yefangwong.guard.cli.shell.InteractiveShell;
import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "guard", mixinStandardHelpOptions = true, version = "1.0.0-alpha",
        description = "Agnostic SQL Security & Diagnostic Hub for Enterprise ERD.")
public class SqlGuardApp implements Callable<Integer> {

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        int exitCode = new CommandLine(new SqlGuardApp()).execute(args);
        AnsiConsole.systemUninstall();
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        try {
            InteractiveShell shell = new InteractiveShell();
            shell.start();
            return 0;
        } catch (Exception e) {
            System.err.println("Fatal Error initializing shell: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }
}
