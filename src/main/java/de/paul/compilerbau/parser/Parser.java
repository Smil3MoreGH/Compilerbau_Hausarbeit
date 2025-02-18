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
        System.out.println("Starte Parsing...");
        return parseProgram();
    }

    private ASTNode parseProgram() {
        ProgramNode root = new ProgramNode();
        System.out.println("Beginne Programmanalyse");

        // Solange wir nicht am Ende (EOF) sind, hole Statements
        while (!match(TokenType.EOF)) {
            System.out.println("parseProgram(): Position " + position + " | Token: " + peek());
            root.addChild(parseStatement());
        }

        System.out.println("Parsing abgeschlossen!");
        return root;
    }

    /**
     * Bestimmt auf Basis des LookAhead, welches Statement wir parsen müssen.
     */
    private ASTNode parseStatement() {
        System.out.println("parseStatement(): Position " + position + " | Token: " + peek());

        // 1) Assignment: var ... oder identifier ...
        if (lookAhead(TokenType.VAR)) {
            System.out.println("VAR erkannt! -> parseAssignment()");
            return parseAssignment();
        }
        // ODER: Nur ein Bezeichner gefolgt von =
        else if (lookAhead(TokenType.IDENTIFIER) && lookAhead(1, TokenType.ASSIGN)) {
            System.out.println("IDENTIFIER + ASSIGN erkannt! -> parseAssignment()");
            return parseAssignment();
        }

        // 2) Function Definition
        else if (lookAhead(TokenType.FUN)) {
            System.out.println("FUN erkannt! -> parseFunctionDefinition()");
            return parseFunctionDefinition();
        }

        // 3) If-Else
        else if (lookAhead(TokenType.IF)) {
            System.out.println("IF erkannt! -> parseIfElse()");
            return parseIfElse();
        }

        // 4) While
        else if (lookAhead(TokenType.WHILE)) {
            System.out.println("WHILE erkannt! -> parseWhile()");
            return parseWhile();
        }

        // 5) Function Call: identifier (
        else if (lookAhead(TokenType.IDENTIFIER) && lookAhead(1, TokenType.LPAREN)) {
            System.out.println("FunctionCall erkannt! -> parseFunctionCall()");
            return parseFunctionCall();
        }

        // Wenn nichts passt, ist es ein Fehler:
        else {
            throw new RuntimeException(
                    "Unerwartetes Token: " + peek().getValue() + " in Zeile " + peek().getLine()
            );
        }
    }

    /**
     *  Parse Assignment:
     *   - Optionales 'var'
     *   - identifier
     *   - '='
     *   - expression
     *   - ';'
     */
    private ASTNode parseAssignment() {
        // 'var' ist optional; match() gibt true zurück, wenn der Token da ist, und erhöht position
        boolean hasVar = match(TokenType.VAR);

        // Jetzt MUSS ein Bezeichner kommen:
        Token identifier = consume(TokenType.IDENTIFIER, "Erwartet einen Variablennamen");

        // Jetzt MUSS '=' kommen:
        consume(TokenType.ASSIGN, "Erwartet '='");

        // Dann ein Ausdruck:
        ASTNode expression = parseExpression();

        // Und schließlich ';'
        consume(TokenType.SEMI, "Erwartet ';'");

        return new AssignmentNode(identifier.getValue(), expression);
    }

    /**
     *  Einfaches (rekursives) Expression-Parsen.
     *  Achtung: Diese Implementierung ist noch sehr simpel und kann zu Problemen
     *  bei komplexeren Ausdrücken führen. Als Demonstration reicht es aber.
     */
    private ASTNode parseExpression() {
        ASTNode left;

        // Erstes Element MUSS eine Zahl ODER ein Bezeichner sein
        if (match(TokenType.NUMBER)) {
            left = new ExpressionNode(tokens.get(position - 1).getValue());
        } else if (match(TokenType.IDENTIFIER)) {
            left = new ExpressionNode(tokens.get(position - 1).getValue());
        } else {
            throw new RuntimeException("Erwartet Zahl oder Variable in Zeile " + peek().getLine());
        }

        // Danach können beliebig viele Operatoren (+,-,*,/) folgen, gefolgt von neuer Expression
        while (match(TokenType.PLUS) || match(TokenType.MINUS) || match(TokenType.MULT) || match(TokenType.DIV)) {
            Token operator = tokens.get(position - 1);
            ASTNode right = parseExpression();
            left = new BinaryExpressionNode(left, operator.getValue(), right);
        }

        return left;
    }

    /**
     *  Parse FunctionDefinition:
     *   fun identifier ( [identifier (, identifier)*] ) { ... }
     */
    private ASTNode parseFunctionDefinition() {
        // Hier verbrauchen wir den FUN-Token nur ein einziges Mal!
        consume(TokenType.FUN, "Erwartet 'fun'");

        Token functionName = consume(TokenType.IDENTIFIER, "Erwartet Funktionsnamen");
        consume(TokenType.LPAREN, "Erwartet '('");

        // Erzeuge Funktionsdefinition-Knoten
        FunctionDefinitionNode functionNode = new FunctionDefinitionNode(functionName.getValue());

        // Parameterliste (optional) parsen
        while (!match(TokenType.RPAREN)) {
            Token param = consume(TokenType.IDENTIFIER, "Erwartet einen Parameter");
            functionNode.addParameter(param.getValue());
            if (!match(TokenType.COMMA)) break; // Wenn kein Komma da, Parameterliste zu Ende
        }

        consume(TokenType.LBRACE, "Erwartet '{'");

        // Funktionskörper parsen, bis '}'
        while (!match(TokenType.RBRACE)) {
            functionNode.addBodyStatement(parseStatement());
        }

        return functionNode;
    }

    /**
     *  Parse Funktionsaufruf:
     *   identifier( <argumente> ) ;
     */
    private ASTNode parseFunctionCall() {
        // Der erste Token MUSS ein Identifier sein
        Token functionName = consume(TokenType.IDENTIFIER, "Erwartet Funktionsnamen");
        consume(TokenType.LPAREN, "Erwartet '('");

        FunctionCallNode functionCallNode = new FunctionCallNode(functionName.getValue());

        // Argumente (optional)
        if (!match(TokenType.RPAREN)) {
            // Wenn wir die ')' nicht sofort gefunden haben, gibt es mindestens ein Argument
            do {
                functionCallNode.addArgument(parseExpression());
            } while (match(TokenType.COMMA)); // mehrere Argumente mit Komma
            consume(TokenType.RPAREN, "Erwartet ')'");
        }

        // Ende mit Semikolon
        consume(TokenType.SEMI, "Erwartet ';'");
        return functionCallNode;
    }

    /**
     *  Parse If-Else:
     *   if ( condition ) { ... } [ else { ... } ]
     */
    private ASTNode parseIfElse() {
        consume(TokenType.IF, "Erwartet 'if'");
        consume(TokenType.LPAREN, "Erwartet '('");
        ASTNode condition = parseExpression();
        consume(TokenType.RPAREN, "Erwartet ')'");
        consume(TokenType.LBRACE, "Erwartet '{'");

        IfElseNode ifNode = new IfElseNode(condition);

        // IF-Block
        while (!match(TokenType.RBRACE)) {
            ifNode.addIfBody(parseStatement());
        }

        // Optionales ELSE
        if (match(TokenType.ELSE)) {
            consume(TokenType.LBRACE, "Erwartet '{'");
            while (!match(TokenType.RBRACE)) {
                ifNode.addElseBody(parseStatement());
            }
        }

        return ifNode;
    }

    /**
     *  Parse While:
     *   while ( condition ) { ... }
     */
    private ASTNode parseWhile() {
        consume(TokenType.WHILE, "Erwartet 'while'");
        consume(TokenType.LPAREN, "Erwartet '('");
        ASTNode condition = parseExpression();
        consume(TokenType.RPAREN, "Erwartet ')'");
        consume(TokenType.LBRACE, "Erwartet '{'");

        WhileNode whileNode = new WhileNode(condition);

        // Schleifen-Body
        while (!match(TokenType.RBRACE)) {
            whileNode.addBodyStatement(parseStatement());
        }

        return whileNode;
    }

    // ----------------------------------------------------------------
    // Hilfsfunktionen für Token-Abgleich und -Konsumierung
    // ----------------------------------------------------------------

    /**
     * match() prüft, ob der aktuelle Token vom erwarteten Typ ist.
     * Wenn ja, wird position um 1 erhöht und true zurückgegeben.
     * Wenn nein, bleibt position unverändert und es kommt false.
     */
    private boolean match(TokenType type) {
        if (position < tokens.size() && tokens.get(position).getType() == type) {
            position++;
            return true;
        }
        return false;
    }

    /**
     * consume() erwartet den übergebenen TokenType an aktueller Position.
     * Wenn er stimmt, wird position erhöht und der Token zurückgegeben.
     * Sonst wird eine Fehlermeldung geworfen.
     */
    private Token consume(TokenType type, String errorMessage) {
        if (match(type)) {
            return tokens.get(position - 1);
        }
        throw new RuntimeException(errorMessage + " in Zeile " + peek().getLine());
    }

    /**
     * peek() gibt den aktuellen Token zurück (ohne die Position zu verändern).
     */
    private Token peek() {
        return tokens.get(position);
    }

    /**
     * Schaut, ob der aktuelle Token vom gegebenen Typ ist,
     * ohne die Position zu verändern.
     */
    private boolean lookAhead(TokenType type) {
        return position < tokens.size() && tokens.get(position).getType() == type;
    }

    /**
     * lookAhead(offset, type) schaut 'offset' Tokens nach vorne,
     * ohne die Position zu verändern.
     */
    private boolean lookAhead(int offset, TokenType type) {
        return (position + offset < tokens.size())
                && (tokens.get(position + offset).getType() == type);
    }
}
