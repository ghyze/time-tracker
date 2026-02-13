package nl.ghyze.timetracker;

import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * Immutable record representing a time tracking entry.
 * Records the active window, process, and input activity for a time period.
 */
public record ProgramTimeRecord(
      DateTime start,
      DateTime end,
      String windowTitle,
      String processName,
      int keys,
      int clicks
) {

   public String toFileString() {
      return start.getMillis() + "," + end.getMillis() + "," +
             escapeCsv(processName) + "," + escapeCsv(windowTitle) + "," +
             keys + "," + clicks;
   }

   @Override
   public String toString() {
      Duration duration = new Duration(start, end);
      return start.toString("HH:mm:ss") + ": [" + windowTitle + "],[" + processName +
             "],[" + duration.getStandardSeconds() + " seconds, " + keys + " keys, " +
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
