/*
* filename: Ops.java
*
* The operator and function table shared by every evaluator in this project.
* Provided for you: you should call these methods, not rewrite them.
*
* Operators, from loosest to tightest binding:
*
*   precedence 1:  +  -            (left-associative)
*   precedence 2:  *  /  %         (left-associative)
*   precedence 3:  ~               unary minus (prefix; never typed by the user)
*   precedence 4:  ^               (right-associative)
*/
public class Ops {

    /** The name used for unary minus inside operator stacks and postfix programs. */
    public static final String NEG = "~";

    /** Is s one of the five binary operators + - * / % ^ ? (NEG is not binary.) */
    public static boolean isBinary(String s) {
        return s.length() == 1 && "+-*/%^".indexOf(s.charAt(0)) >= 0;
    }

    /** Is s the name of a one-argument function: sin cos tan sqrt abs ln exp ? */
    public static boolean isFunction(String s) {
        switch (s) {
            case "sin":
            case "cos":
            case "tan":
            case "sqrt":
            case "abs":
            case "ln":
            case "exp":
                return true;
            default:
                return false;
        }
    }

    /**
     * @param op a binary operator or NEG
     * @return its precedence; larger numbers bind tighter
     * @throws IllegalArgumentException if op is neither
     */
    public static int precedence(String op) {
        switch (op) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
            case "%":
                return 2;
            case NEG:
                return 3;
            case "^":
                return 4;
            default:
                throw new IllegalArgumentException("Not an operator: " + op);
        }
    }

    /** Does op group right-to-left (so that 2^3^2 means 2^(3^2))? Only ^ does. */
    public static boolean isRightAssociative(String op) {
        return op.equals("^");
    }

    /**
     * Computes (left op right) using ordinary double arithmetic. Division and
     * remainder by zero follow IEEE 754 (Infinity or NaN); nothing is thrown.
     *
     * @throws IllegalArgumentException if op is not a binary operator
     */
    public static double applyBinary(String op, double left, double right) {
        switch (op) {
            case "+":
                return left + right;
            case "-":
                return left - right;
            case "*":
                return left * right;
            case "/":
                return left / right;
            case "%":
                return left % right;
            case "^":
                return Math.pow(left, right);
            default:
                throw new IllegalArgumentException("Not a binary operator: " + op);
        }
    }

    /**
     * Computes name(x) for NEG or any function recognized by isFunction.
     * Domain errors (sqrt of a negative, ln of zero) yield NaN or Infinity.
     *
     * @throws IllegalArgumentException if name is neither NEG nor a function
     */
    public static double applyUnary(String name, double x) {
        switch (name) {
            case NEG:
                return -x;
            case "sin":
                return Math.sin(x);
            case "cos":
                return Math.cos(x);
            case "tan":
                return Math.tan(x);
            case "sqrt":
                return Math.sqrt(x);
            case "abs":
                return Math.abs(x);
            case "ln":
                return Math.log(x);
            case "exp":
                return Math.exp(x);
            default:
                throw new IllegalArgumentException("Not a unary operator or function: " + name);
        }
    }

    /** The value of a built-in constant (pi or e), or null if name is not one. */
    public static Double constant(String name) {
        switch (name) {
            case "pi":
                return Math.PI;
            case "e":
                return Math.E;
            default:
                return null;
        }
    }
}
