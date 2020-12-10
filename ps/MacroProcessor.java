package macro;

import java.io.*;
import java.util.ArrayList;

public class MacroProcessor {
    private InputStreamReader input;
    private String address;
    private String newAddress;

    private boolean expanding;
    private NameTab nameTab = new NameTab();
    private ArgTab argTab = new ArgTab();
    private ArrayList<String> defTab = new ArrayList<>();
    private ArrayList<String> finalArch = new ArrayList<>();
    private FormalParameterStack formalParameterStack = new FormalParameterStack();
    private int indexDefTab;
    private String line;

    public MacroProcessor(String address) {
        this.address = address;
    }

    public void MacroProcessor( ) {
        readFile(address);
        expanding = false;
        indexDefTab = 0;

        BufferedReader buffer = new BufferedReader(input);
        File newFile = new File(generateNewAddress("MASMAPRG.ASM"));
        newAddress = newFile.getAbsolutePath();

        try {
            newFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            line = getLine(buffer);

            while(!(line.equals("END"))){
                processLine(buffer);
                line = getLine(buffer);
            }
            finalArch.add(line);

            System.out.println("DefTab: ");
            for (int i = 0; i < defTab.size(); i++){
                System.out.println(defTab.get(i));
            }
            System.out.println("Arquivo Final: ");
            for (int i = 0; i < finalArch.size(); i++){
                System.out.println(finalArch.get(i));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void definitionMode(BufferedReader buffer){
        try {
            //line = buffer.readLine();
            line = getLine(buffer);
            nameTab.addName(getOppCode(line)); //entering macro NAME into nameTab
            nameTab.addStart(defTab.size()); //entering the start position of macro call in nameTab
            defTab.add(line); //entering macro prototype into definition table

            createParameters(line);
            int level = 1;
            while(level>0){

                line = getLine(buffer);//getting line
                defTab.add( replaceParameters(line) ); //entering line with positional notation into definition table

                if (line.equals("MACRO")) {
                    level++;
                }
                else if (line.equals("MEND")) {
                    --level;
                }
            }
            nameTab.addEnd(defTab.size()-1);
            formalParameterStack.popLastLevel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void expansionMode (BufferedReader buffer, String macroName) throws IOException {
        expanding = (true);

        indexDefTab = nameTab.getStart(nameTab.indexOfName(macroName));
        String macroPrototype = defTab.get(indexDefTab);

        createArguments(line);  //set up arguments from macro invocation in ArgTAB
        finalArch.add("*Comment: "+ macroPrototype); //write macro invocation to expanded file as comment
        line = getLine(buffer);
        int counter = 1;
        while(counter>0){
            //++indexDefTab;

            if(line.equals("MACRO")){
                counter= counter +1;
            }
            processLine(buffer);

            if(line.equals("MEND") ){
                --counter;
            }

            line = getLine(buffer);
        }
        argTab.clear();
        expanding = false;
    }

    public String getLine(BufferedReader buffer) throws IOException {
        if(expanding){
            ++indexDefTab;
            return replaceArguments(defTab.get(indexDefTab));
        }
        else{
            return buffer.readLine();
        }
    }
    public void processLine(BufferedReader buffer) throws IOException {
        String oppCode = getOppCode(line);

        if(nameTab.isInNameTab(oppCode)){
            expansionMode(buffer, oppCode);
        }
        else if(line.equals("MACRO")){
            definitionMode(buffer);
        }
        else if(line.equals("MEND") && expanding){
            getLine(buffer);
        }
        else{
            finalArch.add(line);
            // writeFile(newAddress, line);
        }
    }

    public void createArguments(String line){
        char[] arrayChar = line.toCharArray();
        StringBuilder sb = new StringBuilder();

        int a = 0;
        while(arrayChar[a] != ' ') {
            ++a;
        }
        for(a= a + 1 ; a < arrayChar.length; ++a){
            while(a< arrayChar.length && arrayChar[a] != ',' ){

                sb.append(arrayChar[a]);
                a++;

            }
            argTab.add(sb.toString(), -1, -1);
            System.out.println(sb.toString());
            sb.delete(0, sb.length());
        }

    }
    public String replaceArguments(String line){
        int temp = argTab.size();
        String a;
        for(int i = 0; i< temp; ++i){

            a= "#(1," + (i+1) + ")";
            while(line.contains(a)){
                line = line.replace(a, argTab.getName(i));
            }

        }

        return line;
    }
    public void createAr(String line){
        char[] arrayChar = line.toCharArray();
        StringBuilder sb = new StringBuilder();
        int position = 0;

        int a = 0;
        while(arrayChar[a] != ' ') {
            ++a;
        }
        for(a= a + 1 ; a < arrayChar.length; ++a){
            while(arrayChar[a] != ',' && arrayChar[a] != ' '){
                if(arrayChar[a] == '#'){
                    while(arrayChar[a-1] != ')') {
                        sb.append(arrayChar[a]);
                        a++;
                        if (a == arrayChar.length){
                            --a;
                            arrayChar[a-1] = ')';
                            arrayChar[a] = ' ';
                        }
                    }
                }
                else {
                    sb.append(arrayChar[a]);
                    ++a;
                    if (a >= arrayChar.length) {
                        --a;
                        arrayChar[a] = ',';
                    }
                }
            }
            position++;
            argTab.add(sb.toString(), 1, position);
            sb.delete(0, sb.length());
        }

    }

    public void createParameters(String line){

        char[] arrayChar = line.toCharArray();
        StringBuilder sb = new StringBuilder();
        int position = 0;
        int level = formalParameterStack.getLastLevel() + 1;
        for(int a = 0; a < arrayChar.length; ++a){

            if(arrayChar[a] == '&'){
                while(arrayChar[a] != ',' && arrayChar[a] != ' '){

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

            line = line.replaceAll(formalParameterStack.getName(count), "#(" + formalParameterStack.getDLevel(count)+ "," + formalParameterStack.getDPosition(count) +")");
        }
        return line;
    }

    public String getOppCode(String line){
        int j = 0;

        if(line.charAt(j) == '#'){

            while(line.charAt(j) != ' ' && j < line.length()){
                ++j;
            }
            ++j;
        }
        if(line.charAt(j) == '&'){

            while(line.charAt(j) != ' ' && j < line.length()){
                ++j;
            }
            ++j;
        }
        for(int i = j; i<line.length() ; i++){

            if(line.charAt(i) == ' '){
                return line.substring(j, i);
            }
        }
        return line;
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
    public void writeFile(String address, String line){
        try {
            PrintWriter archive = new PrintWriter(address);
            archive.println(line);
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
