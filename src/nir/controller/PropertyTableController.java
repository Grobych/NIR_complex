package nir.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import nir.model.global.GlobalVariables;
import nir.model.global.Variable;

public class PropertyTableController {
    @FXML
    TableView<Variable> paramTableView;
    @FXML
    TableColumn <Variable, String> paramColumn;
    @FXML
    TableColumn<Variable,Double> valueColumn;

    public void initialize(){
        paramTableView.setEditable(true);
        paramColumn.setEditable(true);
        valueColumn.setEditable(true);

        paramColumn.setCellValueFactory(
                new PropertyValueFactory<Variable,String>("key")
        );
        valueColumn.setCellValueFactory(
                new PropertyValueFactory<Variable,Double>("value")
        );

        setupValueColumn();
        paramTableView.setItems(GlobalVariables.getInstance().list);
        setTableEditable();
    }


    private void setTableEditable() {
        paramTableView.setEditable(true);
        // allows the individual cells to be selected
        paramTableView.getSelectionModel().cellSelectionEnabledProperty().set(true);
        // when character or numbers pressed it will start edit in editable
        // fields
        paramTableView.setOnKeyPressed(event -> {
            if (event.getCode().isLetterKey() || event.getCode().isDigitKey()) {
                editFocusedCell();
            } else if (event.getCode() == KeyCode.RIGHT
                    || event.getCode() == KeyCode.TAB) {
                paramTableView.getSelectionModel().selectNext();
                event.consume();
            } else if (event.getCode() == KeyCode.LEFT) {
                // work around due to
                // TableView.getSelectionModel().selectPrevious() due to a bug
                // stopping it from working on
                // the first column in the last row of the paramTableView
                selectPrevious();
                event.consume();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void editFocusedCell() {
        final TablePosition<Variable, ?> focusedCell = paramTableView
                .focusModelProperty().get().focusedCellProperty().get();
        paramTableView.edit(focusedCell.getRow(), focusedCell.getTableColumn());
    }

    @SuppressWarnings("unchecked")
    private void selectPrevious() {
        if (paramTableView.getSelectionModel().isCellSelectionEnabled()) {
            // in cell selection mode, we have to wrap around, going from
            // right-to-left, and then wrapping to the end of the previous line
            TablePosition<Variable, ?> pos = paramTableView.getFocusModel()
                    .getFocusedCell();
            if (pos.getColumn() - 1 >= 0) {
                // go to previous row
                paramTableView.getSelectionModel().select(pos.getRow(),
                        getTableColumn(pos.getTableColumn(), -1));
            } else if (pos.getRow() < paramTableView.getItems().size()) {
                // wrap to end of previous row
                paramTableView.getSelectionModel().select(pos.getRow() - 1,
                        paramTableView.getVisibleLeafColumn(
                                paramTableView.getVisibleLeafColumns().size() - 1));
            }
        } else {
            int focusIndex = paramTableView.getFocusModel().getFocusedIndex();
            if (focusIndex == -1) {
                paramTableView.getSelectionModel().select(paramTableView.getItems().size() - 1);
            } else if (focusIndex > 0) {
                paramTableView.getSelectionModel().select(focusIndex - 1);
            }
        }
    }

    private TableColumn<Variable, ?> getTableColumn(
            final TableColumn<Variable, ?> column, int offset) {
        int columnIndex = paramTableView.getVisibleLeafIndex(column);
        int newColumnIndex = columnIndex + offset;
        return paramTableView.getVisibleLeafColumn(newColumnIndex);
    }

    private void setupValueColumn() {
        valueColumn
                .setCellFactory(EditCell.<Variable, Double>forTableColumn(
                        new MyDoubleStringConverter()));
        valueColumn.setOnEditCommit(event -> {
            final Double value = event.getNewValue() != null ? event.getNewValue()
                    : event.getOldValue();
            ((Variable) event.getTableView().getItems()
                    .get(event.getTablePosition().getRow()))
                    .setValue(value);
            paramTableView.refresh();
        });
    }

//    private void createColumnManually() {
//        TableColumn<Variable, Date> paramColumn = new TableColumn<>(
//                "Date of Birth");
//        paramColumn.setCellValueFactory(person -> {
//            SimpleObjectProperty<Date> property = new SimpleObjectProperty<>();
//            property.setValue(person.getValue().getDateOfBirth());
//            return property;
//        });
//        paramTableView.getColumns().add(2, paramColumn);
//    }

//    @FXML
//    private void submit(final ActionEvent event) {
//        if (allFieldsValid()) {
//            final String firstName = firstNameTextField.getText();
//            final String surname = surnameTextField.getText();
//            Date dateOfBirth = null;
//            try {
//                dateOfBirth = DATE_FORMATTER
//                        .parse(dateOfBirthTextField.getText());
//            } catch (final ParseException e) {
//            }
//            final String occupation = occupationTextField.getText();
//            final double salary = Double.parseDouble(salaryTextField.getText());
//            data.add(new Variable(firstName, surname, dateOfBirth,
//                    occupation, salary));
//        }
//    }
}
