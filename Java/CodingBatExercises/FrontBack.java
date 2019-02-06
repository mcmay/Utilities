/*
Given a string, return a new string where the first and last chars have been exchanged.

frontBack("code") → "eodc"
frontBack("a") → "a"
frontBack("ab") → "ba"
*/
public class Main {
    static String frontBack (String str) {
        if (str.length() < 1) {
            System.err.println("Invalid string length");
            System.exit(1);
        }
        else if (str.length() == 1) {
            return str;
        }
        else if (str.length() == 2) {
            char front = str.charAt(0);
			 char back = str.charAt(1);
			 
			 String s = back + front;
			 
			 return s;
        }
        else {
            char front = str.charAt(0);
            String substr = str.substring(1, str.length() - 1);
            char back = str.charAt(str.length() - 1);
            return back + substr + front;
        }
    }
    public static void main (String[] args) {
        String s = frontBack("code");
        System.out.println(s);
        s = frontBack("a");
        System.out.println(s);
        s = frontBack("ab"); 
        System.out.println(s);
    }
}
