/*
Given a non-empty string and an int n, return a new string where the char at index n has been removed. The value of n will be a valid index of a char in the original string (i.e. n will be in the range 0..str.length()-1 inclusive).


missingChar("kitten", 1) → "ktten"
missingChar("kitten", 0) → "itten"
missingChar("kitten", 4) → "kittn"
*/

public class Main {
    public static String missingChar (String str, int n) {
        if (str.length() == 0 || n > str.length() - 1 || n < 0) {
            System.err.println("str is an empty string or n is of an invalid the length.");
            System.exit(1);
        }
        return str.substring(0, n) + str.substring(n, str.length() - 1);
    }
    public static void main(String[] args) {

	String s = missingChar("kitten", 1);
	System.out.println(s);
	s = missingChar("kitten", 0);
	System.out.println(s);
	s = missingChar("kitten", 4);
	System.out.println(s);
    }
}
