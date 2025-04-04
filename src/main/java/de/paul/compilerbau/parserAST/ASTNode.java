package de.paul.compilerbau.parserAST;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstrakte Basisklasse für alle AST-Knoten.
 * Jeder Knoten kann beliebig viele Kindknoten besitzen.
 */
public abstract class ASTNode {
    protected List<ASTNode> children = new ArrayList<>();

    // Fügt einen Kindknoten hinzu
    public void addChild(ASTNode node) {
        children.add(node);
    }

    // Gibt eine formatierte String-Repräsentation des Knotens zurück
    public abstract String toString(int indent);

    // Hilfsfunktion für Einrückungen
    protected String getIndent(int indent) {
        return "  ".repeat(indent);
    }
}
