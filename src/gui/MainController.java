package gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sudoku.Coordinate;
import sudoku.Difficulty;
import sudoku.Sudoku;
import sudoku.objects.Board;
import sudoku.objects.Field;
import sudoku.objects.Move;

import java.util.Optional;

public class MainController {
    private TextField[][] fields;
    private Sudoku sudoku;

    @FXML
    private Label hintLabel;

    @FXML
    private GridPane grid;

    public void initialize() {
    }

    private void generateGUI() {
        grid.getChildren().clear();
        int boardSize = Board.BOARD_SIZE;
        fields = new TextField[boardSize][];

        for (int i = 0; i < boardSize; i++) {
            fields[i] = new TextField[boardSize];
            for (int j = 0; j < boardSize; j++) {
                TextField newTextField = createTextField(i, j);

                fields[i][j] = newTextField;

                grid.add(newTextField, j, i);
            }
        }

        grid.setDisable(false);
    }

    /**
     * Creates TextField as cell for Sudoku board.
     * @param row
     * @param column
     * @return
     */
    private TextField createTextField(int row, int column) {
        TextField newTextField = new TextField(sudoku.getBoard().getField(row, column).toString());

        String styles = getTextFieldStyles(row, column, false);

        // set up the textfield
        newTextField.setPrefSize(10000, 10000);

        newTextField.setStyle(styles);

        if (!isValueUserSelected(row, column) && sudoku.getBoard().getField(row, column).hasValue()) {
            newTextField.setEditable(false);
        }

        newTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // length is > 1 => rewrite it to the old value
            if (newValue.length() > 1) {
                newTextField.setText(oldValue);
            }
            // length is 1 and is not digit => get old value
            else if (newValue.length() == 1 && !Character.isDigit(newValue.charAt(0))) {
                newTextField.setText(oldValue);
            }
            // is zero => revert
            else if (newValue.length() == 1 && Character.isDigit(newValue.charAt(0)) && newValue.charAt(0) == '0') {
                newTextField.setText(oldValue);
            }
            // otherwise if the new value is filled and correct, play the move
            else if (newValue.length() == 1) {
                Coordinate coordinate = getTextFieldCoordinates(newTextField);
                playMove(new Move(coordinate, Integer.parseInt(newValue)));
            }
        });

        return newTextField;
    }

    private String getTextFieldStyles(int row, int column, boolean isHint) {
        StringBuilder styles = new StringBuilder();

        Field field = sudoku.getBoard().getField(row, column);
        // is hint => red color
        if (isHint) {
            styles.append("-fx-text-fill: red;");
        }
        // is user selected or field has value
        else if (isValueUserSelected(row, column) || !field.hasValue()) {
            styles.append("-fx-text-fill: royalblue;");
        }
        // else it must be pre-generated
        else {
            styles.append("-fx-text-fill: black;");
        }
        // column is area sized
        if (row % Board.AREA_SIZE == 0 && column % Board.AREA_SIZE == 0) {
            // highlight from upper and left side
            styles.append("-fx-border-width: 4 1 1 4;");
        }
        else if (column % Board.AREA_SIZE == 0) {
            // highlight from left side
            styles.append("-fx-border-width: 1 1 1 4;");
        }
        else if (row % Board.AREA_SIZE == 0) {
            // highlight from upper side
            styles.append("-fx-border-width: 4 1 1 1;");
        }
        return styles.toString();
    }

    private boolean isValueUserSelected(int row, int column) {
        return sudoku.getPlayedMoves().stream().anyMatch(x -> x.getRow() == row && x.getColumn() == column);
    }

    private Coordinate getTextFieldCoordinates(TextField textField) {
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            for (int j = 0; j < Board.BOARD_SIZE; j++) {
                if (textField == fields[i][j]) {
                    return new Coordinate(i, j);
                }
            }
        }
        return null;
    }

    /**
     * Is invoked when New game menu button is pressed.
     * @param event
     */
    @FXML
    private void newGame(MouseEvent event) {
        // set up choice dialog
        ChoiceDialog<Difficulty> choiceDialog = new ChoiceDialog<>(Difficulty.MEDIUM, Difficulty.values());
        choiceDialog.setTitle("New game dialog");
        choiceDialog.setHeaderText("");
        choiceDialog.setContentText("Choose difficulty");

        // open dialog
        Optional<Difficulty> dialogResult = choiceDialog.showAndWait();

        // if there OK was pressed
        if (dialogResult.isPresent()) {
            // get the chosen answer
            Difficulty chosenDifficulty = dialogResult.get();
            // generate the new game
            sudoku = Sudoku.generateNewGame(chosenDifficulty);
            // display the newly generated game
            generateGUI();
            // hint button will be enabled for the game
            hintLabel.setDisable(false);
        }
    }

    @FXML
    private void displayHint(MouseEvent event) {
        Move move = sudoku.getHint();

        int row = move.getRow();
        int column = move.getColumn();
        int number = move.getNumber();

        TextField textField = fields[row][column];

        String styles = getTextFieldStyles(row, column, true);
        textField.setText(Integer.toString(number));
        textField.setStyle(styles);
        textField.setEditable(false);

        sudoku.playHint(move);
    }

    private void playMove(Move move) {
        sudoku.play(move);

        if (sudoku.isFinished()) {
            endTheGameIfItHasFinished();
        }
    }

    private void endTheGameIfItHasFinished() {
        grid.setDisable(true);
        hintLabel.setDisable(true);

        // alert user that the game has won
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Congratulations");
        alert.setHeaderText(null);
        alert.setContentText("Congratulations, you have won the game!");

        alert.showAndWait();
    }
}
