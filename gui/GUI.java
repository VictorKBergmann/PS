package gui;

import ps.Cpu;
import ps.Loader;
import ps.Memory;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.util.Arrays;

public class GUI extends JFrame {

    private Memory mem;
    private Cpu cpu;
    private Loader loader;

    private JComboBox opModeComboBox;
    private String[] opMode = {"Continuous Mode", "Debug Mode"};

    private JTable memoryTable;
    private String[][] memoryList;
    private final JScrollPane memoryScrollPane;

    private final JButton runButton;
    private final JButton cleanButton;
    private final JButton stepButton;

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

    //private final JLabel inputLabel;
    //private final JTextField inputTextField;
    private final JOptionPane inputDialog;

    private final JTextArea textArea;
    private final JScrollPane textAreaScrollPane;
    private int currentLine;
    private String[] currentInstructions;
    private DefaultHighlighter highlighter;
    private DefaultHighlighter.DefaultHighlightPainter painter;

    private final JTextArea consoleArea;
    private final JScrollPane consoleAreaScrollPane;

    private final GroupLayout layout;

    private final Timer timer;

    public GUI(Memory mem, Cpu cpu, Loader loader){

        this.cpu = cpu;
        this.mem = mem;
        this.loader = loader;

        pcLabel = new JLabel("PC: ");
        spLabel = new JLabel("SP: ");
        accLabel = new JLabel("ACC: ");
        mopLabel = new JLabel("MOP: ");
        riLabel = new JLabel("RI: ");
        reLabel = new JLabel("RE: ");

        pcValueLabel = new JLabel(cpu.getPc());
        spValueLabel = new JLabel(cpu.getSp());
        accValueLabel = new JLabel(cpu.getAcc());
        mopValueLabel = new JLabel();
        riValueLabel = new JLabel(cpu.getRi());
        reValueLabel = new JLabel(cpu.getRe());

        //inputLabel = new JLabel("Input:");
        //inputTextField = new JTextField();
        inputDialog = new JOptionPane();
        inputDialog.setSize(new Dimension(100, 100));
        inputDialog.setVisible(true);
        cpu.setUserInput(inputDialog);

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        highlighter = (DefaultHighlighter)textArea.getHighlighter();
        painter = new DefaultHighlighter.DefaultHighlightPainter( Color.YELLOW );
        currentLine = 0;
        currentInstructions = new String[0];

        textAreaScrollPane = new JScrollPane(textArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        textAreaScrollPane.setMaximumSize(new Dimension(400, 700));

        consoleArea = new JTextArea();
        consoleArea.setLineWrap(true);
        consoleArea.setWrapStyleWord(true);
        consoleArea.setText("\t########## Console ##########\n");
        consoleArea.setEditable(false);
        cpu.setUserOutput(consoleArea);

        consoleAreaScrollPane = new JScrollPane(consoleArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        consoleAreaScrollPane.setMaximumSize(new Dimension(400, 100));

        opModeComboBox = new JComboBox(opMode);
        opModeComboBox.setMaximumSize(opModeComboBox.getPreferredSize());
        opModeComboBox.addActionListener(e -> opModeListener());

        runButton = new JButton();
        runButton.setText("Run");
        runButton.addActionListener(e -> {
            try {
                runButtonListener();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        });

        cleanButton = new JButton();
        cleanButton.setText("Clean");
        cleanButton.addActionListener(e -> cleanButtonListener());

        stepButton = new JButton();
        stepButton.setText("Step");
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
        memoryScrollPane = new JScrollPane(memoryTable);
        memoryScrollPane.setMaximumSize(new Dimension(200, 700));

        timer = new Timer(1000, e -> {
            stepButtonListener();
            if (currentInstructions.length == 0) {
                ((Timer) e.getSource()).stop();
                runButton.setEnabled(true);
                refresh();
            }
        });

        layout = new GroupLayout(getContentPane());
        initComponents();

    }

    private void initComponents() {

        setDummyInstructions();

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
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(textAreaScrollPane)
                        //.addGroup(layout.createSequentialGroup()
                                //.addComponent(inputLabel)
                                //.addComponent(inputTextField))
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
                .addGroup(layout.createSequentialGroup()
                        .addComponent(textAreaScrollPane)
                        //.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                //.addComponent(inputLabel)
                                //.addComponent(inputTextField))
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

    private void stepButtonListener() {

        try {
            loader.loadAllWordsFromString(currentInstructions[0]);
            try {
                cpu.execute(mem);
            } catch (Exception e) {
                consoleArea.append("\n" + e.getMessage() + "\n");
                if (opModeComboBox.getSelectedIndex() == 0) timer.stop();
                cleanButton.setEnabled(true);
                if (opModeComboBox.getSelectedIndex() == 1) stepButton.setEnabled(false);
                currentInstructions = new String[0];
                return;
            }
            updateGUI();

            highlighter.removeAllHighlights();
            highlighter.addHighlight(
                    textArea.getLineStartOffset(currentLine),
                    textArea.getLineEndOffset(currentLine),
                    painter);
            currentLine++;
        } catch (BadLocationException badLocationException) {
            badLocationException.printStackTrace();
        }

        currentInstructions = Arrays.copyOfRange(currentInstructions, 1, currentInstructions.length);

    }

    private void runButtonListener() throws InterruptedException {

        if (textArea.getText().length() == 0) {
            consoleArea.append("\nCan't run! Empty editor.\n");
            return;
        }

        if (isSyntaxWrong()) {
            consoleArea.append("\nCheck syntax! You can only insert binary code and the " +
                    "instructions need to be one above the other, without spaces.\n");
            return;
        }

        if (currentInstructions.length == 0) {
            currentInstructions = textArea.getText().trim().split("\\r?\\n");
            currentLine = 0;
        }

        if (!currentInstructions[currentInstructions.length-1].equals("0000000000001011")) {
            System.out.println(currentInstructions[currentInstructions.length-1]);
            consoleArea.append("\nSTOP instruction missing!\n");
            return;
        }

        cleanButton.setEnabled(false);
        opModeComboBox.setEnabled(false);
        textArea.setEditable(false);
        if (opModeComboBox.getSelectedIndex() == 0) runButton.setEnabled(false);

        if (opModeComboBox.getSelectedIndex() == 0) {
            timer.start();
        } else {
            stepButtonListener();
            if (currentInstructions.length == 0) refresh();
        }

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
        opModeComboBox.setEnabled(true);
        opModeListener();
        textArea.setEditable(true);

        mem.init();
        cpu.init();
        loader.setPosition();
        updateGUI();

    }

    private void cleanButtonListener() {

        currentLine = 0;
        textArea.setText("");
        consoleArea.setText("\t########## Console ##########\n");
        refresh();

    }

    private void opModeListener() {

        if (opModeComboBox.getSelectedIndex() == 0) {

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

    // TEST PURPOSES
    private void setDummyInstructions() {
        textArea.setText("00000000010000100000000000001011\n");
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
        textArea.append("0000000000001011\n");
    }

}
