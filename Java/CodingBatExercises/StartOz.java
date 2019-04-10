/*
Given a string, return a string made of the first 2 chars (if present), however include first char only if it is 'o' and include the second only if it is 'z', so "ozymandias" yields "oz".

startOz("ozymandias") → "oz"
startOz("bzoo") → "z"
startOz("oxx") → "o"
*/

public class StartOz {
   static String startOz (String str) {
   	String frontTwoChars = str.substring(0, 2);
	   String front = null;
	   
	   String firstChar = Character.toString(frontTwoChars.charAt(0));
	   String secondChar = Character.toString(frontTwoChars.charAt(1));
	   if (firstChar.equals("o"))
	       front = firstChar;
		if (secondChar.equals("z"))
			if (firstChar.equals("o"))
				front = front + secondChar;
			else
	       		front = secondChar;
		return front;
   }
	public static void main(String args[])
	{
		String s = startOz("ozymandias");
		System.out.println(s);
      s = startOz("bzoo");
	   System.out.println(s);
      s = startOz("oxx");
	   System.out.println(s);
	}

}

