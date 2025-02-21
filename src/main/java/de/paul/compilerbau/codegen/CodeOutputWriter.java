package de.paul.compilerbau.codegen;

import java.io.FileWriter;
import java.io.IOException;

public class CodeOutputWriter {

    public void writeToFile(String filename, InstructionList instructions) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(instructions.toString());
            System.out.println("Code erfolgreich nach " + filename + " geschrieben.");
        } catch (IOException e) {
            System.err.println("Fehler beim Schreiben der Datei: " + e.getMessage());
        }
    }

    public void printToConsole(InstructionList instructions) {
        System.out.println(instructions);
    }
}
