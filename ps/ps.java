
package ps;

public class PS {

    public static void main(String[] args) {
        // TODO code application logic here
        Memory mem = new Memory();
        //Loader loader = new Loader(mem);
        Cpu cpu = new Cpu();

        String data;
        boolean signal = true;


        while(signal){            
            data = cpu.read(mem);
            System.out.println("Palavra que buscou no read: " + data);
            signal = cpu.execute(data, mem); 
            System.out.println("Acumulador: " + cpu.getAcc());
            System.out.println("Memoria 31:" +mem.getData(31));
            System.out.println("Memoria 32:" +mem.getData(32));
        }        
    }
    
}
