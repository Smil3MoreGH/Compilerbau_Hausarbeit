package de.paul.compilerbau.scanner;

public enum TokenType {
    // Schlüsselwörter
    VAR, FUN, RETURN, IF, ELSE, WHILE,

    // Operatoren
    PLUS, MINUS, MULT, DIV, ASSIGN,

    // Vergleichsoperatoren
    EQ, NEQ, LT, GT, LTE, GTE,

    // Struktur
    LPAREN, RPAREN, LBRACE, RBRACE, SEMI, COMMA,

    // Werte
    NUMBER, IDENTIFIER,

    // Sonstiges
    EOF  // Ende der Datei
}
