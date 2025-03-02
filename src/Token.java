public record Token(TokenType type, String value, int line, int column) {

    @Override
    public String toString() {
        return STR."\{type}(\{value})";
    }

}
enum TokenType {
    LET,            // let
    IF,             // if
    ELSE,           // else
    WHILE,          // while
    PRINT,          // print
    IDENTIFIER,     // variable names
    NUMBER,         // numeric literals

    // Operators
    PLUS,           // +
    MINUS,          // -
    MULTIPLY,       // *
    DIVIDE,         // /
    ASSIGN,         // =
    EQUAL,          // ==
    NOT_EQUAL,      // !=
    LESS,           // <
    GREATER,        // >
    LESS_EQUAL,     // <=
    GREATER_EQUAL,  // >=

    // Punctuation
    LEFT_PAREN,     // (
    RIGHT_PAREN,    // )
    LEFT_BRACE,     // {
    RIGHT_BRACE,    // }
    SEMICOLON,      // ;
    LEFT_BRACKET,   // [
    RIGHT_BRACKET,  // ]

    EOF             // end of file/input
}



