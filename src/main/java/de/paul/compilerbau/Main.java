package de.paul.compilerbau;

import de.paul.compilerbau.scanner.Scanner;
import de.paul.compilerbau.scanner.Token;
import de.paul.compilerbau.utils.FileLoader;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 1️⃣ Lade den PAUL-Code aus der Datei
        String sourceCode = FileLoader.loadFile("test.paul");

        // 2️⃣ Scanner erstellen und Code analysieren
        Scanner scanner = new Scanner(sourceCode);
        List<Token> tokens = scanner.scan();

        // 3️⃣ Alle Tokens ausgeben
        System.out.println("📌 Scanner-Output:");
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
