package nl.ghyze.timetracker;

import org.joda.time.DateTime;
import org.joda.time.Duration;

public class ProgramTimeRecord
{
   private DateTime start;
   private DateTime end;
   private String windowTitle;
   private String processName;
   
   public ProgramTimeRecord(DateTime start, DateTime end, String windowTitle, String processName){
      this.start = start;
      this.end = end;
      this.windowTitle = windowTitle;
      this.processName = processName;
   }

   public DateTime getStart()
   {
      return start;
   }

   public DateTime getEnd()
   {
      return end;
   }

   public String getWindowTitle()
   {
      return windowTitle;
   }

   public String getProcessName()
   {
      return processName;
   }
   
   public String toFileString(){
      return start.getMillis()+","+end.getMillis()+","+processName+","+windowTitle;
   }
   
   public String toString(){
      Duration duration = new Duration(start, end);
      return start.toString("HH:mm:ss")+": ["+windowTitle+"],["+processName+"],["+duration.getStandardSeconds()+" seconds]";
   }
}
