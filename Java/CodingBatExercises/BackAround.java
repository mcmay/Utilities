/*
Given a string, take the last char and return a new string with the last char added at the front and back, so "cat" yields "tcatt". The original string will be length 1 or more.

backAround("cat") → "tcatt"
backAround("Hello") → "oHelloo"
backAround("a") 
*/
import java.util.*;

public class BackAround {
    static String backAround (String scan) {
        String back = Character.toString(scan.charAt(scan.length()- 1));
        return back + scan + back;
    }
    
    public static void main(String[] args) {
	
        System.out.println ("Enter a word:");
        Scanner s = new Scanner(System.in);
	String str = s.next();
        if (str.length() > 0) {
		System.out.println(backAround(str));
		}
        else {
            System.err.println("Invalid string.");
            System.exit(1);
		}
    }
}
