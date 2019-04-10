/*
  Given an int n, return the absolute difference between n and 21, except return double the absolute difference if n is over 21.

diff21(19) → 2
diff21(10) → 11
diff21(21) → 0
*/

public class Diff21 {
    public static int diff21 (int n) {
        int diff = n - 21;
        
        if (n < 21)
            diff = -diff;
        else
            diff *= 2;
        
        return diff;
    }
    
    public static void main (String[] args) {
        int result;
        
        result = diff21(19);
        System.out.println(result);
        result = diff21(10);
        System.out.println(result);
        result = diff21(21);
        System.out.println(result);
    }
}
