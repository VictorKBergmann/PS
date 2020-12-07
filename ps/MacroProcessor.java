package macro;

import java.io.*;
import java.util.ArrayList;

public class ProcessadorDeMacro {
    private InputStreamReader input;
    private String adress;
    private String newAdress;

    private ArrayList<Integer> startPos;
    private ArrayList<Integer> finalPos;
    private ArrayList<String> nameTab = new ArrayList<>();
    private ArrayList<String> defTab = new ArrayList<>();
    private ArrayList<String> finalArch = new ArrayList<>();





    //private ArrayList<ArgTab> argTab;

    public ProcessadorDeMacro(String adress) {
        this.adress = adress;
    }

    public void oneStepMacroProcessor( ) {
        readFile(adress);
        String line;

        BufferedReader buffer = new BufferedReader(input);
        File newFile = new File(generateNewAdress("MASMAPRG.ASM"));
        newAdress = newFile.getAbsolutePath();

        try {
            newFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            line = buffer.readLine();
            while(!(line.equals("END"))){

                processLine(line, buffer);
                line = buffer.readLine();
            }
            finalArch.add(line);
            for (int i = 0; i < finalArch.size(); i++){
                System.out.println(finalArch.get(i));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void processLine(String line, BufferedReader buffer){
        String oppCode = getOppCode(line);

        if(searchNameTab(oppCode)){
            //expand();
        }
        else if(line.equals("MACRO")){
            definitionMode(line, buffer);
        }
        else{
            finalArch.add(line);
           // writeFile(newAdress, line);
        }


    }

    public void definitionMode(String line, BufferedReader buffer ){
        try {
            line = buffer.readLine();
            nameTab.add(getOppCode(line)); //entering macro NAME into name table
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFile(String adress){
            try{
                FileInputStream file  = new FileInputStream(adress);
                input = new InputStreamReader(file); //""
            }
            catch(Exception error){
                System.out.println("Error on file reading "+ adress + " .");
            }
    }
    public void writeFile(String adress, String line){
        try {
            PrintWriter archive = new PrintWriter(adress);
            archive.println(line);
            archive.close();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

    }

    public String generateNewAdress(String newAdress){
        int a;
        for(a = adress.length(); a > 0; --a){
            if(adress.charAt(a-1) == '/'){
                String temp;
                temp = adress.substring(a);
                newAdress = adress.replaceFirst(temp, newAdress);
                a = -1;
            }
        }
        return newAdress;
    }
    public ArrayList<String> createList (){
        ArrayList<String> str = new ArrayList<>();
        String line;
        readFile(adress);

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

       /* for (int i = 0; i < str.size(); i++){
            System.out.println(str.get(i));
        }*/

        return str;
    }
    public String getOppCode(String line){
        for(int i = 0; i<line.length() ; i++){
            if(line.charAt(i) == ' '){
                return line.substring(0, i);
            }
        }
        return line;
    }

    public boolean searchNameTab(String macroName){
        for(int a =0; a<nameTab.size(); ++a){
            if(getOppCode(nameTab.get(a)).equals(macroName) ){
                return true;
            }
        }
        return false;
    }



}
