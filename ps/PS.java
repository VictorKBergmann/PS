package ps;

import gui.GUI;

import java.awt.*;

public class PS {

    public static void main(String[] args) {
        // TODO code application logic here
        Memory mem = new Memory();
        //Loader loader = new Loader(mem);
        Cpu cpu = new Cpu();

        String data;
        boolean signal = true;

        try{
            while(signal){
                data = cpu.read(mem);
                System.out.println("\nPalavra que buscou no read: " + data);
                signal = cpu.execute(data, mem); 
                System.out.println("RE: " + cpu.getRe());
                System.out.println("Acumulador: " + cpu.getAcc());
                System.out.println("Memoria 44:" +mem.getData(44));
                System.out.println("Memoria 31:" +mem.getData(31));
                System.out.println("Memoria 32:" +mem.getData(32));
            }
        }
        catch(IllegalArgumentException e) {
            System.out.println("\nErro! Tentativa de acessar a memória de instruções.");
        }

        /*EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                GUI gui = new GUI(mem, cpu);
                gui.setVisible(true);
            }
        });*/
    }
    
}
