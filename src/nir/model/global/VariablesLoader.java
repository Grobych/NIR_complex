package nir.model.global;

import java.util.ArrayList;
import java.util.List;

public class VariablesLoader {
    public static List<Variable> get(String ... keys){
        GlobalVariables gs = GlobalVariables.getInstance();
        List<Variable> res = new ArrayList<>();
        for (String key : keys) {
            for (Variable variable : gs.list) {
                if (variable.getKey().equals(key)) res.add(variable);
            }
        }
        return res;
    }
}
