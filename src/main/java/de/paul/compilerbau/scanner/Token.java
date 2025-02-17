package de.paul.compilerbau.scanner;

public class Token {
    private final TokenType type;  // Token-Typ (z. B. VAR, IDENTIFIER, NUMBER)
    private final String value;    // Der Wert des Tokens (z. B. "x", "10", "+")
    private final int line;        // Zeilennummer im Code (nützlich für Fehler)

    public Token(TokenType type, String value, int line) {
        this.type = type;
        this.value = value;
        this.line = line;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value='" + value + '\'' +
                ", line=" + line +
                '}';
    }
}