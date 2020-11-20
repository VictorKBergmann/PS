package gui;

import ps.Cpu;
import ps.Loader;
import ps.Memory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class GUI extends JFrame {

    private final Memory mem; /** Used to keep user instructions and data. */
    private final Cpu cpu; /** Used to run user instructions. */
    private final Loader loader; /** Used to load user instructions. */

    JMenuBar menuBar;
    JMenu examples; /** Used to select default examples. */
    JButton help; /** Button used to show help dialog */
    JOptionPane helpDialog; /** Used to call help dialog */
    JRadioButtonMenuItem continuousOp; /** Radio button representing run op mode */
    JRadioButtonMenuItem debugOp; /** Radio button representing debug op mode */
    JSlider slider; /** Used to increase/decrease execution speed */
    ButtonGroup operation; /** Used to group both radio buttons */

    private final JTable memoryTable; /** Shows memory as a table */
    private String[][] memoryList; /** Keeps memory positions and values */
    private final JScrollPane memoryScrollPane; /** Allows memory list to be scrolled. */

    /**
     * User action buttons
     */
    private final JButton runButton;
    private final JButton cleanButton;
    private final JButton stepButton;

    /**
     * Panels used to centralize text
     */
    private final JPanel inputPanel;
    private final JPanel memoryPanel;
    private final JPanel outputPanel;

    /**
     * Registers label
     */
    private final JLabel pcLabel;
    private final JLabel spLabel;
    private final JLabel accLabel;
    private final JLabel mopLabel;
    private final JLabel riLabel;
    private final JLabel reLabel;

    /**
     * Registers current value
     */
    private final JLabel pcValueLabel;
    private final JLabel spValueLabel;
    private final JLabel accValueLabel;
    private final JLabel mopValueLabel;
    private final JLabel riValueLabel;
    private final JLabel reValueLabel;

    private final JTextArea textArea; /** Area where user writes its instructions */
    private final JScrollPane textAreaScrollPane; /** Allows editor to be scrolled. */
    private int currentLine; /** Used to define a highlight at the current instruction line. */
    private String[] currentInstructions; /** Used to know how many instructions will be executed. */

    /**
     * Execution highlighter
     */
    private final DefaultHighlighter highlighter;
    private final DefaultHighlighter.DefaultHighlightPainter executionColor;
    private final DefaultHighlighter.DefaultHighlightPainter errorColor;

    private final JTextArea consoleArea; /** Text area to show error messages and outputs. */
    private final JScrollPane consoleAreaScrollPane; /** Allow console output to be scrolled. */

    private final GroupLayout layout; /** Used to create an interface custom layout. */

    private final Timer timer; /** Used to set a delay between executions (RUN MODE). */

    private final HashMap<String, Integer> currentLineMap; /** Keeps index of each line and its instruction. */
    private boolean isLoading; /** Used to know if instructions are already loaded in memory. */

    public GUI(Memory mem, Cpu cpu, Loader loader){

        /**
         * Starts menu bar and change its design
         */
        menuBar = new JMenuBar();
        menuBar.setBackground(Color.LIGHT_GRAY);
        menuBar.setBorder(BorderFactory.createEmptyBorder());

        /**
         * Starts slider and define available execution speeds
         */
        slider = new JSlider(JSlider.HORIZONTAL, 100, 1000, 1000);
        slider.setBackground(Color.LIGHT_GRAY);
        slider.setForeground(Color.BLACK);
        slider.setMajorTickSpacing(100);
        slider.setPaintTicks(true);
        slider.addChangeListener((ChangeEvent l) -> changeDelay());

        /**
         * Creates a list of default examples inside the menu bar
         */
        examples = new JMenu("Examples");
        examples.setForeground(Color.DARK_GRAY);
        for (int i = 1; i <= 2; i++) {
            JMenuItem exampleItem = new JMenuItem("Example #" + i);
            exampleItem.addActionListener(e -> setExampleChosen(exampleItem.getText().split("#")[1]));
            examples.add(exampleItem);
        }

        /**
         * Starts help button and define a default message at Util class
         */
        help = new JButton("Help");
        help.setBackground(Color.LIGHT_GRAY);
        help.setForeground(Color.DARK_GRAY);
        help.setBorder(BorderFactory.createEmptyBorder());
        help.setMargin(new Insets(10, 10, 10,10));
        help.addActionListener(e -> helpDialog.showMessageDialog(null, Util.getHelp()));
        helpDialog = new JOptionPane();

        /**
         * Starts run op mode button and define its listener
         */
        continuousOp = new JRadioButtonMenuItem("Run");
        continuousOp.setMaximumSize(new Dimension(100, 20));
        continuousOp.setBackground(Color.LIGHT_GRAY);
        continuousOp.setForeground(Color.DARK_GRAY);
        continuousOp.setSelected(true);
        continuousOp.addActionListener(e -> opModeListener());

        /**
         * Starts debug op mode button and define its listener
         */
        debugOp = new JRadioButtonMenuItem("Debug");
        debugOp.setMaximumSize(new Dimension(100, 20));
        debugOp.setBackground(Color.LIGHT_GRAY);
        debugOp.setForeground(Color.DARK_GRAY);
        debugOp.addActionListener(e -> opModeListener());

        /**
         * Adds both buttons as group inside the interface
         */
        operation = new ButtonGroup();
        operation.add(continuousOp);
        operation.add(debugOp);

        /**
         * Labels used inside the interface
         */
        JLabel op = new JLabel("Operation Mode: ");
        op.setForeground(Color.DARK_GRAY);
        JLabel speed = new JLabel("Execution Speed: ");
        speed.setForeground(Color.DARK_GRAY);

        /**
         * Adds every component in the menu bar
         */
        menuBar.add(op);
        menuBar.add(continuousOp);
        menuBar.add(debugOp);
        menuBar.add(new JSeparator());
        menuBar.add(speed);
        menuBar.add(slider);
        menuBar.add(new JSeparator());
        menuBar.add(examples);
        menuBar.add(help);

        this.cpu = cpu;
        this.mem = mem;
        this.loader = loader;

        /**
         * Defines labels and panels used in the interface
         */
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
        currentInstructions = new String[0];

        /**
         * Changes style of registers label and value
         */
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

        /**
         * Creates READ instruction dialog
         * and sends it to be used at CPU class.
         */
        JOptionPane inputDialog = new JOptionPane();
        inputDialog.setSize(new Dimension(100, 100));
        inputDialog.setVisible(true);
        cpu.setUserInput(inputDialog);

        /**
         * Creates user instructions area
         */
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(Color.LIGHT_GRAY);
        textArea.setForeground(Color.WHITE);
        textArea.setForeground(Color.DARK_GRAY);

        /**
         * Defines execution and error colors used to highlight instructions
         */
        highlighter = (DefaultHighlighter)textArea.getHighlighter();
        executionColor = new DefaultHighlighter.DefaultHighlightPainter( Color.YELLOW );
        errorColor = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);

        /**
         * Allow instructions text area to be scrolled vertically only
         */
        textAreaScrollPane = new JScrollPane(textArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        textAreaScrollPane.setMaximumSize(new Dimension(400, 700));

        /**
         * Starts console area and send it to the CPU
         * It'll be used when the WRITE instruction is executed
         */
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

        /**
         * Starts user buttons and its listeners
         */
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

        /**
         * Clone memory values and defines its design
         */
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

        /**
         * Defines timer initial delay and its listener
         */
        timer = new Timer(slider.getValue(), e -> stepButtonListener());

        /**
         * Initiates GUI
         */
        layout = new GroupLayout(getContentPane());
        initComponents();

    }


    /**
     * Starts interface's main components.
     */
    private void initComponents() {

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

    /**
     * Positions layout components horizontally.
     */
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

    /**
     * Positions layout components vertically.
     */
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

    /**
     * Set highlighter current position.
     * Execute one instruction at a time.
     * In case of exception the error message is shown in the output.
     */
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

    /**
     * Check for errors before attempting to run.
     * If Run is selected, it starts the timer between instructions.
     * If Debug is selected, it executes one and awaits user action.
     */
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
        examples.setEnabled(false);

        if (debugOp.isSelected()) slider.setEnabled(false);

        if (continuousOp.isSelected()) runButton.setEnabled(false);

        if (continuousOp.isSelected()) {
            timer.start();
        } else {
            stepButtonListener();
        }

    }

    /**
     * Removes highlighter from previous instruction and
     * create a new highlight on current instruction.
     */
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

    /**
     * Enable/Disable operation mode options.
     */
    private void setOperationState(boolean c, boolean d) {

        continuousOp.setEnabled(c);
        debugOp.setEnabled(d);

    }

    /**
     * Updates memory list new values.
     * Updates registers new values.
     */
    private void updateGUI() {

        updateMemoryList();
        memoryTable.updateUI();
        accValueLabel.setText(cpu.getAcc());
        pcValueLabel.setText(cpu.getPc());
        spValueLabel.setText(cpu.getSp());
        riValueLabel.setText(cpu.getRi());
        reValueLabel.setText(cpu.getRe());

    }

    /**
     * Updates GUI components after user action.
     * Restarts Memory, CPU and Loader.
     */
    private void refresh() {

        cleanButton.setEnabled(true);
        setOperationState(true, true);
        opModeListener();
        textArea.setEditable(true);
        examples.setEnabled(true);
        slider.setEnabled(true);

        if (!currentLineMap.isEmpty()) currentLineMap.clear();
        currentLine = 0;
        isLoading = true;

        mem.init();
        cpu.init();
        loader.setPosition();
        updateGUI();

    }

    /**
     * Runs when clean button is pressed.
     */
    private void cleanButtonListener() {

        currentLine = 0;
        textArea.setText("");
        refresh();

    }

    /**
     * Changes state of operation mode.
     */
    private void opModeListener() {

        if (continuousOp.isSelected()) {

            mopValueLabel.setText("00000000");
            runButton.setEnabled(true);
            stepButton.setEnabled(false);
            slider.setEnabled(true);

        } else {

            mopValueLabel.setText("00000001");
            runButton.setEnabled(false);
            stepButton.setEnabled(true);

        }

    }

    /**
     * Updates new execution speed selected by user.
     */
    private ChangeListener changeDelay() {

        timer.setDelay(slider.getValue());
        return null;

    }

    /**
     * Gets current values in the memory.
     */
    private void updateMemoryList() {

        String[] valuesList = mem.getMem().toArray(new String[mem.getMem().size()]);
        for (int i=0; i<mem.getMem().size(); i++) {
            memoryList[i][0] = Integer.toString(i);
            memoryList[i][1] = valuesList[i];
        }

    }

    /**
     * Checks if syntax is wrong.
     * Only binary code (0/1) and \n are allowed.
     */
    private boolean isSyntaxWrong() {

        return !textArea.getText().trim().matches("[01\n]+");

    }

    /**
     * Converts an integer to a 16-bit binary string.
     */
    private String bitsPadding(Integer pc) {

        String temp2 = Integer.toString(pc,2);
        String temp1 = "";
        for (int i=16; i > temp2.length(); i--) {
            temp1 += "0";
        }
        return temp1.concat(temp2);

    }

    /**
     * Defines each line of the interface with its
     * respective instruction.
     */
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

    /**
     * Write the chosen example in the editor.
     */
    private ActionListener setExampleChosen(String index) {

        String example = Util.getExample(Integer.parseInt(index));
        textArea.setText(example);
        return null;

    }

}
