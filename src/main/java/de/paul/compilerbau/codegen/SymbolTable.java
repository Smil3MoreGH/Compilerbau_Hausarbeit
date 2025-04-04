package de.paul.compilerbau.codegen;

import java.util.HashMap;
import java.util.Map;

/**
 * Symboltabelle zur Verwaltung von Variablen und Funktionen während der Codegenerierung.
 */
public class SymbolTable {
    private final Map<String, Integer> variables = new HashMap<>();
    private final Map<String, Integer> functions = new HashMap<>();
    private int nextVariableAddress = 0;

    // Fügt eine neue Variable hinzu, wenn noch nicht vorhanden
    public void addVariable(String name) {
        if (!variables.containsKey(name)) {
            variables.put(name, nextVariableAddress++);
        }
    }

    // Liefert die Adresse einer Variable
    public Integer getVariableAddress(String name) {
        return variables.get(name);
    }

    // Fügt eine Funktion mit Startadresse hinzu
    public void addFunction(String name, int address) {
        functions.put(name, address);
    }

    // Liefert die Startadresse einer Funktion
    public Integer getFunctionAddress(String name) {
        return functions.get(name);
    }
}
