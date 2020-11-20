package gui;

import ps.Cpu;
import ps.Loader;
import ps.Memory;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.util.HashMap;

public class GUI extends JFrame {

    private final Memory mem;
    private final Cpu cpu;
    private final Loader loader;

    JMenuBar menuBar;
    JMenu examples;
    JMenu help;
    JRadioButtonMenuItem continuousOp;
    JRadioButtonMenuItem debugOp;
    ButtonGroup operation;

    private final JTable memoryTable;
    private String[][] memoryList;
    private final JScrollPane memoryScrollPane;

    private final JButton runButton;
    private final JButton cleanButton;
    private final JButton stepButton;

    private final JPanel inputPanel;
    private final JPanel memoryPanel;
    private final JPanel outputPanel;

    private final JLabel pcLabel;
    private final JLabel spLabel;
    private final JLabel accLabel;
    private final JLabel mopLabel;
    private final JLabel riLabel;
    private final JLabel reLabel;

    private final JLabel pcValueLabel;
    private final JLabel spValueLabel;
    private final JLabel accValueLabel;
    private final JLabel mopValueLabel;
    private final JLabel riValueLabel;
    private final JLabel reValueLabel;

    private final JTextArea textArea;
    private final JScrollPane textAreaScrollPane;
    private int currentLine;
    private String[] currentInstructions;
    private final DefaultHighlighter highlighter;
    private final DefaultHighlighter.DefaultHighlightPainter executionColor;
    private final DefaultHighlighter.DefaultHighlightPainter errorColor;

    private final JTextArea consoleArea;
    private final JScrollPane consoleAreaScrollPane;

    private final GroupLayout layout;

    private final Timer timer;

    private final HashMap<String, Integer> currentLineMap;
    private boolean isLoading;

    public GUI(Memory mem, Cpu cpu, Loader loader){

        menuBar = new JMenuBar();
        menuBar.setBackground(Color.LIGHT_GRAY);
        menuBar.setBorder(BorderFactory.createEmptyBorder());

        examples = new JMenu("Examples");
        examples.setForeground(Color.DARK_GRAY);
        help = new JMenu("Help");
        help.setForeground(Color.DARK_GRAY);

        continuousOp = new JRadioButtonMenuItem("Continuous");
        continuousOp.setMaximumSize(new Dimension(100, 20));
        continuousOp.setBackground(Color.LIGHT_GRAY);
        continuousOp.setForeground(Color.DARK_GRAY);
        continuousOp.setSelected(true);
        continuousOp.addActionListener(e -> opModeListener());

        debugOp = new JRadioButtonMenuItem("Debug");
        debugOp.setMaximumSize(new Dimension(100, 20));
        debugOp.setBackground(Color.LIGHT_GRAY);
        debugOp.setForeground(Color.DARK_GRAY);
        debugOp.addActionListener(e -> opModeListener());

        operation = new ButtonGroup();
        operation.add(continuousOp);
        operation.add(debugOp);

        JLabel op = new JLabel("Operation Mode: ");
        op.setForeground(Color.DARK_GRAY);

        menuBar.add(op);
        menuBar.add(continuousOp);
        menuBar.add(debugOp);
        menuBar.add(new JSeparator());
        menuBar.add(examples);
        menuBar.add(help);

        this.cpu = cpu;
        this.mem = mem;
        this.loader = loader;

        JLabel inputLabel = new JLabel("INPUT", JLabel.CENTER);
        inputLabel.setForeground(Color.WHITE);
        inputPanel = new JPanel();
        inputPanel.setBackground(Color.DARK_GRAY);
        inputPanel.setMaximumSize(new Dimension(400, 2));
        inputPanel.add(inputLabel);
        JLabel memoryLabel = new JLabel("MEMORY", JLabel.CENTER);
        memoryLabel.setForeground(Color.WHITE);
        memoryPanel = new JPanel();
        memoryPanel.setBackground(Color.DARK_GRAY);
        memoryPanel.setMaximumSize(new Dimension(200, 2));
        memoryPanel.add(memoryLabel);
        JLabel outputLabel = new JLabel("OUTPUT", JLabel.CENTER);
        outputLabel.setForeground(Color.WHITE);
        outputPanel = new JPanel();
        outputPanel.setBackground(Color.DARK_GRAY);
        outputPanel.setMaximumSize(new Dimension(400, 2));
        outputPanel.add(outputLabel);

        currentLineMap = new HashMap<>();
        currentLine = 0;
        isLoading = true;

        pcLabel = new JLabel("PC: ");
        pcLabel.setForeground(Color.LIGHT_GRAY);
        spLabel = new JLabel("SP: ");
        spLabel.setForeground(Color.LIGHT_GRAY);
        accLabel = new JLabel("ACC: ");
        accLabel.setForeground(Color.LIGHT_GRAY);
        mopLabel = new JLabel("MOP: ");
        mopLabel.setForeground(Color.LIGHT_GRAY);
        riLabel = new JLabel("RI: ");
        riLabel.setForeground(Color.LIGHT_GRAY);
        reLabel = new JLabel("RE: ");
        reLabel.setForeground(Color.LIGHT_GRAY);

        pcValueLabel = new JLabel(cpu.getPc());
        pcValueLabel.setForeground(Color.LIGHT_GRAY);
        spValueLabel = new JLabel(cpu.getSp());
        spValueLabel.setForeground(Color.LIGHT_GRAY);
        accValueLabel = new JLabel(cpu.getAcc());
        accValueLabel.setForeground(Color.LIGHT_GRAY);
        mopValueLabel = new JLabel();
        mopValueLabel.setForeground(Color.LIGHT_GRAY);
        riValueLabel = new JLabel(cpu.getRi());
        riValueLabel.setForeground(Color.LIGHT_GRAY);
        reValueLabel = new JLabel(cpu.getRe());
        reValueLabel.setForeground(Color.LIGHT_GRAY);

        JOptionPane inputDialog = new JOptionPane();
        inputDialog.setSize(new Dimension(100, 100));
        inputDialog.setVisible(true);
        cpu.setUserInput(inputDialog);

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(Color.LIGHT_GRAY);
        textArea.setForeground(Color.WHITE);
        textArea.setForeground(Color.DARK_GRAY);
        highlighter = (DefaultHighlighter)textArea.getHighlighter();
        executionColor = new DefaultHighlighter.DefaultHighlightPainter( Color.YELLOW );
        errorColor = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
        currentInstructions = new String[0];

        textAreaScrollPane = new JScrollPane(textArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        textAreaScrollPane.setMaximumSize(new Dimension(400, 700));

        consoleArea = new JTextArea();
        consoleArea.setLineWrap(true);
        consoleArea.setWrapStyleWord(true);
        consoleArea.setEditable(false);
        consoleArea.setBackground(Color.BLACK);
        consoleArea.setForeground(Color.GREEN);
        cpu.setUserOutput(consoleArea);

        consoleAreaScrollPane = new JScrollPane(consoleArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        consoleAreaScrollPane.setMaximumSize(new Dimension(400, 100));

        runButton = new JButton();
        runButton.setText("Run");
        runButton.setBackground(Color.GRAY);
        runButton.setForeground(Color.WHITE);
        runButton.addActionListener(e -> {
            try {
                runButtonListener();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        });

        cleanButton = new JButton();
        cleanButton.setText("Clean");
        cleanButton.setBackground(Color.GRAY);
        cleanButton.setForeground(Color.WHITE);
        cleanButton.addActionListener(e -> cleanButtonListener());

        stepButton = new JButton();
        stepButton.setText("Step");
        stepButton.setBackground(Color.GRAY);
        stepButton.setForeground(Color.WHITE);
        opModeListener();
        stepButton.addActionListener(e -> {
            try {
                runButtonListener();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        });

        memoryList = new String[mem.getMem().size()][2];
        String[] columnNames = {"Position", "Value"};
        updateMemoryList();
        memoryTable = new JTable(memoryList, columnNames);
        memoryTable.getColumnModel().getColumn(0).setPreferredWidth(10);
        memoryTable.setEnabled(false);
        memoryTable.setBackground(Color.LIGHT_GRAY);
        memoryTable.setForeground(Color.DARK_GRAY);
        memoryTable.getTableHeader().setBackground(Color.DARK_GRAY);
        memoryTable.getTableHeader().setForeground(Color.WHITE);
        memoryTable.setBorder(BorderFactory.createEmptyBorder());
        memoryScrollPane = new JScrollPane(memoryTable);
        memoryScrollPane.setBorder(BorderFactory.createEmptyBorder());
        memoryScrollPane.getVerticalScrollBar().setBackground(Color.DARK_GRAY);
        memoryScrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI(){
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
        });
        JPanel panel = new JPanel();
        panel.setBackground(Color.DARK_GRAY);
        memoryScrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, panel);
        memoryScrollPane.setMaximumSize(new Dimension(200, 700));

        timer = new Timer(1000, e -> stepButtonListener());

        layout = new GroupLayout(getContentPane());
        initComponents();

    }

    private void initComponents() {

        setDummyInstructions();

        getContentPane().setBackground(Color.DARK_GRAY);
        getContentPane().setLayout(layout);
        getContentPane().setPreferredSize(new Dimension(800, 700));
        setJMenuBar(menuBar);
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
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(inputPanel)
                        .addComponent(textAreaScrollPane)
                        .addComponent(outputPanel)
                        .addComponent(consoleAreaScrollPane))
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
                        .addGap(130)
                        .addComponent(runButton)
                        .addGap(30)
                        .addComponent(cleanButton)
                        .addGap(30)
                        .addComponent(stepButton))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(memoryPanel)
                        .addComponent(memoryScrollPane));

    }

    private GroupLayout.Group getVerticalGroup() {

        return layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(inputPanel)
                        .addComponent(textAreaScrollPane)
                        .addComponent(outputPanel)
                        .addComponent(consoleAreaScrollPane))
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
                        .addGap(130)
                        .addComponent(runButton)
                        .addGap(30)
                        .addComponent(cleanButton)
                        .addGap(30)
                        .addComponent(stepButton))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(memoryPanel)
                        .addComponent(memoryScrollPane));


    }

    private void stepButtonListener() {

        highlight(executionColor);

        try {
            cpu.execute(mem);
        } catch (Exception e) {
            consoleArea.append("\n" + e.getMessage() + "\n");
            highlight(errorColor);
            if (continuousOp.isSelected()) {
                runButton.setEnabled(true);
                timer.stop();
            }
            setOperationState(true, true);
            isLoading = true;
            currentInstructions = new String[0];
            cleanButton.setEnabled(true);
            return;
        }

        updateGUI();

        currentLine = currentLineMap.get(cpu.getPc());

    }

    private void runButtonListener() throws InterruptedException {

        if (textArea.getText().length() == 0) {
            consoleArea.append("\nCan't run! Empty editor.\n");
            return;
        }

        if (isSyntaxWrong()) {
            consoleArea.append("\nCheck syntax! Only binary code allowed.\n");
            return;
        }


        if (currentInstructions.length == 0)
            currentInstructions = textArea.getText().trim().split("\\r?\\n");


        if (!currentInstructions[currentInstructions.length-1].equals("0000000000001011")) {
            consoleArea.append("\nSTOP instruction missing!\n");
            currentInstructions = new String[0];
            return;
        }

        if (isLoading) {
            refresh();
            fillCurrentLineMap();
        }

        cleanButton.setEnabled(false);
        setOperationState(false, false);
        textArea.setEditable(false);

        if (continuousOp.isSelected()) runButton.setEnabled(false);

        if (continuousOp.isSelected()) {
            timer.start();
        } else {
            stepButtonListener();
        }

    }

    private void highlight(DefaultHighlighter.DefaultHighlightPainter color) {

        highlighter.removeAllHighlights();
        try {
            highlighter.addHighlight(
                    textArea.getLineStartOffset(currentLine),
                    textArea.getLineEndOffset(currentLine),
                    color);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

    }

    private void setOperationState(boolean c, boolean d) {

        continuousOp.setEnabled(c);
        debugOp.setEnabled(d);

    }

    private void updateGUI() {

        updateMemoryList();
        memoryTable.updateUI();
        accValueLabel.setText(cpu.getAcc());
        pcValueLabel.setText(cpu.getPc());
        spValueLabel.setText(cpu.getSp());
        riValueLabel.setText(cpu.getRi());
        reValueLabel.setText(cpu.getRe());

    }

    private void refresh() {

        cleanButton.setEnabled(true);
        setOperationState(true, true);
        opModeListener();
        textArea.setEditable(true);

        if (!currentLineMap.isEmpty()) currentLineMap.clear();
        currentLine = 0;
        isLoading = true;

        mem.init();
        cpu.init();
        loader.setPosition();
        updateGUI();

    }

    private void cleanButtonListener() {

        currentLine = 0;
        textArea.setText("");
        refresh();

    }

    private void opModeListener() {

        if (continuousOp.isSelected()) {

            mopValueLabel.setText("00000000");
            runButton.setEnabled(true);
            stepButton.setEnabled(false);

        } else {

            mopValueLabel.setText("00000001");
            runButton.setEnabled(false);
            stepButton.setEnabled(true);

        }

    }

    private void updateMemoryList() {

        String[] valuesList = mem.getMem().toArray(new String[mem.getMem().size()]);
        for (int i=0; i<mem.getMem().size(); i++) {
            memoryList[i][0] = Integer.toString(i);
            memoryList[i][1] = valuesList[i];
        }

    }

    private boolean isSyntaxWrong() {

        return !textArea.getText().trim().matches("[01\n]+");

    }

    private String bitsPadding(Integer pc) {
        String temp2 = Integer.toString(pc,2);
        String temp1 = "";
        for (int i=16; i > temp2.length(); i--) {
            temp1 += "0";
        }
        return temp1.concat(temp2);
    }

    private void fillCurrentLineMap() {

        int pc = 13;
        int line = 0;

        for(String instruction : currentInstructions) {
            loader.loadAllWordsFromString(instruction);
            currentLineMap.put(bitsPadding(pc), line);
            switch (instruction.length()) {
                case 16 -> pc += 1;
                case 32 -> pc += 2;
                case 48 -> pc += 3;
            }
            line++;
        }
        isLoading = false;
    }

    // TEST PURPOSES
    private void setDummyInstructions() {
        textArea.setText("00000000010000100000000000000111\n");
        textArea.append("00000000000001000000000000010101\n");
        textArea.append("00000000010001100000000000000001\n");
        textArea.append("00000000000000000000000000001111\n");
        textArea.append("00000000010000100000000000001111\n");
        textArea.append("0000000000001011\n");
/*        textArea.setText("00000000010000100000000000001011\n");
        textArea.append("00000000010011100000000000000100\n");
        textArea.append("00000000000001110000000000011111\n");
        textArea.append("00000000010001100000000000011110\n");
        textArea.append("00000000000001110000000000101100\n");
        textArea.append("00000000010000100000000000001010\n");
        textArea.append("00000000000001110000000000011011\n");
        textArea.append("00000000000001010000000000011011\n");
        textArea.append("00000000000000010000000000011011\n");
        textArea.append("00000000000001100000000000000010\n");
        textArea.append("00000000000001110000000000011110\n");
        textArea.append("000000000010110100000000001000000000000000011111\n");
        textArea.append("0000000000001011\n");*/
    }

}
