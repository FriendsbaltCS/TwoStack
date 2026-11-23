/*
* filename: Plot.java
*
* compilation: javac *.java
* execution:   java Plot "expression in x" xmin xmax [ymin ymax]
*
* Draws the graph of an expression in the variable x as text. The expression is
* compiled to a postfix program once and then run once per column of the plot.
* Requires Postfix.toPostfix and Postfix.evaluate. Provided for you.
*
* Examples (keep the double quotes around the expression):
*   java Plot "x^2 - 4" -4 4
*   java Plot "sin(x) * x" -10 10
*   java Plot "1 / x" -5 5 -10 10
*/
import java.util.HashMap;

public class Plot {
    private static final int WIDTH = 72;
    private static final int HEIGHT = 24;

    public static void main(String[] args) {
        if (args.length != 3 && args.length != 5) {
            System.out.println("Usage: java Plot \"expression\" xmin xmax [ymin ymax]");
            System.exit(1);
        }

        try {
            String expr = args[0];
            double xmin = Double.parseDouble(args[1]);
            double xmax = Double.parseDouble(args[2]);
            if (!(xmin < xmax)) {
                throw new IllegalArgumentException("xmin must be less than xmax");
            }

            Queue<String> program = Postfix.toPostfix(expr);
            HashMap<String, Double> vars = new HashMap<>();
            double[] ys = new double[WIDTH];
            for (int c = 0; c < WIDTH; c++) {
                vars.put("x", xmin + (xmax - xmin) * c / (WIDTH - 1));
                ys[c] = Postfix.evaluate(program, vars);
            }

            double ymin = Double.POSITIVE_INFINITY;
            double ymax = Double.NEGATIVE_INFINITY;
            if (args.length == 5) {
                ymin = Double.parseDouble(args[3]);
                ymax = Double.parseDouble(args[4]);
                if (!(ymin < ymax)) {
                    throw new IllegalArgumentException("ymin must be less than ymax");
                }
            } else {
                for (double y : ys) {
                    if (Double.isFinite(y)) {
                        ymin = Math.min(ymin, y);
                        ymax = Math.max(ymax, y);
                    }
                }
                if (ymin > ymax) {
                    throw new IllegalArgumentException("The expression has no finite values on that interval");
                }
                if (ymin == ymax) {
                    ymin -= 1;
                    ymax += 1;
                }
                double pad = 0.05 * (ymax - ymin);
                ymin -= pad;
                ymax += pad;
            }

            draw(expr, ys, xmin, xmax, ymin, ymax);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void draw(String expr, double[] ys, double xmin, double xmax, double ymin, double ymax) {
        char[][] grid = new char[HEIGHT][WIDTH];
        for (char[] row : grid) {
            java.util.Arrays.fill(row, ' ');
        }

        if (ymin <= 0 && 0 <= ymax) {
            int r = row(0, ymin, ymax);
            for (int c = 0; c < WIDTH; c++) {
                grid[r][c] = '-';
            }
        }
        if (xmin <= 0 && 0 <= xmax) {
            int c = (int) Math.round((0 - xmin) / (xmax - xmin) * (WIDTH - 1));
            for (int r = 0; r < HEIGHT; r++) {
                grid[r][c] = grid[r][c] == '-' ? '+' : '|';
            }
        }

        for (int c = 0; c < WIDTH; c++) {
            double y = ys[c];
            if (Double.isFinite(y) && y >= ymin && y <= ymax) {
                grid[row(y, ymin, ymax)][c] = '*';
            }
        }

        System.out.println("y = " + expr);
        for (int r = 0; r < HEIGHT; r++) {
            String label = "";
            if (r == 0) {
                label = String.format("%.3g", ymax);
            } else if (r == HEIGHT - 1) {
                label = String.format("%.3g", ymin);
            }
            System.out.println(String.format("%9s ", label) + new String(grid[r]));
        }

        String left = String.format("%.3g", xmin);
        String right = String.format("%.3g", xmax);
        int gap = Math.max(1, WIDTH - left.length() - right.length());
        System.out.println("          " + left + " ".repeat(gap) + right);
    }

    private static int row(double y, double ymin, double ymax) {
        return (int) Math.round((ymax - y) / (ymax - ymin) * (HEIGHT - 1));
    }
}
