package gui;

import javax.swing.*;
import java.awt.*;

public class CustomInputPanel extends JPanel {

    public CustomInputPanel(String input) {

        JLabel label = new JLabel(input);
        label.setForeground(Color.LIGHT_GRAY);
        add(label);

        setBackground(Color.DARK_GRAY);
        setMaximumSize(new Dimension(400, 30));

    }

}
