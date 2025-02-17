package de.paul.compilerbau.parser;

import java.util.ArrayList;
import java.util.List;

public abstract class ASTNode {
    protected List<ASTNode> children = new ArrayList<>();

    public void addChild(ASTNode node) {
        children.add(node);
    }

    public abstract String toString(int indent);

    protected String getIndent(int indent) {
        return "  ".repeat(indent);
    }
}
