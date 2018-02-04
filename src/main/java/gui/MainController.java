package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
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

    private boolean dontCallListener;

    @FXML
    private Label hintLabel;

    @FXML
    private Label backLabel;

    @FXML
    private GridPane grid;

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

        // reset disable
        grid.setDisable(false);
        hintLabel.setDisable(false);
        backLabel.setDisable(false);
    }

    /**
     * Creates TextField as cell for Sudoku board.
     * @param row
     * @param column
     * @return
     */
    private TextField createTextField(int row, int column) {
        TextField newTextField = new TextField(sudoku.getBoard().getField(row, column).toString());

        String styles = getTextFieldStyle(row, column, false);

        // set up the textfield
        newTextField.setPrefSize(10000, 10000);

        newTextField.setStyle(styles);

        if (!sudoku.didUserWriteThisFieldValue(row, column) && sudoku.getBoard().getField(row, column).hasValue()) {
            newTextField.setEditable(false);
        }

        newTextField.textProperty().addListener((observable, oldValue, newValue) -> textFieldTextChanged(oldValue, newValue, newTextField));

        return newTextField;
    }

    /**
     * Returns style meant for field specified by parameters.
     * @param row
     * @param column
     * @param isHint
     * @return Style of the field
     */
    private String getTextFieldStyle(int row, int column, boolean isHint) {
        StringBuilder styles = new StringBuilder();

        Field field = sudoku.getBoard().getField(row, column);
        // is hint => red color
        if (isHint) {
            styles.append("-fx-text-fill: red;");
        }
        // is user selected or field has value
        else if (sudoku.didUserWriteThisFieldValue(row, column) || !field.hasValue()) {
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
        choiceDialog.setHeaderText(null);
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

    /**
     * Handles displaying hint to the user and refreshing the board (frontend and backend).
     * @param event
     */
    @FXML
    private void displayHint(MouseEvent event) {
        Move move = sudoku.getHint();

        int row = move.getRow();
        int column = move.getColumn();
        int number = move.getNumber();

        TextField textField = fields[row][column];

        String styles = getTextFieldStyle(row, column, true);
        dontCallListener = true;
        textField.setText(Integer.toString(number));
        dontCallListener = false;
        textField.setStyle(styles);
        textField.setEditable(false);

        sudoku.playHint(move);

        if (sudoku.isFinished()) {
            finishTheGame();
        }
    }

    /**
     * Handles "back" event. Resets last non-identical turn of the player.
     * @param event
     */
    @FXML
    private void back(MouseEvent event) {
        Coordinate coordinate = sudoku.back();

        // no move was returned back
        if (coordinate != null) {
            int row = coordinate.getRow();
            int column = coordinate.getColumn();

            Field field = sudoku.getBoard().getField(row, column);

            dontCallListener = true;
            fields[row][column].setText(field.toString());
            dontCallListener = false;
        }
    }

    /**
     * Handles playing the move.
     * @param move
     */
    private void playMove(Move move) {
        sudoku.play(move);

        if (sudoku.isFinished()) {
            finishTheGame();
        }
        else if (sudoku.isBoardFilled()) {
            handleFilledNonFinishedGame();
        }
    }

    /**
     * Handles finishing the game successfully.
     */
    private void finishTheGame() {
        grid.setDisable(true);
        hintLabel.setDisable(true);
        backLabel.setDisable(true);

        // alert user that the game has won
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Congratulations");
        alert.setHeaderText(null);
        alert.setContentText("Congratulations, you have won the game!");

        alert.showAndWait();
    }

    /**
     * Handles filled non-finished game.
     */
    private void handleFilledNonFinishedGame() {
        Alert alert = new Alert(Alert.AlertType.WARNING);

        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText("The board is filled incorrectly!");

        alert.showAndWait();
    }

    /**
     * Handles event when user changes text of any field on the board.
     * @param oldValue
     * @param newValue
     * @param textField
     */
    private void textFieldTextChanged(String oldValue, String newValue, TextField textField) {
        if (newValue.length() > 1) {
            textField.setText(oldValue);
        }
        // length is 1 and is not digit => get old value
        else if (newValue.length() == 1 && !Character.isDigit(newValue.charAt(0))) {
            textField.setText(oldValue);
        }
        // is zero => revert
        else if (newValue.length() == 1 && Character.isDigit(newValue.charAt(0)) && newValue.charAt(0) == '0') {
            textField.setText(oldValue);
        }
        // otherwise if the new value is filled and correct, play the move
        else if (newValue.length() == 1 && !dontCallListener) {
            Coordinate coordinate = getTextFieldCoordinates(textField);
            playMove(new Move(coordinate, Integer.parseInt(newValue)));
        }
    }
}
