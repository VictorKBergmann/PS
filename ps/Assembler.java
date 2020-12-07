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
        firstStep();
        secondStep(adress);;
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


    private void firstStep(){
        String oppCode;

        for (String line: lines) {
            aux = line.split("	");
            oppCode = getOperation(aux); // get oppCode
            if(oppcodeTable.containsKey(oppCode)){
                if(getLabel(aux) != null) {
                    symbolTable.put(getLabel(aux), locationCounter);
                }
                locationCounter += getOperands(aux).size() + 1;
            }
            else if(oppCode.equals("end")){
                return;
            }
            else if(oppCode.equals("space") || oppCode.equals("const")){
                symbolTable.put(getLabel(aux), locationCounter);
                locationCounter++;
            }
            
            //TODO
            
            else{
                throw new IllegalArgumentException("Operator not found");
            }
        }
        throw new IllegalArgumentException("Operator END not found");
    }

    private void secondStep(String adress){
        ArrayList<String> byLine = new ArrayList<>();
        String fill = "000000000";
        String opp = new String();
        String pointer = new String();
        String operation;
        ArrayList<String> operands;
        locationCounter = 0;
        
        for (String line: lines) {
            aux = line.split("	");
            operation = getOperation(aux);
            operands = getOperands(aux);
            switch (operation) {
                case "const":
                    pointer = toString((short)locationCounter);
                    pointer = pointer.concat(toString(Short.parseShort(operands.get(0))));
                    locationCounter++;
                    break;
                case "space":
                    locationCounter++;
                    break;
                case "end":
                    generateObjectFile(byLine, adress);
                    return;
                default:
                    pointer = toString((short)locationCounter);
                    pointer = pointer.concat(fill);
                    pointer = pointer.concat(getAdress(operands));
                    pointer = pointer.concat(oppcodeTable.get(getOperation(aux)));
                    for (String operand: operands) {
                        
                        if (operand.startsWith("#")) {
                            opp = operand.substring(1);
                        }
                        if(operand.endsWith("I")) {
                            opp = operand.substring(0, operand.length() - 1);
                        }
                        if (isNumeric(operand)) {
                            opp = operand;
                        }
                        else { // if is label
                            opp = Integer.toString(symbolTable.get(opp));
                        }
                        opp = toString(Short.parseShort(opp));
                        pointer = pointer.concat(opp);
                    }
                    locationCounter += getOperands(aux).size() + 1;
                    break;
            }
            byLine.add(pointer);
        }       
    }

    private void generateObjectFile(ArrayList<String> lins, String adress){
        File arq = new File(adress.split("[.]")[0].concat(".obj"));
        try{
            arq.createNewFile();
            FileWriter fileWriter = new FileWriter(arq, false);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            for(String lin : lins) {
                printWriter.println(lin);
            }
            printWriter.close();
        }
        catch(IOException e){
            System.out.println("error on creating file!"); 
        }
    }
    
    private void generateListingFile(String adress){
        //TODO
    }


    private String getAdress(ArrayList<String> operands){
        switch (operands.size()) {
            case 1:
                if(operands.get(0).startsWith("#")) {
                    return "100";
                }
                else if(operands.get(0).endsWith("I")) {
                    return "001";
                }
                else{
                    return "000";
                }
            case 2:
                if(operands.get(0).endsWith("I") && operands.get(1).endsWith("I")){
                    return "011";
                }
                else if(operands.get(0).endsWith("I") && operands.get(1).startsWith("#")){
                    return "101";
                }
                else if(operands.get(0).endsWith("I")){
                    return "001";
                }
                else if(operands.get(1).startsWith("#")){
                    return "100";
                }
                else if(operands.get(1).endsWith("I")){
                    return "010";
                }
                else{
                    return "000";
                }
            default:
                return "000";
        }
    }
    
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

    private String bitsPadding(short reg) {
        String temp2 = Integer.toString(reg,2);
        String temp1 = "";
        for (int i=16; i > temp2.length(); i--) {
            temp1 += "0";
        }
        return temp1.concat(temp2);
    }
    
    private String toString(short reg) {
        String bin;
        String res;
        if(reg < 0){
            bin = Integer.toBinaryString(reg);
            res = bin.substring(16, 32);
        }
        else{
            res = bitsPadding(reg);
        }
        return res;
    }
}
