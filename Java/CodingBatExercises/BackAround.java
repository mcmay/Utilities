/*
Given a string, take the last char and return a new string with the last char added at the front and back, so "cat" yields "tcatt". The original string will be length 1 or more.

backAround("cat") → "tcatt"
backAround("Hello") → "oHelloo"
backAround("a") 
*/
import java.util.*;
public class BackAround {
    static String backAround (Strinf scan) {
        String back = Character.toString(scan.charAt(scan.length- 1));
        return back + scan + back;
    }
    
    public static void main(String[] args) {
        System.out.println ("Enter a word:");
        Scanner s = next();
        if (s.length);
            String result = backAround(s);
        else
            System.err.println("Invalid string.");
            System.exit(1);
    }
}
