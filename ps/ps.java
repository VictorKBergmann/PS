
package ps;

public class ps {
    
    public static void main(String[] args) {
        Memory mem = new Memory();
        Cpu cpu = new Cpu();
        //Loader loader = new Loader(mem);

        String data;
        boolean signal = true;


        while(signal){            
            data = cpu.read(mem);
            System.out.println("Palavra que buscou no read: " + data);
            signal = cpu.execute(data, mem); 
            System.out.println("Acumulador: " + cpu.getAcc());
            System.out.println("Memoria 31:" +mem.getData(31));
        }        
    }  
}
