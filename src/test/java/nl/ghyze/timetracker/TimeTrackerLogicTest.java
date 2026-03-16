package nl.ghyze.timetracker;

import nl.ghyze.TimeTracker;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for TimeTracker logic that can be tested without native bindings.
 *
 * Note: Testing TimeTracker is limited because:
 * - ConfigurationService hardcodes config file path
 * - InputCounter uses native hooks
 * - ScheduledExecutorService runs asynchronously
 *
 * This demonstrates the principle of hand-written mocks over frameworks.
 * In practice, TimeTracker would need refactoring for better testability
 * (dependency injection of ActiveWindow, ConfigurationService, etc.)
 */
public class TimeTrackerLogicTest {

    @Test
    public void testMockActiveWindow_behavesCorrectly() {
        MockActiveWindow mock = new MockActiveWindow();

        assertEquals("Initial Window", mock.getActiveWindowTitle());
        assertEquals("initial.exe", mock.getActiveWindowProcessName());

        mock.setWindow("notepad.exe", "Document.txt - Notepad");

        assertEquals("Document.txt - Notepad", mock.getActiveWindowTitle());
        assertEquals("notepad.exe", mock.getActiveWindowProcessName());
    }

    @Test
    public void testMockActiveWindow_canSimulateWindowChanges() {
        MockActiveWindow mock = new MockActiveWindow();

        // Simulate user switching windows
        mock.setWindow("chrome.exe", "Google - Chrome");
        String title1 = mock.getActiveWindowTitle();

        mock.setWindow("notepad.exe", "Document.txt - Notepad");
        String title2 = mock.getActiveWindowTitle();

        assertNotEquals(title1, title2);
        assertEquals("Google - Chrome", title1);
        assertEquals("Document.txt - Notepad", title2);
    }

    /*
     * Note: To properly test TimeTracker, we would need to refactor it to:
     * 1. Accept ActiveWindow as constructor parameter (dependency injection)
     * 2. Accept ConfigurationService as constructor parameter
     * 3. Make the scheduler testable (or extract scheduling logic)
     *
     * This is intentionally not done to keep the production code simple,
     * but demonstrates the trade-off between testability and simplicity.
     */
}
