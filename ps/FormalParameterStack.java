package macro;

import java.util.ArrayList;

public class FormalParameterStack {

    private ArrayList<String> name = new ArrayList<>();
    private ArrayList <Integer[]> definitionPositionAndLevelPair = new ArrayList<>();

    public FormalParameterStack() {
    }

    public void add(String name, int dPosition, int dLevel) {
        Integer[] temp = new Integer[1];
        temp[0]=dPosition;
        temp[1]=dLevel;
        this.definitionPositionAndLevelPair.add(temp);
        this.name.add(name);
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
