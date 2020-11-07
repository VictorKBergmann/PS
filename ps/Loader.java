package ps;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Loader {

    private InputStreamReader input;
    private String adress;
    private int initPosition;
    private Memory memory;

    public Loader (String adress, int position, Memory memory){
        this.adress = adress;
        this.initPosition = position;
        this.memory = memory;
    }

    public void readFile(){
        try {
            FileInputStream   file  = new FileInputStream(adress);//
            input = new InputStreamReader(file); //
        }
        catch(Exception error){
            System.out.println("Erro na leitura do arquivo "+ adress + " .");
        }
    }

    public void loadAllInstructions(){

        try{
            readFile();
            String line;
            int position = initPosition;
            int flag;

            BufferedReader buffer = new BufferedReader(input);
            line = buffer.readLine();

            while ( line != null ) {

                if(line.length() == 16 || line.length() == 32 || line.length() == 48 ){

                    for(flag = 0; flag< line.length(); ++flag){
                        if( !(line.codePointAt(flag) == 48 || line.codePointAt(flag) == 49) ){
                            System.out.println("Letra "+ line.charAt(flag) + " posição  " + flag +" da instrução " + position + " não é 1 ou 0, instrução será ignorada.");
                            flag = line.length() +1;
                        }
                    }
                    if( flag == line.length() ){
                        memory.setData(position, line);
                        ++position;
                    }
                }
                else{
                    System.out.println("Instrução "+ position + " do arquivo txt não está no tamanho correto, ela será ignorada.");
                }
                line = buffer.readLine();
            }
        }
        catch(Exception error){
            System.out.println("Erro.");
        }
    }
}
