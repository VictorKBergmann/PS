package macro;

import java.util.ArrayList;

public class ArgTab {

    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<Integer> sizeLastLevel = new ArrayList<>();


    public ArgTab(){
    }

    public void add(String name){

        this.name.add(name);
    }
    public void addSizeLastLevel(int size){
        sizeLastLevel.add(size);
    }
    public String getName(int indexLastLevel){

        return name.get(lastLevelIndex() + indexLastLevel);
    }

    public String getArgsLastLevel(){
        StringBuilder sb = new StringBuilder();
        for(int i = lastLevelIndex(); i< name.size(); ++i){
            sb.append( " " + name.get(i) );
        }
        return sb.toString();
    }
    public int lastLevelIndex(){
        return name.size() - getSizeLastLevel();
    }
    public int getSizeLastLevel(){
        return sizeLastLevel.get(sizeLastLevel.size() - 1);
    }
    public void popLastLevel(){
        int size = getSizeLastLevel();

        for(int i = name.size() -1 ; i> size; --i){
            name.remove(i);
        }
        sizeLastLevel.remove(sizeLastLevel.size() - 1);
    }


}
