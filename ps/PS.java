
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
        Memory mem = new Memory();
        Loader loader = new Loader(13, mem);
        Cpu cpu = new Cpu();

        EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    GUI gui = new GUI(mem, cpu, loader);
                    gui.setVisible(true);
                }
            });

    }
    
}
