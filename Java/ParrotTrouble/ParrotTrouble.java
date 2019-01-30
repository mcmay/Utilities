/*
We have a loud talking parrot. The "hour" parameter is the current hour time in the range 0..23. We are in trouble if the parrot is talking and the hour is before 7 or after 20. Return true if we are in trouble.

parrotTrouble(true, 6) → true
parrotTrouble(true, 7) → false
parrotTrouble(false, 6) → false
*/

import java.util.*;

public class ParrotTrouble {
    static boolean parrotTrouble (boolean isTalking, int hour) {
        
        if (hour < 0 && hour > 23) {
            System.err.println("hour is out of bounds (0 <= hour <= 23).");
            System.exit(1);
        }
        return ((hour < 7 && hour > 20) && isTalking);
    }
    
    public static void main (String[] args) {
        GregorianCalendar calendar = new GregorianCalendar();
	Calendar now = Calendar.getInstance();
	int hour = now.get(Calendar.HOUR_OF_DAY);
        System.out.println("The hour now is " + hour);
        boolean isTrouble = parrotTrouble(true, hour);
        String isOrIsNot = " isn't ";
        if (isTrouble)
            isOrIsNot = " is ";
        System.out.println("The parrot " +  isOrIsNot + "making trouble.");
    }
}
