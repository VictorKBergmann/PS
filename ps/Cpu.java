package ps;


import javax.swing.*;

public class Cpu {
    private String ri, re, pc, sp, acc;
    private JTextArea out;
    private JOptionPane in;

    public Cpu() {
        init();
    }

    public String getAcc() { return acc; }

    public String getPc() { return pc; }

    public String getRi() { return ri; }

    public String getRe() { return re; }

    public String getSp() { return sp; }

    public void setUserOutput(JTextArea out) {
        this.out = out;
    }

    public void setUserInput(JOptionPane in) {
        this.in = in;
    }

    public String read(Memory mem) { return mem.getInstruction(toShort(pc)); }

    public void init() {

        ri = "0000000000000000";   //reg de instrução (opcode + endereçamento)
        re = "0000000000000000";  //reg de endereço de memória
        acc = "0000000000000000";
        sp = "0000000000000000";
        pc = "0000000000001101";

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
    private short toShort(String bin) {
        short res;
        res = (short)Integer.parseInt(bin, 2);
        return res;
    }

    private String pointerIncrement(String pointer) {
        short aux = toShort(pointer);
        aux++;
        pointer = toString(aux);
        return pointer;
    }

    private String pointerDecrement(String pointer) {
        short aux = toShort(pointer);
        aux--;
        pointer = toString(aux);
        return pointer;
    }

    public boolean execute(Memory mem) throws IllegalArgumentException {
        ri = read(mem);
        re = "0000000000000000";
        String value;  //pega os valores na memória de dados
        short operation;

        switch (ri.substring(12, 16)) {
            case "0000": //BR
                pc = pointerIncrement(pc);
                re = mem.getInstruction(toShort(pc));
                if (ri.substring(9, 12).equals("001")) //verifica se eh indireto
                {
                    re = mem.getData(toShort(re));
                }
                //caso não for, faz como direto
                // pc = mem.getData(Integer.parseInt(re, 2)); jeito antigo
                pc = re;
                pc = pointerDecrement(pc); //atualiza pra pos. anterior a desejada, já que o pc att no final tbm
                break;

            case "0001": //BRPOS
                pc = pointerIncrement(pc);
                re = mem.getInstruction(toShort(pc));
                if (toShort(acc) > 0) {
                    if (ri.substring(9, 12).equals("001")) {
                        re = mem.getData(toShort(re));
                    }
                    pc = re;
                    pc = pointerDecrement(pc);
                }
                break;

            case "0010": //ADD
                pc = pointerIncrement(pc);
                value = mem.getInstruction(toShort(pc));
                if (ri.substring(9, 12).equals("001")) { //verifica se eh indireto
                    re = mem.getData(toShort(value));
                    value = mem.getData(toShort(re));
                }
                if (ri.substring(9, 12).equals("000")) { //verifica se eh direto
                    re = value;
                    value = mem.getData(toShort(re));
                }                               //caso não for nenhum dos dois faz como imediato
                operation = toShort(acc);
                operation += toShort(value);
                acc = toString(operation);
                break;

            case "0011": //LOAD
                pc = pointerIncrement(pc);
                value = mem.getInstruction(toShort(pc));
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(toShort(value));
                    value = mem.getData(toShort(re));
                }
                if (ri.substring(9, 12).equals("000")) {
                    re = value;
                    value = mem.getData(toShort(re));
                }
                acc = value;
                break;

            case "0100": //BRZERO
                pc = pointerIncrement(pc);
                re = mem.getInstruction(toShort(pc));
                if (toShort(acc) == 0) {
                    if (ri.substring(9, 12).equals("001")) {
                        re = mem.getData(toShort(re));
                    }
                    pc = re;
                    pc = pointerDecrement(pc);
                }
                break;

            case "0101": //BRNEG
                pc = pointerIncrement(pc);
                re = mem.getInstruction(toShort(pc));
                if (toShort(acc) < 0) {
                    if (ri.substring(9, 12).equals("001")) {
                        re = mem.getData(toShort(re));
                    }
                    pc = re;
                    pc = pointerDecrement(pc);
                }
                break;

            case "0110": //SUB
                pc = pointerIncrement(pc);
                value = mem.getInstruction(toShort(pc));
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(toShort(value));
                    value = mem.getData(toShort(re));
                }
                if (ri.substring(9, 12).equals("000")) {
                    re = value;
                    value = mem.getData(toShort(re));
                }
                operation = toShort(acc);
                operation -= toShort(value);
                acc = toString(operation);
                break;

            case "0111": //STORE
                pc = pointerIncrement(pc);
                re = mem.getInstruction(toShort(pc));
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(toShort(re));
                }
                mem.setData(toShort(re), acc);
                break;

            case "1000": //WRITE
                pc = pointerIncrement(pc);
                value = mem.getInstruction(toShort(pc));
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(toShort(value));
                    value = mem.getData(toShort(re));
                }
                if (ri.substring(9, 12).equals("000")) {
                    re = value;
                    value = mem.getData(toShort(re));
                }
                out.append("\nOutput: " + value + "\n");
                break;

            case "1010": //DIVIDE
                pc = pointerIncrement(pc);
                value = mem.getInstruction(toShort(pc));
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(toShort(value));
                    value = mem.getData(toShort(re));
                }
                if (ri.substring(9, 12).equals("000")) {
                    re = value;
                    value = mem.getData(toShort(re));
                }
                if (toShort(value) == 0) {
                    throw new IllegalArgumentException("Divisão por 0");
                }
                operation = toShort(acc);
                operation /= toShort(value);
                acc = toString(operation);
                break;

            case "1011": //STOP
                throw new IllegalArgumentException("END OF EXECUTION");

            case "1100": //READ
                boolean flagzin = true;
                String input;
                do {
                    input = JOptionPane.showInputDialog("Digite o número em binário (16 bits):");
                    if(input == null) {
                        throw new IllegalArgumentException("Execução cancelada pelo usuário!");
                    }
                    if(!input.matches("[0-1]+")) {
                        JOptionPane.showMessageDialog(null, "Input deve conter somente 0's e 1's! Tente novamente.");
                    }
                    if(input.length() != 16) {
                        JOptionPane.showMessageDialog(null, "Input deve conter 16 números! Tente novamente.");
                    }
                    if(input.matches("[0-1]+") && input.length() == 16) { flagzin = false; }
                } while (flagzin);
                pc = pointerIncrement(pc);
                re = mem.getInstruction(toShort(pc));
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(toShort(re));
                }
                mem.setData(toShort(re), input);
                break;

            case "1101": //COPY
                String temp;
                pc = pointerIncrement(pc);
                re = mem.getInstruction(toShort(pc));  //pega primeiro operando
                if (ri.substring(9, 12).equals("001") || ri.substring(9, 12).equals("011") || ri.substring(9, 12).equals("101")) {
                    re = mem.getData(toShort(re));
                }
                value = re;
                pc = pointerIncrement(pc);
                temp = mem.getInstruction(toShort(pc)); //pega segundo operando
                if (ri.substring(9, 12).equals("010") || ri.substring(9, 12).equals("011")){
                    re = mem.getData(toShort(temp));
                    temp = mem.getData(toShort(re));
                }
                if (ri.substring(9, 12).equals("000")){
                    re = temp;
                    temp = mem.getData(toShort(re));
                }
                mem.setData(toShort(value), temp);
                break;

            case "1110": //MULT
                pc = pointerIncrement(pc);
                value = mem.getInstruction(toShort(pc));
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(toShort(value));
                    value = mem.getData(toShort(re));
                }
                if (ri.substring(9, 12).equals("000")) {
                    re = value;
                    value = mem.getData(toShort(re));
                }
                operation = toShort(acc);
                operation *= toShort(value);
                acc = toString(operation);
                break;

            case "1111": //CALL
                pc = pointerIncrement(pc);
                re = mem.getInstruction(toShort(pc));
                if (toShort(sp) == 10) {
                    sp = "0000000000000000";
                    throw new IllegalArgumentException("Stack Overflow");  //se tentar colocar mais alguma coisa na pilha e ela já tiver cheia, aponta para a base e lança a exception
                }
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(toShort(re));
                }
                sp = pointerIncrement(sp);
                mem.setDataStack(toShort(sp), pc);
                pc = re;  //mesmo esquema dos branch
                pc = pointerDecrement(pc);
                break;

            case "1001": //RET
                if(toShort(sp) == 0) {
                    throw new IllegalArgumentException("Pilha vazia"); //se não tiver nada na pilha e tentar dar RET, lança a exception
                }
                pc = mem.getDataStack(toShort(sp)); //faz um pop
                sp = pointerDecrement(sp);
                break;
        }
        pc = pointerIncrement(pc);
        return true;
    }
}
