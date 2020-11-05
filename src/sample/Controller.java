package sample;


import javax.swing.plaf.basic.BasicBorders;
import java.util.HashMap;

public class Controller {

    private Short stackPointer, programCounter;
    private HashMap<Integer, Integer> instructionTable;

    Controller(){
        stackPointer = 512;
        programCounter = 0;

        instructionTable = new HashMap<>();
        instructionTable.put(0, 21);//BR
        instructionTable.put(1, 21);//BRPOS
        instructionTable.put(2, 11);//ADD
        instructionTable.put(3, 31);//LOAD
        instructionTable.put(4, 21);//BRZERO
        instructionTable.put(5, 21);//BRNEG
        instructionTable.put(6, 11);//SUB
        instructionTable.put(7, 32);//STORE
        instructionTable.put(8, 51);//WRITE
        instructionTable.put(10,11);//DIVIDE
        instructionTable.put(11, 00);//STOP
        instructionTable.put(12, 52);//READ
        instructionTable.put(13, 33);//COPY
        instructionTable.put(14, 11);//MULTI
        instructionTable.put(15, 41);//CALL
        instructionTable.put(16, 40);//RET
    }

    public Short getStackPointer() {
        return stackPointer;
    }
    public void setStackPointer(Short stackPointer) {
        this.stackPointer = stackPointer;
    }
    public Short getProgramCounter() {
        return programCounter;
    }
    public void setProgramCounter(Short programCounter) {
        programCounter = programCounter;
    }
    public void incrementProgramCounter(){programCounter++; }
    public void incrementStackPointer(){stackPointer--; }
    public void decrementStackPointer(){stackPointer++; }

    int oppCodeDecoder(Short oppCode){
        return instructionTable.get(oppCode);
    }

    public void operation(Short oppCode){
        //TODO

    }
    public void operation(Short oppCode, Short ACC, Short MAR){
        switch (oppCode){
            case 00:
                programCounter = MAR;
                break;
            case 05://negative
                if(ACC < 0){
                    programCounter = MAR;
                }
                break;
            case 01://positive
                if(ACC > 0){
                    programCounter = MAR;
                }
                break;
            case 04://zero
                if(ACC == 0){
                    programCounter = MAR;
                }
                break;
        }

    }


}
