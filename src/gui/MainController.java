package gui;

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
import sudoku.Difficulty;
import sudoku.Sudoku;
import sudoku.objects.Board;
import sudoku.objects.Field;

import java.util.Optional;

public class MainController {
    private TextField[][] fields;
    private Sudoku sudoku;

    @FXML
    private GridPane grid;

    public void initialize() {
    }

    private void generateGUI(Board board) {
        int boardSize = Board.BOARD_SIZE;
        fields = new TextField[boardSize][];

        for (int i = 0; i < boardSize; i++) {
            fields[i] = new TextField[boardSize];
            for (int j = 0; j < boardSize; j++) {
                Field field = board.getField(i, j);

                TextField newTextField = createTextField(i, j, field);

                fields[i][j] = newTextField;

                grid.add(newTextField, j, i);
            }
        }
    }

    /**
     * Creates TextField as cell for Sudoku board.
     * @param row
     * @param column
     * @param field
     * @return
     */
    private TextField createTextField(int row, int column, Field field) {
        TextField newTextField = new TextField(field.toString());

        StringBuilder styles = new StringBuilder();

        if (field.hasValue()) {
            newTextField.setEditable(false);
            styles.append("-fx-text-fill: black;");
        }
        else {
            styles.append("-fx-text-fill: royalblue;");
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

        // set up the textfield
        newTextField.setPrefSize(10000, 10000);

        newTextField.setStyle(styles.toString());

        return newTextField;
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
            generateGUI(sudoku.getBoard());
        }
    }
}
