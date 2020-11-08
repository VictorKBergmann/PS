package ps;

import java.util.ArrayList;

public class Memory {
    private ArrayList<String> mem;
    private int dataPointer;


    public Memory(){
        mem = new ArrayList<>();
        for(int i=0;i<512;i++)
            mem.add("0000000000000000");
        dataPointer = 25;
        
        mem.set(13,"00000000010000100000000000001011"); //add #11 
        mem.set(14,"00000000010011100000000000000100"); //mult #4
        mem.set(15,"00000000000001110000000000011111"); //store 31
        mem.set(16,"00000000010001100000000000011110"); //sub #30
        mem.set(17,"00000000000001110000000000101100"); //store 44
        mem.set(18,"00000000010000100000000000001010"); //add #10
        mem.set(19,"00000000000001110000000000011011"); //store 27
        mem.set(20,"00000000000001010000000000011011"); //brneg 27
        mem.set(21,"00000000000000010000000000011011"); //brpos 27
        mem.set(22,"00000000000001100000000000000010"); //sub 2
        mem.set(23,"00000000000001110000000000011110"); //store 30
        mem.set(24,"000000000010110100000000001000000000000000011111"); //copy 32,(indireto)31
        mem.set(25,"0000000000001011"); // stop

    }

    public String getInstructionData(int position) {     
        return (mem.get(position));
    }
    
    public String getData(int position) {
        if (position < dataPointer) {
            throw new IllegalArgumentException();
        }
        return (mem.get(position));
    }
    
    public void setData(int position, String data) {
        if (position < dataPointer){
            throw new IllegalArgumentException();
        }
        mem.set(position, data);
    }

    public ArrayList<String> getMem() {
        return mem;
    }
}

