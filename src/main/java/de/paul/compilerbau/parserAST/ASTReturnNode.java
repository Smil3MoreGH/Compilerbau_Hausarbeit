package de.paul.compilerbau.parserAST;

/**
 * AST-Knoten für ein return-Statement.
 */
public class ASTReturnNode extends ASTNode {
    private final ASTNode returnValue;

    public ASTReturnNode(ASTNode returnValue) {
        this.returnValue = returnValue;
    }

    // Gibt den Rückgabewert des return-Statements zurück
    public ASTNode getReturnValue() {
        return returnValue;
    }

    @Override
    public String toString(int indent) {
        return getIndent(indent) + "Return:\n" + returnValue.toString(indent + 1);
    }
}
