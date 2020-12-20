package gui;

import ps.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class GUI extends JFrame {

    private final Memory mem; /** Used to keep user instructions and data. */
    private final Cpu cpu; /** Used to run user instructions. */
    private final Loader loader; /** Used to load user instructions. */
    private final MacroProcessor mp; /** Used for processing macros to assembler input. */
    private final Assembler assembler; /** Used to assemble the source code. */
    private final Linker linker; /** Used to link both segments and fix references. */

    JButton helpButton; /** Button used to show help info */
    JButton settingsButton; /** Button used to show settings dialog */
    JOptionPane settingsPanel;
    JSlider speedSlider; /** Used to increase/decrease execution speed */
    JRadioButton continuousOp; /** Radio button representing run op mode */
    JRadioButton debugOp; /** Radio button representing debug op mode */
    
    private final JTextField firstFileField; /** Source code first field */
    private final JTextField secondFileField; /** Source code second field */

    private final JTable memoryTable; /** Show memory as a table */
    private String[][] memoryList; /** Keep memory positions and values */
    private final JScrollPane memoryScrollPane; /** Allow memory list to be scrolled. */

    /**
     * User action buttons
     */
    private final JButton runButton;
    private final JButton cleanButton;
    private final JButton stepButton;

    /**
     * Panels used to centralize text
     */
    private final JPanel inputsPanel;
    private final JPanel registersPanel;
    private final JPanel memoryPanel;

    private final JPanel instructionPanel;
    private final JLabel instructionLabel;

    /**
     * Registers current value
     */
    private final JLabel pcValueLabel;
    private final JLabel spValueLabel;
    private final JLabel accValueLabel;
    private final JLabel mopValueLabel;
    private final JLabel riValueLabel;
    private final JLabel reValueLabel;

    private final JTextArea consoleArea; /** Text area to show error messages and outputs. */
    private final JScrollPane consoleAreaScrollPane; /** Allow console output to be scrolled. */

    private final Timer timer; /** Used to set a delay between executions (RUN MODE). */

    private ArrayList<Boolean> steps; /** Used to know which step the program it is */
    private ArrayList<String> files; /** Store files path */
    private boolean isInitialProcess; /** Used to check whether it should initiate a process or not */

    public GUI(Memory mem, Cpu cpu, Loader loader, MacroProcessor mp, Assembler assembler, Linker linker){

        this.cpu = cpu;
        this.mem = mem;
        this.loader = loader;
        this.mp = mp;
        this.assembler = assembler;
        this.linker = linker;

        /**
         * Define variables related to the running process
         */
        steps = new ArrayList<>();
        files = new ArrayList<>();
        isInitialProcess = true;

        /**
         * Define panels used in the interface
         */
        JPanel sourceCodePanel = new CustomInputPanel("FILE #1");
        firstFileField = new JTextField();
        firstFileField.setPreferredSize(new Dimension(150, 20));
        sourceCodePanel.add(firstFileField, BorderLayout.CENTER);

        JPanel libraryPanel = new CustomInputPanel("FILE #2");
        secondFileField = new JTextField();
        secondFileField.setPreferredSize(new Dimension(150, 20));
        libraryPanel.add(secondFileField, BorderLayout.CENTER);

        inputsPanel = new JPanel();
        TitledBorder inputsBorder = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                "Inputs");
        inputsPanel.setMaximumSize(new Dimension(350, 100));
        inputsBorder.setTitleColor(Color.LIGHT_GRAY);
        inputsPanel.setBorder(inputsBorder);
        inputsPanel.setBackground(Color.DARK_GRAY);
        inputsPanel.add(sourceCodePanel);
        inputsPanel.add(libraryPanel);

        memoryPanel = new JPanel();
        memoryPanel.setBackground(Color.DARK_GRAY);
        memoryPanel.setMaximumSize(new Dimension(200, 2));

        instructionLabel = new JLabel();
        instructionLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
        instructionLabel.setForeground(Color.GREEN);

        instructionPanel = new JPanel();
        instructionPanel.add(instructionLabel);
        instructionPanel.setLayout(new GridBagLayout());
        instructionPanel.setMaximumSize(new Dimension(350, 95));
        instructionPanel.setBackground(Color.DARK_GRAY);
        TitledBorder instructionBorder = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                "Current Instruction");
        instructionBorder.setTitleColor(Color.LIGHT_GRAY);
        instructionPanel.setBorder(instructionBorder);

        /**
         * Change style of registers label and value
         */
        JPanel pcPanel = new RegisterPanel("PC: ");
        pcValueLabel = new JLabel(cpu.getPc());
        pcValueLabel.setForeground(Color.LIGHT_GRAY);
        pcPanel.add(pcValueLabel);

        JPanel spPanel = new RegisterPanel("SP: ");
        spValueLabel = new JLabel(cpu.getSp());
        spValueLabel.setForeground(Color.LIGHT_GRAY);
        spPanel.add(spValueLabel);

        JPanel accPanel = new RegisterPanel("ACC: ");
        accValueLabel = new JLabel(cpu.getAcc());
        accValueLabel.setForeground(Color.LIGHT_GRAY);
        accPanel.add(accValueLabel);

        JPanel mopPanel = new RegisterPanel("MOP: ");
        mopValueLabel = new JLabel();
        mopValueLabel.setForeground(Color.LIGHT_GRAY);
        mopPanel.add(mopValueLabel);

        JPanel riPanel = new RegisterPanel("RI: ");
        riValueLabel = new JLabel(cpu.getRi());
        riValueLabel.setForeground(Color.LIGHT_GRAY);
        riPanel.add(riValueLabel);

        JPanel rePanel = new RegisterPanel("RE: ");
        reValueLabel = new JLabel(cpu.getRe());
        reValueLabel.setForeground(Color.LIGHT_GRAY);
        rePanel.add(reValueLabel);

        registersPanel = new JPanel();
        registersPanel.setLayout(new BoxLayout(registersPanel, BoxLayout.Y_AXIS));
        registersPanel.setMaximumSize(new Dimension(400, 200));
        TitledBorder registerBorder = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                "Registers");
        registerBorder.setTitleColor(Color.LIGHT_GRAY);
        registersPanel.setBorder(registerBorder);
        registersPanel.setBackground(Color.DARK_GRAY);
        registersPanel.add(pcPanel);
        registersPanel.add(spPanel);
        registersPanel.add(accPanel);
        registersPanel.add(mopPanel);
        registersPanel.add(riPanel);
        registersPanel.add(rePanel);

        /**
         * Create READ instruction dialog
         * and sends it to be used at CPU class.
         */
        JOptionPane inputDialog = new JOptionPane();
        inputDialog.setSize(new Dimension(100, 100));
        inputDialog.setVisible(true);
        cpu.setUserInput(inputDialog);

        /**
         * Start console area and send it to the CPU
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
        consoleAreaScrollPane.setMaximumSize(new Dimension(350, 300));
        consoleAreaScrollPane.setBackground(Color.DARK_GRAY);
        consoleAreaScrollPane.setBorder(BorderFactory.createEmptyBorder());
        consoleAreaScrollPane.getVerticalScrollBar().setBackground(Color.DARK_GRAY);
        consoleAreaScrollPane.getVerticalScrollBar().setUI(new CustomScroll());
        TitledBorder consoleBorder = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                "Output");
        consoleBorder.setTitleColor(Color.LIGHT_GRAY);
        consoleAreaScrollPane.setBorder(consoleBorder);

        /**
         * Start user buttons and its listeners
         */
        runButton = new CustomButton("Run", 30, 30);
        runButton.addActionListener(e -> programTranslation());

        cleanButton = new CustomButton("Clean", 25, 25);
        cleanButton.addActionListener(e -> cleanButtonListener());

        stepButton = new CustomButton("Step", 29, 29);
        stepButton.addActionListener(e -> {
            if (!steps.isEmpty() && !steps.contains(false))
                step();
            else
                programTranslation();
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
        memoryScrollPane.getVerticalScrollBar().setUI(new CustomScroll());

        JPanel panel = new JPanel();
        panel.setBackground(Color.DARK_GRAY);
        memoryScrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, panel);
        memoryScrollPane.setMaximumSize(new Dimension(200, 500));
        memoryScrollPane.setBackground(Color.DARK_GRAY);
        TitledBorder memoryBorder = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
                "Memory");
        memoryBorder.setTitleColor(Color.LIGHT_GRAY);
        memoryScrollPane.setBorder(memoryBorder);

        /**
         * Start helpButton button and define a default message at Util class
         */
        helpButton = new JButton();
        helpButton.setFocusPainted(false);
        helpButton.setBorderPainted(false);
        helpButton.setBorder(null);
        helpButton.setMargin(new Insets(0, 0, 0, 0));
        helpButton.setContentAreaFilled(false);
        Image img = null;
        try {
            img = ImageIO.read(getClass().getResource("icons/information.png"));
            helpButton.setIcon(new ImageIcon(img));
        } catch (IOException e) {
            e.printStackTrace();
        }
        helpButton.addActionListener(e -> consoleArea.setText(Util.getHelp()));

        /**
         * Start run op mode button and define its listener
         */
        continuousOp = new JRadioButton("Run");
        continuousOp.setMaximumSize(new Dimension(100, 20));
        continuousOp.setForeground(Color.DARK_GRAY);
        continuousOp.setFocusPainted(false);
        continuousOp.setBorderPainted(false);
        continuousOp.setSelected(true);
        continuousOp.addActionListener(e -> opModeListener());

        /**
         * Start debug op mode button and define its listener
         */
        debugOp = new JRadioButton("Debug");
        debugOp.setMaximumSize(new Dimension(100, 20));
        debugOp.setFocusPainted(false);
        debugOp.setBorderPainted(false);
        debugOp.setForeground(Color.DARK_GRAY);
        debugOp.addActionListener(e -> opModeListener());

        /**
         * Slider that changes running speed
         */
        speedSlider = new JSlider(1, 10, 1);
        speedSlider.setUI(new CustomSlider(speedSlider));
        speedSlider.addChangeListener(e -> changeSpeed());

        opModeListener();

        /**
         * Settings button
         */
        JPanel csp = new CustomSettingsPanel(continuousOp, debugOp, speedSlider);
        settingsPanel = new JOptionPane();
        settingsButton = new JButton();
        settingsButton.setFocusPainted(false);
        settingsButton.setBorderPainted(false);
        settingsButton.setBorder(null);
        settingsButton.setMargin(new Insets(0, 0, 0, 0));
        settingsButton.setContentAreaFilled(false);
        Image img2 = null;
        try {
            img2 = ImageIO.read(getClass().getResource("icons/wrench.png"));
            settingsButton.setIcon(new ImageIcon(img2));
        } catch (IOException e) {
            e.printStackTrace();
        }
        settingsButton.addActionListener(e ->
                settingsPanel.showOptionDialog(this, csp, "Settings",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                        new Object[]{}, null));

        /**
         * Define timer initial delay and its listener
         */
        timer = new Timer(1100 - speedSlider.getValue() * 100, e -> step());

        /**
         * Initiate GUI
         */
        initComponents();

    }

    /**
     * Start interface's main components.
     */
    private void initComponents() {

        getContentPane().setBackground(Color.DARK_GRAY);
        getContentPane().setLayout(new CustomLayout(getContentPane(),
                inputsPanel, instructionPanel, consoleAreaScrollPane,
                registersPanel, runButton, stepButton, cleanButton,
                memoryScrollPane, settingsButton, helpButton));
        getContentPane().setPreferredSize(new Dimension(800, 500));

        Image img = null;
        try {
            img = ImageIO.read(getClass().getResource("icons/binary-code.png"));
            setIconImage(img);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setTitle("Calingaert Computer");
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();

    }

    /**
     * Execute one instruction at a time.
     * In case of exception the error message is shown in the output.
     */
    private void step() {

        try {
            instructionLabel.setText(Util.getCurrentInstruction(
                    cpu.getPc(),
                    mem.getMem()));
            cpu.execute();
        } catch (Exception e) {
            consoleArea.append("\n" + e.getMessage() + "\n");
            instructionLabel.setText(Util.getCurrentInstruction(
                    cpu.getPc(),
                    mem.getMem()));
            refresh();
            return;
        }

        updateGUI();

    }

    /**
     * Take care of every program step before memory execution
     */
    private void programTranslation() {

        if (isInitialProcess)
            if (!initializeProcess()) return;

        boolean hasTwoSegments = files.size() == 2;
        int stepLocation = 0;
        ArrayList<String> auxFiles = new ArrayList<>();
        if (!steps.get(0) || !steps.get(1)) auxFiles.add(files.get(0));
        if (hasTwoSegments && (!steps.get(2) || !steps.get(3))) auxFiles.add(files.get(1));

        try {

            for (String file : auxFiles) {

                // MacroProcessor
                stepLocation = (hasTwoSegments && auxFiles.size() == 1) ? 2 : 0;
                if (isValidStep(stepLocation)) {
                    mp.execute(file);
                    if (!nextStep("MacroProcessor: MASMAPRG.asm generated.", stepLocation)) return;
                }

                // Assembler
                stepLocation = (hasTwoSegments && auxFiles.size() == 1) ? 3 : 1;
                if (isValidStep(stepLocation)) {
                    System.out.println(file);
                    assembler.execute(file);
                    if (!nextStep("Assembler: " + file.split("\\.")[0] + ".lst and " +
                            file.split("\\.")[0] + ".obj generated.", stepLocation)) return;
                }


            }

            // Linker
            stepLocation = hasTwoSegments ? 4 : 2;
            if (isValidStep(stepLocation)) {
                if (hasTwoSegments)
                    linker.execute(files.get(0), files.get(1));
                else
                    linker.execute(files.get(0), null);
                if (!nextStep("Linker: " + files.get(0).split("\\.")[0] +
                        ".hpx generated.", stepLocation)) return;
            }

            // Loader
            stepLocation = hasTwoSegments ? 5 : 3;
            if (isValidStep(stepLocation)) {
                loader.execute(files.get(0).split("\\.")[0] + ".hpx");
                updateGUI();
                cpu.init();
                if (!nextStep("Loader: source code loaded in memory.", stepLocation)) return;
            }

            if (continuousOp.isSelected())
                timer.start();

        } catch (Exception e) {
            consoleArea.append("\n" + e.getMessage() + "\n");
            refresh();
        }

    }

    /**
     * Check for errors and define GUI running state
     */
    private boolean initializeProcess() {

        instructionLabel.setText("");
        consoleArea.setText("");

        String[] firstFileExtension = firstFileField.getText().split("\\.");
        String[] secondFileExtension = secondFileField.getText().split("\\.");

        if (firstFileExtension.length > 2 || secondFileExtension.length > 2) {
            consoleArea.append("\nWrong file format.\n");
            return false;
        }

        if (firstFileExtension.length == 1) {
            consoleArea.append("\nCan't run! FILE #1 field is empty.\n");
            return false;
        }

        if (firstFileExtension.length == 2 && !firstFileExtension[1].equals("asm")) {
            consoleArea.append("\nWrong extension on FILE #1. Only .asm allowed.\n");
            return false;
        }

        if (secondFileExtension.length == 2 && !secondFileExtension[1].equals("asm")) {
            consoleArea.append("\nWrong extension on FILE #2. Only .asm allowed.\n");
            return false;
        }

        cleanButton.setEnabled(false);
        setOperationState(false, false);
        firstFileField.setEditable(false);
        secondFileField.setEditable(false);

        if (debugOp.isSelected()) speedSlider.setEnabled(false);

        if (continuousOp.isSelected()) runButton.setEnabled(false);

        files.add(firstFileField.getText());
        steps.addAll(Arrays.asList(false, false, false, false));
        if (!secondFileField.getText().equals("")) {
            files.add(secondFileField.getText());
            steps.addAll(Arrays.asList(false, false));
        }

        isInitialProcess = false;

        return true;
    }

    /**
     * Allow to perform a next step
     */
    private boolean nextStep(String text, int stepLocation) {
        consoleArea.append("\n" + text + "\n");
        if (debugOp.isSelected()) {
            steps.set(stepLocation, true);
            return false;
        }
        return true;
    }

    /**
     * Check whether is a valid next step or not
     */
    private boolean isValidStep(int stepLocation) {
        return continuousOp.isSelected() || steps.get(stepLocation) == false;
    }

    /**
     * Enable/Disable operation mode options.
     */
    private void setOperationState(boolean c, boolean d) {

        continuousOp.setEnabled(c);
        debugOp.setEnabled(d);

    }

    /**
     * Update memory list new values.
     * Update registers new values.
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
     * Update GUI components after user action.
     * Restart Memory, CPU and Loader.
     */
    private void refresh() {

        cleanButton.setEnabled(true);
        setOperationState(true, true);
        opModeListener();

        if (continuousOp.isSelected()) timer.stop();

        firstFileField.setEditable(true);
        secondFileField.setEditable(true);

        steps.clear();
        files.clear();
        isInitialProcess = true;

        mem.init();
        cpu.init();
        loader.setPosition();
        updateGUI();

    }

    /**
     * Run when clean button is pressed.
     */
    private void cleanButtonListener() {

        firstFileField.setText("");
        secondFileField.setText("");
        instructionLabel.setText("");
        consoleArea.setText("");
        refresh();

    }

    /**
     * Change state of operation mode.
     */
    private void opModeListener() {

        if (continuousOp.isSelected()) {

            mopValueLabel.setText("00000000");
            runButton.setEnabled(true);
            stepButton.setEnabled(false);
            speedSlider.setEnabled(true);

        } else {

            mopValueLabel.setText("00000001");
            runButton.setEnabled(false);
            stepButton.setEnabled(true);
            speedSlider.setEnabled(false);

        }

    }

    /**
     * Update new execution speed selected by user.
     */
    private ChangeListener changeSpeed() {

        timer.setDelay(1100 - speedSlider.getValue() * 100);
        return null;

    }

    /**
     * Get current values in the memory.
     */
    private void updateMemoryList() {

        String[] valuesList = mem.getMem().toArray(new String[mem.getMem().size()]);
        for (int i=0; i<mem.getMem().size(); i++) {
            memoryList[i][0] = Integer.toString(i);
            memoryList[i][1] = valuesList[i];
        }

    }

}
