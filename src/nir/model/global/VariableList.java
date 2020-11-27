package nir.model.global;

import java.util.ArrayList;
import java.util.List;

public class VariableList {
    private List<Variable> list = new ArrayList<>();
    public void load(List<Variable> list){
        this.list = list;
    }
    public Double get(String param){
        for (Variable variable : list) {
            if (variable.getKey().equals(param)) return variable.getValue();
        }
        return null; // TODO: null return
    }
    public Integer getInt(String param){
        for (Variable variable : list) {
            if (variable.getKey().equals(param)) return variable.getValue().intValue();
        }
        return null; // TODO: null return
    }
}
