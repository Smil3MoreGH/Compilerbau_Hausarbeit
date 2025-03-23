package de.paul.compilerbau.vm;

public class InstructionSet {
    public static final String PUSH = "PUSH";
    public static final String LOAD = "LOAD";
    public static final String STORE = "STORE";
    public static final String ADD = "ADD";
    public static final String SUB = "SUB";
    public static final String MUL = "MUL";
    public static final String DIV = "DIV";
    public static final String GT = "GT";
    public static final String LT = "LT";
    public static final String EQ = "EQ";
    public static final String NEQ = "NEQ";
    public static final String GTE = "GTE";
    public static final String LTE = "LTE";
    public static final String JMP = "JMP";
    public static final String JZ = "JZ";
    public static final String CALL = "CALL";
    public static final String RET = "RET";
    public static final String GOTO = "GOTO";

    private InstructionSet() {}  // Verhindert Instanziierung
}
