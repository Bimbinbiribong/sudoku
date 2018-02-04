package sudoku;

import sudoku.fastObjects.EvaluationBoard;
import sudoku.objects.Board;
import sudoku.objects.Field;
import sudoku.objects.Move;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
    boolean hasEnded;

    private Sudoku() {
        playedMoves = new ArrayList<>();
    }

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

    public static Sudoku loadLastGame() {
        Sudoku sudoku = new Sudoku();

        // TODO: sudoku.board = Board.load();

        return sudoku;
    }

    public Board getBoard() {
        return board;
    }

    public List<Move> getPlayedMoves() {
        return playedMoves;
    }

    /**
     * Plays specified move refreshing the board.
     * @param move
     */
    public void play(Move move) {
        board.play(move);
        playedMoves.add(move);
    }

    public void playHint(Move move) {
        board.play(move);
        // set field to the new value
        board.getField(move.getRow(), move.getColumn()).setValue(move.getNumber());

        // remove moves user played on this particular field
        playedMoves.removeIf(x -> x.getRow() == move.getRow() && x.getColumn() == move.getColumn());
    }


    /**
     * Plays one move back, returning coordinate that was reset.
     * @return Coordinate that was reset.
     */
    public Coordinate back() {
        Move lastMove = playedMoves.get(playedMoves.size() - 1);

        int rowIndex = lastMove.getRow();
        int columnIndex = lastMove.getColumn();

        board.getField(rowIndex, columnIndex).resetValue();

        return new Coordinate(rowIndex, columnIndex);
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
}
