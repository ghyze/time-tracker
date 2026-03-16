package nl.ghyze.timetracker;

/**
 * Simple mock implementation of ActiveWindow for testing.
 * No framework needed - just a hand-written test double.
 */
public class MockActiveWindow implements ActiveWindow {

    private String windowTitle;
    private String processName;

    public MockActiveWindow() {
        this.windowTitle = "Initial Window";
        this.processName = "initial.exe";
    }

    @Override
    public String getActiveWindowTitle() {
        return windowTitle;
    }

    @Override
    public String getActiveWindowProcessName() {
        return processName;
    }

    // Test helper methods
    public void setWindowTitle(String title) {
        this.windowTitle = title;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public void setWindow(String processName, String title) {
        this.processName = processName;
        this.windowTitle = title;
    }
}
