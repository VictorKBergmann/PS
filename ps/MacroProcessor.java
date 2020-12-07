package macro;

import java.io.*;
import java.util.ArrayList;

public class ProcessadorDeMacro {
    private InputStreamReader input;
    private String address;
    private String newAddress;

    private NameTab nameTab = new NameTab();
    private ArgTab argTab = new ArgTab();
    private ArrayList<String> defTab = new ArrayList<>();
    private ArrayList<String> finalArch = new ArrayList<>();


    public ProcessadorDeMacro(String address) {
        this.address = address;
    }

    public void oneStepMacroProcessor( ) {
        readFile(address);
        String line;
        boolean expanding = false;

        BufferedReader buffer = new BufferedReader(input);
        File newFile = new File(generateNewAddress("MASMAPRG.ASM"));
        newAddress = newFile.getAbsolutePath();

        try {
            newFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            line = getLine(expanding, buffer);
            while(!(line.equals("END"))){

                processLine(line, buffer, expanding);
                line = getLine(expanding, buffer);
            }
            finalArch.add(line);
            for (int i = 0; i < finalArch.size(); i++){
                System.out.println(finalArch.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getLine(boolean expanding, BufferedReader buffer) throws IOException {
        if(expanding == true){
            return "defTab";
        }
        else{
            return buffer.readLine();
        }
    }
    public void processLine(String line, BufferedReader buffer, boolean expanding){
        String oppCode = getOppCode(line);

        if(nameTab.isInNameTab(oppCode)){
            expansionMode(buffer, expanding);
        }
        else if(line.equals("MACRO")){
            definitionMode(line, buffer);
        }
        else{
            finalArch.add(line);
            // writeFile(newAddress, line);
        }
    }
    public void expansionMode (BufferedReader buffer, boolean expanding){
        expanding = true;
        int i = 0;
        //set up arguments from macro invocation in ARGTAB
        //write macro invocation to expanded file as comment
        for(;defTab.get(i).equals("MEND"); i++){
            //getLine();
            processLine(defTab.get(i), buffer, expanding );
        }
        expanding = false;
    }

    public void definitionMode(String line, BufferedReader buffer ){
        try {
            line = buffer.readLine();
            nameTab.addName(getOppCode(line)); //entering macro NAME into name table
            nameTab.addStart(defTab.size());
            defTab.add(line); //entering macro prototype into definition table
            //startPos.add(defTab.size()); //entering the start position of macro call in nametable
            int level = 1;

            while(level>0){
                line = buffer.readLine(); //geting line
                //TODO -> substituir notações pelos parametros
                defTab.add(line); //entering line into definition table
                if (line.equals("MACRO")) {
                    level++;
                }
                else if (line.equals("MEND")) {
                    --level;
                }
            }
            nameTab.addEnd(defTab.size()-1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getOppCode(String line){
        for(int i = 0; i<line.length() ; i++){
            if(line.charAt(i) == ' '){
                return line.substring(0, i);
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
