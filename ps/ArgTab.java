package macro;

import java.util.ArrayList;

public class ArgTab {

    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<int[]> position = new ArrayList<>();

    public ArgTab(){
    }
    public int[] findLastPosition(String name){

        return position.get(this.name.lastIndexOf(name));

    }
    public void add(String name, int l, int p){
        int[] temp = new int[2];
        temp[0]=l;
        temp[1]=p;
        this.name.add(name);
        this.position.add(temp);
    }
    public String getName(int index){
        return name.get(index);
    }
    public void clear(){
        name.clear();
    }
    public int size(){
        return name.size();
    }
    public void removeLast(){
        int index = name.size()-1;
        this.name.remove(index);
        this.position.remove(index);
    }

}
