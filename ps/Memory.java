
package ps;

import java.util.ArrayList;

public class Memory {
    private ArrayList<String> mem;
    private int dataPointer; // pointer to the end of instructions
    private int stackSize;

    public Memory(){
        mem = new ArrayList<>(); // inicialize array
        init(); //set the size and complete with '0's
    }

    /**
     *
     * @param position on memory
     * @return the instruction
     */
    public String getInstruction(int position) {     
        return (mem.get(position));
    }

    public void setInstruction(int position, String data) {
        mem.set(position, data);
    }

    /**
     * Exception on interface if position is on instruction part
     * @param position
     * @return Data on the position
     */
    public String getData(int position) {
        if (position < dataPointer) {
            throw new IllegalArgumentException("Erro! Tentativa de acessar a memória de instruções.");
        }
        return (mem.get(position));
    }
    /**
     * Exception on interface if position is on instruction part
     * @param position,data
     *
     */
    public void setData(int position, String data) {
        if (position < dataPointer){
            throw new IllegalArgumentException("Erro! Tentativa de acessar a memória de instruções.");
        }
        mem.set(position, data);
    }


    public void setDataPointer(int position) {
        dataPointer = position;
    }

    public String getDataStack(int position) {
        return mem.get(position + 2);
    }

    public void setDataStack(int position, String data) {
        mem.set((position + 2), data);
    }

    public ArrayList<String> getMem() {
        return mem;
    }

    /**
     * set the size and complete with '0's
     */
    public void init() {

        if (mem.size() == 0)
            for(int i=0;i<512;i++) mem.add("0000000000000000");
        else
            for(int i=0;i<512;i++) mem.set(i, "0000000000000000");

        dataPointer = 0;

    }
}
