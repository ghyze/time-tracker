package nl.ghyze.timetracker.windows;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.PointerByReference;

import nl.ghyze.timetracker.ActiveWindow;

public class ActiveWindowWin32 implements ActiveWindow
{
   private static final int MAX_TITLE_LENGTH = 1024;

   public String getActiveWindowTitle()
   {
      char[] buffer = new char[MAX_TITLE_LENGTH * 2];
      HWND foregroundWindow = User32DLL.GetForegroundWindow();
      User32DLL.GetWindowTextW(foregroundWindow, buffer, MAX_TITLE_LENGTH);
      String title = Native.toString(buffer);
      return title;
   }

   public String getActiveWindowProcessName()
   {
      char[] buffer = new char[MAX_TITLE_LENGTH * 2];
      PointerByReference pointer = new PointerByReference();
      HWND foregroundWindow = User32DLL.GetForegroundWindow();
      User32DLL.GetWindowThreadProcessId(foregroundWindow, pointer);
      Pointer process = Kernel32.OpenProcess(Kernel32.PROCESS_QUERY_INFORMATION | Kernel32.PROCESS_VM_READ, false, pointer.getValue());
      Psapi.GetModuleBaseNameW(process, null, buffer, MAX_TITLE_LENGTH);
      String processName = Native.toString(buffer);
      return processName;
   }

}
