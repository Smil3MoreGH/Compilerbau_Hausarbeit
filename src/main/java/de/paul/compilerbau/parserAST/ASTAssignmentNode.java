package de.paul.compilerbau.parserAST;

public class ASTAssignmentNode extends ASTNode {
    private final String variableName;
    private final ASTNode expression;

    public ASTAssignmentNode(String variableName, ASTNode expression) {
        this.variableName = variableName;
        this.expression = expression;
    }

    @Override
    public String toString(int indent) {
        return getIndent(indent) + "Assignment: " + variableName + " =\n" + expression.toString(indent + 1);
    }
}
