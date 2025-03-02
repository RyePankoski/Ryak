import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lexer {
    private final ArrayList<Token> tokens;
    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
    private static final Map<String, TokenType> SPECIAL_CHARS = new HashMap<>();

    static {
        KEYWORDS.put("let", TokenType.LET);
        KEYWORDS.put("if", TokenType.IF);
        KEYWORDS.put("else", TokenType.ELSE);
        KEYWORDS.put("while", TokenType.WHILE);
        KEYWORDS.put("print", TokenType.PRINT);

        SPECIAL_CHARS.put("(", TokenType.LEFT_PAREN);
        SPECIAL_CHARS.put(")", TokenType.RIGHT_PAREN);
        SPECIAL_CHARS.put("{", TokenType.LEFT_BRACE);
        SPECIAL_CHARS.put("}", TokenType.RIGHT_BRACE);
        SPECIAL_CHARS.put("[", TokenType.LEFT_BRACKET);
        SPECIAL_CHARS.put("]", TokenType.RIGHT_BRACKET);
        SPECIAL_CHARS.put(";", TokenType.SEMICOLON);

        SPECIAL_CHARS.put("=", TokenType.ASSIGN);
        SPECIAL_CHARS.put("+", TokenType.PLUS);
        SPECIAL_CHARS.put("-", TokenType.MINUS);
        SPECIAL_CHARS.put("*", TokenType.MULTIPLY);
        SPECIAL_CHARS.put("/", TokenType.DIVIDE);
        SPECIAL_CHARS.put("==", TokenType.EQUAL);
        SPECIAL_CHARS.put("!=", TokenType.NOT_EQUAL);
        SPECIAL_CHARS.put(">", TokenType.GREATER);
        SPECIAL_CHARS.put(">=", TokenType.GREATER_EQUAL);
        SPECIAL_CHARS.put("<", TokenType.LESS);
        SPECIAL_CHARS.put("<=", TokenType.LESS_EQUAL);
    }

    public Lexer() {
        tokens = new ArrayList<>();
    }

    public void updateWithString(String input) throws IOException {
        tokens.clear();

        if (input == null || input.isEmpty()) {
            return;
        }

        parse(new StringReader(input));
        System.out.println(tokens);
    }

    private void parse(Reader reader) throws IOException {
        int currentChar;
        StringBuilder tokenText = new StringBuilder();
        int position = 0;

        while ((currentChar = reader.read()) != -1) {
            char c = (char) currentChar;

            if (c == ' ' || c == '\n' || c == '\r') {
                if (!tokenText.isEmpty()) {
                    addToken(tokenText.toString(), position - tokenText.length());
                    tokenText.setLength(0);
                }
            } else if ("(){};[]".indexOf(c) != -1) {
                if (!tokenText.isEmpty()) {
                    addToken(tokenText.toString(), position - tokenText.length());
                    tokenText.setLength(0);
                }
                addToken(String.valueOf(c), position);
            } else if ("=<>!+-*/".indexOf(c) != -1) {
                if (!tokenText.isEmpty()) {
                    addToken(tokenText.toString(), position - tokenText.length());
                    tokenText.setLength(0);
                }

                tokenText.append(c);
                reader.mark(1);
                int nextChar = reader.read();

                if (nextChar != -1) {
                    char nc = (char) nextChar;
                    if ((c == '=' && nc == '=') ||
                            (c == '>' && nc == '=') ||
                            (c == '<' && nc == '=') ||
                            (c == '!' && nc == '=')) {
                        tokenText.append(nc);
                        position++;
                    } else {
                        reader.reset();
                    }
                } else {
                    reader.reset();
                }

                addToken(tokenText.toString(), position - tokenText.length() + 1);
                tokenText.setLength(0);
            } else {
                tokenText.append(c);
            }
            position++;
        }

        if (!tokenText.isEmpty()) {
            addToken(tokenText.toString(), position - tokenText.length());
        }
    }

    private void addToken(String tokenValue, int position) {
        TokenType type;

        if (KEYWORDS.containsKey(tokenValue.toLowerCase())) {
            type = KEYWORDS.get(tokenValue.toLowerCase());
        }
        else if (SPECIAL_CHARS.containsKey(tokenValue)) {
            type = SPECIAL_CHARS.get(tokenValue);
        }
        else {
            try {
                Integer.parseInt(tokenValue);
                type = TokenType.NUMBER;
            } catch (NumberFormatException e) {
                type = TokenType.IDENTIFIER;
            }
        }

        tokens.add(new Token(type, tokenValue, position, position + tokenValue.length() - 1));
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public static void main(String[] args) throws IOException {
    }
}