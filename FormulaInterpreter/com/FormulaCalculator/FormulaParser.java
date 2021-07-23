package com.FormulaCalculator;

public class FormulaParser {
    /**
      * This method parses the operation strings recursively
      * until all operands are broken down to basic arithmetic
      * operations.
      */
    public static final String OPERATORS = "+-*/%";
    private OperationString rootOperation;
    private OperationString currOperation;
    private OperationString prevOperation;
    private boolean firstMainBranchOperator;

    public FormulaParser () {
       rootOperation = new OperationString();
       firstMainBranchOperator = true;
    }

    /** Check whether str contains any 
      * invalid symbols.
      * @param opString string to be processed
      */

     public boolean stringClean (String opString) {
        boolean isBracket = false;
        boolean isOperator = false;
        boolean isDigit = false;
        String character = null;
        int count = 0;

        for (int i = 0; i < opString.length(); i++) {
            character = Character.toString(opString.charAt(i));
            if (Character.isDigit(opString.charAt(i))) {
                isDigit = true;
                count++;
            }
            else if (OPERATORS.indexOf(opString.charAt(i)) != -1) {
                isOperator = true;
                count++;
            }
            else if (character.equals("(") || character.equals(")")) {
                isBracket = true;
                count++;
            }
        }
        //System.out.println(isDigit + " : " + isOperator + " : " + isBracket + " : " + count);
        if (!isBracket) // 3 + 2 * 5 - 4 / 2
            return isDigit && isOperator && count == opString.length();

        // 3 * (2 + 5) - (4 - 2) / 2
        return isDigit && isOperator && isBracket && count == opString.length();
    }

    public static boolean stringValid (String opString, boolean mute) {
        boolean valid = true;
        boolean isLeftBracket = false;
        boolean isRightBracket = false;
        int openingBaracketCount = 0;
        int closingBaracketCount = 0;
        String character = null;

        String firstChar = Character.toString(opString.charAt(0));
        String lastChar = Character.toString(opString.charAt(opString.length() - 1));

        // System.out.println("opString: " + opString);
        if (OPERATORS.indexOf(firstChar) != -1 || OPERATORS.indexOf(lastChar) != -1) {
            if(!mute)
                System.err.println("First or last character of string invalid.");
            valid = false;
        }
        for (int i = opString.length() - 1; i >= 0; i--) {
            // check (d - e
            if (Character.toString(opString.charAt(i)).equals("(") && !isRightBracket) {
                if(!mute)
                    System.err.println("Opening bracket comes without closing bracket.");
                valid = false;
                //System.out.println("Valid1: " + valid);
            }
            if (Character.toString(opString.charAt(i)).equals(")"))
                isRightBracket = true; 
        }
            
        
        for (int i = 0; i < opString.length(); i++) {
            character = Character.toString(opString.charAt(i));
            
            if (character.equals("(")) {
                isLeftBracket = true;
                // check (+-*/%
                if (OPERATORS.indexOf(opString.charAt(i + 1)) != -1) {
                    if(!mute)
                        System.err.println("Operator comes immediately after opening bracket.");
                    valid = false;
                }
                // check ()
                if (Character.toString(opString.charAt(i + 1)).equals(")")) {
                    if(!mute)
                        System.err.println("Empty brackets.");
                    valid = false;
                }
                // check 2 (
                if (i != 0 && Character.isDigit(opString.charAt(i - 1))) {
                    if(!mute)
                        System.err.println("Number comes immediately before opening bracket.");
                    valid = false;
                }
                // check (10) + 2
                int  j = i + 1;
                int operatorCount = 0;
                while (j < opString.length() && Character.toString(opString.charAt(j)).equals(")") == false) {
                    if (OPERATORS.indexOf(opString.charAt(j)) != -1)
                        operatorCount++;
                    
                    j++;
                }
                if (operatorCount == 0) {
                    if(!mute)
                        System.err.println("No operator(s) between brackets.");
                    valid = false;
                }

                openingBaracketCount++;
            }
            if(character.equals(")")) {
                isRightBracket = true;

                if (!isLeftBracket) {
                    // check a + b)
                    if(!mute)
                        System.err.println("Closing bracket comes before matching opeing bracket.");
                    valid = false;
                }
                // check +-*/%)
                if (OPERATORS.indexOf(opString.charAt(i - 1)) != -1) {
                    if(!mute)
                        System.err.println("Closing bracket comes immediately after operator.");
                    valid = false;
                }
                // check ) 2
                if (i != opString.length() - 1 && Character.isDigit(opString.charAt(i + 1))) {
                    if(!mute)
                        System.err.println("Number comes immediately after closing bracket.");
                    valid = false;
                }

                closingBaracketCount++;
            }
            // check ++ +-, etc.
            if (OPERATORS.indexOf(character) != -1 && OPERATORS.indexOf(opString.charAt(i + 1)) != -1) {
                if(!mute)
                    System.err.println("Consecutive operators.");
                valid = false;
            }
        }
        // check number of brackets match
        if (openingBaracketCount != closingBaracketCount) {
            if(!mute)
                System.err.println("Number of opening brackets does not match that of closing brackets.");
            valid = false;
        }
        //System.out.println("Valid2: " + valid);
        return valid;
    }

    public boolean stringValidator (String opString, boolean mute) {
        boolean cleanString = false;
        boolean validString = false;  

        // Validate OpString before everything else
        cleanString = stringClean(opString);
        validString = stringValid(opString, mute);

        if (!cleanString) {
            if (!mute)
                System.err.println("Input string contains invalid symbols.");
            return false;
        }
        if (!validString) {
            if (!mute)
                System.err.println("Input string contains invalid formats.");
            return false;
        }
        return true;
    }

    public boolean isBracketSurroundedFormula(String opString) {
       
        String subStr = null;

        if (Character.toString(opString.charAt(0)).equals("(") && Character.toString(opString.charAt(opString.length()-1)).equals(")")) {
            subStr = opString.substring(1, opString.length() - 1); // stripping outermost brackets
            return stringValidator(subStr, true);
        }

        return false;
    }

    /** Is the formula one without plus or minus as
      * main branch operator
      * @param String opString
      * a formula in the form of a string
      */
    public boolean isWithoutPlusAndMinus (String opString) {
        boolean isLeftBracket = false;
        boolean isRightBracket = false;
        int leftBracketCount = 0;
        int rightBracketCount = 0;
        String mainBrachOperators = "";

        for (int i = 0; i < opString.length(); i++) {
            if (Character.toString(opString.charAt(i)).equals("(") || isLeftBracket) {
                isLeftBracket = true;
                if (Character.toString(opString.charAt(i)).equals("("))
                    ++leftBracketCount;
                if (Character.toString(opString.charAt(i)).equals(")")) {
                    isRightBracket = true;
                    ++rightBracketCount;
                }
                if (leftBracketCount == rightBracketCount) {
                    isLeftBracket = false;
                    isRightBracket = false;
                }
            }
            if (OPERATORS.indexOf(opString.charAt(i)) != -1 && !isLeftBracket && !isRightBracket)
                mainBrachOperators += Character.toString(opString.charAt(i));
        }
        return mainBrachOperators.indexOf("+") == -1 && mainBrachOperators.indexOf("-") == -1;
    }

    public boolean isNumber (String opString) {

        for(int i = 0; i < opString.length(); i++)
            if(!Character.isDigit(opString.charAt(i)))
                return false;
        return true;
    }

    public void parseOperationString (String opString) {

        OperationString opStr = new OperationString ();
        //OperationString currOperation = null;
        //OperationString prevOperation = null;
        String left = "";
        String right = "";
        boolean isLeftBracket = false;
        boolean isRightBracket = false;
        int leftBracketCount = 0;
        int rightBracketCount = 0;

        if (!stringValidator(opString, false))
            System.exit(1);
        
        if (isBracketSurroundedFormula(opString))
            opString = opString.substring(1, opString.length() - 1);
        
        for (int i = 0; i < opString.length(); i++) {
            if (Character.isDigit(opString.charAt(i)) && !isLeftBracket && !isRightBracket) { 
            // collects digits in a numeric operand outside brackets
                left = left + Character.toString(opString.charAt(i));
            }
            if (Character.toString(opString.charAt(i)).equals("(") || isLeftBracket) { 
            // collects operand between brackets, brackets at both ends inclusive
                left = left + Character.toString(opString.charAt(i));
                isLeftBracket = true;
                if (Character.toString(opString.charAt(i)).equals("("))
                    ++leftBracketCount;
                
                if (Character.toString(opString.charAt(i)).equals(")")) {
                    isRightBracket = true;
                    ++rightBracketCount;
                }
                if (leftBracketCount == rightBracketCount) {
                // Reached last right bracket
                    isLeftBracket = false;
                    isRightBracket = false;
                }
                //System.out.println(leftBracketCount + ": " + rightBracketCount);
                //System.out.println(isLeftBracket + "-" + isRightBracket);
            }
            if (OPERATORS.indexOf(Character.toString(opString.charAt(i))) != -1 && !isLeftBracket && !isRightBracket) { 
            // opString.charAt(i) is an operator outside brackets

                // when a main-branch operator is encountered
                // A main-branch operator is a + or - or any of * / and % in the formula string
                if (Character.toString(opString.charAt(i)).equals("+") || Character.toString(opString.charAt(i)).equals("-") 
                    || isWithoutPlusAndMinus(opString))
                    right = opString.substring(i+1); // after left + operator, all the rest is right
                else { // When a fomula like 32 * 4 + 5 - 4/7 is encountered,
                       // treat 32 * 4 together as the left operand instead of 32.
                    left += Character.toString(opString.charAt(i));
                    continue;
                }
                
                opStr.setLeftOp(left);
                opStr.setOp(Character.toString(opString.charAt(i)));
                opStr.setRightOp(right);
                if (firstMainBranchOperator) {
                    currOperation = rootOperation;
                }
                firstMainBranchOperator = false;
                break;
            }
        }
        opStr.setParentOp(opString);
        prevOperation = currOperation;
        currOperation.setNext(opStr);
        currOperation = currOperation.getNext();
        currOperation.setPrevious(prevOperation);
        
        if(!isNumber(opStr.getLeftOp())) {
            parseOperationString(opStr.getLeftOp());
        }
        if(!isNumber(opStr.getRightOp())){
            parseOperationString(opStr.getRightOp());
        }
        
    }

    public String wrapInBrackets (String opString) {
        if (opString.indexOf("(") == -1 && opString.indexOf(")") == -1)
            return "(" + opString + ")";
        return opString;
    }
    public void testRoot () {
        OperationString op = rootOperation.getNext(); 
        OperationString temp = null;
        Operation atomOp = new Operation ();
        boolean found = false;

        while ( op.getNext() != null) {
            //op.showString();
            op = op.getNext();
        }

        while(op.getPrevious() != null) {
            temp = op;
            
            while (!found && temp.getPrevious() != null) {
                
                if (wrapInBrackets(op.getParentOp()).equals(temp.getPrevious().getLeftOp())) {
                    //System.out.println("Left: " + op.getParentOp());
                    atomOp.setResult(Double.valueOf(op.getLeftOp()), Double.valueOf(op.getRightOp()), op.getOp());
                    temp.getPrevious().setLeftOp(String.valueOf(atomOp.getResult()));
                    found = true;
                }
                
                if (wrapInBrackets(op.getParentOp()).equals(temp.getPrevious().getRightOp())) {
                    //System.out.println("Right: " + op.getParentOp());
                    atomOp.setResult(Double.valueOf(op.getLeftOp()), Double.valueOf(op.getRightOp()), op.getOp());
                    temp.getPrevious().setRightOp(String.valueOf(atomOp.getResult()));
                    found = true;
                }                      
                
                temp = temp.getPrevious();
            }
            
            found = false;
            op = op.getPrevious();
        }
        op = op.getNext();
        atomOp.setResult(Double.valueOf(op.getLeftOp()), Double.valueOf(op.getRightOp()), op.getOp());
        System.out.println("Result: " + atomOp.getResult());
    }
}