// Process Kindle DX Notes & Highlights records
// typical record type and location line: - Highlight Loc. 2758 | Added on Tuesday, January 15, 2019, 01:25 AM
import java.io.BufferedReader;
import java.nio.file.*;
import java.util.*;
import java.nio.charset.Charset;
import java.io.*;

class Record {
    //private String type;
    //private String location;
    private String text;

    public Record (/*String type, String location,*/ String text) {
            //this.type = type;
            //this.location = location;
            this.text = text;
    }
    /*public String getType( ) {
        return type;
    }*/
    /* public String getLocation ( ) {
            return location;
    }*/
    public String getText ( ) {
            return text;
    }
    /*public void setType (String type) {
            this.type = type;
    }*/
    /*public void setLocation (String location) {
            this.location = location;
    }*/
    public void setText (String text) {
            this.text = text;
    }
}

public class KindleHighlightsAndNotesProcessor {

	final static String HIGHLIGHT = "Highlight";
	final static String NOTE = "Note";

    public static void main(String[] args) {

        if (args.length < 1) {
                System.err.println("Usage: <program> <parameter>");
        }
        else {
            Path pathForReadRecords = FileSystems.getDefault().getPath(".", "My Clippings.txt");
            Path pathForReadLocation = FileSystems.getDefault().getPath(".", (args[0] + "_Loc" + ".txt"));
            Charset charset = Charset.forName("utf8");
            List <String> textStrings = new ArrayList<String>();
	        List <String> typeStrings = new ArrayList<String>();
            String line = null;
	        String type = null;
	        String currentLocation = null;
            String lastLocation = null;
            int startIndex, endIndex;
            boolean lastLocationFound = false;
            boolean pastLastLocationRecord = false;
            Record rcd = new Record(/*"Highlight",*/ "noText");

            File locFile = new File(args[0] + "_Loc" + ".txt");
            boolean fileExists = locFile.exists();
            if (fileExists) {
                try(BufferedReader reader = Files.newBufferedReader(pathForReadLocation, charset)) {
                    while ((line =  reader.readLine()) != null) {
                        lastLocation = line;  // read to the end of file to locate the last location (Loc. of last time)
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
                        // typical book title line of a record: Mastering Operating System Concepts (10th edition) -- this is a made-up title
                      if ((line = reader.readLine()) != null && (line.indexOf(HIGHLIGHT) != -1 || line.indexOf(NOTE) != -1)) {

			    		if (line.indexOf(NOTE) != -1)
				    		type = NOTE;
						else if (line.indexOf(HIGHLIGHT) != -1)
							type = HIGHLIGHT;
						typeStrings.add(type);
                        // skip the space after the . in Loc.
                        startIndex = line.indexOf("Loc.") + "Loc.".length() + 1;
                        endIndex = line.indexOf('|') - 1; // back-skip the space before |
                        //rcd.setLocation(line.substring(startIndex, endIndex));
			    		currentLocation = line.substring(startIndex, endIndex);
			    
			    		if (lastLocation != null && lastLocationFound == false) { 
                        // seek the location of the last record of last time
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
                        // Skip empty lines after record type and location line
                        while ((line = reader.readLine()) != null && line.length() == 0)
                            continue;
                        if (line == null)
                            break;

                        rcd.setText(line);
                        textStrings.add(rcd.getText());
                        }
                    }
                } 
            } catch (IOException x) {
                System.err.format("IOException: %s%n", x);
              }     

            // Write text section of a suitable record to a txt file
            Path pathForWriteRecords = FileSystems.getDefault().getPath(".", (args[0] + ".txt"));
            try (BufferedWriter writer = Files.newBufferedWriter(pathForWriteRecords, charset, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
				String typeLine = null;
				String textLine = null;
                GregorianCalendar calendar = new GregorianCalendar();
                Date date = calendar.getTime();
                String dateString = date.toString();

                // date the record
                writer.write("\r\n", 0, "\r\n".length());
                writer.write(dateString);
                writer.write("\r\n", 0, "\r\n".length());

                for (int i = 0; i < textStrings.size(); i++) {
                    textLine = textStrings.get(i);
					typeLine = typeStrings.get(i);

                    writer.write("\r\n", 0, "\r\n".length());
					if (typeLine.equals(NOTE)) {
						writer.write(typeLine, 0, typeLine.length());
                        writer.write("\r\n", 0, "\r\n".length());
                    }
                    writer.write(textLine, 0, textLine.length());
                    writer.write("\r\n", 0, "\r\n".length());
                } 
            } catch (IOException x) {
                    System.err.format("IOException: %s%n", x);
            }
            // Write last location of a particular series of records to a txt file
            Path pathForLocation = FileSystems.getDefault().getPath(".", (args[0] + "_Loc" + ".txt"));
            try (BufferedWriter writer = Files.newBufferedWriter(pathForLocation, charset, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
               writer.write(currentLocation);
                writer.write("\r\n", 0, "\r\n".length());
            } catch (IOException x) {
                    System.err.format("IOException: %s%n", x);
            }
        }
    }
}
