package de.paul.compilerbau;

import de.paul.compilerbau.scanner.Scanner;
import de.paul.compilerbau.scanner.Token;
import de.paul.compilerbau.utils.FileLoader;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Lade den PAUL-Code aus der Datei
        String sourceCode = FileLoader.loadFile("test.paul");

        // Scanner erstellen und Code analysieren
        Scanner scanner = new Scanner(sourceCode);
        List<Token> tokens = scanner.scan();

        // Alle Tokens ausgeben
        System.out.println("Scanner-Output:");
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
