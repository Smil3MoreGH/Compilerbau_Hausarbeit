package de.paul.compilerbau.parserAST;

/**
 * AST-Knoten für binäre Ausdrücke (z. B. a + b, x < y).
 */
public class ASTBinaryExpressionNode extends ASTNode {
    private final ASTNode left;
    private final String operator;
    private final ASTNode right;

    public ASTBinaryExpressionNode(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public ASTNode getLeft() {
        return left;
    }

    public String getOperator() {
        return operator;
    }

    public ASTNode getRight() {
        return right;
    }

    @Override
    public String toString(int indent) {
        return getIndent(indent) + "BinaryExpression (" + operator + "):\n" +
                left.toString(indent + 1) +
                right.toString(indent + 1);
    }
}
