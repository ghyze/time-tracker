package nl.ghyze;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import nl.ghyze.timetracker.ActiveWindow;
import nl.ghyze.timetracker.ProgramTimeRecord;
import nl.ghyze.timetracker.windows.ActiveWindowWin32;

import org.joda.time.DateTime;

public class TimeTracker implements Runnable
{

   ActiveWindow activeWindow;
   String lastTitle   = "none";
   String lastProcess = "none";
   long lastChange    = System.currentTimeMillis();
   private List<ProgramTimeRecord> records;
   private File file;
   private FileWriter writer;
   
   private long lastCheck = 0l;
   
   public TimeTracker(){
      activeWindow = new ActiveWindowWin32();
      records = new ArrayList<ProgramTimeRecord>();
   }
   
   public void run(){

         while (true)
         {
           check();
            try
            {
               Thread.sleep(1000);
            }
            catch (InterruptedException ex)
            {
               // ignore
            }
         }
   }
   
   private void check(){
      long current = System.currentTimeMillis();
      if (!lastTitle.equals(activeWindow.getActiveWindowTitle())) {
         writeRecord();
      } else if (lastCheck > 0 && lastCheck < current - 3000){
         lastProcess = "";
         lastTitle   = "";
         writeRecord();
      }
      lastCheck = current;
   }

   
   private void writeRecord(){
      long change = System.currentTimeMillis();
      
      ProgramTimeRecord record = new ProgramTimeRecord(new DateTime(lastChange), new DateTime(change), lastTitle, lastProcess);
      records.add(record);
      writeToFile(record);
      
      lastChange = change;
      System.out.println(record);
      lastTitle = activeWindow.getActiveWindowTitle();
      lastProcess = activeWindow.getActiveWindowProcessName();
   }
   
   private void writeToFile(ProgramTimeRecord record){
      String fileName = record.getStart().toString("yyyyMMdd")+".csv";
      if (file == null || !file.getName().equals(fileName)){
         file = new File(fileName);
         try {
            if (writer != null){
               writer.close();
            }
         } catch (Exception ex){
            
         } finally {
            writer = null;
         }
      }
      
      if (writer == null){
         try {
            writer = new FileWriter(file, true);
            if (file.length() == 0){
               writer.append("start, end, title, process\r\n");
               writer.flush();
            }
         } catch (Exception ex){
            ex.printStackTrace();
         }
      }
      
      if (writer != null) {
         try {
            writer.append(record.toFileString()+"\r\n");
            writer.flush();
         } catch (Exception ex){
            
         }
      }
   }
   
   public static void main(String[] args){
      TimeTracker tracker = new TimeTracker();
      tracker.run();
   }
}
