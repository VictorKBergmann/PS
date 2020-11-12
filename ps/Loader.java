package ps;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Loader {

    private InputStreamReader input;
    private int initPosition;
    private int position;
    private Memory memory;

    public Loader (int position, Memory memory){
    
        this.initPosition = position;
        this.memory = memory;
        setPosition();

    }

    public void setPosition () {

        position = initPosition;

    }

    public void readFile(String adress){
        try{   
            FileInputStream file  = new FileInputStream(adress);
            input = new InputStreamReader(file); //""
        }
        catch(Exception error){
            System.out.println("Erro na leitura do arquivo "+ adress + " .");
        }
    }

    public void loadAllWordsFromString(String string){

        int flag;
        int flag2=-1;  
        String line;
        Scanner scanner = new Scanner(string);

        while(scanner.hasNextLine()){

            line = scanner.nextLine();
            ++flag2;
            if( line.length() == 16 || line.length() == 32 || line.length() == 48 ){

                for(flag = 0; flag< line.length(); ++flag){
                    if( !(line.codePointAt(flag) == 48 || line.codePointAt(flag) == 49) ){
                        System.out.println("Símbolo "+ line.charAt(flag) + " da posição " + flag +" da instrução " +flag2 + " não é 1 ou 0, instrução será ignorada.");
                        flag = line.length() +1;
                     }
                }
                if( flag == line.length() ){

                    if(flag ==16){
                        memory.setData(position, line);
                        ++position;
                    }
                    else if(flag ==32){
                        memory.setData(position, line.substring(0, 16));
                        ++position;
                        memory.setData(position, line.substring(16, 32));
                        ++position;
                    }
                    else{
                        memory.setData(position, line.substring(0, 16));
                        ++position;
                        memory.setData(position, line.substring(16, 32));
                        ++position;
                        memory.setData(position, line.substring(32, 48));
                        ++position;
                    }
                }
            }
            else{
                System.out.println("instrução "+ flag2 + " não está no tamanho correto, ela será ignorada.");    
            }
        }
        memory.setDataPointer(position);
    }

    public void loadAllWordsFromFile(String adress){

        try{
            readFile(adress);
            String line;
            int position = initPosition;
            int flag;
            int flag2=0;  
            
            BufferedReader buffer = new BufferedReader(input);
            line = buffer.readLine();
            
            while ( line != null ) {

                if( line.length() == 16 || line.length() == 32 || line.length() == 48 ){

                    for(flag = 0; flag< line.length(); ++flag){
                        if( !(line.codePointAt(flag) == 48 || line.codePointAt(flag) == 49) ){
                            System.out.println("Símbolo "+ line.charAt(flag) + " da posição " + flag +" da instrução " + flag2 + " não é 1 ou 0, instrução será ignorada.");
                            flag = line.length() +1;
                        }
                    }
                    if( flag == line.length() ){

                        if(flag ==16){
                            memory.setData(position, line);
                            ++position;
                        }
                        else if(flag ==32){
                            memory.setData(position, line.substring(0, 16));
                            ++position;
                            memory.setData(position, line.substring(16, 32));
                            ++position;
                        }
                        else{
                            memory.setData(position, line.substring(0, 16));
                            ++position;
                            memory.setData(position, line.substring(16, 32));
                            ++position;
                            memory.setData(position, line.substring(32, 48));
                            ++position;
                        }
                    }
                }
                else{
                    System.out.println("instrução "+ flag2 + " do arquivo txt não está no tamanho correto, ela será ignorada.");
                }
                line = buffer.readLine();
                ++flag2;
            }
            memory.setDataPointer(position);
        }
        catch(Exception error){
            System.out.println("Erro.");
        }
    }
}
