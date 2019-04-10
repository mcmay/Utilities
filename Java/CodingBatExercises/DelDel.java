/*
Given a string, if the string "del" appears starting at index 1, return a string where that "del" has been deleted. Otherwise, return the string unchanged.


delDel("adelbc") → "abc"
delDel("adelHello") → "aHello"
delDel("adedbc") → "adedbc"
*/

public class DelDel{
	 static String delDel (String str) {
	 	int idx = str.indexOf("del");
		
		if (idx != -1)
			return Character.toString(str.charAt(0)) + str.substring(4, str.length());
		return str;
	 }
    public static void main(String args[]){

	 String s = delDel("adelbc");
    System.out.println(s);
	 s = delDel("adelHello");
	 System.out.println(s);
    s = delDel("adedbc");
    System.out.println(s);
	 s = delDel("adel");
    System.out.println(s);
    }
}




