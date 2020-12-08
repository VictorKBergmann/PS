package macro;

import java.util.ArrayList;

public class FormalParameterStack {

    private ArrayList<String> name = new ArrayList<>();
    private ArrayList <Integer[]> definitionPositionAndLevelPair = new ArrayList<>();

    public FormalParameterStack() {
    }

    public void add(String name, int dLevel, int dPosition) {
        Integer[] temp = new Integer[2];
        temp[0]=dLevel;
        temp[1]=dPosition;
        this.definitionPositionAndLevelPair.add(temp);
        this.name.add(name);
    }
    public void add(String name, int dPosition) {
        Integer[] temp = new Integer[2];
        temp[0]=getHighDLevel(name)+1;
        temp[1]=dPosition;
        this.definitionPositionAndLevelPair.add(temp);
        this.name.add(name);
    }
    public int size (){
        return name.size();
    }
    public int getHighDLevel(String parameter){

        int index = name.lastIndexOf(parameter);
        if(index == -1){
            return 0;
        }
        else{
            return definitionPositionAndLevelPair.get(index)[0];
        }

    }
    public String getName(int index){
        return this.name.get(index);
    }
    public int getDPosition(int index){
        return definitionPositionAndLevelPair.get(index)[1];
    }
    public int getDLevel(int index){
        return definitionPositionAndLevelPair.get(index)[0];
    }

    public Integer[] getDefinitionPositionAndLevelPair(String name){

        for(int i = name.length()-1; i >= 0; --i){
            if(this.name.get(i).equals(name)){
                return this.definitionPositionAndLevelPair.get(i);
            }
        }
        return null;
    }

}
