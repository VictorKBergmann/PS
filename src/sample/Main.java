package sample;

public class Main {

//inicializa memoria
//inicializa CPU
//INICIALIZA O CARREGADOR
//RODA O CARREGADOR


    ULA ula;
    Controller controller;
    Memory memory;
    Short IR , MAR, ACC;
    byte MOP;
    int numOpp;


    public Main(){
        ula = new ULA();
        controller = new Controller();
        memory = new Memory();
        IR = 0;
        MAR = 0;
        ACC = 0;
    }

    private void Program() {


        while (IR != 11) /*oppCode do stop  */ {

            IR = memory.getData(controller.getProgramCounter());
            numOpp = controller.oppCodeDecoder(IR);

            switch (numOpp) {
                case 11:
                    MAR = memory.getData(controller.getProgramCounter() + 1);
                    ACC = ula.operation(IR, ACC, MAR);
                    break;
                case 21:
                    controller.incrementProgramCounter();
                    MAR = memory.getData(controller.getProgramCounter() + 1);
                    controller.operation(IR, ACC, MAR);
                    break;
                case 30:// copy
                    break;
                case 31://load
                    break;
                default:
                    break;

            }
            if(!(numOpp == 20))  controller.incrementProgramCounter();
        }


        // CPU = new CPU();
        // = 1
        // WHILE{
        //
        //   ATUALIZA RI
        //   MANDA RI PRO CONTROLER
        //   CONTROLER RETORNA QUANTOS OPERANDOS
        //   MANDA PRO CONTROLLER (RI, OPERANDOS)
        //   BUSCA O OPERANDO
        //   CONTROLLER CHAMA A OPERAÇÃO
        //   ULA EXECUTA A OPERAÇÃO
        //   ATUALIZA PC
        //

        System.out.println("hello world!");

    }

    public static void main(String[] args) {
        Main main = new Main();
        main.Program();
    }
}