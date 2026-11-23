/*
* filename: Postfix.java
*
* compilation: javac *.java
* execution:   java Postfix
*/
import java.util.Map;
import java.util.Scanner;

public class Postfix {

    public static Queue<String> toPostfix(String infix) {
        throw new UnsupportedOperationException();
    }

    public static double evaluate(Queue<String> postfix, Map<String, Double> vars) {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.isBlank()) continue;
            try {
                System.out.println(String.join(" ", toPostfix(line)));
            } catch (IllegalArgumentException e) {
                System.out.println("error: " + e.getMessage());
            }
        }
    }
}
