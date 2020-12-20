package gui;

import java.util.ArrayList;

public class Util {

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
                "   WRITE(08): Output stream <- opd1 D/In/Im\n";

    }

    public static String getCurrentInstruction(String pc, String ri, ArrayList<String> mem) {

        int position = Integer.parseInt(pc, 2);
        String data;
        ArrayList<String> instruction = new ArrayList<>();

        position++;
        data = String.valueOf(Integer.parseInt(mem.get(position), 2));

        switch (ri.substring(12, 16)) {

            case "0000":
                instruction.add("BR");
                instruction.add(getData(ri, data));
                break;
            case "0001":
                instruction.add("BRPOS");
                instruction.add(getData(ri, data));
                break;
            case "0010":
                instruction.add("ADD");
                instruction.add(getData(ri, data));
                break;
            case "0011":
                instruction.add("LOAD");
                instruction.add(getData(ri, data));
                break;
            case "0100":
                instruction.add("BRZERO");
                instruction.add(getData(ri, data));
                break;
            case "0101":
                instruction.add("BRNEG");
                instruction.add(getData(ri, data));
                break;
            case "0110":
                instruction.add("SUB");
                instruction.add(getData(ri, data));
                break;
            case "0111":
                instruction.add("STORE");
                instruction.add(getData(ri, data));
                break;
            case "1000":
                instruction.add("WRITE");
                instruction.add(getData(ri, data));
                break;
            case "1010":
                instruction.add("DIVIDE");
                instruction.add(getData(ri, data));
                break;
            case "1011":
                instruction.add("STOP");
                break;
            case "1100":
                instruction.add("READ ");
                instruction.add(getData(ri, data));
                break;
            case "1101":
                instruction.add("COPY");
                instruction.add(getData(ri, data));
                position++;
                data = String.valueOf(Integer.parseInt(mem.get(position), 2));
                instruction.add(getData(ri, data));
                break;
            case "1110":
                instruction.add("MULT");
                instruction.add(getData(ri, data));
                break;
            case "1111":
                instruction.add("CALL");
                instruction.add(getData(ri, data));
                break;
            case "1001":
                instruction.add("RET");
                break;

        }

        return String.join(" ", instruction);
    }

    private static String getData(String ri, String data) {
        if (ri.substring(9, 12).equals("001"))
            return data + ",I";
        else if (ri.substring(9, 12).equals("000"))
            return data;
        else
            return "#" + data;
    }

}
