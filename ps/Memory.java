
package ps;

import java.util.ArrayList;

public class Memory {
    private ArrayList<String> mem;
    private int dataPointer;


    public Memory(){
        mem = new ArrayList<String>();
        for(int i=0;i<512;i++)
            mem.add("0");
        
        mem.set(13,"0000000001000010000000000001111"); //add #15 
        mem.set(14,"0000000001001110000000000000010"); //mult #2
        mem.set(15,"0000000000000111000000000011111"); //store 31
        mem.set(16,"00000000010011010000000001000000000000000001111"); //copy 32, 15
        mem.set(17,"0000000000001011"); // stop

    }

    public String getData(int position) {     
        return (mem.get(position));
    }

    public void setData(int position, String data) {
        mem.set(position, data);
    }
    
    public int getDataPointer() {
        return dataPointer;
    }
}

