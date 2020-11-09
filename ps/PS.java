
package ps;
//import gui.GUI;
//import java.awt.*;
public class PS {

    public static void main(String[] args) {
        // TODO code application logic here
        Memory mem = new Memory(); //Precisa do Loader pra definir final da mem de instruções/inicio da mem de dados 
       // Loader loader = new Loader("teste.txt", 13, mem);
       // loader.loadAllInstructions();
        Cpu cpu = new Cpu();
        boolean signal = true;

        try{
            while(signal){      
                signal = cpu.execute(mem); 
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
