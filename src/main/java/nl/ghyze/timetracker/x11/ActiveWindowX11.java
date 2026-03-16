package nl.ghyze.timetracker.x11;

import nl.ghyze.timetracker.ActiveWindow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ActiveWindowX11 implements ActiveWindow {

   private static final long COMMAND_TIMEOUT_SECONDS = 2L;
   private static final String XPROP_COMMAND = "xprop";
   private static final Pattern WINDOW_ID_PATTERN = Pattern.compile("0x[0-9a-fA-F]+");
   private static final Pattern PID_PATTERN = Pattern.compile("=\\s*(\\d+)");
   private static final Pattern QUOTED_VALUE_PATTERN = Pattern.compile("\"([^\"]*)\"");

   @Override
   public String getActiveWindowTitle() {
      final var windowId = getActiveWindowId();
      if (windowId.isEmpty()) {
         return "";
      }

      final var utf8Title = runCommand(XPROP_COMMAND, "-id", windowId, "_NET_WM_NAME");
      final var title = parseQuotedValue(utf8Title);
      if (!title.isEmpty()) {
         return title;
      }

      final var legacyTitle = runCommand(XPROP_COMMAND, "-id", windowId, "WM_NAME");
      return parseQuotedValue(legacyTitle);
   }

   @Override
   public String getActiveWindowProcessName() {
      final var windowId = getActiveWindowId();
      if (windowId.isEmpty()) {
         return "";
      }

      final var pidOutput = runCommand(XPROP_COMMAND, "-id", windowId, "_NET_WM_PID");
      final var pid = parsePid(pidOutput);
      if (!pid.isEmpty()) {
         final var processNameFromProc = readProcessNameFromProc(pid);
         if (!processNameFromProc.isEmpty()) {
            return processNameFromProc;
         }

         final var processNameFromPs = runCommand("ps", "-p", pid, "-o", "comm=").trim();
         if (!processNameFromPs.isEmpty()) {
            return processNameFromPs;
         }
      }

      final var wmClass = runCommand(XPROP_COMMAND, "-id", windowId, "WM_CLASS");
      return parseLastQuotedValue(wmClass);
   }

   private String getActiveWindowId() {
      final var output = runCommand(XPROP_COMMAND, "-root", "_NET_ACTIVE_WINDOW");
      final var matcher = WINDOW_ID_PATTERN.matcher(output);
      if (matcher.find()) {
         return matcher.group();
      }
      return "";
   }

   private String parsePid(final String output) {
      final var matcher = PID_PATTERN.matcher(output);
      if (matcher.find()) {
         return matcher.group(1);
      }
      return "";
   }

   private String parseQuotedValue(final String output) {
      final var firstQuote = output.indexOf('"');
      final var lastQuote = output.lastIndexOf('"');
      if (firstQuote >= 0 && lastQuote > firstQuote) {
         return output.substring(firstQuote + 1, lastQuote);
      }
      return "";
   }

   private String parseLastQuotedValue(final String output) {
      final var matcher = QUOTED_VALUE_PATTERN.matcher(output);
      String value = "";
      while (matcher.find()) {
         value = matcher.group(1);
      }
      return value;
   }

   private String readProcessNameFromProc(final String pid) {
      final var path = Path.of("/proc", pid, "comm");
      try {
         if (Files.exists(path)) {
            return Files.readString(path, StandardCharsets.UTF_8).trim();
         }
      } catch (final IOException ex) {
         return "";
      }
      return "";
   }

   private String runCommand(final String... command) {
      final var processBuilder = new ProcessBuilder(command).redirectErrorStream(true);

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



