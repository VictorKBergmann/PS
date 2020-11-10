package ps;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Loader {

    private InputStreamReader input;
    private int initPosition;
    private Memory memory;

    public Loader (int position, Memory memory){
    
        this.initPosition = position;
        this.memory = memory;
    }

    public void readFile(String adress){
        try {
            FileInputStream file  = new FileInputStream(adress);//
            input = new InputStreamReader(file); //""
            file.close();
        }
        catch(Exception error){
            System.out.println("Erro na leitura do arquivo "+ adress + " .");
        }
    }

    public void loadAllWordsFromString(String string){
        
        int position = initPosition;
        int flag;  
        String line;
        Scanner scanner = new Scanner(string);

        while(scanner.hasNextLine()){

            line = scanner.nextLine();
            if( line.length() == 16 ){

                for(flag = 0; flag< line.length(); ++flag){
                    if( !(line.codePointAt(flag) == 48 || line.codePointAt(flag) == 49) ){
                        System.out.println("Símbolo "+ line.charAt(flag) + " da posição " + flag +" da palavra " + (position-initPosition) + " não é 1 ou 0, instrução será ignorada.");
                        flag = line.length() +1;
                     }
                }
                if( flag == line.length() ){
                    memory.setData(position, line);
                    ++position;
                }
            }
            else{
                System.out.println("Palavra "+ (position-initPosition) + " não está no tamanho correto, ela será ignorada.");    
            }
        }
        memory.setDataPointer(position);
        scanner.close();
    }

    public void loadAllWordsFromFile(String adress){

        try{
            readFile(adress);
            String line;
            int position = initPosition;
            int flag;

            BufferedReader buffer = new BufferedReader(input);
            line = buffer.readLine();

            while ( line != null ) {

                if( line.length() == 16 ){

                    for(flag = 0; flag< line.length(); ++flag){
                        if( !(line.codePointAt(flag) == 48 || line.codePointAt(flag) == 49) ){
                            System.out.println("Símbolo "+ line.charAt(flag) + " da posição " + flag +" da palavra " + (position-initPosition) + " não é 1 ou 0, instrução será ignorada.");
                            flag = line.length() +1;
                        }
                    }
                    if( flag == line.length() ){
                        memory.setData(position, line);
                        ++position;
                    }
                }
                else{
                    System.out.println("Palavra "+ position + " do arquivo txt não está no tamanho correto, ela será ignorada.");
                }
                line = buffer.readLine();
            }
            memory.setDataPointer(position);
        }
        catch(Exception error){
            System.out.println("Erro.");
        }
    }
}
