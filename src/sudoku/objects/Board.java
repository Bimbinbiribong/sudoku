package sudoku.objects;

import sudoku.Coordinate;
import sudoku.fastObjects.EvaluationBoard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by honza on 1.2.18.
 */
public class Board {
    private Field[][] fields;
    public static final int BOARD_SIZE = 9;
    public static final int AREA_SIZE = 3;

    public Board() {
        fields = new Field[BOARD_SIZE][];

        for (int i = 0; i < BOARD_SIZE; i++) {
            fields[i] = new Field[BOARD_SIZE];
            for (int j = 0; j < BOARD_SIZE; j++) {

                fields[i][j] = new Field();
            }
        }
    }

    Board(Field[][] fields) {
        this.fields = fields;
    }

    public Field getField(int i, int j) {
        return fields[i][j];
    }

    public Move getHint() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates @{@link Board} from @{@link EvaluationBoard} instance.
     * @param evaluationBoard
     * @return
     */
    public static Board fromEvaluationBoard(EvaluationBoard evaluationBoard) {
        Board board = new Board();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                int value = evaluationBoard.getField(i, j);
                if (value != -1) {
                    board.getField(i, j).setValue(value);
                }
            }
        }
        return board;
    }

    public List<Coordinate> getUnoccupiedFieldsCoordinates() {
        List<Coordinate> coordinates = new ArrayList<>();
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            for (int j = 0; j < Board.BOARD_SIZE; j++) {
                if (!fields[i][j].hasValue()) {
                    coordinates.add(new Coordinate(i, j));
                }
            }
        }
        return coordinates;
    }

    /**
     * Plays a move. Throws exception if the move is incorrect.
     * @param move Move to be played
     */
    public void play(Move move) {
        int row = move.getRow();
        int column = move.getColumn();
        int value = move.getNumber();

        fields[row][column].setValue(value);
    }

    public boolean isValid() {
        // TODO
        throw new UnsupportedOperationException();
    }
}
