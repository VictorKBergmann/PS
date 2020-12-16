package macro;

import java.util.ArrayList;

public class NameTab {
    private final ArrayList<String> nameTab = new ArrayList<>();
    private ArrayList<Integer> startNameTab = new ArrayList<>();
    private ArrayList<Integer> endNameTab = new ArrayList<>();

    public NameTab(){
    }
    public void addName(String name){
        nameTab.add(name);
    }
    public void addStart(int start){
        startNameTab.add(start);

    }
    public void addEnd(int end){
       endNameTab.add(end);
    }
    public String getName(int index){
        return nameTab.get(index);
    }
    public int getStart(int index){
        return startNameTab.get(index);
    }
    public int getEnd(int index){
        return endNameTab.get(index);
    }
    public int getSize(){
        return nameTab.size();
    }
    public boolean isInNameTab(String name){
        for(int a =0; a<nameTab.size(); ++a){
            if(name.equals(nameTab.get(a)) ){
                return true;
            }
        }
        return false;
    }
    public int sizeOfName(String name){
        int index = indexOfName(name);
        return endNameTab.get(index) - startNameTab.get(index);
    }
    public int sizeOfName(int index){
        return endNameTab.get(index) - startNameTab.get(index) + 1;
    }
    public int indexOfName(String name){
        return nameTab.indexOf(name);
    }
    public void delete(int index){

        int sizeOfRemoved = sizeOfName(index);
        nameTab.remove(index);
        startNameTab.remove(index);
        endNameTab.remove(index);

        int size = nameTab.size();
        for(int i = index; i< size; ++i){
            startNameTab.set(i, startNameTab.get(i) - sizeOfRemoved);
            endNameTab.set(i, endNameTab.get(i) - sizeOfRemoved);
        }

    }
    public void clear(){
        nameTab.clear();
        startNameTab.clear();
        endNameTab.clear();
    }

}
