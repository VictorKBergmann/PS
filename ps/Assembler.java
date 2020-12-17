package ps;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
    
public class Assembler {		
    private ArrayList<String> lines;
    private HashMap<String, Integer> symbolTable;
    private Map<String, String[]> oppcodeTable;
    private Map<String, ArrayList<Integer>> usageTable;
    private Map<String, Integer> definitionTable;
    private String[] aux;
    private int locationCounter;
    private String stack;
    private String mod;
    private PrintWriter printerLST;
    
    public Assembler() { 
    }
    
    public void execute(String adress) throws IllegalArgumentException {
        symbolTable = new HashMap<>();
        oppcodeTable = new HashMap<>();
        usageTable = new HashMap<>();
        definitionTable = new HashMap<>();
        lines = new ArrayList<>();
        mod = new String();
        locationCounter = 0;
        stack = "10";
        oppcodeTable = mapOppcode();
	printerLST = generateLstFile(adress);
        readFile();
        firstStep();
        secondStep(adress);
    }
    
    private void readFile(){
        try{
        String line;
        BufferedReader lerArq;

        lerArq = new BufferedReader( new FileReader("MASMAPRG.ASM"));
        line = lerArq.readLine();
        while(line != null && !line.equals("CR")) {
            line = lerArq.readLine();

        }
        if(line == null){
            printerLST.println("CR not found");
            printerLST.close();
            throw new IllegalArgumentException("CR not found");

        }
        line = lerArq.readLine();
        while(line != null && !line.equals("LF")) {
            if(line.length() > 80) {
                printerLST.println("Excessively long line");
                printerLST.close();
                throw new IllegalArgumentException("Excessively long line");
            }
            if(!line.startsWith("*")){
                String temp = line.split("\\*")[0];
                lines.add(temp);
            }
            line = lerArq.readLine();
        }
        if(line == null) {
            printerLST.println("CR not found");
            printerLST.close();
            throw new IllegalArgumentException("LF not found");
        }
        lerArq.close();
        }
        catch(IOException e) {
            System.out.println("error on reading the file!");
        }
    }


    private void firstStep(){
        String oppCode;
        int lineCounter = 0;
        for (String line: lines) {
            aux = line.split("\\s+"); //remove all spaces
            oppCode = getOperation(aux).toLowerCase(); // get oppCode
            if(oppcodeTable.containsKey(oppCode)){
                if(!oppcodeTable.get(oppCode)[1].equals(Integer.toString(getOperands(aux).size()))) {
                    printerLST.println("Incorrect operands number - line "+ lineCounter);
                    printerLST.close();
                    throw new IllegalArgumentException("Incorrect operands number");
                }
                if(getLabel(aux) != null) {
                    labelValidator(getLabel(aux), lineCounter);
                    if(symbolTable.containsKey(getLabel(aux))){
                        printerLST.println("Duplicated label - line "+ lineCounter);
                        printerLST.close();
                        throw new IllegalArgumentException("Duplicated label");
                    }
                    else if(definitionTable.containsKey(getLabel(aux))){
                        definitionTable.put(getLabel(aux), locationCounter);
                    }
                    else{
                        symbolTable.put(getLabel(aux), locationCounter);
                    }
                }
                locationCounter += getOperands(aux).size() + 1;
            }
            else if(oppCode.equals("end")){
                return;
            }
            else if(oppCode.equals("space") || oppCode.equals("const")){
                labelValidator(getLabel(aux), lineCounter);
                if(symbolTable.containsKey(getLabel(aux))){
                    printerLST.println("Duplicated label - line "+ lineCounter);
                    printerLST.close();
                    throw new IllegalArgumentException("Duplicated label");
                }
                else if(definitionTable.containsKey(getLabel(aux))){
                    definitionTable.put(getLabel(aux), locationCounter);
                }
                else{
                    symbolTable.put(getLabel(aux), locationCounter);    
                }
                locationCounter++;
            }
            else if(oppCode.equals("extdef")) {
                labelValidator(getOperands(aux).get(0), lineCounter);
                definitionTable.put(getOperands(aux).get(0), null);
            }
            else if(oppCode.equals("extr")) {
                labelValidator(getLabel(aux), lineCounter);
                usageTable.put(getLabel(aux), new ArrayList<>());
                usageTable.get(getLabel(aux)).add(null);
            }
            else if(oppCode.equals("start")) {

            }
            else if(oppCode.equals("stack")) {
                stack = getOperands(aux).get(0);
            }
            else{
                printerLST.println("Operator not found - line "+ lineCounter);
                printerLST.close();
                throw new IllegalArgumentException("Operator not found");
            }
            lineCounter++;
        }
        printerLST.println("Operator END not found");
        printerLST.close();
        throw new IllegalArgumentException("Operator END not found");
    }

    private void secondStep(String adress){
        ArrayList<String> byLine = new ArrayList<>();
        String pointer = new String();
        String help;
        String operation;
        ArrayList<String> operands;
        locationCounter = 0;
        String lst;
        int lineCounter = 0;
        int initPos = 0;

        for (String line: lines) {
            aux = line.split("\\s+");
            operation = getOperation(aux).toLowerCase();
            operands = getOperands(aux);
            help = "";
            for (String s: aux) {help = help.concat(s + " ");}
            switch (operation) {
                case "const":
                    pointer = toString(Short.parseShort(operands.get(0)));
                    mod = mod.concat("0");
                    lst = "[ " + Integer.toString(locationCounter)+ ", " + help + "] "+ Integer.toString(lineCounter) + " " + pointer;
                    printerLST.println(lst);
                    locationCounter++;
                    byLine.add(pointer);
                    break;
                case "space":
                    pointer = toString((short)0);
                    mod = mod.concat("0");
                    lst = "[ " + Integer.toString(locationCounter)+ ", " + help + "] "+ Integer.toString(lineCounter)+ " " + pointer;
                    printerLST.println(lst);
                    locationCounter++;
                    byLine.add(pointer);
                    break;
                case "end":
                    lst = "[ " + Integer.toString(locationCounter)+ ", " + help + "] "+ Integer.toString(lineCounter);
                    printerLST.println(lst);
                    printerLST.println("Assembler successful, CONGRATULATIONS");
                    printerLST.close();
                    locationCounter++;
                    generateObjectFile(byLine, adress, initPos);
                    return;
                case "extdef":
                case "stack":
                case "extr":
                    lst = "[ " + Integer.toString(locationCounter)+ ", " + help + "] "+ Integer.toString(lineCounter);
                    printerLST.println(lst);
                    break;
                case "start":
                    lst = "[ " + Integer.toString(locationCounter)+ ", " + help + "] "+ Integer.toString(lineCounter);
                    printerLST.println(lst);
                    initPos = Integer.parseInt(operands.get(0));
                    locationCounter = Integer.parseInt(operands.get(0));
                    break;
                default:
                    pointer = "000000000";
                    if(!Arrays.asList(oppcodeTable.get(operation)).contains(getAdress(operands))){
                        printerLST.println("Invalid adress mode - line "+ lineCounter);
                        printerLST.close();
                        throw new IllegalArgumentException("Invalid adress mode");
                    }
                    pointer = pointer.concat(getAdress(operands));
                    pointer = pointer.concat(oppcodeTable.get(getOperation(aux).toLowerCase())[0]);
                    mod = mod.concat("0");
                    for (String operand: operands) {
                        if(operand.startsWith("#") && operand.endsWith(",I")) {
                            printerLST.println("Syntax error - line "+ lineCounter);
                            printerLST.close();
                            throw new IllegalArgumentException("Syntax error");
                        }
                        if (operand.startsWith("#")) {
                            mod = mod.concat("0");
                            operand = operand.substring(1);
                            if(!isNumeric(operand)){
                                printerLST.println("Syntax error - line "+ lineCounter);
                                printerLST.close();
                                throw new IllegalArgumentException("Syntax error");
                            }
                            if(Integer.parseInt(operand) > 32767 ||Integer.parseInt(operand) < -32768){
                                printerLST.println("Over Flow - line "+ lineCounter);
                                printerLST.close();
                                throw new IllegalArgumentException("Over Flow");
                            }
                        }
                        if(operand.endsWith(",I")) {
                            mod = mod.concat("0");
                            operand = operand.substring(0, operand.length() - 1);                          
                        }
                        if (!isNumeric(operand)) {   // is a label
                            mod = mod.concat("1");
                            if(symbolTable.containsKey(operand)){
                                operand = Integer.toString(symbolTable.get(operand));
                            }
                            else if(definitionTable.containsKey(operand)){
                                operand = Integer.toString(definitionTable.get(operand));
                            }
                            else if(usageTable.containsKey(operand)){
                                if(usageTable.get(operand).contains(null)){
                                    usageTable.get(operand).set(0, locationCounter + 1);
                                }
                                else{
                                    usageTable.get(operand).add(locationCounter + 1);
                                }
                                operand = "0";
                            }
                            else{
                                printerLST.println("Label not defined - line "+ lineCounter);
                                printerLST.close();
                                throw new IllegalArgumentException("Label not defined");
                            }
                        }
                        operand = toString(Short.parseShort(operand));
                        pointer = pointer.concat(operand);
                    }
                    lst = "[ " + Integer.toString(locationCounter)+ ", " + help + "] "+ Integer.toString(lineCounter)+ " " + pointer;
                    printerLST.println(lst);
                    locationCounter += getOperands(aux).size() + 1;
                    byLine.add(pointer);
                    break;
            }
            lineCounter++;
        }

    }
    
    private void labelValidator(String label, int line) {
        if(label.startsWith("#")){
            label = label.substring(1);
        }
        if(label.endsWith(",I")){
            label = label.substring(0, label.length() - 1);
        }
        if(label.matches("[A-Za-z0-9]+") && Character.isLetter(label.charAt(0)) && label.length() <= 8){
            return;
        }
        printerLST.println("Syntax error - line " + line);
        printerLST.close();
        throw new IllegalArgumentException("Syntax error");

    }

    private void generateObjectFile(ArrayList<String> lins, String adress, int locationC){
        File arq = new File(adress.split("[.]")[0].concat(".obj"));
        try{
            arq.createNewFile();
            FileWriter fileWriter = new FileWriter(arq, false);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(stack);
            printWriter.println(">");
            for(String lin : lins) {
                printWriter.println(lin);
            }
            printWriter.println(">");
            printWriter.println(locationC);
            printWriter.println(">");
            printWriter.println(mod);
            printWriter.println(">");
            for(Map.Entry<String, Integer> pair: definitionTable.entrySet()){
                printWriter.println(pair.getKey() + " " + pair.getValue());
            }
            printWriter.println(">");
            for(Map.Entry<String, ArrayList<Integer>> pair: usageTable.entrySet()){
                for(Integer values: pair.getValue()){
                    printWriter.print(pair.getKey() + " " + values + "\n");
                }
            }


            printWriter.close();
        }
        catch(IOException e){
            System.out.println("error on creating file!"); 
        }
    }
    
    private PrintWriter generateLstFile(String adress){
        File arq = new File(adress.split("[.]")[0].concat(".lst"));
        try {
            arq.createNewFile();
            FileWriter fileWriter = new FileWriter(arq, false);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            return printWriter;
        }
        catch(IOException e){
            System.out.println("error on creating lst file!");
        }
        return null;
    }


    private String getAdress(ArrayList<String> operands){
        switch (operands.size()) {
            case 1:
                if(operands.get(0).startsWith("#")) {
                    return "100";
                }
                else if(operands.get(0).endsWith(",I")) {
                    return "001";
                }
                else{
                    return "000";
                }
            case 2:
                if(operands.get(0).endsWith(",I") && operands.get(1).endsWith(",I")){
                    return "011";
                }
                else if(operands.get(0).endsWith(",I") && operands.get(1).startsWith("#")){
                    return "101";
                }
                else if(operands.get(0).endsWith(",I")){
                    return "001";
                }
                else if(operands.get(1).startsWith("#")){
                    return "100";
                }
                else if(operands.get(1).endsWith(",I")){
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
        oppcodeTable.put("br", new String[]{"0000", "1", "000", "001"});
        oppcodeTable.put("brpos", new String[]{"0001", "1", "000", "001"});
        oppcodeTable.put("add", new String[]{"0010", "1", "000", "001", "100"});
        oppcodeTable.put("load", new String[]{"0011", "1", "000", "001", "100"});
        oppcodeTable.put("brzero", new String[]{"0100", "1", "000", "001"});
        oppcodeTable.put("brneg", new String[]{"0101", "1", "000", "001"});
        oppcodeTable.put("sub", new String[]{"0110", "1", "000", "001", "100"});
        oppcodeTable.put("store", new String[]{"0111", "1", "000", "001"});
        oppcodeTable.put("write", new String[]{"1000", "1", "000", "001", "100"});
        oppcodeTable.put("ret", new String[]{"1001", "0", "000"});
        oppcodeTable.put("divide", new String[]{"1010", "1", "000", "001", "100"});
        oppcodeTable.put("stop", new String[]{"1011","0", "000"});
        oppcodeTable.put("read", new String[]{"1100", "1", "000", "001"});
        oppcodeTable.put("copy", new String[]{"1101", "2", "000", "010", "100", "001", "011", "101"});
        oppcodeTable.put("mult", new String[]{"1110", "1", "000", "001", "100"});
        oppcodeTable.put("call", new String[]{"1111", "1", "000", "001"});
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
