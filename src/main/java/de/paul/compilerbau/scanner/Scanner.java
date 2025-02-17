package de.paul.compilerbau.scanner;

import java.io.*;
import java.util.*;

public class Scanner {
    private final String input;  // Quellcode als String
    private int position = 0;    // Aktuelle Zeichenposition
    private int line = 1;        // Zeilennummer für Fehlermeldungen

    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        // Definiere alle Schlüsselwörter der Sprache PAUL
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

    public List<Token> scan() {
        List<Token> tokens = new ArrayList<>();

        while (position < input.length()) {
            char current = input.charAt(position);

            // Ignoriere Leerzeichen und Tabs
            if (Character.isWhitespace(current)) {
                if (current == '\n') {
                    line++;
                }
                position++;
                continue;
            }

            // Identifikatoren & Schlüsselwörter
            if (Character.isLetter(current)) {
                tokens.add(matchKeywordOrIdentifier());
                continue;
            }

            // Zahlen
            if (Character.isDigit(current)) {
                tokens.add(matchNumber());
                continue;
            }

            // Operatoren und Sonderzeichen
            Token opToken = matchOperator();
            if (opToken != null) {
                tokens.add(opToken);
                continue;
            }

            throw new RuntimeException("Unbekanntes Zeichen: " + current + " in Zeile " + line);
        }

        // Am Ende ein EOF-Token anhängen
        tokens.add(new Token(TokenType.EOF, "", line));
        return tokens;
    }

    private Token matchKeywordOrIdentifier() {
        int start = position;
        while (position < input.length() && Character.isLetterOrDigit(input.charAt(position))) {
            position++;
        }
        String word = input.substring(start, position);
        TokenType type = keywords.getOrDefault(word, TokenType.IDENTIFIER);
        return new Token(type, word, line);
    }

    private Token matchNumber() {
        int start = position;
        while (position < input.length() && Character.isDigit(input.charAt(position))) {
            position++;
        }
        String number = input.substring(start, position);
        return new Token(TokenType.NUMBER, number, line);
    }

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
        return null;
    }
}
