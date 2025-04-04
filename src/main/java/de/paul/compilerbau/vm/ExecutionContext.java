package de.paul.compilerbau.vm;

import java.util.HashMap;
import java.util.Map;

/**
 * Kontext eines Funktionsaufrufs, speichert Rücksprungadresse und lokale Variablen.
 */
public class ExecutionContext {
    private final Map<String, Integer> variables = new HashMap<>();
    private final int returnAddress;

    public ExecutionContext(int returnAddress) {
        this.returnAddress = returnAddress;
    }

    public void storeVariable(String name, int value) {
        variables.put(name, value);
    }

    public int loadVariable(String name) {
        if (!variables.containsKey(name)) {
            throw new IllegalStateException("Variable '" + name + "' nicht definiert!");
        }
        return variables.get(name);
    }

    public int getReturnAddress() {
        return returnAddress;
    }
}
