package ps;


import javax.swing.*;

public class Cpu {
    /**
     * ri = instruction register (registrador de instruções)
     * re = adress register (registrador de endereço de memoria)
     * pc = contador de instruções (program counter)
     * sp = ponteiro de pilha (stack pointer)
     * acc = acumulador
     *
     * out = saida da interface (usado no write)
     * in = entrada da interface (usado no read)
     */
    private String ri, re, pc, sp, acc;
    private JTextArea out;
    private JOptionPane in;
    private Memory mem;

    public Cpu(Memory mem) {
        this.mem = mem;
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

    /**
     *
     * @param mem
     * @return what is PC is pointing to
     */
    public String read(Memory mem) { return mem.getInstruction(toShort(pc)); }

    /**
     *  inicialize registers
     */
    public void init() {

        ri = "0000000000000000";   //reg de instrução (opcode + endereçamento)
        re = "0000000000000000";  //reg de endereço de memória
        acc = "0000000000000000";
        sp = "0000000000000000";
        //pc = "0000000000001101";
        pc = bitsPadding((short) (mem.getStackSize() + mem.getInitialPosition() + 2));

    }

    /**
     * convert positive short to string in binary (in this case is necessary to fill with 0's on the left to complete 16 bits)
     * @param reg short
     * @return String in binary
     */
    private String bitsPadding(short reg) {
        String temp2 = Integer.toString(reg,2);
        String temp1 = "";
        for (int i=16; i > temp2.length(); i--) {
            temp1 += "0";
        }
        return temp1.concat(temp2);
    }

    /**
     * convert short (both positive and negative) to String (binary)
     * @param reg
     * @return
     */
    private String toString(short reg) {
        String bin;
        String res;
        if(reg < 0){
            bin = Integer.toBinaryString(reg);
            res = bin.substring(16, 32);
        }
        else{
            res = bitsPadding(reg);     //uses the bitsPadding function if postive
        }
        return res;
    }

    /**
     * convert string to short
     * @param bin string
     * @return short
     */
    private short toShort(String bin) {
        short res;
        res = (short)Integer.parseInt(bin, 2);
        return res;
    }

    /**
     * increment pointer ( used because pointer are saved in string )
     * @param pointer
     * @return ++pointer
     */
    private String pointerIncrement(String pointer) {
        short aux = toShort(pointer);
        aux++;
        pointer = toString(aux);
        return pointer;
    }
    /**
     * increment pointer ( used because pointer are saved in string )
     * @param pointer
     * @return --pointer
     */
    private String pointerDecrement(String pointer) {
        short aux = toShort(pointer);
        aux--;
        pointer = toString(aux);
        return pointer;
    }


    public boolean execute() throws IllegalArgumentException {
        ri = read(mem);
        String value;  //get values from data memory
        short operation;
/**
 * // 0-8 = '0's
 * 9-12 = pointerType( '100' = immediate, '000' = direct, '001' = indirect )
 * 12-16 oppcode
 */
        switch (ri.substring(12, 16)) {
            case "0000": //BR (jump)
                pc = pointerIncrement(pc);
                re = mem.getInstruction(toShort(pc));
                if (ri.substring(9, 12).equals("001")) //check if is indirect
                {
                    re = mem.getData(toShort(re));
                }
                //else do it direct
                pc = re;
                pc = pointerDecrement(pc); //pc++ is exec in the end, so is decremented now to get the right position
                break;

            case "0001": //BRPOS (jump if acc > 0)
                pc = pointerIncrement(pc);
                re = mem.getInstruction(toShort(pc));
                if (toShort(acc) > 0) { // //checks if acc is positive
                    if (ri.substring(9, 12).equals("001")) {//check if is indirect
                        re = mem.getData(toShort(re));
                    }//else do it direct
                    pc = re;
                    pc = pointerDecrement(pc); //pc++ is exec in the end, so is decremented now to get the right position
                }
                break;

            case "0010": //ADD ( acc = acc + operator)
                pc = pointerIncrement(pc);
                value = mem.getInstruction(toShort(pc));
                if (ri.substring(9, 12).equals("001")) { //check if is indirect
                    re = mem.getData(toShort(value));
                    value = mem.getData(toShort(re));
                }
                if (ri.substring(9, 12).equals("000")) { //check if is direct
                    re = value;
                    value = mem.getData(toShort(re));
                }                               //else do it immediate
                operation = toShort(acc);
                operation += toShort(value);
                acc = toString(operation);//save result on ACC
                break;

            case "0011": //LOAD (load operator in ACC)
                pc = pointerIncrement(pc);
                value = mem.getInstruction(toShort(pc));
                if (ri.substring(9, 12).equals("001")) {//check if is indirect
                    re = mem.getData(toShort(value));
                    value = mem.getData(toShort(re));
                }
                if (ri.substring(9, 12).equals("000")) {//check if is direct
                    re = value;
                    value = mem.getData(toShort(re));
                }
                acc = value;//save result on ACC
                break;

            case "0100": //BRZERO (jump if acc == 0)
                pc = pointerIncrement(pc);
                re = mem.getInstruction(toShort(pc));
                if (toShort(acc) == 0) { //checks if acc is zero
                    if (ri.substring(9, 12).equals("001")) { //check if is indirect
                        re = mem.getData(toShort(re));
                    }//else do it direct
                    pc = re;
                    pc = pointerDecrement(pc);
                }
                break;

            case "0101": //BRNEG (jump if acc < 0)
                pc = pointerIncrement(pc);
                re = mem.getInstruction(toShort(pc));
                if (toShort(acc) < 0) { //checks if acc is negative
                    if (ri.substring(9, 12).equals("001")) { //check if is indirect
                        re = mem.getData(toShort(re));
                    }//else do it direct
                    pc = re;
                    pc = pointerDecrement(pc);
                }
                break;

            case "0110": //SUB
                pc = pointerIncrement(pc);
                value = mem.getInstruction(toShort(pc));
                if (ri.substring(9, 12).equals("001")) {//check if is indirect
                    re = mem.getData(toShort(value));
                    value = mem.getData(toShort(re));
                }
                if (ri.substring(9, 12).equals("000")) {//check if is direct
                    re = value;
                    value = mem.getData(toShort(re));
                }//else do it immediate
                operation = toShort(acc);
                operation -= toShort(value);
                acc = toString(operation);//save result on ACC
                break;

            case "0111": //STORE
                pc = pointerIncrement(pc);
                re = mem.getInstruction(toShort(pc));
                if (ri.substring(9, 12).equals("001")) {//check if is indirect
                    re = mem.getData(toShort(re));
                }
                mem.setData(toShort(re), acc); // set on memory(position on RE) the value on ACC
                break;

            case "1000": //WRITE
                pc = pointerIncrement(pc);
                value = mem.getInstruction(toShort(pc));
                if (ri.substring(9, 12).equals("001")) {//check if is indirect
                    re = mem.getData(toShort(value));
                    value = mem.getData(toShort(re));
                }
                if (ri.substring(9, 12).equals("000")) {//check if is direct
                    re = value;
                    value = mem.getData(toShort(re));
                }
                out.append("\nOutput: " + toShort(value) + "\n");// output on GUI
                break;

            case "1010": //DIVIDE
                pc = pointerIncrement(pc);
                value = mem.getInstruction(toShort(pc));
                if (ri.substring(9, 12).equals("001")) {//check if is indirect
                    re = mem.getData(toShort(value));
                    value = mem.getData(toShort(re));
                }
                if (ri.substring(9, 12).equals("000")) {//check if is direct
                    re = value;
                    value = mem.getData(toShort(re));
                }
                if (toShort(value) == 0) {//exception if is dividing by zero
                    throw new IllegalArgumentException("Dividing by 0");
                }
                operation = toShort(acc);
                operation /= toShort(value);//DIVISION
                acc = toString(operation);//save result on ACC
                break;

            case "1011": //STOP
                throw new IllegalArgumentException("END OF EXECUTION");

            case "1100": //READ
                boolean flagzin = true;
                String input;
                do {
                    input = JOptionPane.showInputDialog("Insert a number:"); //receive the input
                    if(input == null) { // cancel exception
                        throw new IllegalArgumentException("stopped by user!");
                    }
                    if(!input.matches("[0-9]+")) {// input in wrong format
                        JOptionPane.showMessageDialog(null, "Input can only have integers.");
                    }
                    if(input.matches("[0-9]+")) { flagzin = false; }
                } while (flagzin);
                pc = pointerIncrement(pc);
                re = mem.getInstruction(toShort(pc)); // re = operator(position to save)
                if (ri.substring(9, 12).equals("001")) {//check if is indirect
                    re = mem.getData(toShort(re));
                }
                mem.setData(toShort(re), toString(Short.parseShort(input))); //write the input on memory
                break;

            case "1101": //COPY (2 operators) cpy op2 on op1
                String temp;
                pc = pointerIncrement(pc);
                re = mem.getInstruction(toShort(pc));  //get the first operator
                if (ri.substring(9, 12).equals("001") || ri.substring(9, 12).equals("011") || ri.substring(9, 12).equals("101")) {// if ri[2] == 1
                    re = mem.getData(toShort(re));
                }
                value = re; //value recieves the first(value)
                pc = pointerIncrement(pc);
                temp = mem.getInstruction(toShort(pc)); //get the second operator
                if (ri.substring(9, 12).equals("010") || ri.substring(9, 12).equals("011")){ // if [1] == 1
                    re = mem.getData(toShort(temp));
                    temp = mem.getData(toShort(re));
                }
                if (ri.substring(9, 12).equals("000")){
                    re = temp;
                    temp = mem.getData(toShort(re));
                }
                //temp = op2(data)
                //value = op1(pointer)
                mem.setData(toShort(value), temp);
                break;

            case "1110": //MULT
                pc = pointerIncrement(pc);
                value = mem.getInstruction(toShort(pc));
                if (ri.substring(9, 12).equals("001")) {//check if is indirect
                    re = mem.getData(toShort(value));
                    value = mem.getData(toShort(re));
                }
                if (ri.substring(9, 12).equals("000")) {//check if is direct
                    re = value;
                    value = mem.getData(toShort(re));
                }
                operation = toShort(acc);
                operation *= toShort(value); //MULT
                acc = toString(operation); //save result on ACC
                break;

            case "1111": //CALL
                pc = pointerIncrement(pc);
                re = mem.getInstruction(toShort(pc));
                if (toShort(sp) == mem.getStackSize()) { // if it is full
                    sp = "0000000000000000"; // point to base
                    throw new IllegalArgumentException("Stack Overflow"); // Stack Overflow exception
                }
                if (ri.substring(9, 12).equals("001")) {//check if is indirect
                    re = mem.getData(toShort(re));
                }
                sp = pointerIncrement(sp);// point to next part
                mem.setDataStack(toShort(sp), pc); // to remember where you stopped
                pc = re; // jump
                pc = pointerDecrement(pc);
                break;

            case "1001": //RET
                if(toShort(sp) == 0) {//empty stack exception
                    throw new IllegalArgumentException("empty stack");
                }
                pc = mem.getDataStack(toShort(sp)); //pop
                sp = pointerDecrement(sp); //
                break;
        }
        pc = pointerIncrement(pc);
        return true;
    }
}
