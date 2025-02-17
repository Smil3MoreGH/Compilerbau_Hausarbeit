// Aufgabe: Prüft, ob die Token-Reihenfolge gültig ist und erstellt den AST (Abstract Syntax Tree).
// Diese Klasse nimmt die Liste der Tokens und startet den Parsing-Prozess.

package de.paul.compilerbau.parser;

import de.paul.compilerbau.scanner.Token;
import de.paul.compilerbau.scanner.TokenType;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int position = 0; // Aktuelle Position in der Token-Liste

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // Starte das Parsen eines Programms
    public ASTNode parse() {
        return parseProgram();
    }

    private ASTNode parseProgram() {
        ProgramNode root = new ProgramNode();

        while (!match(TokenType.EOF)) {
            root.addChild(parseStatement());
        }

        return root;
    }

    private ASTNode parseStatement() {
        if (lookAhead(TokenType.IDENTIFIER) && lookAhead(1, TokenType.ASSIGN)) {
            return parseAssignment();
        } else if (match(TokenType.VAR)) {
            return parseAssignment();
        } else if (match(TokenType.FUN)) {
            return parseFunctionDefinition();
        } else if (match(TokenType.IF)) {
            return parseIfElse();
        } else if (match(TokenType.WHILE)) {
            return parseWhile();
        } else if (lookAhead(TokenType.IDENTIFIER) && lookAhead(1, TokenType.LPAREN)) {
            return parseFunctionCall();
        } else {
            throw new RuntimeException("Unerwartetes Token: " + peek().getValue() + " in Zeile " + peek().getLine());
        }
    }

    private ASTNode parseAssignment() {
        boolean hasVar = match(TokenType.VAR);  // `var` ist optional
        Token identifier = consume(TokenType.IDENTIFIER, "Erwartet einen Variablennamen");
        consume(TokenType.ASSIGN, "Erwartet '='");
        ASTNode expression = parseExpression();
        consume(TokenType.SEMI, "Erwartet ';'");

        return new AssignmentNode(identifier.getValue(), expression);
    }

    private ASTNode parseExpression() {
        ASTNode left;

        if (match(TokenType.NUMBER)) {
            left = new ExpressionNode(tokens.get(position - 1).getValue());
        } else if (match(TokenType.IDENTIFIER)) {
            left = new ExpressionNode(tokens.get(position - 1).getValue());
        } else {
            throw new RuntimeException("Erwartet Zahl oder Variable in Zeile " + peek().getLine());
        }

        while (match(TokenType.PLUS) || match(TokenType.MINUS) || match(TokenType.MULT) || match(TokenType.DIV)) {
            Token operator = tokens.get(position - 1);
            ASTNode right = parseExpression();
            left = new BinaryExpressionNode(left, operator.getValue(), right);
        }

        return left;
    }

    private ASTNode parseFunctionDefinition() {
        consume(TokenType.FUN, "Erwartet 'fun'");
        Token functionName = consume(TokenType.IDENTIFIER, "Erwartet Funktionsnamen");
        consume(TokenType.LPAREN, "Erwartet '('");

        FunctionDefinitionNode functionNode = new FunctionDefinitionNode(functionName.getValue());

        while (!match(TokenType.RPAREN)) {
            Token param = consume(TokenType.IDENTIFIER, "Erwartet einen Parameter");
            functionNode.addParameter(param.getValue());
            if (!match(TokenType.COMMA)) break;
        }

        consume(TokenType.LBRACE, "Erwartet '{'");
        while (!match(TokenType.RBRACE)) {
            functionNode.addBodyStatement(parseStatement());
        }

        return functionNode;
    }

    private ASTNode parseFunctionCall() {
        // Funktionsname holen
        Token functionName = consume(TokenType.IDENTIFIER, "Erwartet Funktionsnamen");
        consume(TokenType.LPAREN, "Erwartet '('");

        // Erstelle Funktionsaufruf-Knoten
        FunctionCallNode functionCallNode = new FunctionCallNode(functionName.getValue());

        // Argumente parsen
        if (!match(TokenType.RPAREN)) {  // Falls nicht direkt ")"
            do {
                functionCallNode.addArgument(parseExpression());  // Argument parsen
            } while (match(TokenType.COMMA));  // Falls weitere Argumente vorhanden sind
            consume(TokenType.RPAREN, "Erwartet ')'");
        }

        consume(TokenType.SEMI, "Erwartet ';'"); // Funktionsaufrufe enden mit ";"
        return functionCallNode;
    }

    private ASTNode parseIfElse() {
        consume(TokenType.IF, "Erwartet 'if'");
        consume(TokenType.LPAREN, "Erwartet '('");
        ASTNode condition = parseExpression();
        consume(TokenType.RPAREN, "Erwartet ')'");
        consume(TokenType.LBRACE, "Erwartet '{'");

        IfElseNode ifNode = new IfElseNode(condition);

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
        consume(TokenType.LPAREN, "Erwartet '('");
        ASTNode condition = parseExpression();
        consume(TokenType.RPAREN, "Erwartet ')'");
        consume(TokenType.LBRACE, "Erwartet '{'");

        WhileNode whileNode = new WhileNode(condition);

        while (!match(TokenType.RBRACE)) {
            whileNode.addBodyStatement(parseStatement());
        }

        return whileNode;
    }

    // Token-Hilfsfunktionen
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
        return position + offset < tokens.size() && tokens.get(position + offset).getType() == type;
    }
}
