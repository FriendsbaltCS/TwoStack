/*
* filename: Bench.java
*
* compilation: javac *.java
* execution:   java Bench "expression in x" [n]
*
* Evaluates an expression n times (default 200000), with x sweeping from 0 to 10,
* in two different ways and reports the time for each:
*
*   interpreted: ArithmeticEvaluator.evaluate(expr) from scratch every time
*   compiled:    Postfix.toPostfix(expr) once, then Postfix.evaluate n times
*
* Provided for you.
*/
import java.util.HashMap;

public class Bench {
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: java Bench \"expression\" [n]");
            System.exit(1);
        }

        String expr = args[0];
        int n = args.length == 2 ? Integer.parseInt(args[1]) : 200000;

        try {
            interpreted(expr, n / 10);
            compiled(expr, n / 10);

            long t0 = System.nanoTime();
            double sum1 = interpreted(expr, n);
            long t1 = System.nanoTime();
            double sum2 = compiled(expr, n);
            long t2 = System.nanoTime();

            System.out.printf("interpreted: %8.1f ms%n", (t1 - t0) / 1e6);
            System.out.printf("compiled:    %8.1f ms%n", (t2 - t1) / 1e6);
            System.out.printf("speedup:     %8.1fx%n", (double) (t1 - t0) / (t2 - t1));
            System.out.println("(checksums " + (sum1 == sum2 ? "match" : "DIFFER: " + sum1 + " vs " + sum2) + ")");
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static double interpreted(String expr, int n) {
        ArithmeticEvaluator ev = new ArithmeticEvaluator();
        double sum = 0;
        for (int i = 0; i < n; i++) {
            ev.set("x", 10.0 * i / n);
            double y = ev.evaluate(expr);
            if (Double.isFinite(y)) sum += y;
        }
        return sum;
    }

    private static double compiled(String expr, int n) {
        Queue<String> program = Postfix.toPostfix(expr);
        HashMap<String, Double> vars = new HashMap<>();
        double sum = 0;
        for (int i = 0; i < n; i++) {
            vars.put("x", 10.0 * i / n);
            double y = Postfix.evaluate(program, vars);
            if (Double.isFinite(y)) sum += y;
        }
        return sum;
    }
}
