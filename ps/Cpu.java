
package ps;

public class Cpu {
    private int acc, pc, sp;
    private String ri, re;

    public Cpu() {
        ri = "0000000000000000";   //reg de instrução (opcode + endereçamento)
        re = "0000000000000000";  //reg de endereço de memória
        acc = 0;                 
        sp = 2;
        pc = 13;
    }

    public int getAcc() { return acc; }

    public int getPc() { return pc; }
    
    public String getRi() { return ri; }
    
    public String getRe() { return re; }
            
    public int getSp() { return sp; }            

    public String read(Memory mem) { return mem.getInstruction(pc); }        
    
    public boolean execute(Memory mem) throws IllegalArgumentException {
        ri = read(mem);
        System.out.println("\nInstrução que buscou no read(ri): " + ri);
        re = "0000000000000000";
        String value;

        switch (ri.substring(12, 16)) {
            case "0000": //BR
                pc++;
                re = mem.getInstruction(pc);                
                if (ri.substring(9, 12).equals("001")) //verifica se eh indireto
                {
                    re = mem.getData(Integer.parseInt(re, 2));
                }
                                           //caso não for, faz como direto
                pc = Integer.parseInt(mem.getData(Integer.parseInt(re, 2)), 2) - 1; //atualiza pra pos. anterior a desejada, já que o pc att no final tbm
                break;

            case "0001": //BRPOS
                pc++;
                re = mem.getInstruction(pc);
                if (acc > 0) {
                    if (ri.substring(9, 12).equals("001")) {
                        re = mem.getData(Integer.parseInt(re, 2));
                    }
                    pc = Integer.parseInt(mem.getData(Integer.parseInt(re, 2)), 2) - 1;
                }
                break;

            case "0010": //ADD
                pc++;
                value = mem.getInstruction(pc);
                if (ri.substring(9, 12).equals("001")) { //verifica se eh indireto
                    re = mem.getData(Integer.parseInt(value, 2));
                    value = mem.getData(Integer.parseInt(re, 2));
                }
                if (ri.substring(9, 12).equals("000")) { //verifica se eh direto
                    re = value;
                    value = mem.getData(Integer.parseInt(re, 2));
                }                               //caso não for nenhum dos dois faz como imediato
                acc += Integer.parseInt(value, 2);   //todo   
                break;

            case "0011": //LOAD
                pc++;
                value = mem.getInstruction(pc);
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(Integer.parseInt(value, 2));
                    value = mem.getData(Integer.parseInt(re, 2));
                }
                if (ri.substring(9, 12).equals("000")) {
                    re = value;
                    value = mem.getData(Integer.parseInt(re, 2));
                }
                acc = Integer.parseInt(value, 2);
                break;

            case "0100": //BRZERO
                pc++;
                re = mem.getInstruction(pc);
                if (acc == 0) {
                    if (ri.substring(9, 12).equals("001")) {
                        re = mem.getData(Integer.parseInt(re, 2));
                    }
                    pc = Integer.parseInt(mem.getData(Integer.parseInt(re, 2)), 2) - 1;
                }
                break;

            case "0101": //BRNEG
                pc++;
                re = mem.getInstruction(pc);
                if (acc < 0) {
                    if (ri.substring(9, 12).equals("001")) {
                        re = mem.getData(Integer.parseInt(re, 2));
                    }
                    pc = Integer.parseInt(mem.getData(Integer.parseInt(re, 2)), 2) - 1;
                }
                break;

            case "0110": //SUB
                pc++;
                value = mem.getInstruction(pc);
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(Integer.parseInt(value, 2));
                    value = mem.getData(Integer.parseInt(re, 2));
                }
                if (ri.substring(9, 12).equals("000")) {
                    re = value;
                    value = mem.getData(Integer.parseInt(re, 2));
                }
                acc -= Integer.parseInt(value, 2);
                break;
                
            case "0111": //STORE
                value = bitsPadding(acc);
                pc++;
                re = mem.getInstruction(pc);
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(Integer.parseInt(re, 2));
                }
                mem.setData(Integer.parseInt(re,2), value);
                break;

            case "1000": //WRITE
                pc++;
                value = mem.getInstruction(pc);
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
                pc++;
                value = mem.getInstruction(pc);
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(Integer.parseInt(value, 2));
                    value = mem.getData(Integer.parseInt(re, 2));
                }
                if (ri.substring(9, 12).equals("000")) {
                    re = value;
                    value = mem.getData(Integer.parseInt(re, 2));
                }
                acc /= Integer.parseInt(value, 2);
                break;

            case "1011": //STOP                
                return false;

            case "1100": //READ
                String input = null;
             //   input = interface.read();
                pc++;
                re = mem.getInstruction(pc);
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(Integer.parseInt(re, 2));
                }
                 mem.setData(Integer.parseInt(re,2), input);
                break;

            case "1101": //COPY
                pc++;
                value = mem.getInstruction(pc);
                if (ri.substring(9, 12).equals("010") || ri.substring(9, 12).equals("011")) {
                    re = mem.getData(Integer.parseInt(value, 2));
                    value = mem.getData(Integer.parseInt(re, 2));
                }
                if (ri.substring(9, 12).equals("000")) {
                    re = value;
                    value = mem.getData(Integer.parseInt(re, 2));
                }
                pc++;
                re = mem.getInstruction(pc);
                if (ri.substring(9, 12).equals("001") || ri.substring(9, 12).equals("011") || ri.substring(9, 12).equals("101")) {
                    re = mem.getData(Integer.parseInt(re, 2));
                }
                mem.setData(Integer.parseInt(re,2), value);
                break;

            case "1110": //MULT
                pc++;
                value = mem.getInstruction(pc);
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(Integer.parseInt(value, 2));
                    value = mem.getData(Integer.parseInt(re, 2));
                }
                if (ri.substring(9, 12).equals("000")) {
                    re = value;
                    value = mem.getData(Integer.parseInt(re, 2));
                }
                acc *= Integer.parseInt(value, 2);
                break;

            case "1111": //CALL
                pc++;
                re = mem.getInstruction(pc);
                if (ri.substring(9, 12).equals("001")) {
                    re = mem.getData(Integer.parseInt(re, 2));
                }
                mem.setData(sp,Integer.toString(pc)); //faz um push
                sp++;
                pc = Integer.parseInt(mem.getData(Integer.parseInt(re, 2)), 2);  //mesmo esquema dos branch
                break;

            case "1001": //RET
                sp--;
                pc = Integer.parseInt(mem.getData(sp), 2); //faz um pop
                break;
        }
        System.out.println("RE: " + re);
        pc++;
        return true;
    }
    
    private String bitsPadding(int acc) {
        String temp2 = Integer.toString(acc,2);     
        String temp1 = ""; 
        for (int i=16; i > temp2.length(); i--) { 
            temp1 += "0";
        }
        return temp1.concat(temp2);
    }
}
