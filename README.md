# Two-Stack Lab: A Calculator That Plots

In this project you will build, from the ground up, the engine at the heart of every graphing calculator: a program that reads a formula like `sin(x) * x + 2 ^ -1` and computes its value. Along the way you will (1) implement a generic stack and queue, (2) write Dijkstra's famous two-stack algorithm, (3) extend it to handle real mathematical notation, (4) compile formulas into postfix "programs" that a stack machine can run, and (5) use your engine to draw graphs of functions.

This project should take roughly **6 to 8 hours**. Start early and test as you go.

| Part | What you build | Data structures | Approx. time |
|---|---|---|---|
| 1 | `ArrayStack<T>` and `LinkedListQueue<T>` | arrays, linked lists, generics, iterators | 1.5 h |
| 2 | `FullyParenthesized` (Dijkstra's two-stack algorithm) | two stacks | 1 h |
| 3 | `ArithmeticEvaluator` (precedence, unary minus, functions, variables) | two stacks | 2.5 h |
| 4 | `Postfix` (infix to postfix, and a postfix evaluator) | a queue and a stack | 1.5 h |
| 5 | Run `Plot` and `Bench`; write up your results | | 1 h |

## Background

### Why is this hard?

You know that `1 + 2 * 3` is 7 and not 9. You also know that `(1 + 2) * 3` is 9, that `10 - 4 - 3` is 3 and not 9, and that `-2 ^ 2` is a little bit suspicious. You learned all of these rules so long ago that they feel like facts of nature. A computer, however, receives a formula as a flat string of characters, and has to be *told* how to recover the structure hiding inside it. In the early days of programming, this was a serious obstacle: it was a large part of the difficulty in building the first compilers for languages like ALGOL 60.

### Dijkstra and the shunting yard

In 1961, Edsger Dijkstra, working at the Mathematisch Centrum in Amsterdam on a translator for ALGOL 60, published an algorithm for converting ordinary formulas into a form that a machine could evaluate easily. He called it the *shunting-yard algorithm*, because the way it sorts operators resembles the way a railway yard reroutes cars onto different tracks. At about the same time, the German computer scientists Friedrich Bauer and Klaus Samelson independently worked out the same stack-based idea.

The simpler cousin of that algorithm, the **two-stack algorithm** for fully parenthesized expressions, is the one you will write first. Both versions are built from nothing but stacks (and, in the case of the shunting yard, one queue).

### Postfix notation

In 1924 the Polish logician Jan Łukasiewicz invented a notation in which every operator is written *before* its operands, which makes parentheses unnecessary. Writing the operator *after* its operands instead gives **postfix** notation, better known as **reverse Polish notation (RPN)**:

```
infix:    3 + 4 * 2
postfix:  3 4 2 * +
```

Postfix needs no parentheses and no precedence rules: you read left to right, pushing numbers on a stack, and whenever you see an operator you pop its operands, compute, and push the result. Hewlett-Packard's calculators made RPN famous, starting with the HP-9100A in 1968 and the HP-35, the first handheld scientific calculator, in 1972.

### Why it still matters

Almost every modern language runtime evaluates expressions this way. The Java Virtual Machine is a *stack machine*. You can see for yourself: put this method in a file `Calc.java`, compile it, and run `javap -c -p Calc`:

```java
static int f(int a, int b, int c) {
    return a * b + c;
}
```

```
0: iload_0      // push a
1: iload_1      // push b
2: imul         // pop two ints, push their product
3: iload_2      // push c
4: iadd         // pop two ints, push their sum
5: ireturn      // pop the result and return it
```

That is `a b * c +` in postfix. When you finish Part 4, you will have written a tiny version of exactly this: a compiler (infix to postfix) and a virtual machine (the postfix evaluator).

## Getting Started

Create a repository from the starter files. The files are:

| File | Status |
|---|---|
| `Stack.java`, `Queue.java` | Provided. The interfaces you will implement. |
| `Token.java`, `Tokenizer.java`, `Ops.java` | Provided. See "Provided Code" below. |
| `Plot.java`, `Bench.java` | Provided. Used in Part 5. |
| `Tests.java` | Provided. Public tests for every part. |
| `ArrayStack.java`, `LinkedListQueue.java` | **Yours** (Part 1) |
| `FullyParenthesized.java` | **Yours** (Part 2) |
| `ArithmeticEvaluator.java` | **Yours** (Part 3) |
| `Postfix.java` | **Yours** (Part 4) |
| `writeup.md` | **Yours** (Part 5). You create this file. |

Compile everything with

```
javac *.java
```

The starter code compiles right away, but every method you are responsible for throws `UnsupportedOperationException`, so all of the tests fail. Your job is to turn them green. Run all the tests with `java Tests`, or one part at a time:

```
java Tests stacks
java Tests queues
java Tests parens
java Tests infix
java Tests postfix
```

`FullyParenthesized`, `ArithmeticEvaluator`, and `Postfix` each have a `main` that reads one expression per line from the keyboard (end with Ctrl-D, or Ctrl-Z then Enter on Windows) so that you can try things interactively:

```
java ArithmeticEvaluator x=0.5
```

**Important:** do not write `import java.util.Stack;` or `import java.util.Queue;` anywhere. Java has its own classes with those names, and importing them will quietly shadow the `Stack` and `Queue` interfaces in this project. You should never need any collection class from `java.util` in Parts 1 through 4, other than the `List` returned by `Tokenizer` and the `Map` and `HashMap` that are already provided.

### Provided Code

You do not have to write a tokenizer or a table of math functions. Read these three files so that you know exactly what they do.

**`Tokenizer.tokenize(String)`** turns a string into a `List<Token>`. A `Token` has a `type` and the `text` it came from. The types are `NUMBER`, `IDENT` (a name such as `x`, `pi`, or `sin`), `OP` (one of `+ - * / % ^`), `LPAREN`, and `RPAREN`. Whitespace is skipped. Any other character causes an `IllegalArgumentException`. Notice that a `-` is *always* reported as an `OP`: the tokenizer cannot know whether it means "subtract" or "negate", and neither can anyone else without looking at the context.

**`Ops`** holds all of the knowledge about operators and functions, so that every part of the project agrees:

```java
Ops.isBinary(String s)                        // is s one of + - * / % ^ ?
Ops.isFunction(String s)                      // sin cos tan sqrt abs ln exp
Ops.precedence(String op)                     // larger binds tighter
Ops.isRightAssociative(String op)             // true only for ^
Ops.applyBinary(String op, double l, double r)
Ops.applyUnary(String name, double x)         // unary minus, and the functions
Ops.constant(String name)                     // pi and e; null for anything else
Ops.NEG                                       // "~", the name of unary minus
```

Arithmetic follows ordinary `double` rules: `1 / 0` is `Infinity` and `sqrt(-1)` is `NaN`. These are *not* errors.

## Part 1: Stacks and Queues

Both interfaces are generic and extend `Iterable<T>`:

```java
public interface Stack<T> extends Iterable<T> {
    void push(T item);   T pop();   T peek();   boolean isEmpty();   int size();
}

public interface Queue<T> extends Iterable<T> {
    void enqueue(T item);   T dequeue();   T peek();   boolean isEmpty();   int size();
}
```

Everything in Parts 2 through 5 is built on top of these two classes, so get them right first.

### `ArrayStack<T>`

Implement the stack with a resizing array. In addition to the interface methods, provide `public int capacity()`, which returns the length of the underlying array (this exists so that the tests can see your resizing happen).

- The array starts with capacity 8.
- When a `push` finds the array full, the capacity doubles.
- When a `pop` leaves the stack exactly one quarter full, the capacity is halved, but it never drops below 8.
- A popped item must not remain referenced by the array. Java's garbage collector cannot reclaim an object that something still points to.
- The iterator visits items from the top of the stack to the bottom, and does not remove anything.
- `pop`, `peek`, and an exhausted iterator's `next` throw `NoSuchElementException`.

Java will not let you write `new T[8]`. Figuring out why, and what to do about it, is part of this part (see the writeup).

### `LinkedListQueue<T>`

Implement the queue as a singly linked list with a pointer to both the first and the last node, so that both `enqueue` and `dequeue` take constant time.

- The iterator visits items from the front of the queue to the back, and does not remove anything. You must be able to iterate over the same queue as many times as you like.
- `dequeue`, `peek`, and an exhausted iterator's `next` throw `NoSuchElementException`.
- Pay special attention to what happens to your pointers when the queue becomes empty and then is used again. This is the most common bug in this class.

## Part 2: Dijkstra's Two-Stack Algorithm

Now for the algorithm that gives the project its name. It evaluates a **fully parenthesized** expression: one in which every operation is wrapped in its own pair of parentheses, so that no rules about precedence are needed at all.

```
( 1 + ( ( 2 + 3 ) * ( 4 * 5 ) ) )
```

Use two stacks, one for values and one for operators, and read the tokens from left to right:

- If the token is a number, push it onto the value stack.
- If the token is an operator, push it onto the operator stack.
- If the token is a left parenthesis, ignore it.
- If the token is a right parenthesis, a subexpression has just been completed. Pop an operator, pop two values, apply the operator to them, and push the result back onto the value stack.

When the tokens run out, the one remaining value is the answer. Here is the example above, one token at a time (stacks are shown with the top on the right):

| Token | Action | Value stack | Operator stack |
|---|---|---|---|
| `(` | ignore | | |
| `1` | push value | 1 | |
| `+` | push operator | 1 | + |
| `(` | ignore | 1 | + |
| `(` | ignore | 1 | + |
| `2` | push value | 1 2 | + |
| `+` | push operator | 1 2 | + + |
| `3` | push value | 1 2 3 | + + |
| `)` | pop `+`, pop 3 and 2: push 2 + 3 | 1 5 | + |
| `*` | push operator | 1 5 | + * |
| `(` | ignore | 1 5 | + * |
| `4` | push value | 1 5 4 | + * |
| `*` | push operator | 1 5 4 | + * * |
| `5` | push value | 1 5 4 5 | + * * |
| `)` | pop `*`, pop 5 and 4: push 4 * 5 | 1 5 20 | + * |
| `)` | pop `*`, pop 20 and 5: push 5 * 20 | 1 100 | + |
| `)` | pop `+`, pop 100 and 1: push 1 + 100 | 101 | |

Watch the order of the operands: the *first* value you pop is the **right** operand. It makes no difference for `+` and `*`, but it matters a great deal for `-`, `/`, `%`, and `^`.

### Your task

Complete `FullyParenthesized.evaluate`:

```java
public static double evaluate(String expr)
```

Use your own `ArrayStack`, declared through the interface (`Stack<Double> vals = new ArrayStack<>();`). Use `Tokenizer.tokenize` to read the input and `Ops.applyBinary` to do the arithmetic. You need to handle numbers, the six binary operators, and parentheses. Variables, functions, and unary minus are not part of this part.

**Malformed input.** For *any* string at all, your method must either return a value or throw an `IllegalArgumentException`. It must never throw anything else. For example, `( 1 + )` makes a naive implementation pop from an empty stack and throw a `NoSuchElementException`, which is not acceptable. You do not need to catch every conceivable mistake in this part, but you must at least notice when there is nothing to pop, and when the stacks do not end up in the state a correct expression would leave them in.

## Part 3: Real Expressions

Nobody writes `( 1 + ( ( 2 + 3 ) * ( 4 * 5 ) ) )`. In Part 3 you will remove the training wheels and write `ArithmeticEvaluator`, which understands formulas the way you do:

```java
public class ArithmeticEvaluator {
    public void set(String name, double value)
    public double evaluate(String expr)
}
```

```java
ArithmeticEvaluator ev = new ArithmeticEvaluator();
ev.set("x", 3);
ev.evaluate("x^2 + 2*x + 1");   // 16.0
ev.evaluate("-2 ^ 2");          // -4.0
ev.evaluate("sin(pi / 2)");     // 1.0
```

### What it must understand

**Precedence.** From loosest to tightest:

| Operators | Meaning | Grouping |
|---|---|---|
| `+` `-` | add, subtract | left to right |
| `*` `/` `%` | multiply, divide, remainder | left to right |
| unary `-` | negate | |
| `^` | power | **right to left** |

The grouping column is called *associativity*, and it matters whenever an operator meets another one of the same precedence. `10 - 4 - 3` is `(10 - 4) - 3 = 3`, but `2 ^ 3 ^ 2` is `2 ^ (3 ^ 2) = 512`.

**Unary minus.** The same character `-` means two different things. In `5 - 3` it is a binary operator; in `-3`, `2 * -3`, `(-3)`, and `2 ^ -1` it negates what follows. A `-` is unary exactly when it appears where an *operand* is expected: at the very beginning, after an operator, or after a left parenthesis. Because unary minus binds more loosely than `^`, `-2 ^ 2` is `-(2 ^ 2) = -4`, while `(-2) ^ 2` is `4`. Unary minus is a different operator from binary minus with a different precedence, and `Ops.NEG` is the name to use for it on your operator stack.

**Parentheses** override everything.

**Functions.** `sin`, `cos`, `tan`, `sqrt`, `abs`, `ln`, and `exp`, each taking exactly one argument in parentheses. Functions can appear inside expressions and inside each other: `sqrt(abs(-16) + 9)` is 5.

**Variables and constants.** A name that is not a function is a variable. `set(name, value)` defines (or redefines) one, and the same evaluator can be reused with different values. The constants `pi` and `e` are built in (a call to `set` takes priority, so `set("e", 5)` does what you would expect). A name must be a letter followed by letters, digits, or underscores, and may not be the name of a function: `set` throws `IllegalArgumentException` otherwise.

### How the algorithm changes

The two stacks stay. What changes is *when* operators are applied. In Part 2 the parentheses told you when to apply an operator. Without them, you must decide yourself, and the decision is made at the moment a binary operator arrives:

> Before pushing the arriving operator, apply the operator on top of the operator stack if it should be evaluated *first*: that is, if it has higher precedence than the arriving operator, or if the precedences are equal and the arriving operator is left-associative. Repeat until you reach something that should not be applied yet, or until you hit a left parenthesis or an empty stack. Then push the arriving operator.

Left parentheses are now pushed onto the operator stack, where they act as a wall that nothing can apply across. A right parenthesis applies operators until it reaches its left parenthesis, which it discards. When the input ends, whatever is left on the operator stack is applied, top first.

Here is `3 + 4 * 2 - 1`:

| Token | Action | Value stack | Operator stack |
|---|---|---|---|
| `3` | push value | 3 | |
| `+` | stack empty: push | 3 | + |
| `4` | push value | 3 4 | + |
| `*` | `+` is looser than `*`: push | 3 4 | + * |
| `2` | push value | 3 4 2 | + * |
| `-` | `*` is tighter: apply 4 * 2. `+` is equal and left-associative: apply 3 + 8. Push `-` | 11 | - |
| `1` | push value | 11 1 | - |
| end | apply `-` | 10 | |

Think carefully about how to make functions fit this scheme. A function name has to wait until its argument has been reduced to a single value. What do your two stacks look like at the instant you reach the closing parenthesis of `sin(x + 1)`?

### Malformed expressions

Your evaluator must throw an `IllegalArgumentException`, with a message that says something helpful, for each of these, and must never throw any other kind of exception:

- an empty or blank expression, or one that ends early (`1 +`, `-`)
- a missing operand (`* 2`, `1 + * 2`, `()`, `sin()`)
- a missing operator (`2 3`, `2 (3)`, `(1)(2)`, `2 sin(0)`, `x y`)
- unbalanced parentheses (`(1 + 2`, `1 + 2)`, `)(`)
- a function name not followed by a parenthesis (`sin`, `sin 3`)
- an undefined variable or function (`y + 1` when `y` was never set, `foo(2)`)
- anything the tokenizer rejects (`1 $ 2`, `sqrt(1, 2)`)

The following are *not* supported and so are errors as well: unary plus (`+3`), implicit multiplication (`2x`, `2(3)`), scientific notation (`2e5`), and functions of more than one argument.

> **Hint.** There is a pleasant way to catch almost all of these with one extra piece of state. At every moment, your evaluator is in one of two situations: it is *expecting an operand* (at the start, after an operator, after a left parenthesis), or it is *expecting an operator* (after a number, a variable, or a right parenthesis). Decide which tokens are legal in each situation, and the missing-operand and missing-operator errors fall out on their own.

## Part 4: Postfix

You have written an evaluator that works out the answer as it reads the formula. Part 4 does something subtly different and, for many applications, much more useful: it *translates* the formula into postfix, a flat list of instructions that can be run later, as many times as you like, without ever looking at the original text again.

| Infix | Postfix |
|---|---|
| `3 + 4 * 2` | `3 4 2 * +` |
| `(3 + 4) * 2` | `3 4 + 2 *` |
| `8 / 2 / 2` | `8 2 / 2 /` |
| `2 ^ 3 ^ 2` | `2 3 2 ^ ^` |
| `-x ^ 2` | `x 2 ^ ~` |
| `sin(x) + 1` | `x sin 1 +` |
| `sqrt(abs(x) + 9)` | `x abs 9 + sqrt` |

Unary minus is written `~` in postfix, so that it can never be confused with binary subtraction. Parentheses vanish: the *order* of the tokens carries all of the structure.

### Converting to postfix

The conversion is *the same loop as Part 3* with one change: wherever your evaluator would **apply** an operator, the converter instead **writes it to an output queue**. Numbers and variables go straight to the output. Here is `3 + 4 * 2`:

| Token | Output queue (front to back) | Operator stack |
|---|---|---|
| `3` | 3 | |
| `+` | 3 | + |
| `4` | 3 4 | + |
| `*` | 3 4 | + * |
| `2` | 3 4 2 | + * |
| end | 3 4 2 * + | |

### Evaluating postfix

A postfix program needs only a value stack. Read each token in order. A number or variable is pushed. An operator pops its operands (two for binary operators, one for `~` and the functions) and pushes the result. For `3 4 2 * +`:

| Token | Value stack |
|---|---|
| `3` | 3 |
| `4` | 3 4 |
| `2` | 3 4 2 |
| `*` | 3 8 |
| `+` | 11 |

### Your task

Complete `Postfix.java`:

```java
public static Queue<String> toPostfix(String infix)
public static double evaluate(Queue<String> postfix, Map<String, Double> vars)
```

`toPostfix` accepts exactly the same language as `ArithmeticEvaluator.evaluate`, and throws `IllegalArgumentException` for exactly the same kinds of malformed input. It does not need to know the values of any variables (an expression with an unknown name is perfectly good *syntax*). Numbers are written to the output exactly as they were typed (`3.50` stays `3.50`). Use your own `LinkedListQueue` for the output.

`evaluate` runs a program. Variable values come from `vars`, with `pi` and `e` built in. It throws `IllegalArgumentException` if the program is malformed (too few operands, too many values left over at the end, an unknown token, an undefined variable).

**You must iterate over the queue; you may not dequeue from it.** After `evaluate` returns, the program must be exactly as it was, so that it can be run again with different values of `x`. This is the entire point of compiling, and it is why `Queue` extends `Iterable`.

The public tests print a postfix program with `String.join(" ", Postfix.toPostfix(expr))`, which works because your queue is `Iterable<String>`. You can do the same to look at your own output. `java Postfix` does this for every line you type.

## Part 5: Plot, Benchmark, and Writeup

There is no new code to write in this part. The point is to see what you have built.

### Plot

`Plot` draws the graph of any expression in `x`. It compiles the expression to postfix *once*, then runs the program once for each of the 72 columns of the plot (changing only `x`). Keep the double quotes around the expression:

```
java Plot "sin(x) * x" -10 10
```

```
y = sin(x) * x
     8.57                                     |                                   
                 **                           |                          **       
                *                             |                            *      
               *   *                          |                         *   *     
                                              |                                   
                    *                         |                        *          
              *                               |                              *    
                     *                        |                       *           
                                              |                                   
                                              |                                   
             *        *                       |                      *        *   
                                     ****     |    ****                           
                                    *    **   |  **    *                          
          -------------*-----------*-------******-------*-----------*-------------
            *                     *           |          *                     *  
                                 *            |           *                       
                        *                     |                    *              
                                *             |            *                      
           *             *                    |                   *             * 
                               *              |             *                     
                          *   *               |              *   *                
                           ***                |               ***                 
          *                                   |                                  *
    -6.11                                     |                                   
          -10.0                                                               10.0
```

The optional last two arguments fix the vertical range, which is handy for functions that blow up: `java Plot "1 / x" -5 5 -10 10`. Values that are `Infinity` or `NaN` are simply not drawn.

### Benchmark

`Bench` evaluates an expression 200,000 times in two ways: *interpreted* (calling `ArithmeticEvaluator.evaluate` on the text every single time) and *compiled* (calling `toPostfix` once, then `Postfix.evaluate` 200,000 times). It prints how long each took:

```
java Bench "sin(x) * x + sqrt(abs(x)) - x ^ 2 / 3"
```

### Writeup

Create a file named `writeup.md` in your repository and answer these questions. Short answers (a few sentences) are fine, unless the question asks for a trace or a measurement.

1. **(Part 1)** Why won't Java let you write `new T[8]`? What did you do instead, and why is that safe inside `ArrayStack`? What could go wrong if `data` were `public`?
2. **(Part 1)** Why does `ArrayStack` double its capacity rather than add a fixed amount, like 10? Time pushing 100,000 items under both policies if you want evidence. Also, why does the stack shrink when it is a *quarter* full instead of when it is half full? (What would happen to a stack that repeatedly pushes and pops right at the boundary?)
3. **(Part 1)** Suppose you had also written a `LinkedListStack<T>` in class. What is the *only* line in `FullyParenthesized` that would have to change to use it instead of `ArrayStack`? Why is that possible?
4. **(Part 3)** Evaluate `2 * -3 ^ 2 - 1` by hand, in the style of the tables in this README: show the value stack and operator stack after every token. State the final answer.
5. **(Part 3 and 4)** `ArithmeticEvaluator.evaluate` and `Postfix.toPostfix` contain almost the same loop. Say exactly how they differ. Could you have written `evaluate(s)` as `Postfix.evaluate(Postfix.toPostfix(s), vars)`? Why might you still want both?
6. **(Part 4)** Why must `Postfix.evaluate` iterate over the queue instead of dequeuing from it? What would happen to `Plot` if it dequeued?
7. **(Part 5)** Run `Bench` on at least three expressions of different lengths and report the results. What work does the compiled version avoid doing on every evaluation? Does the advantage grow or shrink as the expression gets longer? Why?
8. **(Part 5)** Pick a function whose graph is interesting (an asymptote, a discontinuity, rapid oscillation, a function that is undefined on part of the interval), paste its `Plot` output into your writeup, and explain what you see. How does `Plot` treat the points where the function has no finite value, and why is that the right thing to do?

## Housekeeping

Make sure that you follow good coding practices. Every method you write should have a clear comment explaining its inputs, outputs, and any additional effects (see `Ops.java` for the style).

Please also test your code *as you go*. `Tests.java` is a useful start, but passing it does not mean that you are done: the tests I use to grade your work are harder, and include thousands of randomly generated expressions and sequences of operations, checked against independent reference implementations. They are particularly keen on edge cases and on finding input that makes your code throw the wrong kind of exception. Write your own tests, in your own file, for the things the public tests do not cover. A few suggestions: an expression with a very long chain of operators, a stack that grows and shrinks repeatedly, a queue that is emptied and refilled, and as many weird malformed expressions as you can dream up.

Do not modify the provided files (`Stack`, `Queue`, `Token`, `Tokenizer`, `Ops`, `Plot`, `Bench`, `Tests`): they will be replaced with the originals when your work is graded.

### What to submit

Your repository should contain `ArrayStack.java`, `LinkedListQueue.java`, `FullyParenthesized.java`, `ArithmeticEvaluator.java`, `Postfix.java`, `writeup.md`, and any test files you wrote.

### Collaboration

You may not collaborate with AI on this project in any way.

You may look things up, but you must not simply copy and paste things without understanding them. I would advise you not to look up the answers to this lab: the algorithms are well known, so the answers are out there. In any event, if I doubt that your code is your code, I will ask you to explain it to me. Code you cannot adequately explain does not count as your code.

You may work together, but please do not just copy each other without understanding. And definitely don't copy code you haven't tested.

## Optional Challenges

These are not required, and they are not extra credit. They are here for the curious.

- **Watch the machine think.** Add a mode to `Postfix.evaluate` that prints the value stack after every token.
- **Implicit multiplication.** Make `2x`, `2(3 + 4)`, and `3 sin(x)` mean what a mathematician would expect. Where does the tokenizer's job end and the evaluator's begin?
- **Functions of two arguments.** Add `max(a, b)` and `atan2(y, x)`. You will need commas, and you will need to work out what a comma does to the operator stack.
- **Calculus.** Write a method that takes a postfix program for $f(x)$ and returns a postfix program for $f'(x)$ using the product, quotient, and chain rules. Then plot both on the same axes.
- **Optimize.** Teach your compiler *constant folding*: `2 * 3 + x` should compile to `6 x +`.
- **Round trip.** Convert postfix back to infix with as few parentheses as possible.
- **Linked versus array.** Write a `LinkedListStack<T>`, change one line of `ArithmeticEvaluator`, and run `Bench`. Which is faster? Why might that be?
- **Read real bytecode.** Use `javap -c` on a method with a few arithmetic expressions and a few method calls. Convince yourself that you can read it.

## Acknowledgments

Dijkstra's two-stack algorithm for evaluating fully parenthesized expressions is presented in Section 1.3 of Robert Sedgewick and Kevin Wayne, *Algorithms* (4th edition), which this unit loosely follows. The shunting-yard algorithm is from E. W. Dijkstra, *Algol 60 translation: An Algol 60 translator for the X1 and making a translator for Algol 60* (Mathematisch Centrum, 1961).
