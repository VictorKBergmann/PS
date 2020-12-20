package gui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class CustomScroll extends BasicScrollBarUI {

    public CustomScroll() {}

    @Override
    protected void configureScrollBarColors(){
        this.thumbColor = Color.GRAY;
    }

    @Override
    protected JButton createDecreaseButton(int orientation)  {
        return new BasicArrowButton(orientation,
                UIManager.getColor(Color.GRAY),
                UIManager.getColor(Color.GRAY),
                UIManager.getColor("ScrollBar.thumbDarkShadow"),
                UIManager.getColor(Color.GRAY));
    }

    @Override
    protected JButton createIncreaseButton(int orientation)  {
        return new BasicArrowButton(orientation,
                UIManager.getColor(Color.GRAY),
                UIManager.getColor(Color.GRAY),
                UIManager.getColor("ScrollBar.thumbDarkShadow"),
                UIManager.getColor(Color.GRAY));
    }

}
