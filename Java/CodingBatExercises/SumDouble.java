/*
Given two int values, return their sum. Unless the two values are the same, then return double their sum.


sumDouble(1, 2) → 3
(3, 2) → 5
sumDouble(2, 2) → 8
*/
public class SumDouble {
    public static int sumDouble(int a,  int b) {
        return a == b? (a + b) : (a + b) * 2;
    }
    
   public static void main (String[] args) {
       int sum;
       
        sum = sumDouble(1, 2);
        System.out.println(sum);
        sum = sumDouble(3, 2);
        System.out.println(sum);
        sum = sumDouble(2, 2);
        System.out.println(sum);
    }
}
