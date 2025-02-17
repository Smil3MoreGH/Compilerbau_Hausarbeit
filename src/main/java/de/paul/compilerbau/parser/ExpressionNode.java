package de.paul.compilerbau.parser;

public class ExpressionNode extends ASTNode {
    private final String value;

    public ExpressionNode(String value) {
        this.value = value;
    }

    @Override
    public String toString(int indent) {
        return getIndent(indent) + "Expression: " + value + "\n";
    }
}
