package de.paul.compilerbau.codegen;

public class LabelGenerator {
    private int counter = 0;

    public String generateLabel(String prefix) {
        return prefix + "_" + (counter++);
    }
}
