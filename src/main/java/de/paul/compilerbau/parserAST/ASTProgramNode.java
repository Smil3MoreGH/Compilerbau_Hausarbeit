package de.paul.compilerbau.parserAST;

import java.util.ArrayList;
import java.util.List;

public class ASTProgramNode extends ASTNode {
    private final List<ASTNode> statements = new ArrayList<>();

    @Override
    public void addChild(ASTNode node) {
        statements.add(node);
    }

    public List<ASTNode> getStatements() {
        return statements;
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(getIndent(indent) + "Program:\n");
        for (ASTNode stmt : statements) {
            sb.append(stmt.toString(indent + 1));
        }
        return sb.toString();
    }
}
