package ps;

import java.io.*;
import java.util.ArrayList;

public class MacroProcessor {
    private InputStreamReader input;
    private String address;
    private String newAddress;

    private final NameTab nameTab;
    private final ArgTab argTab;
    private final ArrayList<String> defTab;
    private final FormalParameterStack formalParameterStack;
    private int expanding;
    private String line;
    private int indexDefTab;
    String oppCode;
    private int labelCount;
    private BufferedWriter arch;

    public MacroProcessor() {
        this.nameTab = new NameTab();
        this.argTab = new ArgTab();
        this.defTab = new ArrayList<>();
        this.formalParameterStack = new FormalParameterStack();
    }

    public void execute(String address) {
        this.address = address;
        readFile(address);
        expanding = 0;
        indexDefTab = 0;
        labelCount = 0;

        BufferedReader buffer = new BufferedReader(input);//buffer to read input file
        File finalFile = new File(generateNewAddress("MASMAPRG.asm")); //creating output file
        newAddress = finalFile.getAbsolutePath();

        try {
            finalFile.createNewFile();
            createBufferedWriter(finalFile);//buffer to write in output file

            line = getLine(buffer).replaceAll("\\s+", " ");

            while (!(line.equals("LF"))) {//processing lines from input file until end
                processLine(buffer);
                line = getLine(buffer);
            }
            arch.write(line);
            arch.close();

        } catch (IOException exception) {
            throw new IllegalArgumentException("Error while reading the file");
        }
        defTab.clear();
        nameTab.clear();

    }

    public boolean definitionMode(BufferedReader buffer) {
        try {
            //line = buffer.readLine();
            line = getLine(buffer);

            if (nameTab.isInNameTab(oppCode)) {     //in case of a macro redefinition
                deleteFromDefTab();
            }

            nameTab.addName(oppCode); //entering macro NAME into nameTab
            nameTab.addStart(defTab.size()); //entering the start position of macro call in nameTab

            defTab.add(line); //entering macro prototype into definition table

            createParameters(line);
            int level = 1;
            while (level > 0) {
                line = getLine(buffer);//getting line
                if (oppCode.equals("STOP")){//if macro isn't defined
                    return false;
                }

                if (line.charAt(0) != '*') {
                    defTab.add(replaceParameters(line)); //entering line with positional notation into definition table

                    if (oppCode.equals("MACRO")) {
                        level++;
                    } else if (oppCode.equals("MEND")) {
                        --level;
                    }
                }

            }
            nameTab.addEnd(defTab.size() - 1);//entering the last position of macro into nameTab
            formalParameterStack.popLastLevel();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void expansionMode(BufferedReader buffer, String macroName) throws IOException {
        ++expanding;
        ++labelCount;   // .SER count

        int tempIndexDefTab = indexDefTab;  // local variable in case of multiple expansions
        int tempLabelCount = labelCount;    // local variable in case of multiple expansions
        indexDefTab = nameTab.getStart(nameTab.indexOfName(macroName));
        String macroPrototype = defTab.get(indexDefTab); //getting prototype of macro from defTAB

        String label = createArgumentsWithOmission(line, macroPrototype);  //setting up arguments from macro invocation in ArgTAB

        arch.write("*Macro: "+ macroPrototype+ " Args: " + argTab.getArgsLastLevel());// writing in output file
        arch.newLine();

        line = getLine(buffer).replaceAll(".SER", ""+ tempLabelCount); //getting line already replacing .SER by label counter

        while (!oppCode.equals("MEND")) {

            if(label != null){      // case of a no parameter Label passed in the macro call
                if( !oppCode.equals("MACRO") ){ // jump a macro definition
                    if( !line.startsWith(" ") ){  
                        throw new IllegalArgumentException("Label conflict detected! First line from macro:" + macroPrototype + " -" + line + "- already has a label and the call received a non-paremeter label!");
                    }
                    else{   // the no parameter label will be putted in the first instruction of the macro, and if there is not a instruction, the label will be losted
                        line = line.replaceFirst(" ", label + " ");
                        label = null;
                    }
                }
            }
            processLine(buffer);
            line = getLine(buffer).replaceAll(".SER", ""+ tempLabelCount);  //getting line already replacing .SER by label counter

        }
        indexDefTab = tempIndexDefTab;
        argTab.popLastLevel();
        --expanding;
    }
    public String getLine(BufferedReader buffer) throws IOException {
        String s;
        if (expanding > 0) {// get next line from macro definition on defTAB
            ++indexDefTab;
            s = replaceArguments(defTab.get(indexDefTab)).replaceAll("\\s+", " ");
        }
        else {
            s = buffer.readLine().replaceAll("\\s+", " ");// read next line from archive
        }
        oppCode = getOppCode(s);
        return s;
    }

    public void processLine(BufferedReader buffer) throws IOException {
        //String oppCode = getOppCode(line);

        if (nameTab.isInNameTab(oppCode)) {     // case of a macro call
            expansionMode(buffer, oppCode);
        }
        else if (oppCode.equals("MACRO")) {     //case of a macro definition
            boolean flag = definitionMode(buffer);
            if(!flag)
                buffer.close();
        } else {
            arch.write(line);
            arch.newLine();
        }
    }

    public String createArgumentsWithOmission(String line, String macroPrototype) {//used in expansion mode

        String[] aux = macroPrototype.split("\\s+");
        int numParameters = (aux[2].split(",")).length;
        boolean labelFlag = false;
        String label = null;
        aux = line.split("\\s+");

        if(macroPrototype.startsWith("&")){//checking if macro prototype has a label
            labelFlag = true;
        }
        else if(line.charAt(0) != ' '){ // if macro prototype doesnt has a label, but the call has, its a no parameter label
            label = aux[0];
        }

        if (aux.length > 1) {//adding arguments in argTAB

            int lastParameterFlag = 0;
            if (aux[2].endsWith(",")) {     // in case of omission of the last parameter
                lastParameterFlag = 1;
            }
            aux = aux[2].split(",");

            if(aux.length + lastParameterFlag == numParameters){    // checking number of parameters in the call
                int i;
                for (i = 0; i < aux.length; ++i) {
                    argTab.add(aux[i]);
                }
                if (lastParameterFlag == 1) {   // case of omission of the last parameter
                    ++i;
                    argTab.add("");
                }
                if(labelFlag){    // the label parameter is the last parameter to be added in the argTab
                    argTab.add(line.substring(0, line.indexOf(' ')));
                    ++i;
                }
                argTab.addSizeLastLevel(i); // number of parameters (label parameter included)
            }
            else{
                throw new IllegalArgumentException("Macro call: " +line+ " -prototype: "+ macroPrototype+ " -Number of Arguments doesn't match!");
            }
        }
        return label;   // return the no parameter label or NULL
    }
    public void deleteFromDefTab() {
        int index = nameTab.indexOfName(oppCode);
        int size = nameTab.sizeOfName(index);

        int address = nameTab.getStart(index);
        for (int i = 0; i < size; ++i) {
            //  defTab[i]= defTab[i+size];
            defTab.remove(address);

        }
        nameTab.delete(index);
    }

    public void createArguments(String line) { 

        char[] arrayChar;

        int length = line.indexOf(" *");
        if (length != -1) {
            arrayChar = line.substring(0, length).toCharArray();
        } else {
            arrayChar = line.toCharArray();
        }

        StringBuilder sb = new StringBuilder();

        int a = 0;
        int size = 0;
        if (arrayChar[a] == '&') {
            while (arrayChar[a] != ' ' && arrayChar[a] != '\t')
                a++;

        }
        while (arrayChar[a] == ' ' || arrayChar[a] == '\t')
            a++;

        while (arrayChar[a] != ' ' && arrayChar[a] != '\t') {
            ++a;
        }
        for (a = a + 1; a < arrayChar.length; ++a) {
            while (a < arrayChar.length && (arrayChar[a] != ',' && arrayChar[a] != ' ' && arrayChar[a] != '\t')) {

                sb.append(arrayChar[a]);
                a++;

            }
            if (sb.toString() != "")
                ++size;
            argTab.add(sb.toString());

            sb.delete(0, sb.length());
        }
        argTab.addSizeLastLevel(size);

    }

    public String replaceArguments(String line) { //used in expansion mode
        int temp = argTab.getSizeLastLevel();
        String a;

        for (int i = 1; i < temp + 1; ++i) {

            a = "#(" + (i) + ")";
            while (line.contains(a)) {
                line = line.replace(a, argTab.getName(i - 1));  // getting the right argument from argTAB and replacing
            }

        }

        return line;
    }

    public void createParameters(String line) {

        char[] arrayChar = line.toCharArray();
        StringBuilder sb = new StringBuilder();
        int position = 0;
        int level = formalParameterStack.getLastLevel() + 1;
        int a;
        for (a = 1; a < arrayChar.length; ++a) {

            if (arrayChar[a] == '&') {
                while (arrayChar[a] != ',' && arrayChar[a] != ' ' && arrayChar[a] != '\t') {//adding all line parameters to sb

                    sb.append(arrayChar[a]);
                    ++a;
                    if (a >= arrayChar.length) {
                        --a;
                        arrayChar[a] = ',';
                    }
                }
                formalParameterStack.add(sb.toString(), level, ++position); //adding sb to stack
                sb.delete(0, sb.length()); // sb clear
            }
        }
        if(arrayChar[0]== '&'){
            formalParameterStack.add( (line.split(" ") )[0], level, ++position);
        }
    }

    public String replaceParameters(String line) {//used to adding line in defTAB with 

        for (int count = formalParameterStack.size() - 1; count >= 0; --count) { //getting line parameters and replacing for positions

            line = line.replaceAll(formalParameterStack.getName(count), "#(" + formalParameterStack.getDPosition(count) + ")");
        }
        return line;
    }

    public String getOppCode(String line) { // return the oppCode or "" if doesnt has one

        // [ [<label>] <opcode> [<operand1> [<operand2>]] ]  [<comentário>]
        String[] aux = line.split("\\s+");

        if (aux.length > 1) {
            return aux[1];
        } else {
            return "";
        }
    }

    public void readFile(String address) {
        try {
            FileInputStream file = new FileInputStream(address);
            input = new InputStreamReader(file); //""
        } catch (Exception error) {
            throw new IllegalArgumentException("Error on file reading " + address + " .");
        }
    }

    public String generateNewAddress(String newAddress) {//getting input archive patch
        int a;
        for (a = address.length(); a > 0; --a) {
            if (address.charAt(a - 1) == '/') {
                String temp;
                temp = address.substring(a);
                newAddress = address.replaceFirst(temp, newAddress);
                a = -1;
            }
        }
        return newAddress;
    }
    public void createBufferedWriter (File file){ //used to write in output archive
        try {
            FileWriter fw = new FileWriter(file);
            arch = new BufferedWriter(fw);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    public ArrayList<String> createList() {
        ArrayList<String> str = new ArrayList<>();
        String line;
        readFile(address);

        BufferedReader buffer = new BufferedReader(input);

        try {
            line = buffer.readLine();
            while (line != null) {
                str.add(line);
                line = buffer.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }
}
