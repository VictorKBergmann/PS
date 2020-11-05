package sample;

public class Controller {

    /*
    reg SP, PC



    */

    int stackPointer, ProgramCounter;


    public int getStackPointer() {
        return stackPointer;
    }
    public void setStackPointer(int stackPointer) {
        this.stackPointer = stackPointer;
    }
    public int getProgramCounter() {
        return ProgramCounter;
    }
    public void setProgramCounter(int programCounter) {
        ProgramCounter = programCounter;
    }

    private int oppCodeDecoder(short oppCode){
        switch (oppCode){
            //TODO
        }
        return 0;
    }
    public void operation(short oppCode){
        //TODO
    }
    public void operation(short oppCode, short oper){
        //TODO

    }
    public void operation(short oppCode, short oper1, short oper2){
        //TODO
    }


}
