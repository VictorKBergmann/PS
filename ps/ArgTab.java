package macro;

import java.util.ArrayList;

public class ArgTab {

    private ArrayList<String> name = new ArrayList<>();
    private ArrayList <Integer[]> position = new ArrayList<>();

    public ArgTab(){
    }
    public Integer[] findLastPosition(String name){

        return position.get(this.name.lastIndexOf(name));

    }
    public void add(String name, int l, int p){
        Integer[] temp = new Integer[1];
        temp[0]=l;
        temp[1]=p;
        this.name.add(name);
        this.position.add(temp);
    }
    public void removeLast(){
        int index = name.size()-1;
        this.name.remove(index);
        this.position.remove(index);
    }

}
