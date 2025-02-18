package de.paul.compilerbau.parser;

public class ReturnNode extends ASTNode {
    private final ASTNode returnValue;

    public ReturnNode(ASTNode returnValue) {
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
