/*
Given a string, return true if the string starts with "hi" and false otherwise.

startHi("hi there") → true
startHi("hi") → true
startHi("hello hi") → false
*/

public class StartWithHi {
    public static boolean startWithHi (String s) {
        String front = null;
        if (s.length() > 2) {
            front = s.substring(0, 2);
            return front == "hi";
        }
        return false;
    }
    public static void main (String[] args) {
        System.out.println(startWithHi("hi, there.");
    }
        System.out.println(startWithHi("hi");
        System.out.println(startWithHi("hello, hi");
}
