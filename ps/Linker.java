package ps;

import java.io.*;
import java.util.*;

public class Linker {

    private String[] modules;
    private ArrayList<Integer> modulesSize;
    private HashMap<String, ArrayList<String>> modulesInstructions;
    private ArrayList<Integer> instructionsLength;
    private HashMap<String, HashMap<String, ArrayList<Integer>>> modulesUsageTable;
    private HashMap<String, HashMap<String, Integer>> modulesDefinitionTable;
    private ArrayList<String> relativeOrAbsolute;

    private HashMap<String, Integer> linkageSymbolTable;
    private ArrayList<String> unifiedInstructions;
    private Integer stackSize;

    public Linker(String firstModule, String secondModule) {

        if (secondModule == null)
            modules = new String[]{firstModule};
        else
            modules = new String[]{firstModule, secondModule};
        modulesSize = new ArrayList<>();
        modulesInstructions = new HashMap<>();
        instructionsLength = new ArrayList<>();
        modulesUsageTable = new HashMap<>();
        modulesDefinitionTable = new HashMap<>();
        relativeOrAbsolute = new ArrayList<>();

        linkageSymbolTable = new HashMap<>();
        unifiedInstructions = new ArrayList<>();

        readFiles();

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

                file = new BufferedReader(new FileReader("./" + moduleName + ".obj"));

                //stack size
                line = file.readLine();
                stackSize = Integer.parseInt(line);

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
                modulesSize.add(Integer.valueOf(line));

                //>
                file.readLine();

                // relative or absolute
                line = file.readLine();
                for (String bit : line.split(""))
                    relativeOrAbsolute.add(bit);

                //>
                file.readLine();

                //definition table
                line = file.readLine();
                while (!line.equals(">")) {

                    table = line.split(" ");
                    //modulesDefinitionTable.get(moduleName).computeIfAbsent(table[0], k -> new Integer());
                    modulesDefinitionTable.get(moduleName).put(table[0], Integer.parseInt(table[1]));

                    line = file.readLine();
                }

                //usage table
                line = file.readLine();
                while (line != null) {

                    table = line.split(" ");
                    modulesUsageTable.get(moduleName).computeIfAbsent(table[0], k -> new ArrayList<>());
                    modulesUsageTable.get(moduleName).get(table[0]).add(Integer.parseInt(table[1]));

                    line = file.readLine();
                }

            } catch (FileNotFoundException e) {
                System.out.println("file not found!");;
            } catch (IOException e) {
                System.out.println("error reading the file!");
            }

        }

    }

    /**
     * Linker's first pass
     * Create global table (unified definition tables) and update needed references
     * Update usage table
     */
    private void createLinkageSymbolTable() {

        int firstModuleSize = modulesSize.get(0);
        ArrayList<Integer> arr = new ArrayList<>();

        // Segment 1
        for (Map.Entry<String, Integer> entry : modulesDefinitionTable.get(modules[0]).entrySet())
            linkageSymbolTable.put(entry.getKey(), entry.getValue());

        // Segment 2
        if (modules.length != 1) {

            // Object Code

            // Usage Table
            for (Map.Entry<String, ArrayList<Integer>> entry : modulesUsageTable.get(modules[1]).entrySet()) {
                entry.getValue().forEach((x) -> arr.add(x + firstModuleSize));
                modulesUsageTable.get(modules[1]).put(entry.getKey(), (ArrayList<Integer>) arr.clone());
                arr.clear();
            }

            // taking appropriate error action if any symbol has more than one definition. (IMPLEMENT)
            // the length of the first segment is added to the address of each relative symbol entered from the second definition table.
                // how do i know if it's relative?
            // This ensures that its address is now relative to the origin of the first segment in the about-to-be-linked collection of segments.
            // the same adjustment must be made to relative addresses within the program and to location counter values of entries in the use table
            // Definition Table
            for (Map.Entry<String, Integer> entry : modulesDefinitionTable.get(modules[1]).entrySet()) {
                linkageSymbolTable.put(entry.getKey(), entry.getValue() + firstModuleSize);
            }

        }

        System.out.println(linkageSymbolTable);

    }

    /**
     * Linker's second pass
     * Unify program modules and update needed references
     */
    private void unifyModules() {
        //  Necessarily deferred to Pass 2 is the actual patching of external references.
        // The object code is copied unchanged, except for the updating just described (if it was indeed deferred),
        // until the field is reached whose address is given in the next entry of the use table for the segment being copied.
        // The symbol in that entry is looked up in the global symbol table, and its address therefrom added to the object code field.
        // The symbol PROG2 is looked up in the global symbol table and found to have address 31. This value is added (as directed by the use table sign field)
        // to the 00 in word 11 to yield the correct branch address. The relocation mode indicator of word 11 is also adjusted.
        //  The original address is absolute; the address added in is relative. The resulting sum is therefore relative.
        for (String module : modules)
            for (String instruction : modulesInstructions.get(module))
                unifiedInstructions.add(instruction);

        for (String module : modules)
            for (Map.Entry<String, ArrayList<Integer>> entry : modulesUsageTable.get(module).entrySet())
                for (Integer addr : entry.getValue())
                    unifiedInstructions.set(addr, bitsPadding(linkageSymbolTable.get(entry.getKey())));

        System.out.println(unifiedInstructions);

    }

    /**
     * Write executable file
     */
    private void writeFile() {

        File file = new File("./" + modules[0] + ".hpx");

        try {

            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file, false);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            // Stack Size
            printWriter.println(stackSize);

            printWriter.println(">");

            // Program size
            if (modules.length == 1)
                printWriter.println(modulesSize.get(0));
            else
                printWriter.println(modulesSize.get(0) + modulesSize.get(1));

            printWriter.println(">");

            // Relocation bits
            printWriter.println(String.join("", relativeOrAbsolute));

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
            ioException.printStackTrace();
        }

    }

    /**
     * Convert an integer to a 16-bit binary string
     */
    private String bitsPadding(Integer pc) {

        String temp2 = Integer.toString(pc,2);
        String temp1 = "";
        for (int i=16; i > temp2.length(); i--) {
            temp1 += "0";
        }
        return temp1.concat(temp2);

    }

}
