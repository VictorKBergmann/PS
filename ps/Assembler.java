package ps;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Assembler {
    private ArrayList<String> lines;
    private String line;
    private HashMap<String, Integer> symbolTable;
    private Map<String, String> oppcodeTable;
    private String[] aux;

    private int locationCounter;

    public Assembler(String adress){
        symbolTable = new HashMap<>();
        oppcodeTable = new HashMap<>();
        lines = new ArrayList<>();

        locationCounter = 0;

        oppcodeTable = mapOppcode(oppcodeTable);

        readFile(adress);


    }

    private void readFile(String adress){
        try{
            BufferedReader lerArq;

            lerArq = new BufferedReader( new FileReader(adress));
            line = lerArq.readLine();
            while(line != null) {
                lines.add(line);
                line = lerArq.readLine();
            }
            lerArq.close();
        }
        catch(IOException e) {
            System.out.println("error on reading the file!");
        }
    }


    private void firstStep(String adress){
        String oppCode;

        for (String line: lines) {


            aux = line.split(" ");
            oppCode = oppcodeTable.get(getOperation(aux));// get oppCode


            if(!getLabel(aux).equals(null)) {
                symbolTable.put(getLabel(aux), locationCounter);
            }

            if(getOperation(aux).equals("end")){return;}

            if(!oppCode.equals(null)){
                locationCounter += getOperands(aux).size() + 1;

            }
            else if(true){/* verify psedo map TODO*/}

            else{throw new IllegalArgumentException("instruction not found");}
        }

    }

    private void secondStep(){
        ArrayList<String> byLine = new ArrayList<>();
        String oppcode, pointer = new String(), opp = new String();
        ArrayList<String> operands;

        for (String line: lines) {
            operands = getOperands(aux);

            aux = line.split(" ");


            pointer = adress(operands);
            pointer.concat(oppcodeTable.get(getOperation(aux)));// get oppCode);

            for (String operand: operands) {

                if (operand.charAt(0) == '#' || operand.charAt(0) == 'I') {// if has pointer
                    opp.concat(operand.substring(1));
                }
                else if (isNumeric(operand)) { // if dont have pointer
                    opp.concat(operand);
                }
                else { // if is label
                    opp.concat(Integer.toString(symbolTable.get(operand), 2));
                }
            }

            pointer.concat(opp);
            byLine.add(pointer);
        }
        generateObjectFile(byLine);
    }

    private void generateObjectFile(ArrayList<String> lins){
        //genarate file TODO
    }


    String adress(ArrayList<String> operands){
        if(operands.size() == 1){return "";}//TODO
        else if(operands.size() == 2){return "";}//TODO
        else return "000";
    }
    // # = imediato
    // I = indireto
    //   = direto

    private String getLabel(String[] line) {
        if(!line[0].equals("")){
            return line[0];
        }
        return null;
    }

    private String getOperation(String[] line) {
        return line[1];
    }

    private ArrayList<String> getOperands(String[] line) {
        ArrayList<String> takeOperands = new ArrayList<>();
        for(int i = 2; i < line.length; i++) {
            takeOperands.add(line[i]);
        }
        return takeOperands;
    }


    private Map<String, String> mapOppcode(Map<String, String> pot) {
        pot.put("br", "0000");
        pot.put("brpos", "0001");
        pot.put("add", "0010");
        pot.put("load", "0011");
        pot.put("brzero", "0100");
        pot.put("brneg", "0101");
        pot.put("sub", "0110");
        pot.put("store", "0111");
        pot.put("write", "1000");
        pot.put("ret", "1001");
        pot.put("divide", "1010");
        pot.put("stop", "1011");
        pot.put("read", "1100");
        pot.put("copy", "1101");
        pot.put("mult", "1110");
        pot.put("call", "1111");
        return pot;
    }


    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }


}
