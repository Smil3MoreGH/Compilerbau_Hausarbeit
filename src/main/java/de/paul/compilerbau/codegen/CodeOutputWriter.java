package de.paul.compilerbau.codegen;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Hilfsklasse zur Ausgabe der generierten Instruktionen.
 *
 * Wird aktuell nicht verwendet, eignet sich aber gut zum Debuggen oder zur Analyse
 * des erzeugten Codes während der Entwicklung.
 */
public class CodeOutputWriter {

    /**
     * Schreibt die generierten Instruktionen in eine Datei.
     *
     * @param filename     Zielpfad der Ausgabedatei
     * @param instructions Die zu speichernden Instruktionen
     */
    public void writeToFile(String filename, InstructionList instructions) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(instructions.toString());
            System.out.println("Code erfolgreich nach " + filename + " geschrieben.");
        } catch (IOException e) {
            System.err.println("Fehler beim Schreiben der Datei: " + e.getMessage());
        }
    }

    /**
     * Gibt die generierten Instruktionen in der Konsole aus.
     *
     * @param instructions Die auszugebenden Instruktionen
     */
    public void printToConsole(InstructionList instructions) {
        System.out.println(instructions);
    }
}
