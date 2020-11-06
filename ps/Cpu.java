package ps;

public class Cpu {

    private int acc, pc, sp;

    public Cpu() {
        acc = 0;
        sp = 2;
        pc = 13;
    }

    public int getAcc() { return acc; }

    public void setAcc(int acc) { this.acc = acc; }

    public int getPc() { return pc; } 

    public void setPc(int pc) { this.pc = pc; }
            
    public int getSp() { return sp; }            

    public String read(Memory mem) { return mem.getData(pc); }        
    
    public boolean execute(String data, Memory mem) {

        String opCode = data.substring(12, 16);
        System.out.println("Opcode: " + opCode);
        
        String adrMode = data.substring(4, 7);
        System.out.println("Adress Mode: " + adrMode);
        
        String op1, op2;

        switch (opCode) {
            case "0000": //BR
                op1 = data.substring(16, 31);                
                if (adrMode.equals("001") || adrMode.equals("010") || adrMode.equals("011")) //verifica se eh indireto
                {
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                
                pc = Integer.parseInt(op1, 2);
                break;

            case "0001": //BRPOS
                op1 = data.substring(16, 31);
                if (acc > 0) {
                    if (adrMode.equals("001") || adrMode.equals("010") || adrMode.equals("011")) {
                        op1 = mem.getData(Integer.parseInt(op1, 2));
                    }
                    pc = Integer.parseInt(op1, 2);
                }
                break;

            case "0010": //ADD
                op1 = data.substring(16, 31); 
                System.out.println("Operador 1: " + op1);
                if (adrMode.equals("001") || adrMode.equals("010") || adrMode.equals("011")) {
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                acc += Integer.parseInt(op1, 2);   //todo
                break;

            case "0011": //LOAD
                op1 = data.substring(16, 31);
                if (adrMode.equals("001") || adrMode.equals("010") || adrMode.equals("011")) {
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                acc = Integer.parseInt(op1, 2);
                break;

            case "0100": //BRZERO
                op1 = data.substring(16, 31);
                if (acc == 0) {
                    if (adrMode.equals("001") || adrMode.equals("010") || adrMode.equals("011")) {
                        op1 = mem.getData(Integer.parseInt(op1, 2));
                    }
                    pc = Integer.parseInt(op1, 2);
                }
                break;

            case "0101": //BRNEG
                op1 = data.substring(16, 31);
                if (acc < 0) {
                    if (adrMode.equals("001") || adrMode.equals("010") || adrMode.equals("011")) {
                        op1 = mem.getData(Integer.parseInt(op1, 2));
                    }
                    pc = Integer.parseInt(op1, 2);
                }
                break;

            case "0110": //SUB
                op1 = data.substring(16, 31);
                if (adrMode.equals("001") || adrMode.equals("010") || adrMode.equals("011")) {
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                acc -= Integer.parseInt(op1, 2);
                break;
                
            case "0111": //STORE                     
                op1 = data.substring(16, 31);                
                mem.setData(Integer.parseInt(op1,2), Integer.toString(acc,2));
                break;

            case "1000": //WRITE
                op1 = data.substring(16, 31);
                if (adrMode.equals("001") || adrMode.equals("010") || adrMode.equals("011")) {
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                //INTERFACE.print(op1);
                break;

            case "1010": //DIVIDE
                op1 = data.substring(16, 31);
                if (adrMode.equals("001") || adrMode.equals("010") || adrMode.equals("011")) {
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                acc /= Integer.parseInt(op1, 2);
                break;

            case "1011": //STOP                
                return false;

//            case "1100": //READ
//                //op1 = interface.read();
//                if (op1.contains("#")) {
//                    op1 = mem.getData(Integer.parseInt(op1, 2));
//                }
//                break;

            case "1101": //COPY
                //op1 = data.substring(16, 31);
                op2 = data.substring(32, 47);
                //op1Mode = op1.subString(4,6);
                String op2Mode = op2.substring(4, 6);
                if (op2Mode.equals("001") || op2Mode.equals("010") || op2Mode.equals("011")) {
                    op2 = mem.getData(Integer.parseInt(op2, 2));
                }
                op1 = op2;
                break;

            case "1110": //MULT
                op1 = data.substring(16, 31);
                if (adrMode.equals("001") || adrMode.equals("010") || adrMode.equals("011")) {
                    op1 = mem.getData(Integer.parseInt(op1, 2));
                }
                acc *= Integer.parseInt(op1, 2);
                break;

            case "1111": //CALL
                op1 = data.substring(16, 31);
                mem.setData(sp,Integer.toString(pc)); //faz um push
                sp++;
                pc = Integer.parseInt(op1, 2);
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