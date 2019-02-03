/* Given 2 int values, return true if one is negative and one is positive. Except if the parameter "negative" is true, then return true only if both are negative.


posNeg(1, -1, false) → true
posNeg(-1, 1, false) → true
posNeg(-4, -5, true) → true
*/

public class Main {
    public static boolean posNeg (int m, int n, boolean neg) {
        if (0 < m && n > 0 || m > 0 && 0 < n)
			return true;
		 if (neg && m < 0 && n < 0)
		 	return true;
		 
		 return false;
    }
	public static void main (String[] args) {
        boolean result;
        
        result = posNeg(1, -1, false);
        System.out.println(result);
        result = posNeg(-1, 1, false);
        System.out.println(result);
        result = posNeg(-1, -1, true);
        System.out.println(result);
    }
}
