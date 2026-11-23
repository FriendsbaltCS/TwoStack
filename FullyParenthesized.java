/*
* filename: FullyParenthesized.java
*
* compilation: javac *.java
* execution:   java FullyParenthesized
*/
import java.util.Scanner;

public class FullyParenthesized {

    public static double evaluate(String expr) {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.isBlank()) continue;
            try {
                System.out.println(evaluate(line));
            } catch (IllegalArgumentException e) {
                System.out.println("error: " + e.getMessage());
            }
        }
    }
}
