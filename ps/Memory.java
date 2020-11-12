
package ps;

import java.util.ArrayList;

public class Memory {
    private ArrayList<String> mem;
    private int dataPointer;

    public Memory(){
        mem = new ArrayList<>();
        for(int i=0;i<512;i++)
            mem.add("0000000000000000");
        dataPointer = 0;
    }

    public String getInstruction(int position) {     
        return (mem.get(position));
    }
    public void setInstruction(int position, String data) {
        mem.set(position, data);
    }
    
    public String getData(int position) {
        if (position < dataPointer) {
            throw new IllegalArgumentException("Erro! Tentativa de acessar a memória de instruções.");
        }
        return (mem.get(position));
    }
    
    public void setData(int position, String data) {
        if (position < dataPointer){
            throw new IllegalArgumentException();
        }
        mem.set(position, data);
    }
    public void setDataPointer(int position) {
        dataPointer = position;
    }
    public ArrayList<String> getMem() {
        return mem;
    }
    public String getDataStack(int position) {
        return mem.get(position + 2);
    }
    public void setDataStack(int position, String data) {
        mem.set((position + 2), data);
    }
}
