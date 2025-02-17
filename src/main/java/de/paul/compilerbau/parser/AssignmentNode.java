package de.paul.compilerbau.parser;

public class AssignmentNode extends ASTNode {
    private final String variableName;
    private final ASTNode expression;

    public AssignmentNode(String variableName, ASTNode expression) {
        this.variableName = variableName;
        this.expression = expression;
    }

    @Override
    public String toString(int indent) {
        return getIndent(indent) + "Assignment: " + variableName + " =\n" + expression.toString(indent + 1);
    }
}
