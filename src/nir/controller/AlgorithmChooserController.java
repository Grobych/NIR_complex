package nir.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import nir.tst.TestType;

import java.net.URL;
import java.util.ResourceBundle;

public class AlgorithmChooserController implements Initializable {
    @FXML
    ChoiceBox<TestType> choiceBox;

    public static TestType type;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<TestType> types = FXCollections.observableArrayList(TestType.values());
        choiceBox.setItems(types);
        choiceBox.setValue(types.get(0));
        type = choiceBox.getValue();
        choiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                type = choiceBox.getValue();
            }
        });
    }

    public static TestType getType(){
        return type;
    }
}
