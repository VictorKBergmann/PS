package ps;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Assembler {
    private ArrayList<String> lines;
    private String line;
    private HashMap<String, Integer> symbolTable;
    private Map<String, String[]> oppcodeTable;
    private String[] aux;
    private int locationCounter;

    public Assembler(String adress){
        symbolTable = new HashMap<>();
        oppcodeTable = new HashMap<>();
        lines = new ArrayList<>();
        locationCounter = 0;
        oppcodeTable = mapOppcode();
        readFile(adress);
        firstStep();
        secondStep(adress);
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
            aux = line.split("\\s+"); //remove all spaces
            oppCode = getOperation(aux); // get oppCode
            if(oppcodeTable.containsKey(oppCode)){
                if(!oppcodeTable.get(oppCode)[1].equals(Integer.toString(getOperands(aux).size()))) {
                    throw new IllegalArgumentException("Syntax error");
                }
                if(getLabel(aux) != null) {
                    labelValidator(getLabel(aux));
                    if(symbolTable.containsKey(getLabel(aux))){
                        throw new IllegalArgumentException("Duplicated label");
                    }
                    symbolTable.put(getLabel(aux), locationCounter);
                }
                locationCounter += getOperands(aux).size() + 1;
            }
            else if(oppCode.equals("end")){
                return;
            }
            else if(oppCode.equals("space") || oppCode.equals("const")){
                labelValidator(getLabel(aux));
                if(symbolTable.containsKey(getLabel(aux))){
                    throw new IllegalArgumentException("Duplicated label");
                }
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
        String pointer;
        String operation;
        ArrayList<String> operands;
        locationCounter = 0;
        
        for (String line: lines) {
            aux = line.split("\\s+");
            operation = getOperation(aux);
            operands = getOperands(aux);
            switch (operation) {
                case "const":
                    pointer = toString((short)locationCounter);
                    pointer = pointer.concat(toString(Short.parseShort(operands.get(0))));
                    locationCounter++;
                    break;
                case "space":
                    pointer = toString((short)locationCounter);
                    pointer = pointer.concat(toString((short)0));
                    locationCounter++;
                    break;
                case "end":
                    generateObjectFile(byLine, adress);
                    return;
                default:
                    pointer = toString((short)locationCounter);
                    pointer = pointer.concat(fill);
                    pointer = pointer.concat(getAdress(operands));
                    pointer = pointer.concat(oppcodeTable.get(getOperation(aux))[0]);
                    for (String operand: operands) {
                        
                        if (operand.startsWith("#")) {
                            operand = operand.substring(1);
                        }
                        if(operand.endsWith("I")) {
                            operand = operand.substring(0, operand.length() - 1);
                        }
                        if (!isNumeric(operand)) {
                            if(symbolTable.get(operand) == null){
                                throw new IllegalArgumentException("Label not defined"); 
                            }
                            operand = Integer.toString(symbolTable.get(operand));
                        }
                        operand = toString(Short.parseShort(operand));
                        pointer = pointer.concat(operand);
                    }
                    locationCounter += getOperands(aux).size() + 1;
                    break;
            }
            byLine.add(pointer);
        }       
    }
    
    private boolean labelValidator(String label) {
        if(label.matches("[A-Za-z0-9]+") && Character.isLetter(label.charAt(0)) && label.length() <= 8){
            return true;
        }
        throw new IllegalArgumentException("Syntax error");
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


    private Map<String,String[]> mapOppcode() {
        oppcodeTable.put("br", new String[]{"0000", "1"});
        oppcodeTable.put("brpos", new String[]{"0001", "1"});
        oppcodeTable.put("add", new String[]{"0010", "1"});
        oppcodeTable.put("load", new String[]{"0011", "1"});
        oppcodeTable.put("brzero", new String[]{"0100", "1"});
        oppcodeTable.put("brneg", new String[]{"0101", "1"});
        oppcodeTable.put("sub", new String[]{"0110", "1"});
        oppcodeTable.put("store", new String[]{"0111", "1"});
        oppcodeTable.put("write", new String[]{"1000", "1"});
        oppcodeTable.put("ret", new String[]{"1001", "1"});
        oppcodeTable.put("divide", new String[]{"1010", "1"});
        oppcodeTable.put("stop", new String[]{"1011", "0"});
        oppcodeTable.put("read", new String[]{"1100", "1"});
        oppcodeTable.put("copy", new String[]{"1101", "2"});
        oppcodeTable.put("mult", new String[]{"1110", "1"});
        oppcodeTable.put("call", new String[]{"1111", "1"});
        return oppcodeTable;
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
