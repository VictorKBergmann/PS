package macro;

import java.io.*;
import java.util.ArrayList;

public class MacroProcessor {
    private InputStreamReader input;
    private String address;
    private String newAddress;

    private NameTab nameTab = new NameTab();
    private ArgTab argTab = new ArgTab();
    private ArrayList<String> defTab = new ArrayList<>();
    private ArrayList<String> finalArch = new ArrayList<>();
    private FormalParameterStack formalParameterStack = new FormalParameterStack();
    private int expanding;
    private String line;
    private int indexDefTab =0;
    String oppCode;
    private int labelCount = 0;

    public MacroProcessor(String address) {
        this.address = address;
    }

    public void MacroProcessor( ) {
        readFile(address);
        expanding = 0;

        BufferedReader buffer = new BufferedReader(input);
        File newFile = new File(generateNewAddress("MASMAPRG.ASM"));
        newAddress = newFile.getAbsolutePath();

        try {
            newFile.createNewFile();

            line = getLine(buffer);

            while( !(oppCode.equals("END")) ){
                processLine(buffer);
                line = getLine(buffer);
            }
            finalArch.add(line);

        } catch (IOException e) {
            e.printStackTrace();
        }
        writeFile("MASMAPRG.ASM", finalArch);
    }

    public void deleteFromDefTab(){
        int index = nameTab.indexOfName(oppCode);
        int size = nameTab.sizeOfName(index);

        int address = nameTab.getStart(index);
        for(int i = 0; i< size; ++i){
            //  defTab[i]= defTab[i+size];
            defTab.remove(address);

        }
        nameTab.delete(index);

    }
    public void definitionMode(BufferedReader buffer){
        try {
            //line = buffer.readLine();
            line = getLine(buffer);

            if(nameTab.isInNameTab(oppCode)){
                deleteFromDefTab();
            }

            nameTab.addName(oppCode); //entering macro NAME into nameTab
            nameTab.addStart(defTab.size()); //entering the start position of macro call in nameTab

            defTab.add(line); //entering macro prototype into definition table

            createParameters(line);
            int level = 1;
            while(level>0){
                line = getLine(buffer);//getting line

                if( line.charAt(0) != '*' ) {
                    defTab.add( replaceParameters(line) ); //entering line with positional notation into definition table

                    if (oppCode.equals("MACRO")) {
                        level++;
                    }
                    else if (oppCode.equals("MEND")) {
                        --level;
                    }
                }

            }
            nameTab.addEnd(defTab.size()-1);
            formalParameterStack.popLastLevel();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void expansionMode (BufferedReader buffer, String macroName) throws IOException {
        ++expanding;

        int tempIndexDefTab = indexDefTab;
        indexDefTab = nameTab.getStart(nameTab.indexOfName(macroName));
        String macroPrototype = defTab.get(indexDefTab);

        createArguments(line);  //set up arguments from macro invocation in ArgTAB
        finalArch.add("*Comment: "+ macroPrototype + "  -Argumentos: " + argTab.getArgsLastLevel()); //write macro invocation to expanded file as comment

        line = getLine(buffer);
        while(!oppCode.equals("MEND")){

            processLine(buffer);
            line = getLine(buffer);

        }
        indexDefTab = tempIndexDefTab;
        argTab.popLastLevel();
        --expanding;
    }
    public String getLine(BufferedReader buffer) throws IOException {
        String s;
        if(expanding>0){
            ++indexDefTab;
            s = replaceArguments(defTab.get(indexDefTab));
        }
        else{
            s = buffer.readLine();
        }
        oppCode = getOppCode(s);
        return s;
    }
    public void processLine(BufferedReader buffer) throws IOException {
        //String oppCode = getOppCode(line);

        if(nameTab.isInNameTab(oppCode)){
            expansionMode(buffer, oppCode);
        }
        else if(oppCode.equals("MACRO")){
            definitionMode(buffer);
        }
        else{
            finalArch.add(line);
            // writeFile(newAddress, line);
        }
    }

    public void createArguments(String line){

        char[] arrayChar;

        int length = line.indexOf(" *");
        if(length != -1){
            arrayChar = line.substring(0, length).toCharArray();
        }
        else{
            arrayChar = line.toCharArray();
        }

        StringBuilder sb = new StringBuilder();

        int a = 0;
        int size = 0;
        if(arrayChar[a] == '&') {
            while (arrayChar[a] != ' ' && arrayChar[a] != '\t')
                a++;

        }
        while(arrayChar[a] == ' ' || arrayChar[a] == '\t')
            a++;

        while(arrayChar[a] != ' ' && arrayChar[a] != '\t') {
            ++a;
        }
        for(a= a + 1 ; a < arrayChar.length; ++a){
            while(a< arrayChar.length && (arrayChar[a] != ',' && arrayChar[a] != ' ' && arrayChar[a] != '\t')){

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
    public String replaceArguments(String line){
        int temp = argTab.getSizeLastLevel();
        String a;
        for(int i = 1; i< temp+1; ++i){

            a = "#("+ (i) + ")";
            while(line.contains(a)){
                line = line.replace(a, argTab.getName( i-1));
            }

        }

        return line;
    }
    public void createParameters(String line){

        char[] arrayChar = line.toCharArray();
        StringBuilder sb = new StringBuilder();
        int position = 0;
        int level = formalParameterStack.getLastLevel() + 1;
        for(int a = 1; a < arrayChar.length; ++a){ // a = 1, para ignorar o label, já que n consideremos parâmetro...

            if(arrayChar[a] == '&'){
                while(arrayChar[a] != ',' && arrayChar[a] != ' ' && arrayChar[a] != '\t'){

                    sb.append(arrayChar[a]);
                    ++a;
                    if(a >= arrayChar.length){
                        --a;
                        arrayChar[a] = ',';
                    }
                }
                //if sb = &lab else
                formalParameterStack.add(sb.toString(), level, ++position);
                sb.delete(0, sb.length());
            }

        }
        /*while(line!=null) {
            int firstIndex = line.indexOf("&");
            int lastIndex = line.indexOf(",");
            parameters.add(line.substring(firstIndex, lastIndex));
            line.substring(lastIndex);
        }
        */
    }
    public String replaceParameters(String line){

        for(int count = formalParameterStack.size()-1; count >= 0; --count){

            line = line.replaceAll(formalParameterStack.getName(count), "#(" + formalParameterStack.getDPosition(count) +")");
        }
        return line;
    }

    public String getOppCode(String line) {

        // [ [<label>] <opcode> [<operand1> [<operand2>]] ]  [<comentário>]
        String[] aux = line.split("\\s+");

        if( aux.length > 1){
            return aux[1];
        }
        else{
            return "";
        }
    }

    public void readFile(String address){
        try{
            FileInputStream file  = new FileInputStream(address);
            input = new InputStreamReader(file); //""
        }
        catch(Exception error){
            System.out.println("Error on file reading "+ address + " .");
        }
    }
    public void writeFile(String address, ArrayList<String> line){
        try {
            PrintWriter archive = new PrintWriter(address);
            for(int i = 0; i < line.size(); i++) {
                archive.println(line.get(i));
            }
            archive.close();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

    }
    public String generateNewAddress(String newAddress){
        int a;
        for(a = address.length(); a > 0; --a){
            if(address.charAt(a-1) == '/'){
                String temp;
                temp = address.substring(a);
                newAddress = address.replaceFirst(temp, newAddress);
                a = -1;
            }
        }
        return newAddress;
    }
    public ArrayList<String> createList (){
        ArrayList<String> str = new ArrayList<>();
        String line;
        readFile(address);

        BufferedReader buffer = new BufferedReader(input);

        try {
            line = buffer.readLine();
            while (line != null){
                str.add(line);
                line = buffer.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }
}
