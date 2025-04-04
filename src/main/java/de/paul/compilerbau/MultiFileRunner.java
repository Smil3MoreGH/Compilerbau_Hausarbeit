package de.paul.compilerbau;

import de.paul.compilerbau.codegen.CodeGenerator;
import de.paul.compilerbau.codegen.InstructionList;
import de.paul.compilerbau.parserAST.ASTNode;
import de.paul.compilerbau.parserAST.ASTParser;
import de.paul.compilerbau.scanner.Scanner;
import de.paul.compilerbau.scanner.Token;
import de.paul.compilerbau.utils.FileLoader;
import de.paul.compilerbau.vm.VirtualMachine;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Stream;

public class MultiFileRunner {
    private static final boolean waitForUser = true;

    public static void main(String[] args) throws IOException {
        Path dir = Paths.get("src/main/resources");

        try (Stream<Path> files = Files.list(dir)) {
            files
                    .filter(path -> path.toString().endsWith(".paul"))
                    .forEach(MultiFileRunner::runTestFile);
        }
    }

    private static void runTestFile(Path path) {
        System.out.println("\n==================================");
        System.out.println("Datei: " + path.getFileName());
        System.out.println("==================================");

        String resourceName = path.getFileName().toString();
        String sourceCode = FileLoader.loadFile(resourceName);

        Scanner scanner = new Scanner(sourceCode);
        List<Token> tokens = scanner.scan();

        wait("Parser starten...");
        ASTParser parser = new ASTParser(tokens);
        ASTNode ast = parser.parse();

        System.out.println("\nParser-Output (AST):");
        System.out.println(ast.toString(0));
        wait("Codegenerator starten...");

        CodeGenerator codeGenerator = new CodeGenerator();
        InstructionList instructions = codeGenerator.generate(ast);

        System.out.println("\nZwischencode:");
        System.out.println(instructions);
        wait("Virtuelle Maschine starten...");

        System.out.println("\nStarte Virtuelle Maschine...");
        VirtualMachine vm = new VirtualMachine(instructions);
        vm.run();

        System.out.println("Test abgeschlossen für: " + path.getFileName());
        wait("Weiter zur nächsten Datei...");
    }

    private static void wait(String message) {
        if (!waitForUser) return;

        System.out.println(message);
        System.out.print("[Drücke ENTER zum Fortfahren]");
        try {
            System.in.read();
        } catch (IOException ignored) {}
        System.out.println();
    }
}
