package de.paul.compilerbau.parserAST;

/**
 * AST-Knoten für einen Ausdruck (z. B. Variable oder Konstante).
 */
public class ASTExpressionNode extends ASTNode {
    private final String value;

    public ASTExpressionNode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString(int indent) {
        return getIndent(indent) + "Expression: " + value + "\n";
    }
}
