package ps;

import java.io.*;
import java.util.*;

public class Linker {

    private String[] modules; /** Modules name */
    private ArrayList<Integer> modulesInitialAddress; /** Modules initial address passed by the assembler */
    private HashMap<String, ArrayList<String>> modulesInstructions; /** Instructions of each module */
    private ArrayList<Integer> instructionsLength; /** Size of each instruction line (used to write .exe file easier) */
    private HashMap<String, HashMap<String, ArrayList<Integer>>> modulesUsageTable; /** Usage table of each module */
    private HashMap<String, HashMap<String, Integer>> modulesDefinitionTable; /** Definition table of each module */
    private ArrayList<String> relocationList; /** Relocation list (1 - relative, 0 - absolute)  */
    private ArrayList<Integer> stackSize; /** Stack size of each module */

    private HashMap<String, Integer> linkageSymbolTable; /** or Global symbol table */
    private ArrayList<String> unifiedInstructions; /** Unified list of each module instructions */

    public Linker() {}

    /**
     * Initiate Linker process
     */
    public void execute(String firstModule, String secondModule) {

        if (secondModule == null)
            modules = new String[]{firstModule};
        else
            modules = new String[]{firstModule, secondModule};
        modulesInitialAddress = new ArrayList<>();
        modulesInstructions = new HashMap<>();
        instructionsLength = new ArrayList<>();
        modulesUsageTable = new HashMap<>();
        modulesDefinitionTable = new HashMap<>();
        relocationList = new ArrayList<>();
        stackSize = new ArrayList<>();

        linkageSymbolTable = new HashMap<>();
        unifiedInstructions = new ArrayList<>();

        readFiles();

        checkErrors();

        // First step
        createLinkageSymbolTable();

        //Second step
        unifyModules();

        writeFile();

    }

    /**
     * Read .obj files
     */
    private void readFiles() {

        BufferedReader file;
        String line;
        List<String> instruction;
        String[] table;

        for (String moduleName : modules) {

            modulesInstructions.computeIfAbsent(moduleName, k -> new ArrayList<>());
            modulesDefinitionTable.computeIfAbsent(moduleName, k -> new HashMap<>());
            modulesUsageTable.computeIfAbsent(moduleName, k -> new HashMap<>());

            try {

                file = new BufferedReader(new FileReader(moduleName.split("\\.")[0] + ".obj"));

                //stack size
                line = file.readLine();
                stackSize.add(Integer.parseInt(line));

                //>
                file.readLine();

                //instructions
                line = file.readLine();
                while (!line.equals(">")) {

                    instruction = Arrays.asList(line.split("(?<=\\G.{16})"));
                    instructionsLength.add(instruction.size());
                    for (String i : instruction)
                        modulesInstructions.get(moduleName).add(i);

                    line = file.readLine();
                }

                // module size
                line = file.readLine();
                modulesInitialAddress.add(Integer.valueOf(line));

                //>
                file.readLine();

                // relative or absolute
                line = file.readLine();
                relocationList.addAll(Arrays.asList(line.split("")));

                //>
                file.readLine();

                //definition table
                line = file.readLine();
                while (!line.equals(">")) {

                    table = line.split(" ");
                    modulesDefinitionTable.get(moduleName).put(table[0], Integer.parseInt(table[1]));

                    line = file.readLine();
                }

                //usage table
                line = file.readLine();
                while (line != null) {

                    table = line.split(" ");

                    if (!table[1].equals("null")) {
                        modulesUsageTable.get(moduleName).computeIfAbsent(table[0], k -> new ArrayList<>());
                        modulesUsageTable.get(moduleName).get(table[0]).add(Integer.parseInt(table[1]));
                    }

                    line = file.readLine();
                }

            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("Linker: file not found!");
            } catch (IOException e) {
                throw new IllegalArgumentException("Linker: error reading the file!");
            }

        }

    }

    /**
     * Check for definition errors
     */
    private void checkErrors() {

        if (modules.length == 2) {

            // START defined on segment 2
            if (modulesInitialAddress.get(1) > 0)
                throw new IllegalArgumentException("START instruction defined on Segment 2");

            // global symbol not defined
            for (String usedSymbol : modulesUsageTable.get(modules[0]).keySet())
                if (!modulesDefinitionTable.get(modules[1]).containsKey(usedSymbol))
                    throw new IllegalArgumentException("Global symbol not defined: "
                            + usedSymbol + "[" + modules[0] + "]");

            for (String usedSymbol : modulesUsageTable.get(modules[1]).keySet())
                if (!modulesDefinitionTable.get(modules[0]).containsKey(usedSymbol))
                    throw new IllegalArgumentException("Global symbol not defined: "
                            + usedSymbol + "[" + modules[1] + "]");

            // global symbol already defined
            for (String defSymbol : modulesDefinitionTable.get(modules[0]).keySet())
                if (modulesDefinitionTable.get(modules[1]).containsKey(defSymbol))
                    throw new IllegalArgumentException("Global symbol already defined: "
                            + defSymbol + "[" + modules[1] + "/" + modules[0] + "]");
        }

    }

    /**
     * Linker's first pass
     * Create global table (unified definition tables) and update needed references
     * Update usage table
     */
    private void createLinkageSymbolTable() {

        int relocationConstant = modulesInstructions.get(modules[0]).size();
        ArrayList<Integer> arr = new ArrayList<>();

        // Segment 1
        for (Map.Entry<String, Integer> entry : modulesDefinitionTable.get(modules[0]).entrySet())
            linkageSymbolTable.put(entry.getKey(), entry.getValue());

        // Segment 2
        if (modules.length != 1) {

            // Usage Table
            for (Map.Entry<String, ArrayList<Integer>> entry : modulesUsageTable.get(modules[1]).entrySet()) {
                entry.getValue().forEach((x) -> arr.add(x + relocationConstant));
                modulesUsageTable.get(modules[1]).put(entry.getKey(), (ArrayList<Integer>) arr.clone());
                arr.clear();
            }

            // Definition Table
            for (Map.Entry<String, Integer> entry : modulesDefinitionTable.get(modules[1]).entrySet()) {
                linkageSymbolTable.put(entry.getKey(), entry.getValue() + relocationConstant);
            }

        }

    }

    /**
     * Linker's second pass
     * Unify program modules and update needed references
     */
    private void unifyModules() {

        for (String module : modules)
            unifiedInstructions.addAll(modulesInstructions.get(module));

        ArrayList<Integer> relativePositions = new ArrayList<>();
        for (int i = modules[0].length(); i < relocationList.size(); i++)
            if (relocationList.get(i).equals("1"))
                relativePositions.add(i);

        int sum;

        for (String module : modules)
            for (Map.Entry<String, ArrayList<Integer>> entry : modulesUsageTable.get(module).entrySet())
                for (Integer addr : entry.getValue()) {
                    sum = Integer.parseInt(unifiedInstructions.get(addr), 2) +
                            linkageSymbolTable.get(entry.getKey());
                    unifiedInstructions.set(addr, bitsPadding(sum));

                    if (relocationList.get(addr).equals("0"))
                        relocationList.set(addr, "1");

                    if (relativePositions.contains(addr))
                        relativePositions.remove(addr);
                }
        System.out.println(relativePositions);
        int relocationConstant = modulesInstructions.get(modules[0]).size();
        for (int pos : relativePositions) {

            sum = Integer.parseInt(unifiedInstructions.get(pos), 2) +
                    relocationConstant;
            unifiedInstructions.set(pos, bitsPadding(sum));

        }


    }

    /**
     * Write executable file
     */
    private void writeFile() {

        File file = new File(modules[0].split("\\.")[0] + ".hpx");

        try {

            FileWriter fileWriter = new FileWriter(file, false);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            // Stack Size
            if (modules.length == 1)
                printWriter.println(stackSize.get(0));
            else
                printWriter.println(stackSize.get(0) + stackSize.get(1));

            printWriter.println(">");

            // Initial address
            printWriter.println(modulesInitialAddress.get(0));

            printWriter.println(">");

            // Relocation bits
            printWriter.println(String.join("", relocationList));

            printWriter.println(">");

            int idx = 0;
            for (Integer instrSize : instructionsLength) {
                for (int i = idx; i < instrSize + idx; i++)
                    printWriter.print(unifiedInstructions.get(i));
                printWriter.println("");
                idx += instrSize;
            }

            printWriter.close();


        } catch (IOException ioException) {
            throw new IllegalArgumentException("Problem creating file!");
        }

    }

    /**
     * Convert an integer to a 16-bit binary string
     */
    private String bitsPadding(Integer value) {

        String temp2 = Integer.toString(value,2);
        StringBuilder temp1 = new StringBuilder();
        temp1.append("0".repeat(Math.max(0, 16 - temp2.length())));
        return temp1.toString().concat(temp2);

    }

}
