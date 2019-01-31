
public class NearHundred {
    public static boolean nearHundred (int n){
        int value1 = Math.abs(n - 100);
        int value2 = Math.abs(n - 200);
        return (0 < value1 && value1 < 10) || (0 < value2 && value2 < 10);
    }
    public static void main (String[] args) {
        boolean result;
        
        result = nearHundred(193);
        System.out.println(result);
        result = nearHundred(91);
        System.out.println(result);
        result = nearHundred(89);
        System.out.println(result);
    }
}
