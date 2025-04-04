package de.paul.compilerbau.parserAST;

import de.paul.compilerbau.scanner.Token;
import de.paul.compilerbau.scanner.TokenType;
import java.util.List;

/**
 * Wandelt eine Liste von Tokens in einen abstrakten Syntaxbaum (AST) um.
 */
public class ASTParser {
    private final List<Token> tokens;
    private int position = 0; // Aktuelle Position in der Tokenliste

    public ASTParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // Einstiegspunkt für das Parsen
    public ASTNode parse() {
        return parseProgram();
    }

    // Parsen eines gesamten Programms (besteht aus beliebig vielen Statements)
    private ASTNode parseProgram() {
        ASTProgramNode root = new ASTProgramNode();

        // Solange wir nicht das Ende erreichen, werden Statements geparst
        while (!match(TokenType.EOF)) {
            root.addChild(parseStatement());
        }
        return root;
    }

    // Parsen eines einzelnen Statements
    private ASTNode parseStatement() {
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

    // Parsen einer Variablenzuweisung (mit optionalem 'var')
    private ASTNode parseAssignment() {
        boolean hasVar = match(TokenType.VAR); // kann optional sein
        Token identifier = consume(TokenType.IDENTIFIER, "Variablenname erwartet");
        consume(TokenType.ASSIGN, "Erwartet '='");
        ASTNode expression = parseExpression();
        consume(TokenType.SEMI, "Erwartet ';'");
        return new ASTAssignmentNode(identifier.getValue(), expression);
    }

    // Ausdrucksparsing
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

    // Parsing von Multiplikation/Division (höhere Priorität als + / -)
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

    // Parsen einzelner Werte: Zahlen, Variablen, Klammerausdrücke oder Funktionsaufrufe
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

    // Parsen eines Vergleichsausdrucks
    private ASTNode parseComparison() {
        ASTNode left = parseExpression();
        if (match(TokenType.GT) || match(TokenType.LT) || match(TokenType.GTE) || match(TokenType.LTE) || match(TokenType.EQ) || match(TokenType.NEQ)) {
            Token operator = tokens.get(position - 1);
            ASTNode right = parseExpression();
            return new ASTBinaryExpressionNode(left, operator.getValue(), right);
        }
        return left;
    }

    // Parsen einer Funktionsdefinition
    private ASTNode parseFunctionDefinition() {
        consume(TokenType.FUN, "Erwartet 'fun'");
        Token functionName = consume(TokenType.IDENTIFIER, "Funktionsname erwartet");
        consume(TokenType.LPAREN, "Erwartet '(' ");

        ASTFunctionDefinitionNode functionNode = new ASTFunctionDefinitionNode(functionName.getValue());
        parseParameterList(functionNode);

        consume(TokenType.RPAREN, "Erwartet ')' ");
        consume(TokenType.LBRACE, "Erwartet '{'");

        // Funktionsrumpf
        while (!match(TokenType.RBRACE)) {
            functionNode.addBodyStatement(parseStatement());
        }
        return functionNode;
    }

    // Parameterliste in Funktionsdefinition
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

    // Parsen eines Funktionsaufrufs mit Argumenten
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

    // Parsen eines If-Else-Statements
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

    // Parsen einer While-Schleife
    private ASTNode parseWhile() {
        consume(TokenType.WHILE, "Erwartet 'while'");
        consume(TokenType.LPAREN, "Erwartet '(' ");
        ASTNode condition = parseComparison();
        consume(TokenType.RPAREN, "Erwartet ')' ");

        ASTWhileNode whileNode = new ASTWhileNode(condition);
        consume(TokenType.LBRACE, "Erwartet '{'");
        while (!match(TokenType.RBRACE)) {
            whileNode.addBodyStatement(parseStatement());
        }
        return whileNode;
    }

    // Parsen eines Return-Statements
    private ASTNode parseReturn() {
        consume(TokenType.RETURN, "Erwartet 'return'");
        ASTNode expression = parseExpression();
        consume(TokenType.SEMI, "Erwartet ';'");
        return new ASTReturnNode(expression);
    }

    // Versucht, einen bestimmten Token zu "matchen" (und zu verbrauchen)
    private boolean match(TokenType type) {
        if (position < tokens.size() && tokens.get(position).getType() == type) {
            position++;
            return true;
        }
        return false;
    }

    // Konsumiert einen bestimmten Token oder wirft Fehler
    private Token consume(TokenType type, String errorMessage) {
        if (match(type)) {
            return tokens.get(position - 1);
        }
        throw new RuntimeException(errorMessage + " in Zeile " + peek().getLine());
    }

    // Gibt den aktuellen Token zurück
    private Token peek() {
        return tokens.get(position);
    }

    // Vorschau auf Token (ohne ihn zu konsumieren)
    private boolean lookAhead(TokenType type) {
        return position < tokens.size() && tokens.get(position).getType() == type;
    }

    // Vorschau auf Token mit Offset
    private boolean lookAhead(int offset, TokenType type) {
        return (position + offset < tokens.size()) && (tokens.get(position + offset).getType() == type);
    }
}
