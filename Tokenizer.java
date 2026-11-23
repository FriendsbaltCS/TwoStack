/*
* filename: Tokenizer.java
*
* Breaks an expression string into Tokens. Provided for you.
*/
import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    /**
     * Splits an expression into tokens, skipping whitespace.
     *
     * NUMBER: digits with an optional decimal point ("3", "3.25", ".5", "3.")
     * IDENT:  a letter followed by letters, digits, or underscores
     * OP:     one of + - * / % ^   (a '-' is always reported as an OP; deciding
     *         whether it means subtract or negate is not the tokenizer's job)
     * LPAREN / RPAREN: ( and )
     *
     * @param s the expression
     * @return the tokens, in order
     * @throws IllegalArgumentException on any character that cannot start a token
     */
    public static List<Token> tokenize(String s) {
        List<Token> tokens = new ArrayList<>();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (Character.isWhitespace(c)) {
                i++;
            } else if (isDigit(c) || c == '.') {
                int start = i;
                int digits = 0;
                while (i < s.length() && isDigit(s.charAt(i))) {
                    i++;
                    digits++;
                }
                if (i < s.length() && s.charAt(i) == '.') {
                    i++;
                    while (i < s.length() && isDigit(s.charAt(i))) {
                        i++;
                        digits++;
                    }
                }
                if (digits == 0) {
                    throw new IllegalArgumentException("Malformed number at position " + start);
                }
                tokens.add(new Token(Token.Type.NUMBER, s.substring(start, i)));
            } else if (isLetter(c)) {
                int start = i;
                while (i < s.length() && (isLetter(s.charAt(i)) || isDigit(s.charAt(i)) || s.charAt(i) == '_')) {
                    i++;
                }
                tokens.add(new Token(Token.Type.IDENT, s.substring(start, i)));
            } else if ("+-*/%^".indexOf(c) >= 0) {
                tokens.add(new Token(Token.Type.OP, String.valueOf(c)));
                i++;
            } else if (c == '(') {
                tokens.add(new Token(Token.Type.LPAREN, "("));
                i++;
            } else if (c == ')') {
                tokens.add(new Token(Token.Type.RPAREN, ")"));
                i++;
            } else {
                throw new IllegalArgumentException("Unexpected character '" + c + "' at position " + i);
            }
        }
        return tokens;
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
}
