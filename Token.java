/*
* filename: Token.java
*
* A single lexical unit of an arithmetic expression. Provided for you.
*/
public class Token {
    public enum Type { NUMBER, IDENT, OP, LPAREN, RPAREN }

    public final Type type;
    public final String text;

    public Token(Type type, String text) {
        this.type = type;
        this.text = text;
    }

    /** The numeric value of a NUMBER token. */
    public double number() {
        return Double.parseDouble(text);
    }

    public String toString() {
        return text;
    }
}
