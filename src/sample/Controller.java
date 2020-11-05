package sample;


import javax.swing.plaf.basic.BasicBorders;
import java.util.HashMap;

public class Controller {

    private Short stackPointer, ProgramCounter;
    private HashMap<Integer, Integer> instructionTable;

    Controller(){
        stackPointer = 512;
        ProgramCounter = 0;

        instructionTable = new HashMap<>();
        instructionTable.put(0, 21);
        instructionTable.put(1, 21);
        instructionTable.put(2, 11);
        instructionTable.put(3, 31);
        instructionTable.put(4, 21);
        instructionTable.put(5, 21);
        instructionTable.put(6, 11);
        instructionTable.put(7, 01);
        instructionTable.put(8, 01);
        instructionTable.put(10,11);
        instructionTable.put(11, 00);
        instructionTable.put(12, 01);
        instructionTable.put(13, 30);
        instructionTable.put(14, 11);
        instructionTable.put(15, 01);
        instructionTable.put(16, 00);
    }

    public Short getStackPointer() {
        return stackPointer;
    }
    public void setStackPointer(Short stackPointer) {
        this.stackPointer = stackPointer;
    }
    public Short getProgramCounter() {
        return ProgramCounter;
    }
    public void setProgramCounter(Short programCounter) {
        ProgramCounter = programCounter;
    }
    public void incrementProgramCounter(){ProgramCounter++; }

    int oppCodeDecoder(Short oppCode){
        return instructionTable.get(oppCode);
    }

    public void operation(Short oppCode){
        //TODO

    }
    public void operation(Short oppCode, Short ACC, Short MAR){
        switch (oppCode){
            case 00:
                ProgramCounter = MAR;
                break;
            case 05://negative
                if(ACC < 0){
                    ProgramCounter = MAR;
                }
                break;
            case 01://positive
                if(ACC > 0){
                    ProgramCounter = MAR;
                }
                break;
            case 04://zero
                if(ACC == 0){
                    ProgramCounter = MAR;
                }
                break;
        }

    }


}
