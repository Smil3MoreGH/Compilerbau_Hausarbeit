package de.paul.compilerbau.codegen;

/**
 * Erzeugt eindeutige Labels für Sprungbefehle.
 */
public class LabelGenerator {
    private int counter = 0;

    // Gibt ein eindeutiges Label mit Präfix zurück (z.B. "ELSE_0", "ENDIF_1")
    public String generateLabel(String prefix) {
        return prefix + "_" + (counter++);
    }
}
