package de.paul.compilerbau.parserAST;

import java.util.ArrayList;
import java.util.List;

public class ASTWhileNode extends ASTNode {
    private final ASTNode condition;
    private final List<ASTNode> bodyStatements = new ArrayList<>();

    public ASTWhileNode(ASTNode condition) {
        this.condition = condition;
    }

    public void addBodyStatement(ASTNode stmt) {
        bodyStatements.add(stmt);
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(getIndent(indent) + "WhileLoop:\n" + condition.toString(indent + 1));
        for (ASTNode stmt : bodyStatements) {
            sb.append(stmt.toString(indent + 1));
        }
        return sb.toString();
    }
}
