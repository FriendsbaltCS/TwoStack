/*
* filename: ArithmeticEvaluator.java
*
* compilation: javac *.java
* execution:   java ArithmeticEvaluator [name=value ...]
*/
import java.util.HashMap;
import java.util.Scanner;

public class ArithmeticEvaluator {
    private final HashMap<String, Double> vars = new HashMap<>();

    public void set(String name, double value) {
        throw new UnsupportedOperationException();
    }

    public double evaluate(String expr) {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) {
        ArithmeticEvaluator ev = new ArithmeticEvaluator();
        for (String arg : args) {
            String[] parts = arg.split("=");
            ev.set(parts[0], Double.parseDouble(parts[1]));
        }

        Scanner in = new Scanner(System.in);
        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.isBlank()) continue;
            try {
                System.out.println(ev.evaluate(line));
            } catch (IllegalArgumentException e) {
                System.out.println("error: " + e.getMessage());
            }
        }
    }
}
