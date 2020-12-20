package gui;

import javax.swing.*;
import java.awt.*;

public class CustomSettingsPanel extends JPanel {

    public CustomSettingsPanel(JRadioButton continuousOp, JRadioButton debugOp, JSlider execSlider) {

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel opMode = new JLabel("Execution Mode: ");
        opMode.setForeground(Color.DARK_GRAY);

        ButtonGroup bg = new ButtonGroup();
        bg.add(continuousOp);
        bg.add(debugOp);

        JLabel execDelay = new JLabel("Execution Speed: ");
        opMode.setForeground(Color.DARK_GRAY);

        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(opMode, gbc);

        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(continuousOp, gbc);

        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 2;
        gbc.gridy = 0;
        add(debugOp, gbc);

        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(execDelay, gbc);

        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.gridy = 1;
        add(execSlider, gbc);

    }

}
