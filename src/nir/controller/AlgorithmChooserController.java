package nir.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import nir.tst.AlgorithmType;

import java.net.URL;
import java.util.ResourceBundle;

public class AlgorithmChooserController implements Initializable {
    @FXML
    ChoiceBox<AlgorithmType> choiceBox;

    public static AlgorithmType type;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<AlgorithmType> types = FXCollections.observableArrayList(AlgorithmType.values());
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

    public static AlgorithmType getType(){
        return type;
    }
}
