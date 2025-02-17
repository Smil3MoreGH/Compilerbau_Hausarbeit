package de.paul.compilerbau.parser;

public class BinaryExpressionNode extends ASTNode {
    private final ASTNode left;
    private final String operator;
    private final ASTNode right;

    public BinaryExpressionNode(ASTNode left, String operator, ASTNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String toString(int indent) {
        return getIndent(indent) + "BinaryExpression (" + operator + "):\n" +
                left.toString(indent + 1) +
                right.toString(indent + 1);
    }
}
