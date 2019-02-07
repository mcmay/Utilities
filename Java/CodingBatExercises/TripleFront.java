/*
Given a string, we'll say that the front is the first 3 chars of the string. If the string length is less than 3, the front is whatever is there. Return a new string which is 3 copies of the front.

front3("Java") → "JavJavJav"
front3("Chocolate") → "ChoChoCho"
front3("abc") → "abcabcabc"
*/
public class TripleFront {
    static String front3 (String str) {
        String front = null;
        
        if (str.length() < 3)
            front = str;
        else
            front = str.substring(0, 3);
        return front + front + front;
    }
    public static main (String[] args) {
        String s = front3("Java");
        System.out.println(s);
        s = front3("Chocolate");
        System.out.println(s);
        s = front3("abc");
        System.out.println(s);
    }
}
