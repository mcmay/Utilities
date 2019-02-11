/*
Given a string, take the first 2 chars and return the string with the 2 chars added at both the front and back, so "kitten" yields"kikittenki". If the string length is less than 2, use whatever chars are there.

front22("kitten") → "kikittenki"
front22("Ha") → "HaHaHa"
front22("abc") 
*/
public class FrontTwoChars {
    static String frontTwoChars (String str) {
        String front = null;
        
        if (str.length() <= 2 && str.length() > 0)
            front = str;
        else if (str.length() > 2) {
            front = str.substring(0, 2);
        else {
            System.err.println("Invalid string.");
            }
        }
    }
}
