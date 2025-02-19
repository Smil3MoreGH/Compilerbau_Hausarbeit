package de.paul.compilerbau.parserAST;

import java.util.ArrayList;
import java.util.List;

public class ASTIfElseNode extends ASTNode {
    private final ASTNode condition;
    private final List<ASTNode> ifBody = new ArrayList<>();
    private final List<ASTNode> elseBody = new ArrayList<>();

    public ASTIfElseNode(ASTNode condition) {
        this.condition = condition;
    }

    public void addIfBody(ASTNode stmt) {
        ifBody.add(stmt);
    }

    public void addElseBody(ASTNode stmt) {
        elseBody.add(stmt);
    }

    @Override
    public String toString(int indent) {
        StringBuilder sb = new StringBuilder(getIndent(indent) + "IfCondition:\n" + condition.toString(indent + 1));
        sb.append(getIndent(indent) + "Then:\n");
        for (ASTNode stmt : ifBody) {
            sb.append(stmt.toString(indent + 1));
        }
        if (!elseBody.isEmpty()) {
            sb.append(getIndent(indent) + "Else:\n");
            for (ASTNode stmt : elseBody) {
                sb.append(stmt.toString(indent + 1));
            }
        }
        return sb.toString();
    }
}
