package de.paul.compilerbau.scanner;

/**
 * Aufzählung aller unterstützten Token-Typen.
 */
public enum TokenType {
    // Schlüsselwörter der Sprache
    VAR, FUN, RETURN, IF, ELSE, WHILE,

    // Arithmetische Operatoren
    PLUS, MINUS, MULT, DIV, ASSIGN,

    // Vergleichsoperatoren
    EQ, NEQ, LT, GT, LTE, GTE,

    // Strukturzeichen (z. B. Klammern, Semikolon, etc.)
    LPAREN, RPAREN, LBRACE, RBRACE, SEMI, COMMA,

    // Literale und Identifier
    NUMBER, IDENTIFIER,

    // Dateiende (End of File)
    EOF
}
