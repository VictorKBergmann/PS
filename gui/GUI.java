package gui;

import ps.Cpu;
import ps.Memory;

import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {

    private Memory mem;
    private Cpu cpu;

    private JComboBox opModeComboBox;
    private String[] opMode = {"Mode 1", "Mode 2", "Mode 3"};

    private JList memoryList;

    private final JButton runButton;
    private final JButton cleanButton;
    private final JButton stepButton;

    private final JLabel pcLabel;
    private final JLabel spLabel;
    private final JLabel accLabel;
    private final JLabel mopLabel;
    private final JLabel riLabel;
    private final JLabel reLabel;
    private final JLabel adressLabel;

    private final JLabel pcValueLabel;
    private final JLabel spValueLabel;
    private final JLabel accValueLabel;
    private final JLabel mopValueLabel;
    private final JLabel riValueLabel;
    private final JLabel reValueLabel;
    private final JLabel adressValueLabel;

    private final JTextArea textArea;

    private final JScrollPane textAreaScrollPane;
    private final JScrollPane memoryScrollPane;

    private final GroupLayout layout;

    public GUI(Memory mem, Cpu cpu){

        this.cpu = cpu;
        this.mem = mem;

        pcLabel = new JLabel("PC: ");
        spLabel = new JLabel("SP: ");
        accLabel = new JLabel("ACC: ");
        mopLabel = new JLabel("MOP: ");
        riLabel = new JLabel("RI: ");
        reLabel = new JLabel("RE: ");
        adressLabel = new JLabel("ADDRESS: ");

        pcValueLabel = new JLabel("0");
        spValueLabel = new JLabel("0");
        accValueLabel = new JLabel("0");
        mopValueLabel = new JLabel("0");
        riValueLabel = new JLabel("0");
        reValueLabel = new JLabel("0");
        adressValueLabel = new JLabel("0");

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        textAreaScrollPane = new JScrollPane(textArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        textAreaScrollPane.setMaximumSize(new Dimension(400, 700));

        opModeComboBox = new JComboBox(opMode);
        opModeComboBox.setMaximumSize(opModeComboBox.getPreferredSize());
        //opModeComboBox.addActionListener();

        runButton = new JButton();
        runButton.setText("Run");
        runButton.addActionListener(e -> {
            accValueLabel.setText(Integer.toString(cpu.getAcc()));
            pcValueLabel.setText(Integer.toString(cpu.getPc()));
            spValueLabel.setText(Integer.toString(cpu.getSp()));
            riValueLabel.setText(cpu.getRi());
            reValueLabel.setText(cpu.getRe());
            memoryList.updateUI();
        });

        cleanButton = new JButton();
        cleanButton.setText("Clean");
        cleanButton.addActionListener(e -> textArea.setText(""));

        stepButton = new JButton();
        stepButton.setText("Step");
        //stepButton.addActionListener();

        String[] list = new String[mem.getMem().size()];
        //for (int i=0; i<512; i++) {
        //    list[i] = i+1 + " - ";
        //}
        memoryList = new JList(mem.getMem().toArray(list));
        memoryScrollPane = new JScrollPane(memoryList);
        memoryScrollPane.setMaximumSize(new Dimension(200, 700));

        layout = new GroupLayout(getContentPane());
        initComponents();

    }

    private void initComponents() {

        getContentPane().setLayout(layout);
        getContentPane().setPreferredSize(new Dimension(800, 700));
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(getHorizontalGroup());
        layout.setVerticalGroup(getVerticalGroup());

        setTitle("Calingaert Computer");
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();

    }

    private GroupLayout.Group getHorizontalGroup() {

        return layout.createSequentialGroup()
                .addComponent(textAreaScrollPane)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(200)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(pcLabel)
                                .addComponent(pcValueLabel)
                                .addGap(30))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(spLabel)
                                .addComponent(spValueLabel)
                                .addGap(30))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(accLabel)
                                .addComponent(accValueLabel)
                                .addGap(30))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(mopLabel)
                                .addComponent(mopValueLabel)
                                .addGap(30))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(riLabel)
                                .addComponent(riValueLabel)
                                .addGap(30))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(reLabel)
                                .addComponent(reValueLabel)
                                .addGap(30))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(adressLabel)
                                .addComponent(adressValueLabel)
                                .addGap(30))
                        .addGap(70)
                        .addComponent(opModeComboBox)
                        .addGap(30)
                        .addComponent(runButton)
                        .addGap(30)
                        .addComponent(cleanButton)
                        .addGap(30)
                        .addComponent(stepButton))
                .addComponent(memoryScrollPane);

    }

    private GroupLayout.Group getVerticalGroup() {

        return layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(textAreaScrollPane)
                .addGroup(layout.createSequentialGroup()
                        .addGap(200)
                        .addGroup(layout.createParallelGroup()
                                .addComponent(pcLabel)
                                .addComponent(pcValueLabel)
                                .addGap(30))
                        .addGroup(layout.createParallelGroup()
                                .addComponent(spLabel)
                                .addComponent(spValueLabel)
                                .addGap(30))
                        .addGroup(layout.createParallelGroup()
                                .addComponent(accLabel)
                                .addComponent(accValueLabel)
                                .addGap(30))
                        .addGroup(layout.createParallelGroup()
                                .addComponent(mopLabel)
                                .addComponent(mopValueLabel)
                                .addGap(30))
                        .addGroup(layout.createParallelGroup()
                                .addComponent(riLabel)
                                .addComponent(riValueLabel)
                                .addGap(30))
                        .addGroup(layout.createParallelGroup()
                                .addComponent(reLabel)
                                .addComponent(reValueLabel)
                                .addGap(30))
                        .addGroup(layout.createParallelGroup()
                                .addComponent(adressLabel)
                                .addComponent(adressValueLabel)
                                .addGap(30))
                        .addGap(70)
                        .addComponent(opModeComboBox)
                        .addGap(30)
                        .addComponent(runButton)
                        .addGap(30)
                        .addComponent(cleanButton)
                        .addGap(30)
                        .addComponent(stepButton))
                .addComponent(memoryScrollPane);

    }
}
