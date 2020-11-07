
package ps;

public class Cpu {
    private int acc, pc, sp;
    private String ri, re;

    public Cpu() {
        ri = "0000000000000000";   //reg de instrução (opcode)
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

    public String read(Memory mem) { return mem.getInstructionData(pc); }        
    
    public boolean execute(String data, Memory mem) throws IllegalArgumentException{

        ri = data.substring(12, 16);
        System.out.println("RI(Opcode): " + ri);
        
        String adrMode = data.substring(9, 12);
        System.out.println("Adress Mode: " + adrMode);
        
        String dado;

        switch (ri) {
            case "0000": //BR
                re = data.substring(16, 32);                
                if (adrMode.equals("001")) //verifica se eh indireto
                {
                    re = mem.getData(Integer.parseInt(re, 2));
                }
                                           //caso não for, faz como direto
                pc = Integer.parseInt(mem.getData(Integer.parseInt(re, 2)), 2) - 1; //atualiza pra pos. anterior a desejada, já que o pc att no final tbm
                break;

            case "0001": //BRPOS
                re = data.substring(16, 32);
                if (acc > 0) {
                    if (adrMode.equals("001")) {
                        re = mem.getData(Integer.parseInt(re, 2));
                    }
                    pc = Integer.parseInt(mem.getData(Integer.parseInt(re, 2)), 2) - 1;
                }
                break;

            case "0010": //ADD
                dado = data.substring(16, 32);
                if (adrMode.equals("001")) { //verifica se eh indireto
                    re = dado;
                    re = mem.getData(Integer.parseInt(re, 2));
                    dado = mem.getData(Integer.parseInt(re, 2));
                }
                if (adrMode.equals("000")) { //verifica se eh direto
                    re = dado;
                    dado = mem.getData(Integer.parseInt(re, 2));
                }                               //caso não for nenhum dos dois faz como imediato
                acc += Integer.parseInt(dado, 2);   //todo   
                break;

            case "0011": //LOAD
                dado = data.substring(16, 32);
                if (adrMode.equals("001")) {
                    re = dado;
                    re = mem.getData(Integer.parseInt(re, 2));
                    dado = mem.getData(Integer.parseInt(re, 2));
                }
                if (adrMode.equals("000")) {
                    re = dado;
                    dado = mem.getData(Integer.parseInt(re, 2));
                }
                acc = Integer.parseInt(dado, 2);
                break;

            case "0100": //BRZERO
                re = data.substring(16, 32);
                if (acc == 0) {
                    if (adrMode.equals("001")) {
                        re = mem.getData(Integer.parseInt(re, 2));
                    }
                    pc = Integer.parseInt(mem.getData(Integer.parseInt(re, 2)), 2) - 1;
                }
                break;

            case "0101": //BRNEG
                re = data.substring(16, 32);
                if (acc < 0) {
                    if (adrMode.equals("001")) {
                        re = mem.getData(Integer.parseInt(re, 2));
                    }
                    pc = Integer.parseInt(mem.getData(Integer.parseInt(re, 2)), 2) - 1;
                }
                break;

            case "0110": //SUB
                dado = data.substring(16, 32);
                if (adrMode.equals("001")) {
                    re = dado;
                    re = mem.getData(Integer.parseInt(re, 2));
                    dado = mem.getData(Integer.parseInt(re, 2));
                }
                if (adrMode.equals("000")) {
                    re = dado;
                    dado = mem.getData(Integer.parseInt(re, 2));
                }
                acc -= Integer.parseInt(dado, 2);
                break;
                
            case "0111": //STORE
                String temp2 = Integer.toString(acc,2);     
                String temp1 = ""; 
                for (int i=16; i > temp2.length(); i--) {  //Gambiarra
                    temp1 += "0";
                }
                temp1 = temp1.concat(temp2);
                
                re = data.substring(16, 32);
                if (adrMode.equals("001")) {
                    re = mem.getData(Integer.parseInt(re, 2));
                }
                mem.setData(Integer.parseInt(re,2), temp1);
                break;

            case "1000": //WRITE
                dado = data.substring(16, 32);
                if (adrMode.equals("001")) {
                    re = dado;
                    re = mem.getData(Integer.parseInt(re, 2));
                    dado = mem.getData(Integer.parseInt(re, 2));
                }
                if (adrMode.equals("000")) {
                    re = dado;
                    dado = mem.getData(Integer.parseInt(re, 2));
                }
                //INTERFACE.print(op1);
                break;

            case "1010": //DIVIDE
                dado = data.substring(16, 32);
                if (adrMode.equals("001")) {
                    re = dado;
                    re = mem.getData(Integer.parseInt(re, 2));
                    dado = mem.getData(Integer.parseInt(re, 2));
                }
                if (adrMode.equals("000")) {
                    re = dado;
                    dado = mem.getData(Integer.parseInt(re, 2));
                }
                acc /= Integer.parseInt(dado, 2);
                break;

            case "1011": //STOP                
                return false;

            case "1100": //READ
                String input = null;
             //   input = interface.read();
                re = data.substring(16, 32);
                if (adrMode.equals("001")) {
                    re = mem.getData(Integer.parseInt(re, 2));
                }
                 mem.setData(Integer.parseInt(re,2), input);
                break;

            case "1101": //COPY
                dado = data.substring(32, 48);
                if (adrMode.equals("010") || adrMode.equals("011")) {
                    re = dado;
                    re = mem.getData(Integer.parseInt(re, 2));
                    dado = mem.getData(Integer.parseInt(re, 2));
                }
                if (adrMode.equals("000")) {
                    re = dado;
                    dado = mem.getData(Integer.parseInt(re, 2));
                }
                re = data.substring(16, 32);
                if (adrMode.equals("001") || adrMode.equals("011") || adrMode.equals("101")) {
                    re = mem.getData(Integer.parseInt(re, 2));
                }
                mem.setData(Integer.parseInt(re,2), dado);
                break;

            case "1110": //MULT
                dado = data.substring(16, 32);
                if (adrMode.equals("001")) {
                    re = dado;
                    re = mem.getData(Integer.parseInt(re, 2));
                    dado = mem.getData(Integer.parseInt(re, 2));
                }
                if (adrMode.equals("000")) {
                    re = dado;
                    dado = mem.getData(Integer.parseInt(re, 2));
                }
                acc *= Integer.parseInt(dado, 2);
                break;

            case "1111": //CALL
                re = data.substring(16, 32);
                if (adrMode.equals("001")) {
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
        pc++;
        return true;
    }
}

