package ps;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Loader {

    private InputStreamReader input; //input's from interface
    private int initPosition; //position to start saving instructions
    private int position; // actual writing position
    private Memory memory;


    public Loader (int position, Memory memory){//builder used on GUI
    
        this.initPosition = position;
        this.memory = memory;
        setPosition();

    }

    public void setPosition () {

        position = initPosition;

    }

    /**
     * read the file and give Exception if it's needed
     * @param adress
     */
    public void readFile(String adress){
        try{   
            FileInputStream file  = new FileInputStream(adress);
            input = new InputStreamReader(file); //""
        }
        catch(Exception error){
            System.out.println("Error on file reading "+ adress + " .");
        }
    }

    /**
     * load instructions from string to the memory
     * @param string instructions
     */
    public void loadAllWordsFromString(String string){

        int flag;
        int flag2=-1;  
        String line;
        Scanner scanner = new Scanner(string); //Scanner is better to read files, it's a list of strings to separate lines

        while(scanner.hasNextLine()){

            line = scanner.nextLine();
            ++flag2; // used to know when it has Exception

            if( line.length() == 16 || line.length() == 32 || line.length() == 48 ){// test if the line has the right length
                // 16 == instruction, 32 == instruction + operator, 48 == instruction + 2 operator

                for(flag = 0; flag< line.length(); ++flag){// test if it has non binary symbols
                    if( !(line.codePointAt(flag) == 48 || line.codePointAt(flag) == 49) ){
                        System.out.println("symbol "+ line.charAt(flag) + " on position " + flag +" of instruction " +flag2 + " isn't binary, instruction will be ignored.");
                        flag = line.length() +1;
                     }
                }
                if( flag == line.length() ){
                    //saves instruction and operator on memory
                    // 16 == instruction, 32 == instruction + operator, 48 == instruction + 2 operator
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
                System.out.println("instruction "+ flag2 + " has not the right length. it will be ignored");
            }
        }//end of while
        memory.setDataPointer(position);// saves the end of instructions
    }//end of loadAllWordsFromString

    /**
     * this function is not used anymore, we do this on
     * GUI and use loadAllWordsFromString
     * they do the same thing, but loadAllWordsFromString receive a String
     *
     *
     * load instructions from a file to the memory
     * @param adress of file with instructions
     **
     */
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
                            System.out.println("symbol "+ line.charAt(flag) + " on position " + flag +" of instruction " +flag2 + " isn't binary, instruction will be ignored.");
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
                    System.out.println("instruction "+ flag2 + " has not the right length. it will be ignored");
                }
                line = buffer.readLine();
                ++flag2;
            }
            memory.setDataPointer(position);
        }
        catch(Exception error){
            System.out.println("couldn't read the file.");
        }
    }
}
