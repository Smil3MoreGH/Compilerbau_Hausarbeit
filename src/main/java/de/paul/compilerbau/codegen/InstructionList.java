package de.paul.compilerbau.codegen;

import java.util.ArrayList;
import java.util.List;

/**
 * Liste von Instruktionen mit Hilfsmethoden zum Aufbau des Programmcodes.
 */
public class InstructionList {
    private final List<Instruction> instructions = new ArrayList<>();

    // Fügt eine vollständige Instruktion hinzu
    public void add(Instruction instruction) {
        instructions.add(instruction);
    }

    // Fügt eine Instruktion ohne Operand hinzu
    public void add(String opcode) {
        instructions.add(new Instruction(opcode));
    }

    // Fügt eine Instruktion mit Operand hinzu
    public void add(String opcode, String operand) {
        instructions.add(new Instruction(opcode, operand));
    }

    // Hängt alle Instruktionen einer anderen Liste an
    public void addAll(InstructionList other) {
        this.instructions.addAll(other.instructions);
    }

    public Instruction get(int index) {
        return instructions.get(index);
    }

    public int size() {
        return instructions.size();
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Instruction instr : instructions) {
            builder.append(instr).append("\n");
        }
        return builder.toString();
    }
}
