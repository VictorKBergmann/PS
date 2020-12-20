package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class CustomButton extends JButton {

    public CustomButton(String bodyText, int left, int right) {

        setText(bodyText);
        setBorder(null);
        setBackground(Color.GRAY);
        setForeground(Color.LIGHT_GRAY);
        setFocusPainted(false);
        setMargin(new Insets(0, 0, 0, 0));
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        setBorder(BorderFactory.createEmptyBorder(5,left,5,right));
        addMouseListener(new MouseListener() {
            @Override public void mouseClicked(MouseEvent e) {}
            @Override public void mousePressed(MouseEvent e) {}
            @Override public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(Color.LIGHT_GRAY);
                    setForeground(Color.DARK_GRAY);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(Color.GRAY);
                    setForeground(Color.LIGHT_GRAY);
                }
            }
        });
        
    }

}
