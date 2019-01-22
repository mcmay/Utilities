/*The parameter weekday is true if it is a weekday, and the parameter vacation is true if we are on vacation. We sleep in if it is not a weekday or we're on vacation. Return true if we sleep in.

sleepIn(false, false) → true
sleepIn(true, false) → false
sleepIn(false, true) → true
*/
class SleepIn {
	public boolean sleepIn (boolean weekday, boolean vacation) {
		if (!weekday)
			return true;
		if (vacation)
			return true;
		
		return false;
	}
}
public class Main {

	public static void main (String[] args) {
	SleepIn slpin = new SleepIn();
	boolean tof;
	
	tof = slpin.sleepIn(false, false);
	System.out.println(tof);
	tof = slpin.sleepIn(true, false);
	System.out.println(tof);
	tof = slpin.sleepIn(false, true);
    System.out.println(tof);
	tof = slpin.sleepIn(true, true);
    System.out.println(tof);
	}
}
