package de.paul.compilerbau.parserAST;

import de.paul.compilerbau.scanner.Token;
import de.paul.compilerbau.scanner.TokenType;
import java.util.List;

public class ASTParser {
    private final List<Token> tokens;
    private int position = 0; // Aktuelle Position in der Token-Liste

    public ASTParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public ASTNode parse() {
        System.out.println("Starte Parsing...");
        return parseProgram();
    }

    private ASTNode parseProgram() {
        ASTProgramNode root = new ASTProgramNode();
        System.out.println("Beginne Programmanalyse");

        // Solange wir nicht am Ende (EOF) sind, hole Statements
        while (!match(TokenType.EOF)) {
            root.addChild(parseStatement());
        }
        System.out.println("Parsing abgeschlossen!");
        return root;
    }

    private ASTNode parseStatement() {
        // Print zum debuggen um zu schauen was wie erkannt wurde
        System.out.println("parseProgram(): Position " + position + " | Token: " + peek());
        if (lookAhead(TokenType.VAR)) {
            return parseAssignment();
        } else if (lookAhead(TokenType.IDENTIFIER) && lookAhead(1, TokenType.ASSIGN)) {
            return parseAssignment();
        } else if (lookAhead(TokenType.FUN)) {
            return parseFunctionDefinition();
        } else if (lookAhead(TokenType.IF)) {
            return parseIfElse();
        } else if (lookAhead(TokenType.WHILE)) {
            return parseWhile();
        } else if (lookAhead(TokenType.IDENTIFIER) && lookAhead(1, TokenType.LPAREN)) {
            Token functionName = consume(TokenType.IDENTIFIER, "Funktionsaufruf erwartet");
            return parseFunctionCall(functionName);
        } else if (lookAhead(TokenType.RETURN)) {
            return parseReturn();
        } else {
            throw new RuntimeException("Unerwartetes Token: " + peek().getValue() + " in Zeile " + peek().getLine());
        }
    }

    private ASTNode parseAssignment() {
        boolean hasVar = match(TokenType.VAR);
        Token identifier = consume(TokenType.IDENTIFIER, "Variablenname erwartet");
        consume(TokenType.ASSIGN, "Erwartet '='");
        ASTNode expression = parseExpression();
        consume(TokenType.SEMI, "Erwartet ';'");
        return new ASTAssignmentNode(identifier.getValue(), expression);
    }

    private ASTNode parseExpression() {
        ASTNode term = parseTerm();
        return parseExpressionTail(term);
    }

    private ASTNode parseExpressionTail(ASTNode left) {
        if (match(TokenType.PLUS) || match(TokenType.MINUS)) {
            Token operator = tokens.get(position - 1);
            ASTNode right = parseTerm();
            ASTNode newNode = new ASTBinaryExpressionNode(left, operator.getValue(), right);
            return parseExpressionTail(newNode);
        }
        return left;
    }

    private ASTNode parseTerm() {
        ASTNode factor = parseFactor();
        return parseTermTail(factor);
    }

    private ASTNode parseTermTail(ASTNode left) {
        if (match(TokenType.MULT) || match(TokenType.DIV)) {
            Token operator = tokens.get(position - 1);
            ASTNode right = parseFactor();
            ASTNode newNode = new ASTBinaryExpressionNode(left, operator.getValue(), right);
            return parseTermTail(newNode);
        }
        return left;
    }

    private ASTNode parseFactor() {
        if (match(TokenType.NUMBER)) {
            return new ASTExpressionNode(tokens.get(position - 1).getValue());
        } else if (match(TokenType.IDENTIFIER)) {
            Token identifier = tokens.get(position - 1);
            if (lookAhead(TokenType.LPAREN)) {
                return parseFunctionCall(identifier);
            }
            return new ASTExpressionNode(identifier.getValue());
        } else if (match(TokenType.LPAREN)) {
            ASTNode expression = parseExpression();
            consume(TokenType.RPAREN, "Erwartet ')' ");
            return expression;
        } else {
            throw new RuntimeException("Ungültiger Faktor in Zeile " + peek().getLine());
        }
    }

    private ASTNode parseComparison() {
        ASTNode left = parseExpression();
        if (match(TokenType.GT) || match(TokenType.LT) || match(TokenType.GTE) || match(TokenType.LTE) || match(TokenType.EQ) || match(TokenType.NEQ)) {
            Token operator = tokens.get(position - 1);
            ASTNode right = parseExpression();
            return new ASTBinaryExpressionNode(left, operator.getValue(), right);
        }
        return left;
    }

    private ASTNode parseFunctionDefinition() {
        consume(TokenType.FUN, "Erwartet 'fun'");
        Token functionName = consume(TokenType.IDENTIFIER, "Funktionsname erwartet");
        consume(TokenType.LPAREN, "Erwartet '(' ");

        ASTFunctionDefinitionNode functionNode = new ASTFunctionDefinitionNode(functionName.getValue());
        parseParameterList(functionNode);

        consume(TokenType.RPAREN, "Erwartet ')' ");
        consume(TokenType.LBRACE, "Erwartet '{'");

        while (!match(TokenType.RBRACE)) {
            functionNode.addBodyStatement(parseStatement());
        }
        return functionNode;
    }

    private void parseParameterList(ASTFunctionDefinitionNode functionNode) {
        if (lookAhead(TokenType.IDENTIFIER)) {
            functionNode.addParameter(consume(TokenType.IDENTIFIER, "Parameter erwartet").getValue());
            parseParameterListTail(functionNode);
        }
    }

    private void parseParameterListTail(ASTFunctionDefinitionNode functionNode) {
        if (match(TokenType.COMMA)) {
            functionNode.addParameter(consume(TokenType.IDENTIFIER, "Parameter erwartet").getValue());
            parseParameterListTail(functionNode);
        }
    }

    private ASTNode parseFunctionCall(Token functionNameToken) {
        consume(TokenType.LPAREN, "Erwartet '(' für Funktionsaufruf");
        ASTFunctionCallNode callNode = new ASTFunctionCallNode(functionNameToken.getValue());
        parseArgumentList(callNode);
        consume(TokenType.RPAREN, "Erwartet ')' ");
        return callNode;
    }

    private void parseArgumentList(ASTFunctionCallNode callNode) {
        if (!lookAhead(TokenType.RPAREN)) {
            callNode.addArgument(parseExpression());
            parseArgumentListTail(callNode);
        }
    }

    private void parseArgumentListTail(ASTFunctionCallNode callNode) {
        if (match(TokenType.COMMA)) {
            callNode.addArgument(parseExpression());
            parseArgumentListTail(callNode);
        }
    }

    private ASTNode parseIfElse() {
        consume(TokenType.IF, "Erwartet 'if'");
        consume(TokenType.LPAREN, "Erwartet '(' ");
        ASTNode condition = parseComparison();
        consume(TokenType.RPAREN, "Erwartet ')' ");

        ASTIfElseNode ifNode = new ASTIfElseNode(condition);
        consume(TokenType.LBRACE, "Erwartet '{'");
        while (!match(TokenType.RBRACE)) {
            ifNode.addIfBody(parseStatement());
        }

        if (match(TokenType.ELSE)) {
            consume(TokenType.LBRACE, "Erwartet '{'");
            while (!match(TokenType.RBRACE)) {
                ifNode.addElseBody(parseStatement());
            }
        }
        return ifNode;
    }

    private ASTNode parseWhile() {
        consume(TokenType.WHILE, "Erwartet 'while'");
        consume(TokenType.LPAREN, "Erwartet '(' ");
        ASTNode condition = parseComparison();
        consume(TokenType.RPAREN, "Erwartet ')' ");

        ASTWhileNode ASTWhileNode = new ASTWhileNode(condition);
        consume(TokenType.LBRACE, "Erwartet '{'");
        while (!match(TokenType.RBRACE)) {
            ASTWhileNode.addBodyStatement(parseStatement());
        }
        return ASTWhileNode;
    }

    private ASTNode parseReturn() {
        consume(TokenType.RETURN, "Erwartet 'return'");
        ASTNode expression = parseExpression();
        consume(TokenType.SEMI, "Erwartet ';'");
        return new ASTReturnNode(expression);
    }

    private boolean match(TokenType type) {
        if (position < tokens.size() && tokens.get(position).getType() == type) {
            position++;
            return true;
        }
        return false;
    }

    private Token consume(TokenType type, String errorMessage) {
        if (match(type)) {
            return tokens.get(position - 1);
        }
        throw new RuntimeException(errorMessage + " in Zeile " + peek().getLine());
    }

    private Token peek() {
        return tokens.get(position);
    }

    private boolean lookAhead(TokenType type) {
        return position < tokens.size() && tokens.get(position).getType() == type;
    }

    private boolean lookAhead(int offset, TokenType type) {
        return (position + offset < tokens.size()) && (tokens.get(position + offset).getType() == type);
    }
}
