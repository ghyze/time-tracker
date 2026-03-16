package nl.ghyze.timetracker.macos;

import nl.ghyze.timetracker.ActiveWindow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class ActiveWindowMacOS implements ActiveWindow {

   private static final long COMMAND_TIMEOUT_SECONDS = 2L;

   @Override
   public String getActiveWindowTitle() {
      final var script = """
            tell application "System Events"
            set frontApp to first application process whose frontmost is true
            try
            return name of front window of frontApp
            on error
            return ""
            end try
            end tell
            """;
      return runAppleScript(script);
   }

   @Override
   public String getActiveWindowProcessName() {
      final var script = """
            tell application "System Events"
            set frontApp to first application process whose frontmost is true
            return name of frontApp
            end tell
            """;
      return runAppleScript(script);
   }

   private String runAppleScript(final String script) {
      final var processBuilder = new ProcessBuilder("osascript", "-e", script)
            .redirectErrorStream(true);

      try {
         final var process = processBuilder.start();
         final var finished = process.waitFor(COMMAND_TIMEOUT_SECONDS, TimeUnit.SECONDS);

         if (!finished) {
            process.destroyForcibly();
            return "";
         }

         final var output = readAll(process);
         if (process.exitValue() != 0) {
            return "";
         }

         return output.trim();
      } catch (final InterruptedException ex) {
         Thread.currentThread().interrupt();
         return "";
      } catch (final IOException ex) {
         return "";
      }
   }

   private String readAll(final Process process) throws IOException {
      final var builder = new StringBuilder();
      try (final var reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
         String line;
         while ((line = reader.readLine()) != null) {
            if (!builder.isEmpty()) {
               builder.append('\n');
            }
            builder.append(line);
         }
      }
      return builder.toString();
   }
}


