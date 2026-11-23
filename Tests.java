/*
* filename: Tests.java
*
* compilation: javac *.java
* execution:   java Tests [stacks | queues | parens | infix | postfix]
*
* Runs the public tests for each part of the project (all of them if no part is
* named) and prints one line for every failure. Passing these tests is necessary
* but not sufficient: the tests used for grading are more demanding. Provided
* for you. Add your own tests in a separate file.
*/
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Tests {
    private interface Body {
        void run() throws Exception;
    }

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        String which = args.length > 0 ? args[0] : "all";
        boolean all = which.equals("all");
        boolean known = false;

        if (all || which.equals("stacks")) { known = true; section("Part 1: ArrayStack"); stacks(); }
        if (all || which.equals("queues")) { known = true; section("Part 1: LinkedListQueue"); queues(); }
        if (all || which.equals("parens")) { known = true; section("Part 2: FullyParenthesized"); parens(); }
        if (all || which.equals("infix"))  { known = true; section("Part 3: ArithmeticEvaluator"); infix(); }
        if (all || which.equals("postfix")) { known = true; section("Part 4: Postfix"); postfix(); }

        if (!known) {
            System.out.println("Usage: java Tests [stacks | queues | parens | infix | postfix]");
            System.exit(2);
        }

        System.out.println();
        System.out.println(passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }

    // ------------------------------------------------------------------ Part 1

    private static void stacks() {
        test("new stack is empty", () -> {
            Stack<Integer> s = new ArrayStack<>();
            eq(true, s.isEmpty());
            eq(0, s.size());
        });

        test("push makes it non-empty and updates size", () -> {
            Stack<Integer> s = new ArrayStack<>();
            s.push(1);
            s.push(2);
            eq(false, s.isEmpty());
            eq(2, s.size());
        });

        test("pop returns items in last-in first-out order", () -> {
            Stack<Integer> s = new ArrayStack<>();
            for (int i = 0; i < 5; i++) s.push(i);
            for (int i = 4; i >= 0; i--) eq(i, s.pop());
            eq(true, s.isEmpty());
        });

        test("peek returns the top without removing it", () -> {
            Stack<String> s = new ArrayStack<>();
            s.push("a");
            s.push("b");
            eq("b", s.peek());
            eq("b", s.peek());
            eq(2, s.size());
        });

        test("pop on an empty stack throws NoSuchElementException", () ->
            throwsA(NoSuchElementException.class, () -> new ArrayStack<Integer>().pop()));

        test("peek on an empty stack throws NoSuchElementException", () ->
            throwsA(NoSuchElementException.class, () -> new ArrayStack<Integer>().peek()));

        test("pop after emptying a used stack throws", () -> {
            Stack<Integer> s = new ArrayStack<>();
            s.push(1);
            s.pop();
            throwsA(NoSuchElementException.class, () -> s.pop());
        });

        test("holds Strings, Doubles, and other stacks", () -> {
            Stack<Double> d = new ArrayStack<>();
            d.push(2.5);
            eq(2.5, d.pop());
            Stack<Stack<String>> nested = new ArrayStack<>();
            Stack<String> inner = new ArrayStack<>();
            inner.push("x");
            nested.push(inner);
            eq("x", nested.pop().pop());
        });

        test("can hold null", () -> {
            Stack<String> s = new ArrayStack<>();
            s.push(null);
            eq(1, s.size());
            eq(null, s.pop());
        });

        test("iterator runs from top to bottom without changing the stack", () -> {
            Stack<Integer> s = new ArrayStack<>();
            for (int i = 1; i <= 4; i++) s.push(i);
            String seen = "";
            for (int x : s) seen += x;
            eq("4321", seen);
            eq(4, s.size());
        });

        test("iterator on an empty stack has no elements", () ->
            eq(false, new ArrayStack<Integer>().iterator().hasNext()));

        test("iterator throws NoSuchElementException when exhausted", () -> {
            Stack<Integer> s = new ArrayStack<>();
            s.push(1);
            Iterator<Integer> it = s.iterator();
            it.next();
            throwsA(NoSuchElementException.class, () -> it.next());
        });

        test("two iterators are independent", () -> {
            Stack<Integer> s = new ArrayStack<>();
            s.push(1);
            s.push(2);
            Iterator<Integer> a = s.iterator();
            Iterator<Integer> b = s.iterator();
            eq(2, a.next());
            eq(1, a.next());
            eq(2, b.next());
        });

        test("starts with capacity 8 and doubles when full", () -> {
            ArrayStack<Integer> s = new ArrayStack<>();
            eq(8, s.capacity());
            for (int i = 0; i < 8; i++) s.push(i);
            eq(8, s.capacity());
            s.push(8);
            eq(16, s.capacity());
            for (int i = 9; i < 17; i++) s.push(i);
            eq(32, s.capacity());
        });

        test("halves when one quarter full, never below 8", () -> {
            ArrayStack<Integer> s = new ArrayStack<>();
            for (int i = 0; i < 33; i++) s.push(i);
            eq(64, s.capacity());
            while (s.size() > 17) s.pop();
            eq(64, s.capacity());
            s.pop();
            eq(32, s.capacity());
            while (s.size() > 8) s.pop();
            eq(16, s.capacity());
            while (s.size() > 4) s.pop();
            eq(8, s.capacity());
            while (!s.isEmpty()) s.pop();
            eq(8, s.capacity());
        });

        test("contents survive resizing in both directions", () -> {
            Stack<Integer> s = new ArrayStack<>();
            for (int i = 0; i < 1000; i++) s.push(i);
            for (int i = 999; i >= 0; i--) eq(i, s.pop());
        });

        test("handles a million push/pop operations", () -> {
            Stack<Integer> s = new ArrayStack<>();
            for (int i = 0; i < 500000; i++) s.push(i);
            for (int i = 0; i < 500000; i++) s.pop();
            eq(true, s.isEmpty());
        });
    }

    private static void queues() {
        test("new queue is empty", () -> {
            Queue<Integer> q = new LinkedListQueue<>();
            eq(true, q.isEmpty());
            eq(0, q.size());
        });

        test("dequeue returns items in first-in first-out order", () -> {
            Queue<Integer> q = new LinkedListQueue<>();
            for (int i = 0; i < 5; i++) q.enqueue(i);
            eq(5, q.size());
            for (int i = 0; i < 5; i++) eq(i, q.dequeue());
            eq(true, q.isEmpty());
        });

        test("peek returns the front without removing it", () -> {
            Queue<String> q = new LinkedListQueue<>();
            q.enqueue("a");
            q.enqueue("b");
            eq("a", q.peek());
            eq("a", q.peek());
            eq(2, q.size());
        });

        test("dequeue and peek on an empty queue throw NoSuchElementException", () -> {
            throwsA(NoSuchElementException.class, () -> new LinkedListQueue<Integer>().dequeue());
            throwsA(NoSuchElementException.class, () -> new LinkedListQueue<Integer>().peek());
        });

        test("a queue can be emptied and then reused", () -> {
            Queue<Integer> q = new LinkedListQueue<>();
            q.enqueue(1);
            q.dequeue();
            eq(true, q.isEmpty());
            q.enqueue(2);
            q.enqueue(3);
            eq(2, q.size());
            eq(2, q.dequeue());
            eq(3, q.dequeue());
            eq(true, q.isEmpty());
            throwsA(NoSuchElementException.class, () -> q.dequeue());
        });

        test("interleaved enqueues and dequeues", () -> {
            Queue<Integer> q = new LinkedListQueue<>();
            q.enqueue(1);
            q.enqueue(2);
            eq(1, q.dequeue());
            q.enqueue(3);
            eq(2, q.dequeue());
            q.enqueue(4);
            eq(3, q.dequeue());
            eq(4, q.dequeue());
        });

        test("holds Strings and Doubles", () -> {
            Queue<String> q = new LinkedListQueue<>();
            q.enqueue("x");
            eq("x", q.dequeue());
            Queue<Double> d = new LinkedListQueue<>();
            d.enqueue(0.5);
            eq(0.5, d.dequeue());
        });

        test("iterator runs from front to back without changing the queue", () -> {
            Queue<Integer> q = new LinkedListQueue<>();
            for (int i = 1; i <= 4; i++) q.enqueue(i);
            String seen = "";
            for (int x : q) seen += x;
            eq("1234", seen);
            eq(4, q.size());
            eq(1, q.peek());
        });

        test("iterator throws NoSuchElementException when exhausted", () -> {
            Queue<Integer> q = new LinkedListQueue<>();
            q.enqueue(1);
            Iterator<Integer> it = q.iterator();
            it.next();
            eq(false, it.hasNext());
            throwsA(NoSuchElementException.class, () -> it.next());
        });

        test("a queue can be iterated many times", () -> {
            Queue<Integer> q = new LinkedListQueue<>();
            q.enqueue(7);
            q.enqueue(8);
            for (int round = 0; round < 3; round++) {
                int sum = 0;
                for (int x : q) sum += x;
                eq(15, sum);
            }
        });

        test("handles a million enqueue/dequeue operations", () -> {
            Queue<Integer> q = new LinkedListQueue<>();
            for (int i = 0; i < 500000; i++) q.enqueue(i);
            for (int i = 0; i < 500000; i++) eq(i, q.dequeue());
        });
    }

    // ------------------------------------------------------------------ Part 2

    private static void parens() {
        test("Dijkstra's original example", () ->
            near(101, FullyParenthesized.evaluate("( 1 + ( ( 2 + 3 ) * ( 4 * 5 ) ) )")));

        test("each operator", () -> {
            near(7, FullyParenthesized.evaluate("( 3 + 4 )"));
            near(-1, FullyParenthesized.evaluate("( 3 - 4 )"));
            near(12, FullyParenthesized.evaluate("( 3 * 4 )"));
            near(2.5, FullyParenthesized.evaluate("( 10 / 4 )"));
            near(3, FullyParenthesized.evaluate("( 7 % 4 )"));
            near(1024, FullyParenthesized.evaluate("( 2 ^ 10 )"));
        });

        test("subtraction and division are not commutative", () -> {
            near(2, FullyParenthesized.evaluate("( 5 - 3 )"));
            near(2, FullyParenthesized.evaluate("( 8 / 4 )"));
        });

        test("works without spaces", () -> near(9, FullyParenthesized.evaluate("((1+2)*3)")));

        test("decimal numbers", () -> near(0.75, FullyParenthesized.evaluate("( .5 + 0.25 )")));

        test("nested on both sides", () ->
            near(-1.5, FullyParenthesized.evaluate("( ( 1 + 2 ) * ( ( 3 - 5 ) / 4 ) )")));

        test("deeply nested", () -> {
            String e = "1";
            for (int i = 0; i < 200; i++) e = "( " + e + " + 1 )";
            near(201, FullyParenthesized.evaluate(e));
        });

        test("a lone number is a valid expression", () -> near(42, FullyParenthesized.evaluate("42")));

        test("malformed input throws IllegalArgumentException", () -> {
            String[] bad = { "", "   ", "( 1 + )", "( + 2 )", "( 1 2 )", "( 1 + 2", "( 1 + 2 ) )",
                             "1 +", "x", "( 1 + $ )" };
            for (String s : bad) {
                throwsA(IllegalArgumentException.class, () -> FullyParenthesized.evaluate(s));
            }
        });
    }

    // ------------------------------------------------------------------ Part 3

    private static double ev(String expr) {
        return new ArithmeticEvaluator().evaluate(expr);
    }

    private static double evx(String expr, double x) {
        ArithmeticEvaluator e = new ArithmeticEvaluator();
        e.set("x", x);
        return e.evaluate(expr);
    }

    private static void infix() {
        test("single numbers and whitespace", () -> {
            near(5, ev("5"));
            near(5, ev("   5   "));
            near(0.5, ev(".5"));
            near(3.25, ev("3.25"));
        });

        test("multiplication binds tighter than addition", () -> {
            near(7, ev("1 + 2 * 3"));
            near(7, ev("2 * 3 + 1"));
            near(10, ev("3 + 4 * 2 - 1"));
        });

        test("parentheses override precedence", () -> {
            near(9, ev("(1 + 2) * 3"));
            near(21, ev("(1 + 2) * (3 + 4)"));
            near(5, ev("((((5))))"));
        });

        test("equal precedence groups left to right", () -> {
            near(3, ev("10 - 4 - 3"));
            near(2, ev("8 / 2 / 2"));
            near(6, ev("7 % 4 * 2"));
            near(1, ev("7 % 4 / 3"));
        });

        test("^ groups right to left", () -> {
            near(512, ev("2 ^ 3 ^ 2"));
            near(64, ev("(2 ^ 3) ^ 2"));
        });

        test("^ binds tighter than * and /", () -> {
            near(18, ev("2 * 3 ^ 2"));
            near(1.5, ev("6 / 2 ^ 2"));
        });

        test("mixed precedence", () -> near(17, ev("1 + 2 * 3 ^ 2 - 4 / 2")));

        test("unary minus", () -> {
            near(-3, ev("-3"));
            near(3, ev("--3"));
            near(3, ev("- -3"));
            near(-6, ev("2 * -3"));
            near(5, ev("2 - -3"));
            near(-1, ev("1 + -2"));
            near(-5, ev("2 * -3 + 1"));
            near(1, ev("-(1 - 2)"));
        });

        test("unary minus binds looser than ^ but tighter than *", () -> {
            near(-4, ev("-2 ^ 2"));
            near(4, ev("(-2) ^ 2"));
            near(0.5, ev("2 ^ -1"));
            near(-18, ev("2 * -3 ^ 2"));
            near(Math.pow(2, -9), ev("2 ^ -3 ^ 2"));
            near(2, ev("2 ^ -1 * 4"));
            near(1.5, ev("3 * 2 ^ -1"));
        });

        test("functions", () -> {
            near(4, ev("sqrt(16)"));
            near(0, ev("sin(0)"));
            near(1, ev("cos(0)"));
            near(0, ev("tan(0)"));
            near(3, ev("abs(-3)"));
            near(1, ev("exp(0)"));
            near(1, ev("ln(e)"));
        });

        test("functions inside expressions and inside each other", () -> {
            near(2, ev("2 * sin(pi / 2)"));
            near(5, ev("sqrt(abs(-16) + 9)"));
            near(-2, ev("-sqrt(4)"));
            near(7, ev("1 + sqrt(sqrt(81)) * 2"));
            near(4, ev("sqrt(2 + 2 * 7) + sqrt(4)^2 - sqrt(16)"));
        });

        test("pi and e are built in", () -> {
            near(Math.PI, ev("pi"));
            near(Math.E, ev("e"));
            near(2 * Math.PI, ev("2 * pi"));
        });

        test("variables", () -> {
            near(16, evx("x^2 + 2*x + 1", 3));
            near(-9, evx("-x^2", 3));
            near(1, evx("sin(x)^2 + cos(x)^2", 0.7));
            near(0, evx("x - x", 123.456));
        });

        test("variables can be redefined and reused", () -> {
            ArithmeticEvaluator e = new ArithmeticEvaluator();
            e.set("x", 2);
            e.set("rate_1", 10);
            near(20, e.evaluate("x * rate_1"));
            e.set("x", 5);
            near(50, e.evaluate("x * rate_1"));
            near(50, e.evaluate("x * rate_1"));
        });

        test("one evaluator can evaluate many expressions", () -> {
            ArithmeticEvaluator e = new ArithmeticEvaluator();
            near(3, e.evaluate("1 + 2"));
            near(12, e.evaluate("3 * 4"));
            throwsA(IllegalArgumentException.class, () -> e.evaluate("3 *"));
            near(8, e.evaluate("2 ^ 3"));
        });

        test("division by zero follows IEEE 754", () -> {
            eq(Double.POSITIVE_INFINITY, ev("1 / 0"));
            eq(true, Double.isNaN(ev("0 / 0")));
            eq(true, Double.isNaN(ev("sqrt(-1)")));
        });

        test("deeply nested parentheses and long chains", () -> {
            String p = "1";
            for (int i = 0; i < 500; i++) p = "(" + p + ")";
            near(1, ev(p));
            String chain = "0";
            for (int i = 1; i <= 1000; i++) chain += " + " + i;
            near(500500, ev(chain));
        });

        test("malformed expressions throw IllegalArgumentException", () -> {
            String[] bad = { "", "   ", "1 +", "+ 1", "* 2", "1 + * 2", "1 2", "(1", "1)", ")(", "()",
                             "(1 + 2", "1 + 2)", "2 (3)", "(1)(2)", "sin", "sin 3", "sin()", "sin(", "2 sin(0)",
                             "foo(2)", "1 $ 2", "2e5", "1.2.3", "3 +* 4", "-", "- - ", "x y", "2 ^", "^ 2",
                             "(((1 + 2) * 3)", "sqrt(1, 2)" };
            for (String s : bad) {
                throwsA(IllegalArgumentException.class, () -> evx(s, 1));
            }
        });

        test("an undefined variable throws IllegalArgumentException", () -> {
            throwsA(IllegalArgumentException.class, () -> ev("y + 1"));
            throwsA(IllegalArgumentException.class, () -> ev("x"));
        });

        test("set rejects invalid names", () -> {
            ArithmeticEvaluator e = new ArithmeticEvaluator();
            String[] bad = { "", "2x", "x y", "sin", "sqrt", "a-b", "x+" };
            for (String name : bad) {
                throwsA(IllegalArgumentException.class, () -> e.set(name, 1));
            }
        });
    }

    // ------------------------------------------------------------------ Part 4

    private static String pf(String infix) {
        return String.join(" ", Postfix.toPostfix(infix));
    }

    private static Queue<String> prog(String postfix) {
        Queue<String> q = new LinkedListQueue<>();
        for (String t : postfix.trim().split("\\s+")) {
            if (!t.isEmpty()) q.enqueue(t);
        }
        return q;
    }

    private static double run(String infix, double x) {
        HashMap<String, Double> vars = new HashMap<>();
        vars.put("x", x);
        return Postfix.evaluate(Postfix.toPostfix(infix), vars);
    }

    private static void postfix() {
        test("toPostfix: numbers and variables", () -> {
            eq("5", pf("5"));
            eq("x", pf("x"));
            eq("3 4 +", pf("3 + 4"));
        });

        test("toPostfix: precedence", () -> {
            eq("3 4 2 * +", pf("3 + 4 * 2"));
            eq("3 4 * 2 +", pf("3 * 4 + 2"));
            eq("3 4 2 * + 1 -", pf("3 + 4 * 2 - 1"));
        });

        test("toPostfix: parentheses disappear", () -> {
            eq("1 2 + 3 *", pf("(1 + 2) * 3"));
            eq("1 2 3 + 4 * +", pf("1 + (2 + 3) * 4"));
            eq("1", pf("((1))"));
        });

        test("toPostfix: associativity", () -> {
            eq("8 2 / 2 /", pf("8 / 2 / 2"));
            eq("10 4 - 3 -", pf("10 - 4 - 3"));
            eq("2 3 2 ^ ^", pf("2 ^ 3 ^ 2"));
            eq("2 3 ^ 2 ^", pf("(2 ^ 3) ^ 2"));
        });

        test("toPostfix: unary minus is written ~", () -> {
            eq("x ~", pf("-x"));
            eq("2 3 ~ *", pf("2 * -3"));
            eq("2 2 ^ ~", pf("-2 ^ 2"));
            eq("2 ~ 2 ^", pf("(-2) ^ 2"));
            eq("1 2 ~ -", pf("1 - -2"));
            eq("2 ~ ~", pf("--2"));
            eq("2 1 ~ ^ 4 *", pf("2 ^ -1 * 4"));
        });

        test("toPostfix: functions follow their argument", () -> {
            eq("x sin", pf("sin(x)"));
            eq("x sin 1 +", pf("sin(x) + 1"));
            eq("x abs 9 + sqrt", pf("sqrt(abs(x) + 9)"));
            eq("2 x sin *", pf("2 * sin(x)"));
            eq("x ~ abs", pf("abs(-x)"));
        });

        test("toPostfix: numbers keep their spelling", () -> eq("3.50 .5 +", pf("3.50 + .5")));

        test("toPostfix: malformed infix throws IllegalArgumentException", () -> {
            String[] bad = { "", "1 +", "* 2", "1 2", "(1", "1)", "()", "2 (3)", "sin", "sin 3", "sin()",
                             "1 $ 2", "-", "x y", "2 ^", "foo(2)" };
            for (String s : bad) {
                throwsA(IllegalArgumentException.class, () -> Postfix.toPostfix(s));
            }
        });

        test("evaluate: hand-written programs", () -> {
            HashMap<String, Double> none = new HashMap<>();
            near(11, Postfix.evaluate(prog("3 4 2 * +"), none));
            near(5, Postfix.evaluate(prog("5"), none));
            near(2, Postfix.evaluate(prog("8 2 / 2 /"), none));
            near(-3, Postfix.evaluate(prog("3 ~"), none));
            near(5, Postfix.evaluate(prog("16 sqrt 1 +"), none));
            near(Math.PI, Postfix.evaluate(prog("pi"), none));
        });

        test("evaluate: variables come from the map", () -> {
            HashMap<String, Double> vars = new HashMap<>();
            vars.put("x", 4.0);
            vars.put("y", 0.5);
            near(8, Postfix.evaluate(prog("x 2 *"), vars));
            near(2, Postfix.evaluate(prog("x y ^"), vars));
        });

        test("evaluate: does not consume the program", () -> {
            Queue<String> p = Postfix.toPostfix("x * x + 1");
            int size = p.size();
            HashMap<String, Double> vars = new HashMap<>();
            vars.put("x", 3.0);
            near(10, Postfix.evaluate(p, vars));
            eq(size, p.size());
            vars.put("x", 5.0);
            near(26, Postfix.evaluate(p, vars));
            eq(size, p.size());
        });

        test("evaluate: malformed programs throw IllegalArgumentException", () -> {
            HashMap<String, Double> vars = new HashMap<>();
            String[] bad = { "", "+", "1 +", "1 2", "1 2 3 +", "~", "sin", "1 2 + +", "nope", "1 nope +" };
            for (String s : bad) {
                throwsA(IllegalArgumentException.class, () -> Postfix.evaluate(prog(s), vars));
            }
        });

        test("compile-then-run agrees with ArithmeticEvaluator", () -> {
            String[] exprs = { "1 + 2 * 3", "(1 + 2) * 3", "2 ^ 3 ^ 2", "-x ^ 2", "2 * -x + 1",
                               "sin(x) ^ 2 + cos(x) ^ 2", "sqrt(abs(x - 10)) / (1 + x)", "x % 3 - 7 / 2 * x",
                               "-(-(-x))", "2 ^ -x", "exp(-x * x) * ln(x + 1)" };
            for (String s : exprs) {
                for (double x : new double[] { 0, 0.5, 1, 2.5, 7 }) {
                    near(evx(s, x), run(s, x));
                }
            }
        });
    }

    // ----------------------------------------------------------------- helpers

    private static void section(String name) {
        System.out.println(name);
    }

    private static void test(String name, Body body) {
        try {
            body.run();
            passed++;
        } catch (Throwable t) {
            failed++;
            String why = t instanceof AssertionError ? t.getMessage() : "threw " + t;
            System.out.println("  FAIL  " + name + "\n          " + why);
        }
    }

    private static void eq(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("expected " + expected + " but got " + actual);
        }
    }

    private static void near(double expected, double actual) {
        if (Double.compare(expected, actual) == 0) return;
        if (Math.abs(expected - actual) <= 1e-9 * Math.max(1.0, Math.abs(expected))) return;
        throw new AssertionError("expected " + expected + " but got " + actual);
    }

    private static void throwsA(Class<? extends Throwable> type, Body body) {
        try {
            body.run();
        } catch (Throwable t) {
            if (type.isInstance(t)) return;
            throw new AssertionError("expected " + type.getSimpleName() + " but got " + t);
        }
        throw new AssertionError("expected " + type.getSimpleName() + " but nothing was thrown");
    }
}
