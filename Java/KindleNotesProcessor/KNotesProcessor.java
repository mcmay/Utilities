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
    /*public void displayRecord ( ) {
            System.out.println(location);
            System.out.println(text);
    }*/
}

public class KNotesProcessor {

    public static void main(String[] args) {

        if (args.length < 1) {
                System.err.println("Usage: <program> <parameter>");
        }
        else {
            Path pathForRead = FileSystems.getDefault().getPath(".", "My Clippings.txt");
            Charset charset = Charset.forName("utf8");
            List <String> strings = new ArrayList<String>();
            String line = null;
            String dbg = null;
            Record rcd = new Record("noLocation", "noText");

            try(BufferedReader reader = Files.newBufferedReader(pathForRead, charset)){
               while ((line = reader.readLine()) != null) {
                   if (line.indexOf(args[0]) != -1) {
                      if ((line = reader.readLine()) != null && line.indexOf("Highlight") != -1) {
                           int startIndex, endIndex;
                           // skip the space after the . in Loc.
                            startIndex = line.indexOf("Loc.") + "Loc.".length() + 1;
                            endIndex = line.indexOf('|') - 1; // back-skip the space before |
                            rcd.setLocation(line.substring(startIndex, endIndex));
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
            Path pathForWrite = FileSystems.getDefault().getPath(".", (args[0] + ".txt"));
            try (BufferedWriter writer = Files.newBufferedWriter(pathForWrite, charset, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

                for (int i = 0; i < strings.size(); i++) {
                    line = strings.get(i);
                    writer.write(line, 0, line.length());
                } 
            } catch (IOException x) {
                    System.err.format("IOException: %s%n", x);
            }
        }
    }
}
