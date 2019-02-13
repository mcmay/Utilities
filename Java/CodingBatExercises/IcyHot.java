/*
Given two temperatures, return true if one is less than 0 and the other is greater than 100.

icyHot(120, -1) → true
icyHot(-1, 120) → true
icyHot(2, 120) → false
*/
public class IcyHot {
    static boolean icyHot (int temp1, temp2) {
        return (temp1 < 0 || temp1 > 100) && (temp2 < 0 || temp2 > 100);
    }
    public static void main (String[] args) {
        boolean b = icyHot(120, -1);
        System.out.println(b);
        boolean b = icyHot(-1, 120);
        System.out.println(b);
        boolean b = icyHot(2, 120);
        System.out.println(b);
    }
}
