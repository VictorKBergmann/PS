
package ps;

public class Cpu {
    private int acc, pc, sp;
    private String ri;

    public Cpu() {
        ri = null;
        acc = 0;
        sp = 2;
        pc = 13;
    }

    public int getAcc() { return acc; }

    public int getPc() { return pc; }
    
    public String getRi() { return ri; }
            
    public int getSp() { return sp; }            

    public String read(Memory mem) { return mem.getData(pc); }        
    
    public boolean execute(String data, Memory mem) {

        ri = data.substring(12, 16);
        System.out.println("RI(Opcode): " + ri);
        
        String adrMode = data.substring(9, 12);
        System.out.println("Adress Mode: " + adrMode);
        
        String op1, op2;

        switch (ri) {
            case "0000": //BR
                op1 = data.substring(16, 31);                
                if (adrMode.equals("001")) //verifica se eh indireto
                {
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                op1 = mem.getData(Integer.parseInt(op1, 2)); //caso não for, faz como direto
                pc = Integer.parseInt(op1, 2) - 1; //atualiza pra pos. anterior a desejada, já que o pc att no final tbm
                break;

            case "0001": //BRPOS
                op1 = data.substring(16, 31);
                if (acc > 0) {
                    if (adrMode.equals("001")) {
                        op1 = mem.getData(Integer.parseInt(op1, 2));
                    }
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                    pc = Integer.parseInt(op1, 2) - 1;
                }
                break;

            case "0010": //ADD
                op1 = data.substring(16, 31); 
                System.out.println("Operador 1: " + op1);
                if (adrMode.equals("001")) { //verifica se eh indireto
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                if (adrMode.equals("000")) { //verifica se eh direto
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }                               //caso não for nenhum dos dois faz como imediato
                acc += Integer.parseInt(op1, 2);   //todo   
                break;

            case "0011": //LOAD
                op1 = data.substring(16, 31);
                if (adrMode.equals("001")) {
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                if (adrMode.equals("000")) {
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                acc = Integer.parseInt(op1, 2);
                break;

            case "0100": //BRZERO
                op1 = data.substring(16, 31);
                if (acc == 0) {
                    if (adrMode.equals("001")) {
                        op1 = mem.getData(Integer.parseInt(op1, 2));
                    }
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                    pc = Integer.parseInt(op1, 2) - 1;
                }
                break;

            case "0101": //BRNEG
                op1 = data.substring(16, 31);
                if (acc < 0) {
                    if (adrMode.equals("001")) {
                        op1 = mem.getData(Integer.parseInt(op1, 2));
                    }
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                    pc = Integer.parseInt(op1, 2) - 1;
                }
                break;

            case "0110": //SUB
                op1 = data.substring(16, 31);
                if (adrMode.equals("001")) {
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                if (adrMode.equals("000")) {
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                acc -= Integer.parseInt(op1, 2);
                break;
                
            case "0111": //STORE                     
                op1 = data.substring(16, 31);
                if (adrMode.equals("001")) {
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                mem.setData(Integer.parseInt(op1,2), Integer.toString(acc,2));
                break;

            case "1000": //WRITE
                op1 = data.substring(16, 31);
                if (adrMode.equals("001")) {
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                if (adrMode.equals("000")) {
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                //INTERFACE.print(op1);
                break;

            case "1010": //DIVIDE
                op1 = data.substring(16, 31);
                if (adrMode.equals("001")) {
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                if (adrMode.equals("000")) {
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                acc /= Integer.parseInt(op1, 2);
                break;

            case "1011": //STOP                
                return false;

            case "1100": //READ
                String input = null;
             //   input = interface.read();
                op1 = data.substring(16, 31);
                if (adrMode.equals("001")) {
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                 mem.setData(Integer.parseInt(op1,2), input);
                break;

            case "1101": //COPY
                op1 = data.substring(16, 31);
                op2 = data.substring(32, 47);
                if (adrMode.equals("010") || adrMode.equals("011")) {
                    op2 = mem.getData(Integer.parseInt(op2, 2));
                    op2 = mem.getData(Integer.parseInt(op2, 2));
                }
                if (adrMode.equals("001") || adrMode.equals("011") || adrMode.equals("101")) {
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                if (adrMode.equals("000")) {
                    op2 = mem.getData(Integer.parseInt(op2, 2));
                }
                mem.setData(Integer.parseInt(op1,2), op2);
                break;

            case "1110": //MULT
                op1 = data.substring(16, 31);
                if (adrMode.equals("001")) {
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                if (adrMode.equals("000")) {
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                acc *= Integer.parseInt(op1, 2);
                break;

            case "1111": //CALL
                op1 = data.substring(16, 31);
                if (adrMode.equals("001")) {
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                op1 = mem.getData(Integer.parseInt(op1, 2));
                mem.setData(sp,Integer.toString(pc)); //faz um push
                sp++;
                pc = Integer.parseInt(op1, 2) - 1;  //mesmo esquema dos branch
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
