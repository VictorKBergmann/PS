

package ps;

public class Cpu {
    private String ri, re, pc, sp, acc;

    public Cpu() {
        ri = "0000000000000000";   //reg de instrução (opcode + endereçamento)
        re = "0000000000000000";  //reg de endereço de memória
        acc = "0000000000000000";                 
        sp = "0000000000000000";
        pc = "0000000000001101";   //13 = posição inicial memória de instruções
    }

    public String getAcc() { return acc; }

    public String getPc() { return pc; }
    
    public String getRi() { return ri; }
    
    public String getRe() { return re; }
            
    public String getSp() { return sp; }            

    public String read(Memory mem) { return mem.getInstruction(Integer.parseInt(pc, 2)); }
    
    private String bitsPadding(int reg) {
        String temp2 = Integer.toString(reg,2);     
        String temp1 = ""; 
        for (int i=16; i > temp2.length(); i--) { 
            temp1 += "0";
        }
        return temp1.concat(temp2);
    }
    
    private String pointerIncrement(String pointer) {
        int aux = Integer.parseInt(pointer, 2);
        aux++;
        pointer = bitsPadding(aux);
        return pointer;
    }
    
    private String pointerDecrement(String pointer) {
        int aux = Integer.parseInt(pointer, 2);
        aux--;
        pointer = bitsPadding(aux);
        return pointer;
    }
    
    public boolean execute(Memory mem) throws IllegalArgumentException {
        ri = read(mem);
        re = "0000000000000000";
        String value;
        int operation;

        switch (ri.substring(12, 16)) {
            case "0000": //BR
                pc = pointerIncrement(pc);
                re = mem.getInstruction(Integer.parseInt(pc, 2));                
                if (ri.substring(9, 12).equals("001")) //verifica se eh indireto
                {
                    re = mem.getData(Integer.parseInt(re, 2));
                }
                                           //caso não for, faz como direto
                pc = mem.getInstruction(Integer.parseInt(re, 2)); 
                pointerDecrement(pc); //atualiza pra pos. anterior a desejada, já que o pc att no final tbm
                break;

            case "0001": //BRPOS
                pc = pointerIncrement(pc);
                re = mem.getInstruction(Integer.parseInt(pc, 2));
                if (Integer.parseInt(acc, 2) > 0) {
                    if (ri.substring(9, 12).equals("001")) {
                        re = mem.getData(Integer.parseInt(re, 2));
                    }
                    pc = mem.getInstruction(Integer.parseInt(re, 2));
                    pointerDecrement(pc);
                }
                break;

            case "0010": //ADD
                pc = pointerIncrement(pc);
                value = mem.getInstruction(Integer.parseInt(pc, 2));
                if (ri.substring(9, 12).equals("001")) { //verifica se eh indireto
                    re = mem.getData(Integer.parseInt(value, 2));
                    value = mem.getData(Integer.parseInt(re, 2));
                }
                if (ri.substring(9, 12).equals("000")) { //verifica se eh direto
                    re = value;
                    value = mem.getData(Integer.parseInt(re, 2));
                }                               //caso não for nenhum dos dois faz como imediato
                operation = Integer.parseInt(acc, 2);
                operation += Integer.parseInt(value, 2);   
                acc = bitsPadding(operation);
                break;

            case "0011": //LOAD
                pc = pointerIncrement(pc);
                value = mem.getInstruction(Integer.parseInt(pc, 2));
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(Integer.parseInt(value, 2));
                    value = mem.getData(Integer.parseInt(re, 2));
                }
                if (ri.substring(9, 12).equals("000")) {
                    re = value;
                    value = mem.getData(Integer.parseInt(re, 2));
                }
                acc = value;
                break;

            case "0100": //BRZERO
                pc = pointerIncrement(pc);
                re = mem.getInstruction(Integer.parseInt(pc, 2));
                if (Integer.parseInt(acc, 2) == 0) {
                    if (ri.substring(9, 12).equals("001")) {
                        re = mem.getData(Integer.parseInt(re, 2));
                    }
                    pc = mem.getInstruction(Integer.parseInt(re, 2));
                    pc = pointerDecrement(pc);
                }
                break;

            case "0101": //BRNEG
                pc = pointerIncrement(pc);
                re = mem.getInstruction(Integer.parseInt(pc, 2));
                if (Integer.parseInt(acc, 2) < 0) {
                    if (ri.substring(9, 12).equals("001")) {
                        re = mem.getData(Integer.parseInt(re, 2));
                    }
                    pc = mem.getInstruction(Integer.parseInt(re, 2));
                   pc = pointerDecrement(pc);
                }
                break;

            case "0110": //SUB
                pc = pointerIncrement(pc);
                value = mem.getInstruction(Integer.parseInt(pc, 2));
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(Integer.parseInt(value, 2));
                    value = mem.getData(Integer.parseInt(re, 2));
                }
                if (ri.substring(9, 12).equals("000")) {
                    re = value;
                    value = mem.getData(Integer.parseInt(re, 2));
                }
                operation = Integer.parseInt(acc, 2);
                operation -= Integer.parseInt(value, 2);   
                acc = bitsPadding(operation);
                break;
                
            case "0111": //STORE
                pc = pointerIncrement(pc);
                re = mem.getInstruction(Integer.parseInt(pc, 2));
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(Integer.parseInt(re, 2));
                }
                mem.setData(Integer.parseInt(re,2), acc);
                break;

            case "1000": //WRITE
                pc = pointerIncrement(pc);
                value = mem.getInstruction(Integer.parseInt(pc, 2));
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(Integer.parseInt(value, 2));
                    value = mem.getData(Integer.parseInt(re, 2));
                }
                if (ri.substring(9, 12).equals("000")) {
                    re = value;
                    value = mem.getData(Integer.parseInt(re, 2));
                }
                //INTERFACE.print(value);
                break;

            case "1010": //DIVIDE
                pc = pointerIncrement(pc);
                value = mem.getInstruction(Integer.parseInt(pc, 2));
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(Integer.parseInt(value, 2));
                    value = mem.getData(Integer.parseInt(re, 2));
                }
                if (ri.substring(9, 12).equals("000")) {
                    re = value;
                    value = mem.getData(Integer.parseInt(re, 2));
                }
                if (Integer.parseInt(value, 2) == 0) {
                    throw new IllegalArgumentException("Divisão por 0");
                }
                operation = Integer.parseInt(acc, 2);
                operation /= Integer.parseInt(value, 2);
                acc = bitsPadding(operation);
                break;

            case "1011": //STOP                
                return false;

            case "1100": //READ
                String input = null;
             //   input = interface.read();
                pc = pointerIncrement(pc);
                re = mem.getInstruction(Integer.parseInt(pc, 2));
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(Integer.parseInt(re, 2));
                }
                 mem.setData(Integer.parseInt(re,2), input);
                break;

            case "1101": //COPY
                String temp;
                pc = pointerIncrement(pc);
                value = mem.getInstruction(Integer.parseInt(pc, 2));
                if (ri.substring(9, 12).equals("001") || ri.substring(9, 12).equals("011") || ri.substring(9, 12).equals("101")) {
                    re = mem.getData(Integer.parseInt(value, 2));
                    value = re;
                }
                if (ri.substring(9, 12).equals("100")) {
                    re = value;
                }
                pc = pointerIncrement(pc);
                temp = mem.getInstruction(Integer.parseInt(pc, 2));
                if (ri.substring(9, 12).equals("010") || ri.substring(9, 12).equals("011")){
                    re = mem.getData(Integer.parseInt(temp, 2));
                     temp = mem.getData(Integer.parseInt(re, 2));
                }
                if (ri.substring(9, 12).equals("000")){
                    re = temp;
                    temp = mem.getData(Integer.parseInt(re, 2));
                }
                mem.setData(Integer.parseInt(value,2), temp);
                break;

            case "1110": //MULT
                pc = pointerIncrement(pc);
                value = mem.getInstruction(Integer.parseInt(pc, 2));
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(Integer.parseInt(value, 2));
                    value = mem.getData(Integer.parseInt(re, 2));
                }
                if (ri.substring(9, 12).equals("000")) {
                    re = value;
                    value = mem.getData(Integer.parseInt(re, 2));
                }
                operation = Integer.parseInt(acc, 2);
                operation *= Integer.parseInt(value, 2);   
                acc = bitsPadding(operation);
                break;

            case "1111": //CALL
                pc = pointerIncrement(pc);
                re = mem.getInstruction(Integer.parseInt(pc, 2));
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(Integer.parseInt(re, 2));
                }
                if (Integer.parseInt(sp, 2) == 10) {
                    sp = "0000000000000000";
                    throw new IllegalArgumentException("Stack Overflow");  //se tentar colocar mais alguma coisa na pilha e ela já tiver cheia, aponta para a base e lança a exception
                }
                sp = pointerIncrement(sp);
                mem.setDataStack(Integer.parseInt(sp, 2), pc);
                pc = mem.getInstruction(Integer.parseInt(re, 2));  //mesmo esquema dos branch
                pc = pointerDecrement(pc);
                break;

            case "1001": //RET                                                             
                pc = mem.getDataStack(Integer.parseInt(sp, 2)); //faz um pop
                sp = pointerDecrement(sp);
                break;
        }
        pc = pointerIncrement(pc);
        return true;
    }
    
}
