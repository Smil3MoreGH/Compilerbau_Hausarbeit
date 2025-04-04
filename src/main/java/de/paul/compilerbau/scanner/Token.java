package de.paul.compilerbau.scanner;

/**
 * Repräsentiert ein einzelnes Token im Quellcode.
 */
public class Token {
    private final TokenType type;    // Typ des Tokens (IDENTIFIER, PLUS, RETURN, etc.)
    private final String value;      // Der tatsächliche Wert aus dem Quellcode
    private final int line;          // Zeilennummer im Quellcode

    public Token(TokenType type, String value, int line) {
        this.type = type;
        this.value = value;
        this.line = line;
    }

    // Liefert den Token-Typ
    public TokenType getType() {
        return type;
    }

    // Liefert den Token-Text (Name eines Identifiers oder Zahl)
    public String getValue() {
        return value;
    }

    // Liefert die Zeile, in der das Token gefunden wurde
    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        // Formatierte Darstellung für Debugging und Logging
        return "Token{" +
                "type=" + type +
                ", value='" + value + '\'' +
                ", line=" + line +
                '}';
    }
}
