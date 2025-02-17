package de.paul.compilerbau.parser;

import java.util.ArrayList;
import java.util.List;

public class WhileNode extends ASTNode {
    private final ASTNode condition;
    private final List<ASTNode> bodyStatements = new ArrayList<>();

    public WhileNode(ASTNode condition) {
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
