/*
Given a string, take the last char and return a new string with the last char added at the front and back, so "cat" yields "tcatt". The original string will be length 1 or more.

backAround("cat") → "tcatt"
backAround("Hello") → "oHelloo"
backAround("a") 
*/
import java.util.*;

public class Main {
    static String backAround (String scan) {
        String back = Character.toString(scan.charAt(scan.length- 1));
        return back + scan + back;
    }
    
    public static void main(String[] args) {
		String result = null;
	
        System.out.println ("Enter a word:");
        Scanner s = new Scanner(System.in);
		    s.next();
        if (s.length) {
			result = backAround(s);
			System.out.println(s);
		}
        else {
            System.err.println("Invalid string.");
            System.exit(1);
		}
    }
}
