package nl.ghyze.timetracker;

import nl.ghyze.timetracker.macos.ActiveWindowMacOS;
import nl.ghyze.timetracker.windows.ActiveWindowWin32;
import nl.ghyze.timetracker.x11.ActiveWindowX11;

public final class ActiveWindowFactory {

   private ActiveWindowFactory() {
      // Utility class
   }

   public static ActiveWindow create() {
      final var osName = System.getProperty("os.name", "").toLowerCase();

      if (osName.contains("win")) {
         return new ActiveWindowWin32();
      }

      if (osName.contains("mac")) {
         return new ActiveWindowMacOS();
      }

      if (osName.contains("linux")) {
         final var sessionType = System.getenv("XDG_SESSION_TYPE");
         final var hasX11Display = System.getenv("DISPLAY") != null;

         if ("x11".equalsIgnoreCase(sessionType) || hasX11Display) {
            return new ActiveWindowX11();
         }

         throw new UnsupportedOperationException(
               "Linux active-window tracking currently supports X11 sessions only."
         );
      }

      throw new UnsupportedOperationException("Unsupported operating system: " + osName);
   }
}


