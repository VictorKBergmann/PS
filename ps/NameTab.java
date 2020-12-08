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

    public int indexOfName(String name){
        return nameTab.indexOf(name);
    }

}
