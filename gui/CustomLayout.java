package gui;

import javax.swing.*;
import java.awt.*;

public class CustomLayout extends GroupLayout {

    JPanel inputsPanel;
    JPanel instructionPanel;
    JScrollPane consoleAreaScrollPane;
    JPanel registersPanel;
    JButton runButton;
    JButton stepButton;
    JButton cleanButton;
    JScrollPane memoryScrollPane;
    JButton settingsButton;
    JButton helpButton;

    public CustomLayout(Container host, JPanel inputsPanel, JPanel instructionPanel,
                        JScrollPane consoleAreaScrollPane, JPanel registersPanel,
                        JButton runButton, JButton stepButton, JButton cleanButton,
                        JScrollPane memoryScrollPane, JButton settingsButton, JButton helpButton) {
        super(host);
        this.inputsPanel = inputsPanel;
        this.instructionPanel = instructionPanel;
        this.consoleAreaScrollPane = consoleAreaScrollPane;
        this.registersPanel = registersPanel;
        this.runButton = runButton;
        this.stepButton = stepButton;
        this.cleanButton = cleanButton;
        this.memoryScrollPane = memoryScrollPane;
        this.settingsButton = settingsButton;
        this.helpButton = helpButton;

        setAutoCreateGaps(true);
        setAutoCreateContainerGaps(true);

        setHorizontalGroup(getHorizontalGroup());
        setVerticalGroup(getVerticalGroup());
    }

    /**
     * Positions layout components horizontally.
     */
    private GroupLayout.Group getHorizontalGroup() {

        return createSequentialGroup()
                .addGroup(createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(inputsPanel)
                        .addGap(10)
                        .addComponent(instructionPanel)
                        .addGap(10)
                        .addComponent(consoleAreaScrollPane))
                .addGap(20)
                .addGroup(createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(15)
                        .addComponent(registersPanel)
                        .addGap(55)
                        .addGroup(createSequentialGroup()
                                .addGap(50)
                                .addGroup(createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(runButton)
                                        .addGap(30)
                                        .addComponent(stepButton)
                                        .addGap(30)
                                        .addComponent(cleanButton))))
                .addGap(20)
                .addGroup(createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(memoryScrollPane)
                        .addGroup(createSequentialGroup()
                                .addGap(160)
                                .addComponent(settingsButton)
                                .addComponent(helpButton)));

    }

    /**
     * Positions layout components vertically.
     */
    private GroupLayout.Group getVerticalGroup() {

        return createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addGroup(createSequentialGroup()
                        .addComponent(inputsPanel)
                        .addGap(10)
                        .addComponent(instructionPanel)
                        .addGap(10)
                        .addComponent(consoleAreaScrollPane))
                .addGap(20)
                .addGroup(createSequentialGroup()
                        .addGap(15)
                        .addComponent(registersPanel)
                        .addGap(55)
                        .addGroup(createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addGap(50)
                                .addGroup(createSequentialGroup()
                                        .addComponent(runButton)
                                        .addGap(30)
                                        .addComponent(stepButton)
                                        .addGap(30)
                                        .addComponent(cleanButton))))
                .addGap(20)
                .addGroup(createSequentialGroup()
                        .addComponent(memoryScrollPane)
                        .addGroup(createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(settingsButton)
                                .addComponent(helpButton)));

    }
}
