
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
        while(signal){      
            signal = cpu.execute(mem); 
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
