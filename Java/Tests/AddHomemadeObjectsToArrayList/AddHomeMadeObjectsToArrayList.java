import java.util.*;

class MyObject {
    private String str;

    public MyObject(String s) {
            this.str = s;
    }
    public void setStr(String str) {
            this.str = str;
    }
    public String getStr() {
            return str;
    }
}

public class AddHomeMadeObjectsToArrayList {
        public static void main (String[] args) {
                String[] strings = {"Java is an object-oriented programming language.",
                                    "Everything in Java lives in a class.",
                                    "Java programms (bytecodes) run in the Java Virtual Machine (JVM).",
                                    "Ok, enough for this time. See you later."
                };
                List <MyObject>  myObList = new ArrayList<MyObject>();
		List <String> strList = new ArrayList<String>();
                for (int i = 0; i < strings.length; i++) {
			// Every time there must be a "new" MyObject instance mo 
			// created to hold a different string
                	MyObject mo = new MyObject("noString");
			strList.add(strings[i]);
                        mo.setStr(strings[i]);
                        myObList.add(mo);
                }
		String dispStr = null;
        System.out.println("Strings stored in myObList:");
                for (int i = 0; i < myObList.size(); i++) {
			dispStr = myObList.get(i).getStr();
                        System.out.println(dispStr);
                }
                System.out.println("Strings stored in strList:");
                for (int i = 0; i < strList.size(); i++) {
			dispStr = strList.get(i);
                        System.out.println(dispStr);
                }
        }
}
