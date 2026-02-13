package nl.ghyze.timetracker;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProgramTimeRecordTest {

    @Test
    public void testToFileString_formatsCorrectly() {
        DateTime start = new DateTime(2024, 1, 15, 10, 30, 0, 0);
        DateTime end = new DateTime(2024, 1, 15, 10, 35, 30, 0);

        ProgramTimeRecord record = new ProgramTimeRecord(
                start, end, "Document.txt - Notepad", "notepad.exe", 245, 12
        );

        String result = record.toFileString();

        // Format: start,end,process,title,keys,clicks
        String expected = start.getMillis() + "," + end.getMillis() +
                ",notepad.exe,Document.txt - Notepad,245,12";

        assertEquals(expected, result);
    }

    @Test
    public void testToFileString_handlesCommasInTitle() {
        DateTime start = new DateTime(2024, 1, 15, 10, 30, 0, 0);
        DateTime end = new DateTime(2024, 1, 15, 10, 35, 0, 0);

        ProgramTimeRecord record = new ProgramTimeRecord(
                start, end, "Email, Draft, Inbox - Outlook", "outlook.exe", 150, 8
        );

        String result = record.toFileString();

        // RFC 4180: Fields with commas should be quoted
        String expected = start.getMillis() + "," + end.getMillis() +
                ",outlook.exe,\"Email, Draft, Inbox - Outlook\",150,8";
        assertEquals(expected, result);
    }

    @Test
    public void testToFileString_handlesQuotesInTitle() {
        DateTime start = new DateTime(2024, 1, 15, 10, 30, 0, 0);
        DateTime end = new DateTime(2024, 1, 15, 10, 35, 0, 0);

        ProgramTimeRecord record = new ProgramTimeRecord(
                start, end, "Document \"Final\" Version", "word.exe", 100, 10
        );

        String result = record.toFileString();

        // RFC 4180: Quotes should be doubled and field should be quoted
        String expected = start.getMillis() + "," + end.getMillis() +
                ",word.exe,\"Document \"\"Final\"\" Version\",100,10";
        assertEquals(expected, result);
    }

    @Test
    public void testToFileString_handlesCommasInProcessName() {
        DateTime start = new DateTime(2024, 1, 15, 10, 30, 0, 0);
        DateTime end = new DateTime(2024, 1, 15, 10, 35, 0, 0);

        ProgramTimeRecord record = new ProgramTimeRecord(
                start, end, "Window Title", "process,name.exe", 50, 5
        );

        String result = record.toFileString();

        // Process names with commas should also be quoted
        String expected = start.getMillis() + "," + end.getMillis() +
                ",\"process,name.exe\",Window Title,50,5";
        assertEquals(expected, result);
    }

    @Test
    public void testToFileString_zeroInputCounts() {
        DateTime start = new DateTime(2024, 1, 15, 10, 30, 0, 0);
        DateTime end = new DateTime(2024, 1, 15, 10, 30, 5, 0);

        ProgramTimeRecord record = new ProgramTimeRecord(
                start, end, "Idle", "", 0, 0
        );

        String result = record.toFileString();

        assertTrue(result.endsWith(",0,0"));
    }

    @Test
    public void testToString_includesAllFields() {
        DateTime start = new DateTime(2024, 1, 15, 10, 30, 0, 0);
        DateTime end = new DateTime(2024, 1, 15, 10, 30, 15, 0);

        ProgramTimeRecord record = new ProgramTimeRecord(
                start, end, "Chrome - Google", "chrome.exe", 50, 5
        );

        String result = record.toString();

        assertTrue(result.contains("10:30:00")); // Start time
        assertTrue(result.contains("Chrome - Google")); // Window title
        assertTrue(result.contains("chrome.exe")); // Process name
        assertTrue(result.contains("15 seconds")); // Duration
        assertTrue(result.contains("50 keys")); // Key count
        assertTrue(result.contains("5 clicks")); // Click count
    }

    @Test
    public void testGetters() {
        DateTime start = new DateTime(2024, 1, 15, 10, 30, 0, 0);
        DateTime end = new DateTime(2024, 1, 15, 10, 35, 0, 0);

        ProgramTimeRecord record = new ProgramTimeRecord(
                start, end, "Test Window", "test.exe", 100, 20
        );

        assertEquals(start, record.getStart());
        assertEquals(end, record.getEnd());
        assertEquals("Test Window", record.getWindowTitle());
        assertEquals("test.exe", record.getProcessName());
    }
}
