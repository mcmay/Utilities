/*
 Write a Java program to display the following pattern. Go to the editor
Sample Pattern :

   J    a   v     v  a                                                  
   J   a a   v   v  a a                                                 
J  J  aaaaa   V V  aaaaa                                                
 JJ  a     a   V  a     a

 */

 public class PrintLetterPattern {
 	public static final int HEIGHT = 4;
 	public static final String SPACE = " ";
 	static void printChar(String ch, int n) {
 		for (int i = 0; i < n; i++)
 			System.out.print(ch);
 	}
 	
 	static void printLetterPattern ( ) {
 		for (int i = 0; i < HEIGHT; i++) {
 			printChar(SPACE, 1);
 			switch(i) {
 				case 0: // Line 1
 					printChar(SPACE, 3);
 					printChar("J", 1);
 					printChar(SPACE, 3);
 					printChar("a", 1);
 					printChar(SPACE, 2);
 					printChar("V", 1);
 					printChar(SPACE, 5);
 					printChar("V", 1);
 					printChar(SPACE, 2);
 					printChar("a", 1);
 					break;
 				case 1:
 					printChar(SPACE, 3);
 					printChar("J", 1);
 					printChar(SPACE, 2);
 					printChar("a", 1);
 					printChar(SPACE, 1);
 					printChar("a", 1);
 					printChar(SPACE, 2);
 					printChar("V", 1);
 					printChar(SPACE, 3);
 					printChar("V", 1);
 					printChar(SPACE, 2);
 					printChar("a", 1);
 					printChar(SPACE, 1);
 					printChar("a", 1);
					break;
 				case 2:
 					printChar("J", 1);
 					printChar(SPACE, 2);
 					printChar("J", 1);
 					printChar(SPACE, 1);
 					printChar("a", 5);
 					printChar(SPACE, 2);
 					printChar("V", 1);
 					printChar(SPACE, 1);
 					printChar("V", 1);
 					printChar(SPACE, 2);
 					printChar("a", 5);
 					break;
 				case 3:
 					printChar(SPACE, 1);
 					printChar("J", 2);
 					printChar(SPACE, 1);
 					printChar("a", 1);
 					printChar(SPACE, 5);
 					printChar("a", 1);
 					printChar(SPACE, 2);
 					printChar("V", 1);
 					printChar(SPACE, 2);
 					printChar("a", 1);
 					printChar(SPACE, 5);
 					printChar("a", 1);
 					break;
 				default:
 					break;
 			}
 			System.out.println();
 		}
 	}
 	public static void main(String[] args) {
 		printLetterPattern();
 	}
 }