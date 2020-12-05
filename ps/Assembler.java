package ps;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Assembler {
    private InputStreamReader input; //input's from interface
    private ArrayList<String> lines;
    private String line;
    private HashMap<String, Integer> symbolTable;
    private HashMap<String, String> oppcodeTable;
    //private HashMap<, String> pointerTable;

 //00000000000 000 0000000000
    public Assembler(){
        symbolTable = new HashMap<>();
        oppcodeTable = new HashMap<>();
        //pointerTable = new HashMap<>();
        lines = new ArrayList<>();
        //0000000000010 100
        oppcodeTable.put("add", "010");




    }

    private void readFile(String adress){
        try {
            FileInputStream file = new FileInputStream(adress);
            input = new InputStreamReader(file); //""
        }
        catch(Exception error){
            System.out.println("Error on file reading "+ adress + " .");
        }

        BufferedReader buffer = new BufferedReader(input);

        try {
            line = buffer.readLine();
            while (lines != null) {
                lines.add(line);
                line = buffer.readLine();

            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void map(){

    }

    private void firstStep(String adress){
        String[] aux;
        int count = 0, i ;
        int locationCounter = 0;
        int opperatorscount = 0;
        readFile(adress);
        String oppcode, pointer = new String(), opp = new String();
        ArrayList<String> byLine = new ArrayList<>();

        for (String line: lines) {


            aux = line.split(" ");
            label:

            if (oppcodeTable.get(aux[count]) == null) {// get label
                /*add on labels*/
                symbolTable.put(aux[count], locationCounter);
                locationCounter++; //TODO
                count++;
            }

            oppcode = oppcodeTable.get(aux[count]);// get oppCode
            count++;


            if (aux.length >= count) { // verify if have opp1
                count++;

                if (aux.length >= count) {// verify if have opp2
                    count++;
                    opperatorscount = 2;
                } else {
                    opperatorscount = 1;
                }
            }
            if(opperatorscount == 2){// get pointer for 2 operand
                pointer = adress(aux[aux.length - 1].charAt(0), aux[count - 2].charAt(0));
            }
            if(opperatorscount == 1){// get pointer for 1 operand
                pointer = adress(aux[aux.length - 1].charAt(0));
            }

            for(;opperatorscount!=0; opperatorscount--){ // get operand
                if(aux[aux.length - opperatorscount].charAt(0)== '#' || aux[aux.length - opperatorscount].charAt(0) == 'I') {// if has pointer
                    opp.concat(aux[aux.length - opperatorscount].substring(1));
                }
                else if(isNumeric(aux[aux.length - opperatorscount])){ // if dont have pointer
                    opp.concat(aux[aux.length - opperatorscount]);
                }
                else{ // if is label
                    opp.concat("????????????????");
                }

            }
            pointer.concat(oppcode);
            pointer.concat(opp);
            byLine.add(pointer);

        }


    }
    String adress(char a1,char a2){// TODO
        return "";
    }String adress(char a1){// TODO
        return "";
    }
    // # = imediato
    // I = indireto
    //   = direto

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }


}
