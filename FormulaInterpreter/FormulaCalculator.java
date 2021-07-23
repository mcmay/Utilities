/**
 * Analyze and computer a formula
 * like the following:
 * 3 + ((42 - 5.6) * 2 + 7.2) - 1.4
 * The program receive a string 
 * representing a formula like the 
 * above, analze it, turn it into an
 * expression for numerical calculation
 * and computer the final result.
 */

import com.FormulaCalculator.*;

import java.util.*;

public class FormulaCalculator {

    public static String despaceFromString (String str) {
        String spacelessString = "";

        for (int i = 0; i < str.length(); i++) {
            if (Character.isWhitespace(str.charAt(i)))
                continue;
            spacelessString = spacelessString + Character.toString(str.charAt(i));

        }
        return spacelessString;
    }

    public static void main (String[] args) {
        
        System.out.println("Enter a calculation formula:");
        Scanner in = new Scanner(System.in);
        String inputString = in.nextLine();
                
        String spacelessString = despaceFromString(inputString);


        FormulaParser formuPar = new FormulaParser();
        formuPar.parseOperationString(spacelessString); 
        formuPar.testRoot();
    }
}
