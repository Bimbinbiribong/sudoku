package sudoku.objects;

import sudoku.fastObjects.EvaluationBoard;

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

    /**
     * Plays a move. Throws exception if the move is incorrect.
     * @param move Move to be played
     */
    public void play(Move move) {
        int row = move.getRow();
        int column = move.getColumn();
        int value = move.getNumber();

        // check cell
        if (fields[row][column].hasValue()) {
            throw new IllegalArgumentException("Cannot play to this coordinates, field is already occupied.");
        }

        // check row
        for (int i = 0; i < fields[row].length; i++) {
            if (i != column && fields[row][i].hasValue()) {
                int val = fields[row][i].getValue();

                if (val == value) {
                    throw new IllegalArgumentException("Row is already occupied by this number");
                }
            }
        }

        // check column
        for (int i = 0; i < fields[i].length; i++) {
            if (i != row && fields[i][column].hasValue()) {
                int val = fields[i][column].getValue();

                if (val == value) {
                    throw new IllegalArgumentException("Column is already occupied by this number");
                }
            }
        }

        // check cell
        if (!areCellsValidAfterMove(move)) {
            throw new IllegalArgumentException("Cell rule has been broken by this move.");
        }

        fields[row][column].setValue(value);
    }

    /**
     * Verifies whether after the move cells are still valid.
     * @param move
     * @return
     */
    private boolean areCellsValidAfterMove(Move move) {
        int row = move.getRow();
        int column = move.getColumn();

        // initialize to get first row and column index of the cell so we can iterate through it
        int firstRowIndex;
        int firstColumnIndex;
        if (row < AREA_SIZE && column < AREA_SIZE) {
            firstRowIndex = 0;
            firstColumnIndex = 0;
        }
        else if (row < AREA_SIZE && column >= AREA_SIZE && column < 2 * AREA_SIZE) {
            firstRowIndex = 0;
            firstColumnIndex = AREA_SIZE;
        }
        else if (row < AREA_SIZE && column >= 2 * AREA_SIZE) {
            firstRowIndex = 0;
            firstColumnIndex = 2 * AREA_SIZE;
        }
        else if ((row >= AREA_SIZE && row < 2 * AREA_SIZE) && column < AREA_SIZE) {
            firstRowIndex = AREA_SIZE;
            firstColumnIndex = 0;
        }
        else if (row >= AREA_SIZE && row < 2 * AREA_SIZE && column >= AREA_SIZE && column < 2 * AREA_SIZE) {
            firstRowIndex = AREA_SIZE;
            firstColumnIndex = AREA_SIZE;
        }
        else if (row >= AREA_SIZE && row < 2 * AREA_SIZE && column >= 2 * AREA_SIZE) {
            firstRowIndex = AREA_SIZE;
            firstColumnIndex = 2 * AREA_SIZE;
        }
        else if (row >= 2 * AREA_SIZE && column < AREA_SIZE) {
            firstRowIndex = 2 * AREA_SIZE;
            firstColumnIndex = 0;
        }
        else if (row >= 2 * AREA_SIZE && column >= AREA_SIZE && column < 2 * AREA_SIZE) {
            firstRowIndex = 2 * AREA_SIZE;
            firstColumnIndex = AREA_SIZE;
        }
        else {
            firstRowIndex = 2 * AREA_SIZE;
            firstColumnIndex = 2 * AREA_SIZE;
        }

        for (int i = firstRowIndex; i < firstRowIndex + AREA_SIZE; i++) {
            for (int j = firstColumnIndex; j < firstColumnIndex + AREA_SIZE; j++) {
                // if it is a different cell and there is same number in that same cell => cell is not correct after move
                if ((row != i|| column != j) && fields[i][j].hasValue()
                        && fields[i][j].getValue() == move.getNumber()) {
                    return false;
                }
            }
        }

        return true;
    }
}
