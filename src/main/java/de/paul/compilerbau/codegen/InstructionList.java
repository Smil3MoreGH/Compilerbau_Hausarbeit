package de.paul.compilerbau.codegen;

import java.util.ArrayList;
import java.util.List;

public class InstructionList {
    private final List<Instruction> instructions = new ArrayList<>();

    public void add(Instruction instruction) {
        instructions.add(instruction);
    }

    public void add(String opcode) {
        instructions.add(new Instruction(opcode));
    }

    public void add(String opcode, String operand) {
        instructions.add(new Instruction(opcode, operand));
    }

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
