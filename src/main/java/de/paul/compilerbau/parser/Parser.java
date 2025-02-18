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
            Token functionName = consume(TokenType.IDENTIFIER, "Erwartet Funktionsnamen für Funktionsaufruf");
            return parseFunctionCall(functionName);
        }

        // 6) Return-Statement
        else if (lookAhead(TokenType.RETURN)) {
            System.out.println("RETURN erkannt! -> parseReturn()");
            return parseReturn();
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
        System.out.println("parseAssignment(): Position " + position);

        boolean hasVar = match(TokenType.VAR);
        System.out.println("hasVar = " + hasVar);

        Token identifier = consume(TokenType.IDENTIFIER, "Erwartet einen Variablennamen");
        System.out.println("Identifier = " + identifier.getValue());

        consume(TokenType.ASSIGN, "Erwartet '='");
        System.out.println("Gefunden: '='");

        ASTNode expression = parseExpression();
        System.out.println("Expression parsed: " + expression);

        consume(TokenType.SEMI, "Erwartet ';'");
        System.out.println("Gefunden: ';'");

        return new AssignmentNode(identifier.getValue(), expression);
    }

    /**
     *  Einfaches (rekursives) Expression-Parsen.
     *  Achtung: Diese Implementierung ist noch sehr simpel und kann zu Problemen
     *  bei komplexeren Ausdrücken führen. Als Demonstration reicht es aber.
     */
    private ASTNode parseExpression() {
        System.out.println("parseExpression(): Position " + position);
        ASTNode left;

        // Erstes Element MUSS eine Zahl, ein Bezeichner oder ein Funktionsaufruf sein
        if (match(TokenType.NUMBER)) {
            left = new ExpressionNode(tokens.get(position - 1).getValue());
            System.out.println("Zahl erkannt: " + left);
        } else if (match(TokenType.IDENTIFIER)) {
            Token identifier = tokens.get(position - 1);

            // Falls danach eine öffnende Klammer kommt -> Funktionsaufruf
            if (lookAhead(TokenType.LPAREN)) {
                System.out.println("Funktionsaufruf in Expression erkannt!");
                left = parseFunctionCall(identifier);
            } else {
                left = new ExpressionNode(identifier.getValue());
                System.out.println("Bezeichner erkannt: " + left);
            }
        } else {
            throw new RuntimeException("Erwartet Zahl oder Variable in Zeile " + peek().getLine());
        }

        // Operatoren verarbeiten (+,-,*,/)
        while (match(TokenType.PLUS) || match(TokenType.MINUS) || match(TokenType.MULT) || match(TokenType.DIV)) {
            Token operator = tokens.get(position - 1);
            System.out.println("Operator erkannt: " + operator.getValue());

            ASTNode right = parseExpression();
            System.out.println("Rechter Ausdruck: " + right);

            left = new BinaryExpressionNode(left, operator.getValue(), right);
            System.out.println("Neue Ausdrucksstruktur: " + left);
        }

        // **Vergleichsoperatoren verarbeiten**
        if (match(TokenType.GT) || match(TokenType.LT) || match(TokenType.GTE) || match(TokenType.LTE) || match(TokenType.EQ) || match(TokenType.NEQ)) {
            Token operator = tokens.get(position - 1);
            System.out.println("Vergleichsoperator erkannt: " + operator.getValue());

            ASTNode right = parseExpression();
            System.out.println("Rechter Ausdruck der Vergleichsoperation: " + right);

            left = new BinaryExpressionNode(left, operator.getValue(), right);
            System.out.println("Neue Vergleichsstruktur: " + left);
        }

        return left;
    }



    /**
     *  Parse FunctionDefinition:
     *   fun identifier ( [identifier (, identifier)*] ) { ... }
     */
    private ASTNode parseFunctionDefinition() {
        System.out.println("parseFunctionDefinition(): Position " + position);

        consume(TokenType.FUN, "Erwartet 'fun'");
        System.out.println("Gefunden: 'fun'");

        Token functionName = consume(TokenType.IDENTIFIER, "Erwartet Funktionsnamen");
        System.out.println("Funktionsname: " + functionName);

        consume(TokenType.LPAREN, "Erwartet '('");
        System.out.println("Gefunden: '('");

        FunctionDefinitionNode functionNode = new FunctionDefinitionNode(functionName.getValue());

        // Parameterliste (optional) parsen
        while (!match(TokenType.RPAREN)) {
            Token param = consume(TokenType.IDENTIFIER, "Erwartet einen Parameter");
            functionNode.addParameter(param.getValue());
            System.out.println("Parameter: " + param);

            if (!match(TokenType.COMMA)) break;
        }

        consume(TokenType.RPAREN, "Erwartet ')'");
        System.out.println("Gefunden: ')'");

        consume(TokenType.LBRACE, "Erwartet '{'");
        System.out.println("Gefunden: '{'");

        while (!match(TokenType.RBRACE)) {
            functionNode.addBodyStatement(parseStatement());
        }

        System.out.println("Funktionsdefinition abgeschlossen.");
        return functionNode;
    }


    /**
     *  Parse Funktionsaufruf:
     *   identifier( <argumente> ) ;
     */
    private ASTNode parseFunctionCall(Token functionNameToken) {
        consume(TokenType.LPAREN, "Erwartet '(' für Funktionsaufruf");
        System.out.println("Funktionsaufruf: " + functionNameToken.getValue());

        FunctionCallNode functionCallNode = new FunctionCallNode(functionNameToken.getValue());

        if (!match(TokenType.RPAREN)) { // Falls Argumente existieren
            do {
                ASTNode argument = parseExpression();
                functionCallNode.addArgument(argument);
                System.out.println("Argument erkannt: " + argument);
            } while (match(TokenType.COMMA));

            consume(TokenType.RPAREN, "Erwartet ')' am Ende des Funktionsaufrufs");
        }

        return functionCallNode;
    }

    /**
     *  Parse If-Else:
     *   if ( condition ) { ... } [ else { ... } ]
     */
    private ASTNode parseIfElse() {
        System.out.println("parseIfElse(): Position " + position);

        consume(TokenType.IF, "Erwartet 'if'");
        System.out.println("Gefunden: 'if'");

        consume(TokenType.LPAREN, "Erwartet '('");
        ASTNode condition = parseExpression();
        consume(TokenType.RPAREN, "Erwartet ')'");
        System.out.println("Bedingung erkannt: " + condition);

        consume(TokenType.LBRACE, "Erwartet '{'");

        IfElseNode ifNode = new IfElseNode(condition);
        while (!match(TokenType.RBRACE)) {
            ifNode.addIfBody(parseStatement());
        }
        System.out.println("IF-Block abgeschlossen.");

        if (match(TokenType.ELSE)) {
            consume(TokenType.LBRACE, "Erwartet '{'");
            while (!match(TokenType.RBRACE)) {
                ifNode.addElseBody(parseStatement());
            }
            System.out.println("ELSE-Block abgeschlossen.");
        }

        return ifNode;
    }


    /**
     *  Parse While:
     *   while ( condition ) { ... }
     */
    private ASTNode parseWhile() {
        System.out.println("parseWhile(): Position " + position);

        consume(TokenType.WHILE, "Erwartet 'while'");
        System.out.println("Gefunden: 'while'");

        consume(TokenType.LPAREN, "Erwartet '('");
        ASTNode condition = parseExpression();
        consume(TokenType.RPAREN, "Erwartet ')'");
        System.out.println("Bedingung erkannt: " + condition);

        consume(TokenType.LBRACE, "Erwartet '{'");

        WhileNode whileNode = new WhileNode(condition);
        while (!match(TokenType.RBRACE)) {
            whileNode.addBodyStatement(parseStatement());
        }
        System.out.println("While-Schleife abgeschlossen.");

        return whileNode;
    }

    private ASTNode parseReturn() {
        System.out.println("parseReturn(): Position " + position);

        consume(TokenType.RETURN, "Erwartet 'return'");
        ASTNode expression = parseExpression();  // Return-Wert parsen
        consume(TokenType.SEMI, "Erwartet ';'");

        System.out.println("Return-Statement erkannt mit Ausdruck: " + expression);
        return new ReturnNode(expression);
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
