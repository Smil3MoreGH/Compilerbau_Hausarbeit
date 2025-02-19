package de.paul.compilerbau.parserAST;

public class ASTReturnNode extends ASTNode {
    private final ASTNode returnValue;

    public ASTReturnNode(ASTNode returnValue) {
        this.returnValue = returnValue;
    }

    public ASTNode getReturnValue() {
        return returnValue;
    }

    @Override
    public String toString(int indent) {
        return getIndent(indent) + "Return:\n" + returnValue.toString(indent + 1);
    }
}
