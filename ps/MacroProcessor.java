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

        BufferedReader buffer = new BufferedReader(input);
        File finalFile = new File(generateNewAddress("MASMAPRG.asm"));
        newAddress = finalFile.getAbsolutePath();

        try {
            finalFile.createNewFile();
            createBufferedWriter(finalFile);

            line = getLine(buffer).replaceAll("\\s+", " ");

            while (!(line.equals("LF"))) {
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

    public boolean definitionMode(BufferedReader buffer) {
        try {
            //line = buffer.readLine();
            line = getLine(buffer);

            if (nameTab.isInNameTab(oppCode)) {
                deleteFromDefTab();
            }

            nameTab.addName(oppCode); //entering macro NAME into nameTab
            nameTab.addStart(defTab.size()); //entering the start position of macro call in nameTab

            defTab.add(line); //entering macro prototype into definition table

            createParameters(line);
            int level = 1;
            while (level > 0) {
                line = getLine(buffer);//getting line
                if (oppCode.equals("STOP")){
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
            nameTab.addEnd(defTab.size() - 1);
            formalParameterStack.popLastLevel();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void expansionMode(BufferedReader buffer, String macroName) throws IOException {
        ++expanding;
        ++labelCount;

        int tempIndexDefTab = indexDefTab;
        int tempLabelCount = labelCount;
        indexDefTab = nameTab.getStart(nameTab.indexOfName(macroName));
        String macroPrototype = defTab.get(indexDefTab);

        createArgumentsWithOmission(line, macroPrototype);  //set up arguments from macro invocation in ArgTAB
        arch.write("*Macro: "+ macroPrototype+ " Args: " + argTab.getArgsLastLevel());
        arch.newLine();

        line = getLine(buffer).replaceAll(".SER", ""+ tempLabelCount);;
        while (!oppCode.equals("MEND")) {

            processLine(buffer);
            line = getLine(buffer).replaceAll(".SER", ""+ tempLabelCount);;

        }
        indexDefTab = tempIndexDefTab;
        argTab.popLastLevel();
        --expanding;
    }
    public String getLine(BufferedReader buffer) throws IOException {
        String s;
        if (expanding > 0) {
            ++indexDefTab;
            s = replaceArguments(defTab.get(indexDefTab)).replaceAll("\\s+", " ");
        }
        else {
            s = buffer.readLine().replaceAll("\\s+", " ");
        }
        oppCode = getOppCode(s);
        return s;
    }

    public void processLine(BufferedReader buffer) throws IOException {
        //String oppCode = getOppCode(line);

        if (nameTab.isInNameTab(oppCode)) {
            expansionMode(buffer, oppCode);
        } else if (oppCode.equals("MACRO")) {
            boolean flag = definitionMode(buffer);
            if(!flag)
                buffer.close();
        } else {
            arch.write(line);
            arch.newLine();
        }
    }

    public void createArgumentsWithOmission(String line, String macroPrototype) {

        String[] aux = macroPrototype.split("\\s+");
        int numParameters = (aux[2].split(",")).length;
        boolean labelFlag = false;

        if(aux[0].startsWith("&")){
            labelFlag = true;
        }
        aux = line.split("\\s+");
        if (aux.length > 1) {

            int lastParameterFlag = 0;
            if (aux[2].endsWith(",")) {
                lastParameterFlag = 1;
            }
            aux = aux[2].split(",");

            if(aux.length + lastParameterFlag == numParameters){
                int i;
                for (i = 0; i < aux.length; ++i) {
                    argTab.add(aux[i]);
                }
                if (lastParameterFlag == 1) {
                    ++i;
                    argTab.add("");
                }
                if(labelFlag==true){
                    argTab.add(line.substring(0, line.indexOf(' ')));
                    ++i;
                }
                argTab.addSizeLastLevel(i);
            }
            else{
                throw new IllegalArgumentException("Macro call: " +line+ " -prototype: "+ macroPrototype+ " -Number of Arguments doesn't match!");
            }
        }
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

    public String replaceArguments(String line) {
        int temp = argTab.getSizeLastLevel();
        String a;

        for (int i = 1; i < temp + 1; ++i) {

            a = "#(" + (i) + ")";
            while (line.contains(a)) {
                line = line.replace(a, argTab.getName(i - 1));
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
        for (a = 1; a < arrayChar.length; ++a) { // a = 1, para ignorar o label, já que n consideremos parâmetro...

            if (arrayChar[a] == '&') {
                while (arrayChar[a] != ',' && arrayChar[a] != ' ' && arrayChar[a] != '\t') {

                    sb.append(arrayChar[a]);
                    ++a;
                    if (a >= arrayChar.length) {
                        --a;
                        arrayChar[a] = ',';
                    }
                }
                //if sb = &lab else
                formalParameterStack.add(sb.toString(), level, ++position);
                sb.delete(0, sb.length());
            }
        }
        if(arrayChar[0]== '&'){
            formalParameterStack.add( (line.split(" ") )[0], level, ++position);
        }
    }

    public String replaceParameters(String line) {

        for (int count = formalParameterStack.size() - 1; count >= 0; --count) {

            line = line.replaceAll(formalParameterStack.getName(count), "#(" + formalParameterStack.getDPosition(count) + ")");
        }
        return line;
    }

    public String getOppCode(String line) {

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

    /*public void writeFile(String address, ArrayList<String> line) {
        try {
            PrintWriter archive = new PrintWriter(address);
            for (int i = 0; i < line.size(); i++) {
                archive.println(line.get(i));
            }
            archive.close();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

    }*/

    public String generateNewAddress(String newAddress) {
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
    public void createBufferedWriter (File file){
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
