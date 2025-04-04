package de.paul.compilerbau.parserAST;

import java.util.ArrayList;
import java.util.List;

/**
 * AST-Knoten für eine if-else-Bedingung.
 */
public class ASTIfElseNode extends ASTNode {
    private final ASTNode condition; // Bedingung (z.B. x < 5)
    private final List<ASTNode> ifBody = new ArrayList<>();
    private final List<ASTNode> elseBody = new ArrayList<>();

    public ASTIfElseNode(ASTNode condition) {
        this.condition = condition;
    }

    // Fügt ein Statement zum if-Block hinzu
    public void addIfBody(ASTNode stmt) {
        ifBody.add(stmt);
    }

    // Fügt ein Statement zum else-Block hinzu
    public void addElseBody(ASTNode stmt) {
        elseBody.add(stmt);
    }

    public ASTNode getCondition() {
        return condition;
    }

    public List<ASTNode> getIfBody() {
        return ifBody;
    }

    public List<ASTNode> getElseBody() {
        return elseBody;
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
