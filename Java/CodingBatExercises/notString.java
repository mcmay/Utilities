/*
Given a string, return a new string where "not " has been added to the front. However, if the string already begins with "not", return the string unchanged. Note: use .equals() to compare 2 strings.


notString("candy") → "not candy"
notString("x") → "not x"
notString("not bad") → "not bad"
*/

public class NotString {
    static String notString (String str) {
        if (str.startWith("not"))
            return str;
        return "not " + str;
    }
    public static main(String[] args) {
        String not_string = notString("candy");
        System.out.println(not_string);
        not_string = notString("x");
        System.out.println(not_string);
        not_string = notString("not bad");
        System.out.println(not_string);
        not_string = notString("notGood");
        System.out.println(not_string);
    }
}
