package de.paul.compilerbau.parserAST;

/**
 * AST-Knoten für eine Zuweisung (z.B. x = 5).
 */
public class ASTAssignmentNode extends ASTNode {
    private final String variableName;
    private final ASTNode expression;

    public ASTAssignmentNode(String variableName, ASTNode expression) {
        this.variableName = variableName;
        this.expression = expression;
    }

    public String getVariableName() {
        return variableName;
    }

    public ASTNode getExpression() {
        return expression;
    }

    @Override
    public String toString(int indent) {
        return getIndent(indent) + "Assignment: " + variableName + " =\n" + expression.toString(indent + 1);
    }
}
