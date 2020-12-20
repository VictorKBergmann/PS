package gui;

import javax.swing.*;
import java.awt.*;

public class RegisterPanel extends JPanel {

    public RegisterPanel(String registerName) {
        setBackground(Color.DARK_GRAY);
        JLabel label = new JLabel(registerName);
        label.setForeground(Color.LIGHT_GRAY);
        add(label);
    }

}
