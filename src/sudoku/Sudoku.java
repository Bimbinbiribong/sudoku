package sudoku;

import sudoku.fastObjects.EvaluationBoard;
import sudoku.objects.Board;
import sudoku.objects.Field;
import sudoku.objects.Move;

import java.util.*;

/**
 * Created by honza on 2.2.18.
 */

/**
 * Represents one game of Sudoku.
 */
public class Sudoku {
    private Board board;
    private Board solutionBoard;
    private List<Move> playedMoves;

    private Sudoku() {
        playedMoves = new ArrayList<>();
    }

    /**
     * Generates new game of Sudoku with specified difficulty.
     * @param difficulty Difficulty of the game.
     * @return New game of Sudoku.
     */
    public static Sudoku generateNewGame(Difficulty difficulty) {
        Sudoku sudoku = new Sudoku();

        // generate new board
        EvaluationBoard evaluationSolutionBoard = EvaluationBoard.generateNew();

        // reset fields according to difficulty
        EvaluationBoard evaluationBoard;
        switch (difficulty) {
            case EASY:
                evaluationBoard = evaluationSolutionBoard.resetFields(15);
                break;
            case MEDIUM:
                evaluationBoard = evaluationSolutionBoard.resetFields(30);
                break;
            case HARD:
                evaluationBoard = evaluationSolutionBoard.resetFields(40);
                break;
            default:
                throw new IllegalArgumentException("Difficulty passed as parameter does not exist.");
        }

        sudoku.solutionBoard = Board.fromEvaluationBoard(evaluationSolutionBoard);

        sudoku.board = Board.fromEvaluationBoard(evaluationBoard);

        return sudoku;
    }

    /**
     * Returns the board.
     * @return Board of this sudoku game.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Reports whether user wrote this field value. True, if he did, false if it was pre-generated or wrote by hint.
     * @param row
     * @param column
     * @return
     */
    public boolean didUserWriteThisFieldValue(int row, int column) {
        return playedMoves.stream().anyMatch(x -> x.getRow() == row && x.getColumn() == column);
    }

    /**
     * Plays specified move refreshing the board.
     * @param move
     */
    public void play(Move move) {
        board.play(move);
        playedMoves.add(move);
    }

    /**
     * Plays a hint move, refreshing the board.
     * @param move
     */
    public void playHint(Move move) {
        board.play(move);
        // set field to the new value
        board.getField(move.getRow(), move.getColumn()).setValue(move.getNumber());

        // remove moves user played on this particular field
        playedMoves.removeIf(x -> x.getRow() == move.getRow() && x.getColumn() == move.getColumn());
    }


    /**
     * Plays one move back (resets last move), returning coordinate that was reset.
     * @return Coordinate that was reset.
     */
    public Coordinate back() {
        if (playedMoves.size() == 0) {
            return null;
        }

        Move lastMove = playedMoves.get(playedMoves.size() - 1);

        int rowIndex = lastMove.getRow();
        int columnIndex = lastMove.getColumn();

        // get move such that it was on the same coordinates as @lastMove but is not same
        Move previousDifferentMove = getPreviousDifferentMove(lastMove);

        // if exists previous different move => new number there will be from it
        if (previousDifferentMove != null) {
            board.getField(rowIndex, columnIndex).setValue(previousDifferentMove.getNumber());
        }
        // otherwise just reset it
        else {
            board.getField(rowIndex, columnIndex).resetValue();
        }
        // remove the last move
        playedMoves.remove(lastMove);

        return new Coordinate(rowIndex, columnIndex);
    }

    /**
     * Obtains move that occurred before move with @Move values, but had different @value.
     * @return
     */
    private Move getPreviousDifferentMove(Move move) {
        int row = move.getRow();
        int column = move.getColumn();
        int value = move.getNumber();

        List<Move> moves = new ArrayList<>();
        for (Move item : playedMoves) {
            if (item.getRow() == row && item.getColumn() == column) {
                moves.add(item);
            }
        }

        for (int i = moves.size() - 2; i >= 0; i--) {
            Move item = moves.get(i);
            if (item.getNumber() != value) {
                return item;
            }
        }

        return null;
    }

    /**
     * Returns move that player should have played (and played wrongly) or move that player should play.
     * @return
     */
    public Move getHint() {
        // check for errors
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            for (int j = 0; j < Board.BOARD_SIZE; j++) {
                Field field = board.getField(i, j);
                Field solutionField = solutionBoard.getField(i, j);

                if (field.hasValue()) {
                    int userSelectedValue = field.getValue();
                    int correctValue = solutionField.getValue();

                    if (userSelectedValue != correctValue) {
                        return new Move(i, j, correctValue);
                    }
                }
            }
        }

        // get what player can play next
        List<Coordinate> unoccupiedFields = board.getUnoccupiedFieldsCoordinates();

        if (unoccupiedFields.size() == 0) {
            return null;
        }
        // randomly select which hint to give
        Random random = new Random();
        int hintCoordinateListIndex = random.nextInt(unoccupiedFields.size());

        // get the coordinates
        Coordinate coordinate = unoccupiedFields.get(hintCoordinateListIndex);

        return new Move(coordinate, solutionBoard.getField(coordinate.getRow(), coordinate.getColumn()).getValue());
    }

    /**
     * Reports whether the game was successfully finished.
     * @return True, if game is won, false otherwise.
     */
    public boolean isFinished() {
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            for (int j = 0; j < Board.BOARD_SIZE; j++) {
                Field field = board.getField(i, j);

                // if board field doesn't have value => surely didn't ended (need to fill that field)
                if (!field.hasValue()) {
                    return false;
                }

                Field solutionField = solutionBoard.getField(i, j);

                // if the values are not equal => user didn't find correct solution => he has to fix it
                if (solutionField.getValue() != field.getValue()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Reports whether the game board is filled.
     * @return True, if there are all fields filled, false otherwise.
     */
    public boolean isBoardFilled() {
        return board.isFilled();
    }
}
