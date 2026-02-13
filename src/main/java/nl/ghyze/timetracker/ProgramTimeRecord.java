package nl.ghyze.timetracker;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Immutable record representing a time tracking entry.
 * Records the active window, process, and input activity for a time period.
 */
public record ProgramTimeRecord(
      Instant start,
      Instant end,
      String windowTitle,
      String processName,
      int keys,
      int clicks
) {

   public String toFileString() {
      return start.toEpochMilli() + "," + end.toEpochMilli() + "," +
             escapeCsv(processName) + "," + escapeCsv(windowTitle) + "," +
             keys + "," + clicks;
   }

   @Override
   public String toString() {
      Duration duration = Duration.between(start, end);
      DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            .withZone(ZoneId.of("UTC"));
      return timeFormatter.format(start) + ": [" + windowTitle + "],[" + processName +
             "],[" + duration.getSeconds() + " seconds, " + keys + " keys, " +
             clicks + " clicks]";
   }

   /**
    * Escapes a field for CSV output according to RFC 4180.
    * Fields containing commas, quotes, or newlines are quoted.
    * Quotes within fields are escaped by doubling them.
    */
   private static String escapeCsv(String field) {
      if (field == null || field.isEmpty()) {
         return field;
      }

      // Check if field needs quoting (contains comma, quote, newline, or carriage return)
      boolean needsQuoting = field.contains(",") || field.contains("\"") ||
                             field.contains("\n") || field.contains("\r");

      if (needsQuoting) {
         // Escape quotes by doubling them
         String escaped = field.replace("\"", "\"\"");
         return "\"" + escaped + "\"";
      }

      return field;
   }
}
