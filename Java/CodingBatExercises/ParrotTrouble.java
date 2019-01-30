
/*
We have a loud talking parrot. The "hour" parameter is the current hour time in the range 0..23. We are in trouble if the parrot is talking and the hour is before 7 or after 20. Return true if we are in trouble.

parrotTrouble(true, 6) → true
parrotTrouble(true, 7) → false
parrotTrouble(false, 6) → false
*/

public class ParrotTrouble {
    static void parrotTrouble (boolean isTalking, int hour) {
        boolean isTroubleHour = false;
        
        if (hour < 0 && hour > 23) {
            System.err.println("hour is out of bounds (0 <= hour <= 23).");
            System.exit();
        }
    }
}
