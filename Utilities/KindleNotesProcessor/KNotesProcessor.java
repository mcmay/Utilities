// Process Kindle DX Notes
import java.io.BufferedReader;
import java.nio.file.*;
import java.util.*;
import java.nio.charset.Charset;
import java.io.*;

class Record {
    private String location;
    private String text;

    public Record (String location, String text) {
            this.location = location;
            this.text = text;
    }
    public String getLocation ( ) {
            return location;
    }
    public String getText ( ) {
            return text;
    }
    public void setLocation (String location) {
            this.location = location;
    }
    public void setText (String text) {
            this.text = text;
    }
}

public class KNotesProcessor {

    public static void main(String[] args) {

        if (args.length < 1) {
                System.err.println("Usage: <program> <parameter>");
        }
        else {
            Path pathForReadRecords = FileSystems.getDefault().getPath(".", "My Clippings.txt");
            Path pathForReadLocation = FileSystems.getDefault().getPath(".", ("Location_" + args[0] + ".txt"));
            Charset charset = Charset.forName("utf8");
            List <String> strings = new ArrayList<String>();
            String line = null;
            String lastLocation = null;
            boolean lastLocationFound = false;
            boolean pastLastLocationRecord = false;
            Record rcd = new Record("noLocation", "noText");

            File locFile = new File("Location_" + args[0] + ".txt");
            boolean fileExists = locFile.exists();
            if (fileExists) {
                try(BufferedReader reader = Files.newBufferedReader(pathForReadLocation, charset)) {
                    while ((line =  reader.readLine()) != null) {
                        lastLocation = line;
                    }
                } catch (IOException x) {
                    System.err.format("IOException: %s%n", x);
                }
            }
            else {
                    System.err.println("Location file not found. Creating a new file.");
            }
            // Reading through My Clippings.txt file
            try(BufferedReader reader = Files.newBufferedReader(pathForReadRecords, charset)){
               while ((line = reader.readLine()) != null) {
                   if (line.indexOf(args[0]) != -1) {
                      if ((line = reader.readLine()) != null && line.indexOf("Highlight") != -1) {
                           int startIndex, endIndex;
                           // skip the space after the . in Loc.
                            startIndex = line.indexOf("Loc.") + "Loc.".length() + 1;
                            endIndex = line.indexOf('|') - 1; // back-skip the space before |
                            rcd.setLocation(line.substring(startIndex, endIndex));
			                 if (lastLocation != null && lastLocationFound == false) { // seek the location of the last record of last time
				                String currentLocation = rcd.getLocation();
				                if (currentLocation.equals(lastLocation) == false)
					               continue;
                               else
                                    lastLocationFound = true;
                               
                               // skip the last lines of the LastLocation block below the last Loc. e.g. line feed and text
                               if ((line = reader.readLine()) != null && line.indexOf("==========") == -1 
                                    && pastLastLocationRecord == false)
                                    continue;
                               else    
                                    pastLastLocationRecord = true;
                            }

                            while ((line = reader.readLine()) != null && line.length() == 0)
                                continue;
                            if (line == null)
                                break;

                            rcd.setText(line);
                            strings.add(rcd.getText());
                        }
                    }
                } 
            } catch (IOException x) {
                System.err.format("IOException: %s%n", x);
              }     
            Path pathForWriteRecords = FileSystems.getDefault().getPath(".", (args[0] + ".txt"));
            try (BufferedWriter writer = Files.newBufferedWriter(pathForWriteRecords, charset, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                GregorianCalendar calendar = new GregorianCalendar();
                Date date = calendar.getTime();
                String dateString = date.toString();

                // date the record
                writer.write("\r\n", 0, "\r\n".length());
                writer.write(dateString);
                writer.write("\r\n", 0, "\r\n".length());

                for (int i = 0; i < strings.size(); i++) {
                    line = strings.get(i);
                    writer.write(line, 0, line.length());
                    writer.write("\r\n", 0, "\r\n".length());
                } 
            } catch (IOException x) {
                    System.err.format("IOException: %s%n", x);
            }
            Path pathForLocation = FileSystems.getDefault().getPath(".", ("Location_" + args[0] + ".txt"));
            try (BufferedWriter writer = Files.newBufferedWriter(pathForLocation, charset, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
               writer.write(rcd.getLocation()); 
                writer.write("\r\n", 0, "\r\n".length());
            } catch (IOException x) {
                    System.err.format("IOException: %s%n", x);
            }
        }
    }
}
