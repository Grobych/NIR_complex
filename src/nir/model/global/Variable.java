package nir.model.global;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class Variable {

    private SimpleStringProperty key;
    private SimpleDoubleProperty value;

    @Override
    public String toString(){
        return new String(key + " - " + value);
    }

    public Variable(String key, Double value){
        this.key = new SimpleStringProperty(key);
        this.value = new SimpleDoubleProperty(value);
    }

    public Double getValue() {
        return value.getValue();
    }

    public String getKey() {
        return key.getValue();
    }

    public void setValue(Double value) {
        this.value = new SimpleDoubleProperty(value);
    }

    public SimpleStringProperty getKeyProperty(){
        return key;
    }
    public SimpleDoubleProperty getValueProperty(){
        return value;
    }
}
