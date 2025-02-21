package de.paul.compilerbau.codegen;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, Integer> variables = new HashMap<>();
    private final Map<String, Integer> functions = new HashMap<>();
    private int nextVariableAddress = 0;

    public void addVariable(String name) {
        if (!variables.containsKey(name)) {
            variables.put(name, nextVariableAddress++);
        }
    }

    public Integer getVariableAddress(String name) {
        return variables.get(name);
    }

    public void addFunction(String name, int address) {
        functions.put(name, address);
    }

    public Integer getFunctionAddress(String name) {
        return functions.get(name);
    }
}
