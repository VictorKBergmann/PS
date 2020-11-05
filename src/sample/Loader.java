package sample;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.ArrayList;

public class Loader {

    private InputStreamReader input;
    private ArrayList <Short> file;
    private String adress;

    public Loader (String adress){
        this.adress = adress;
        file = new ArrayList<Short>();
    }

    public void L() {
        readFile();
        int position = 0;

        BufferedReader br = new BufferedReader(input); // lê a linha inteira até \n
        String linha;

        Scanner sc = new Scanner(input);

        try {
            int z = 0;
            while (sc.hasNextLine()){

                linha = sc.nextLine();
                System.out.println("" + linha);
                int soma = 0;
                int counte2 = 15;

                for (int counter = 0; counter < 16; ++counter) {
                    System.out.println("Char " + counte2 + "= " + linha.charAt(counter));
                    if (Character.compare(linha.charAt(counter), '1') == 0) {
                        soma = soma + (int) Math.pow(2, counte2);
                    }
                    --counte2;
                }
                System.out.println("Soma = " + soma);

                file.add((short)soma);
                System.out.println(file.get(z));

                ++z;
            }
            sc.close();
            System.out.println("sem erro");

        } catch (Exception error) {
            System.out.println("erro");
        }
    }

    public void readFile(){
        try {
            FileInputStream   file  = new FileInputStream(adress);//
            input = new InputStreamReader(file); //lê o arquivo


        }
        catch(Exception error){
            System.out.println("erro 1");
        }
    }

}




