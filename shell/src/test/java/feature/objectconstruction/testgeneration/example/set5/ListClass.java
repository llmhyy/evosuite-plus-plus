package feature.objectconstruction.testgeneration.example.set5;

import java.util.ArrayList;
import java.util.List;

public class ListClass {
    private List<Integer> list = new ArrayList<>();

    public void setList(List<Integer> list) {
        this.list = list;
    }

    public List<Integer> getList() {
        return list;
    }

    public void add(Integer i) {
        list.add(i);
    }

    public int getAt(int index) {
        return list.get(index);
    }

    public void setAt(int index, int value) {
        list.set(index, value);
    }

    public Integer getAt5() {
        if (list.size() >= 6)
            return this.getAt(5);
        else
            return null;
    }

    public void setAt5(int value) {
        if (list.size() >= 6)
            this.setAt(5, value);
    }

    public int getSize() {
        return list.size();
    }


}