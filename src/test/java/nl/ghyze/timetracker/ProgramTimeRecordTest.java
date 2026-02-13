package nl.ghyze.timetracker;

import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.*;

public class ProgramTimeRecordTest {

    @Test
    public void testToFileString_formatsCorrectly() {
        Instant start = Instant.parse("2024-01-15T10:30:00Z");
        Instant end = Instant.parse("2024-01-15T10:35:30Z");

        ProgramTimeRecord record = new ProgramTimeRecord(
                start, end, "Document.txt - Notepad", "notepad.exe", 245, 12
        );

        String result = record.toFileString();

        // Format: start,end,process,title,keys,clicks
        String expected = start.toEpochMilli() + "," + end.toEpochMilli() +
                ",notepad.exe,Document.txt - Notepad,245,12";

        assertEquals(expected, result);
    }

    @Test
    public void testToFileString_handlesCommasInTitle() {
        Instant start = Instant.parse("2024-01-15T10:30:00Z");
        Instant end = Instant.parse("2024-01-15T10:35:00Z");

        ProgramTimeRecord record = new ProgramTimeRecord(
                start, end, "Email, Draft, Inbox - Outlook", "outlook.exe", 150, 8
        );

        String result = record.toFileString();

        // RFC 4180: Fields with commas should be quoted
        String expected = start.toEpochMilli() + "," + end.toEpochMilli() +
                ",outlook.exe,\"Email, Draft, Inbox - Outlook\",150,8";
        assertEquals(expected, result);
    }

    @Test
    public void testToFileString_handlesQuotesInTitle() {
        Instant start = Instant.parse("2024-01-15T10:30:00Z");
        Instant end = Instant.parse("2024-01-15T10:35:00Z");

        ProgramTimeRecord record = new ProgramTimeRecord(
                start, end, "Document \"Final\" Version", "word.exe", 100, 10
        );

        String result = record.toFileString();

        // RFC 4180: Quotes should be doubled and field should be quoted
        String expected = start.toEpochMilli() + "," + end.toEpochMilli() +
                ",word.exe,\"Document \"\"Final\"\" Version\",100,10";
        assertEquals(expected, result);
    }

    @Test
    public void testToFileString_handlesCommasInProcessName() {
        Instant start = Instant.parse("2024-01-15T10:30:00Z");
        Instant end = Instant.parse("2024-01-15T10:35:00Z");

        ProgramTimeRecord record = new ProgramTimeRecord(
                start, end, "Window Title", "process,name.exe", 50, 5
        );

        String result = record.toFileString();

        // Process names with commas should also be quoted
        String expected = start.toEpochMilli() + "," + end.toEpochMilli() +
                ",\"process,name.exe\",Window Title,50,5";
        assertEquals(expected, result);
    }

    @Test
    public void testToFileString_zeroInputCounts() {
        Instant start = Instant.parse("2024-01-15T10:30:00Z");
        Instant end = Instant.parse("2024-01-15T10:30:05Z");

        ProgramTimeRecord record = new ProgramTimeRecord(
                start, end, "Idle", "", 0, 0
        );

        String result = record.toFileString();

        assertTrue(result.endsWith(",0,0"));
    }

    @Test
    public void testToString_includesAllFields() {
        Instant start = Instant.parse("2024-01-15T10:30:00Z");
        Instant end = Instant.parse("2024-01-15T10:30:15Z");

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
        Instant start = Instant.parse("2024-01-15T10:30:00Z");
        Instant end = Instant.parse("2024-01-15T10:35:00Z");

        ProgramTimeRecord record = new ProgramTimeRecord(
                start, end, "Test Window", "test.exe", 100, 20
        );

        assertEquals(start, record.start());
        assertEquals(end, record.end());
        assertEquals("Test Window", record.windowTitle());
        assertEquals("test.exe", record.processName());
    }
}
