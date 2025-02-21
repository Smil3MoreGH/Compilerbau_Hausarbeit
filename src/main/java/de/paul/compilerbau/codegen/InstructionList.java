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
