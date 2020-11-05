package sample;

import java.util.Scanner;

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
    Scanner scanner;

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
                    controller.incrementProgramCounter();
                    MAR = memory.getData(controller.getProgramCounter());
                    ACC = ula.operation(IR, ACC, MAR);
                    break;
                case 21:
                    controller.incrementProgramCounter();
                    MAR = memory.getData(controller.getProgramCounter());
                    controller.operation(IR, ACC, MAR);
                    break;
                case 33:// copy
                    controller.incrementProgramCounter();
                    MAR = memory.getData(controller.getProgramCounter() + 1);
                    ACC = memory.getData(MAR);
                    MAR = memory.getData(controller.getProgramCounter());
                    memory.setData(controller.getProgramCounter(), ACC);
                    break;
                case 31://load
                    controller.incrementProgramCounter();
                    ACC = memory.getData(controller.getProgramCounter());
                    break;
                case 32://store
                    controller.incrementProgramCounter();
                    MAR = memory.getData(controller.getProgramCounter());
                    memory.setData(MAR, ACC);
                    break;
                case 40:
                    controller.setProgramCounter(controller.getStackPointer());
                    controller.decrementStackPointer();
                    break;
                case 41://call
                    memory.setData(controller.getStackPointer(), controller.getProgramCounter());
                    controller.incrementStackPointer();
                    controller.incrementProgramCounter();
                    MAR = memory.getData(controller.getProgramCounter());
                    controller.setProgramCounter(MAR);
                case 51://write
                    controller.incrementProgramCounter();
                    System.out.println(memory.getData(controller.getProgramCounter()));
                    break;
                case 52://read
                    controller.incrementProgramCounter();
                    scanner = new Scanner(System.in);
                    memory.setData(controller.getProgramCounter(), scanner.nextShort());
                    break;
                default:
                    break;
            }
            if(!(numOpp == 20))  controller.incrementProgramCounter();
        }


        System.out.println("hello world!");

    }

    public static void main(String[] args) {
        Main main = new Main();
        main.Program();
    }
}