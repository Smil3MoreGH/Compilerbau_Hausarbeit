package de.paul.compilerbau.scanner;

import java.io.*;
import java.util.*;

/**
 * Wandelt Quellcode in eine Liste von Tokens zur weiteren syntaktischen Analyse um.
 */
public class Scanner {
    private final String input;  // Eingabequelle (Quellcode als String)
    private int position = 0;    // Aktuelle Position im Eingabestring
    private int line = 1;        // Aktuelle Zeilennummer (für Fehlermeldungen)

    // Schlüsselwörter der Sprache und zugehörige Token-Typen
    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        // Initialisierung der unterstützten Schlüsselwörter
        keywords.put("var", TokenType.VAR);
        keywords.put("fun", TokenType.FUN);
        keywords.put("return", TokenType.RETURN);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("while", TokenType.WHILE);
    }

    public Scanner(String input) {
        this.input = input;
    }

    // Führt die lexikalische Analyse durch und liefert eine Tokenliste
    public List<Token> scan() {
        List<Token> tokens = new ArrayList<>();

        while (position < input.length()) {
            char current = input.charAt(position);

            // Überspringe Leerzeichen und Zeilenumbrüche
            if (Character.isWhitespace(current)) {
                if (current == '\n') {
                    line++;  // Zeilenzähler erhöhen
                }
                position++;
                continue;
            }

            // Verarbeite Identifier oder Schlüsselwörter
            if (Character.isLetter(current)) {
                tokens.add(matchKeywordOrIdentifier());
                continue;
            }

            // Verarbeite Zahlenliterale
            if (Character.isDigit(current)) {
                tokens.add(matchNumber());
                continue;
            }

            // Verarbeite Operatoren und Sonderzeichen
            Token opToken = matchOperator();
            if (opToken != null) {
                tokens.add(opToken);
                continue;
            }

            // Fehler bei unbekannten Zeichen
            throw new RuntimeException("Unbekanntes Zeichen: " + current + " in Zeile " + line);
        }

        // Hänge abschließendes EOF-Token an
        tokens.add(new Token(TokenType.EOF, "", line));
        return tokens;
    }

    // Erkennt Identifier oder Schlüsselwörter
    private Token matchKeywordOrIdentifier() {
        int start = position;
        while (position < input.length() && Character.isLetterOrDigit(input.charAt(position))) {
            position++;
        }
        String word = input.substring(start, position);
        TokenType type = keywords.getOrDefault(word, TokenType.IDENTIFIER);
        return new Token(type, word, line);
    }

    // Erkennt Ganzzahlen
    private Token matchNumber() {
        int start = position;
        while (position < input.length() && Character.isDigit(input.charAt(position))) {
            position++;
        }
        String number = input.substring(start, position);
        return new Token(TokenType.NUMBER, number, line);
    }

    // Erkennt Operatoren und Sonderzeichen (+, -, ==, !=, etc.)
    private Token matchOperator() {
        char current = input.charAt(position);
        position++;

        switch (current) {
            case '+': return new Token(TokenType.PLUS, "+", line);
            case '-': return new Token(TokenType.MINUS, "-", line);
            case '*': return new Token(TokenType.MULT, "*", line);
            case '/': return new Token(TokenType.DIV, "/", line);
            case '=':
                if (position < input.length() && input.charAt(position) == '=') {
                    position++;
                    return new Token(TokenType.EQ, "==", line);
                }
                return new Token(TokenType.ASSIGN, "=", line);
            case '!':
                if (position < input.length() && input.charAt(position) == '=') {
                    position++;
                    return new Token(TokenType.NEQ, "!=", line);
                }
                break;
            case '<':
                if (position < input.length() && input.charAt(position) == '=') {
                    position++;
                    return new Token(TokenType.LTE, "<=", line);
                }
                return new Token(TokenType.LT, "<", line);
            case '>':
                if (position < input.length() && input.charAt(position) == '=') {
                    position++;
                    return new Token(TokenType.GTE, ">=", line);
                }
                return new Token(TokenType.GT, ">", line);
            case '(': return new Token(TokenType.LPAREN, "(", line);
            case ')': return new Token(TokenType.RPAREN, ")", line);
            case '{': return new Token(TokenType.LBRACE, "{", line);
            case '}': return new Token(TokenType.RBRACE, "}", line);
            case ';': return new Token(TokenType.SEMI, ";", line);
            case ',': return new Token(TokenType.COMMA, ",", line);
        }
        return null;  // Kein gültiger Operator erkannt
    }
}
