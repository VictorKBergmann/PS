package gui;

import java.util.HashMap;

public class Util {

    /**
     * HashMap containing all default examples.
     */
    private static HashMap<Integer, String> examples;
    static {
            examples = new HashMap<>();
            //EVEN NUMBER TEST
            examples.put(1, "00000000000011000000000000100100\n" +
                    "00000000000000110000000000100100\n" +
                    "00000000000001000000000000011100\n" +
                    "00000000010001100000000000000010\n" +
                    "00000000000001010000000000011001\n" +
                    "00000000000000000000000000010001\n" +
                    "000000000100110100000000001001000000000000000001\n" +
                    "00000000000000110000000000100100\n" +
                    "00000000000010000000000000100100\n" +
                    "0000000000001011");
            // FACTORIAL
            examples.put(2, "00000000000011000000000000100110\n" +
                    "00000000000000110000000000100110\n" +
                    "00000000010001100000000000000001\n" +
                    "00000000000001000000000000100010\n" +
                    "00000000000011110000000000011001\n" +
                    "00000000000000000000000000010001\n" +
                    "00000000000001110000000000100111\n" +
                    "00000000000011100000000000100110\n" +
                    "00000000000001110000000000100110\n" +
                    "00000000000000110000000000100111\n" +
                    "0000000000001001\n" +
                    "00000000000010000000000000100110\n" +
                    "0000000000001011");
    }

    /**
     * Send the instructions according to the chosen example.
     */
    public static String getExample(int val) {

        return examples.get(val);

    }

    /**
     * Returns the default text to the help dialog.
     */
    public static String getHelp() {

        return "Operation Modes:\n" +
                "   Run: Continuous mode interacting with UI at each instruction cycle.\n" +
                "   Debug: Executes one instruction at a time.\n" +
                "Registers:\n" +
                "   PC: Keeps the address of the next instruction to be executed.\n" +
                "   SP: Stack Pointer.\n" +
                "   ACC: Stores ULA's data.\n" +
                "   MOP: Stores operation mode.\n" +
                "   RI: Keeps the opcode of the current instruction.\n" +
                "   RE: Keeps the access adress of the data memory.\n" +
                "Adressing Modes: Direct(D) Indirect(In) Immediate(Im)\n" +
                "Available Instructions:\n" +
                "   ADD(02): ACC <- ACC + opd1 D/In/Im\n" +
                "   BR(00): PC <- opd1 D/In\n" +
                "   BRNEG(05): PC <- opd1, if ACC < 0 D/In\n" +
                "   BRPOS(01): PC <- opd1, if ACC > 0 D/In\n" +
                "   BRZERO(04): PC <- opd1, if ACC = 0 D/In\n" +
                "   CALL(15): [SP] <- PC; PC <- opd1 D/In\n" +
                "   COPY(13): opd1 <- opd2 D/In D/In/Im\n" +
                "   DIVIDE(10): ACC <- ACC / opd1 D/In/Im\n" +
                "   LOAD(03): ACC <- opd1 D/In/Im\n" +
                "   MULT(14): ACC <- ACC * opd1 D/In/Im\n" +
                "   READ(12): opd1 <- input stream D/In\n" +
                "   RET(09): PC <- [SP]\n" +
                "   STOP(11): end of execution\n" +
                "   STORE(07): opd1 <- ACC D/In\n" +
                "   SUB(06): ACC <- ACC - opd1 D/In/Im\n" +
                "   WRITE(08): Output stream <- opd1 D/In/Im\n" +
                "Rules:\n" +
                "   1 - Instructions must be only in BINARY code with its respective bit sizes.\n" +
                "   2 - Instructions must be separate by only ONE \\n.\n" +
                "   3 - You can only choose one operation mode at a time.\n" +
                "Custom Examples:\n" +
                "   1 - Checks if user input is an even number.\n" +
                "   2 - Calculates factorial number from user input.";

    }

}
