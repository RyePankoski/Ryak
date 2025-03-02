import java.util.ArrayList;
import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int currentPosition;
    private AST rootNode;
    ArrayList<String> errors;
    Main main;

    public Parser(Main main) {
        this.main = main;
        this.tokens = new ArrayList<>();
        this.currentPosition = 0;
        errors = new ArrayList<>();
    }

    public void update() {
        currentPosition = 0;
        rootNode = parseProgram();
    }

    private AST parseProgram() {
        AST program = new AST("Program", 0, 0,main);
        errors.clear();

        while (currentPosition < tokens.size()) {
            AST statement = parseStatement();
            if (statement != null) {
                program.addChild(statement);
            }
        }

        return program;
    }

    private AST parseStatement() {
        if (currentPosition >= tokens.size()) return null;

        Token token = tokens.get(currentPosition);

        switch (token.type()) {
            case LET:
                return parseDeclaration();
            case IF:
                return parseIfStatement();
            case WHILE:
                return parseWhileStatement();
            case PRINT:
                return parsePrintStatement();
            case IDENTIFIER:
                return parseAssignment();
            default:
                addError(STR."Unexpected token: \{token.value()}", token);
                consume();
                return null;
        }
    }

    private AST parseDeclaration() {
        Token letToken = tokens.get(currentPosition);
        consume();

        if (currentPosition >= tokens.size() || tokens.get(currentPosition).type() != TokenType.IDENTIFIER) {
            addError("Expected identifier after 'let'", letToken);
            return null;
        }

        Token identifier = tokens.get(currentPosition);
        consume();

        if (currentPosition >= tokens.size() || tokens.get(currentPosition).type() != TokenType.ASSIGN) {
            addError("Expected '=' after identifier in declaration", identifier);
            return null;
        }

        consume();

        AST expression = parseExpression();

        if (currentPosition >= tokens.size() || tokens.get(currentPosition).type() != TokenType.SEMICOLON) {
            Token lastToken = currentPosition > 0 ? tokens.get(currentPosition - 1) : letToken;
            addError("Expected ';' after declaration", lastToken);
            return null;
        }

        consume();

        AST declaration = new AST("Declaration:", letToken.line(), letToken.column(), main);
        AST identifierNode = new AST("Identifier", identifier.value(), identifier.line(), identifier.column(), main);
        declaration.addChild(identifierNode);
        declaration.addChild(expression);

        return declaration;
    }


    private AST parseAssignment() {
        Token identifier = tokens.get(currentPosition);
        consume();

        if (currentPosition >= tokens.size() || tokens.get(currentPosition).type() != TokenType.ASSIGN) {
            addError("Expected '=' in assignment", identifier);
            return null;
        }

        consume();

        AST expression = parseExpression();

        if (currentPosition >= tokens.size() || tokens.get(currentPosition).type() != TokenType.SEMICOLON) {
            Token lastToken = currentPosition > 0 ? tokens.get(currentPosition - 1) : identifier;
            addError("Expected ';' after assignment", lastToken);
            return null;
        }

        consume();

        AST assignment = new AST("Assignment", identifier.line(), identifier.column(), main);
        AST identifierNode = new AST("Identifier", identifier.value(), identifier.line(), identifier.column(), main);
        assignment.addChild(identifierNode);
        assignment.addChild(expression);

        return assignment;
    }

    private AST parseIfStatement() {
        Token ifToken = tokens.get(currentPosition);
        consume();

        if (currentPosition >= tokens.size() || tokens.get(currentPosition).type() != TokenType.LEFT_PAREN) {
            addError("Expected '(' after 'if'", ifToken);
            return null;
        }

        consume();

        AST condition = parseCondition();

        if (currentPosition >= tokens.size() || tokens.get(currentPosition).type() != TokenType.RIGHT_PAREN) {
            Token lastToken = currentPosition > 0 ? tokens.get(currentPosition - 1) : ifToken;
            addError("Expected ')' after condition", lastToken);
            return null;
        }

        consume();

        if (currentPosition >= tokens.size() || tokens.get(currentPosition).type() != TokenType.LEFT_BRACE) {
            addError("Expected '{' after if condition", tokens.get(currentPosition - 1));
            return null;
        }

        consume();

        AST ifStatement = new AST("IfStatement", ifToken.line(), ifToken.column(), main);
        ifStatement.addChild(condition);

        AST body = new AST("Body", ifToken.line(), ifToken.column(), main);

        while (currentPosition < tokens.size() && tokens.get(currentPosition).type() != TokenType.RIGHT_BRACE) {
            AST statement = parseStatement();
            if (statement != null) {
                body.addChild(statement);
            }
        }

        ifStatement.addChild(body);

        if (currentPosition >= tokens.size() || tokens.get(currentPosition).type() != TokenType.RIGHT_BRACE) {
            Token lastToken = currentPosition > 0 && currentPosition < tokens.size() ?
                    tokens.get(currentPosition - 1) : ifToken;
            addError("Expected '}' to close if statement", lastToken);
            return null;
        }

        consume();

        return ifStatement;
    }

    private AST parseWhileStatement() {
        Token whileToken = tokens.get(currentPosition);
        consume();

        if (currentPosition >= tokens.size() || tokens.get(currentPosition).type() != TokenType.LEFT_PAREN) {
            addError("Expected '(' after 'while'", whileToken);
            return null;
        }

        consume();

        AST condition = parseCondition();

        if (currentPosition >= tokens.size() || tokens.get(currentPosition).type() != TokenType.RIGHT_PAREN) {
            Token lastToken = currentPosition > 0 ? tokens.get(currentPosition - 1) : whileToken;
            addError("Expected ')' after condition", lastToken);
            return null;
        }

        consume();

        if (currentPosition >= tokens.size() || tokens.get(currentPosition).type() != TokenType.LEFT_BRACE) {
            addError("Expected '{' after while condition", tokens.get(currentPosition - 1));
            return null;
        }

        consume();

        AST whileStatement = new AST("WhileStatement:", whileToken.line(), whileToken.column(), main);
        whileStatement.addChild(condition);

        AST body = new AST("Body:", whileToken.line(), whileToken.column(), main);

        while (currentPosition < tokens.size() && tokens.get(currentPosition).type() != TokenType.RIGHT_BRACE) {
            AST statement = parseStatement();
            if (statement != null) {
                body.addChild(statement);
            }
        }

        whileStatement.addChild(body);

        if (currentPosition >= tokens.size() || tokens.get(currentPosition).type() != TokenType.RIGHT_BRACE) {
            Token lastToken = currentPosition > 0 && currentPosition < tokens.size() ?
                    tokens.get(currentPosition - 1) : whileToken;
            addError("Expected '}' to close while statement", lastToken);
            return null;
        }

        consume();

        return whileStatement;
    }

    private AST parsePrintStatement() {
        Token printToken = tokens.get(currentPosition);
        consume();

        if (currentPosition >= tokens.size() || tokens.get(currentPosition).type() != TokenType.LEFT_PAREN) {
            addError("Expected '(' after 'print'", printToken);
            return null;
        }

        consume();

        AST expression = parseExpression();

        if (currentPosition >= tokens.size() || tokens.get(currentPosition).type() != TokenType.RIGHT_PAREN) {
            Token lastToken = currentPosition > 0 ? tokens.get(currentPosition - 1) : printToken;
            addError("Expected ')' after expression in print statement", lastToken);
            return null;
        }

        consume();

        if (currentPosition >= tokens.size() || tokens.get(currentPosition).type() != TokenType.SEMICOLON) {
            Token lastToken = currentPosition > 0 ? tokens.get(currentPosition - 1) : printToken;
            addError("Expected ';' after print statement", lastToken);
            return null;
        }

        consume();

        AST printStatement = new AST("PrintStatement", printToken.line(), printToken.column(), main);
        printStatement.addChild(expression);

        return printStatement;
    }

    private AST parseCondition() {
        AST leftExpression = parseExpression();

        if (currentPosition >= tokens.size() || !isRelationalOperator(tokens.get(currentPosition).type())) {
            Token lastToken = currentPosition > 0 ? tokens.get(currentPosition - 1) : tokens.getFirst();
            addError("Expected relational operator in condition", lastToken);
            return null;
        }

        Token operator = tokens.get(currentPosition);
        consume();

        AST rightExpression = parseExpression();

        AST condition = new AST("Condition", operator.line(), operator.column(), main);
        condition.addChild(leftExpression);

        AST operatorNode = new AST("RelationalOperator", operator.value(), operator.line(), operator.column(), main);
        condition.addChild(operatorNode);

        condition.addChild(rightExpression);

        return condition;
    }

    private AST parseExpression() {
        AST leftTerm = parseTerm();

        while (currentPosition < tokens.size() &&
                (tokens.get(currentPosition).type() == TokenType.PLUS ||
                        tokens.get(currentPosition).type() == TokenType.MINUS)) {

            Token operator = tokens.get(currentPosition);
            consume();

            AST rightTerm = parseTerm();

            AST expression = new AST("Expression", operator.line(), operator.column(), main);
            expression.addChild(leftTerm);

            AST operatorNode = new AST("Operator", operator.value(), operator.line(), operator.column(), main);
            expression.addChild(operatorNode);

            expression.addChild(rightTerm);

            leftTerm = expression;
        }

        return leftTerm;
    }

    private AST parseTerm() {
        AST leftFactor = parseFactor();

        while (currentPosition < tokens.size() &&
                (tokens.get(currentPosition).type() == TokenType.MULTIPLY ||
                        tokens.get(currentPosition).type() == TokenType.DIVIDE)) {

            Token operator = tokens.get(currentPosition);
            consume();

            AST rightFactor = parseFactor();

            AST term = new AST("Term", operator.line(), operator.column(), main);
            term.addChild(leftFactor);

            AST operatorNode = new AST("Operator", operator.value(), operator.line(), operator.column(), main);
            term.addChild(operatorNode);

            term.addChild(rightFactor);

            leftFactor = term;
        }

        return leftFactor;
    }

    private AST parseFactor() {
        if (currentPosition >= tokens.size()) {
            Token lastToken = currentPosition > 0 ? tokens.get(currentPosition - 1) : tokens.getFirst();
            addError("Unexpected end of input while parsing factor", lastToken);
            return null;
        }

        Token token = tokens.get(currentPosition);

        switch (token.type()) {
            case NUMBER:
                consume();
                return new AST("Number", token.value(), token.line(), token.column(), main);

            case IDENTIFIER:
                consume();
                return new AST("Identifier", token.value(), token.line(), token.column(), main);

            case LEFT_PAREN:
                consume();
                AST expression = parseExpression();

                if (currentPosition >= tokens.size() || tokens.get(currentPosition).type() != TokenType.RIGHT_PAREN) {
                    Token lastToken = currentPosition > 0 ? tokens.get(currentPosition - 1) : token;
                    addError("Expected ')' to close expression", lastToken);
                    return null;
                }

                consume();
                return expression;

            default:
                addError(STR."Unexpected token in factor: \{token.value()}", token);
                consume();
                return null;
        }
    }

    private boolean isRelationalOperator(TokenType type) {
        return type == TokenType.EQUAL ||
                type == TokenType.NOT_EQUAL ||
                type == TokenType.LESS ||
                type == TokenType.GREATER ||
                type == TokenType.LESS_EQUAL ||
                type == TokenType.GREATER_EQUAL;
    }

    private void consume() {
        currentPosition++;
    }

    public void setTokens(ArrayList<Token> arrayTokens) {
        this.tokens = arrayTokens;
    }

    public void printParseTree() {
        if (rootNode != null) {
            rootNode.printTree(0);
        } else {
            System.out.println("No parse tree available. Run update() first.");
        }
    }

    private void addError(String message, Token token) {
        String errorMsg = String.format("Line %d, Column %d: %s",
                token.line()/2, token.column(), message);
        errors.add(errorMsg);
        System.err.println(errorMsg);
    }

   ArrayList<String> getErrors(){
        return errors;
   }
}