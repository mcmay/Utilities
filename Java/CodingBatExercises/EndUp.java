/*
Given a string, return a new string where the last 3 chars are now in upper case. If the string has less than 3 chars, uppercase whatever is there. Note that str.toUpperCase() returns the uppercase version of a string.

endUp("Hello") → "HeLLO"
endUp("hi there") → "hi thERE"
endUp("hi") → "HI"
*/

public class EndUp{
    static String endUp (String str) {
		if (str.length() <= 3) {
			return str.toUpperCase();
		}
		int index = str.length() - 1 - 3;
		String front = str.substring(0, index);
		String end3 = str.substring(index, str.length() - 1 - 3);
		end3 = end3.toUpperCase();
		return front + end3;
	}
    public static void main(String args[]){
        String s = endUp("Hello");
        System.out.println(s);
		  s = endUp("hi there");
		  System.out.println(s);
        s = endUp("hi");
        System.out.println(s);
    }
}
