
package ps;

import gui.GUI;

import java.awt.*;

public class PS {

    public static void main(String[] args) {
/**
 * run the application
 * and inicialize interface
 *
 *
 */
        MacroProcessor mp = new MacroProcessor();
        Assembler assembler = new Assembler();
        Linker linker = new Linker();
        Memory mem = new Memory();
        Loader loader = new Loader(mem);
        Cpu cpu = new Cpu(mem);

        EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    GUI gui = new GUI(mem, cpu, loader, mp, assembler, linker);
                    gui.setVisible(true);
                }
            });

    }
    
}
