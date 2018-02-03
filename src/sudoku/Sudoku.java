package sudoku;

import sudoku.fastObjects.EvaluationBoard;
import sudoku.objects.Board;
import sudoku.objects.Field;
import sudoku.objects.Move;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by honza on 2.2.18.
 */
public class Sudoku {
    private Board board;
    private Board solutionBoard;
    private List<Move> playedMoves;

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

    /**
     * Plays specified move refreshing the board.
     * @param move
     */
    public void play(Move move) {
        board.play(move);
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
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            for (int j = 0; j < Board.BOARD_SIZE; j++) {
                Field field = board.getField(i, j);

                if (!field.hasValue()) {
                    Field solutionField = solutionBoard.getField(i, j);

                    return new Move(i, j, solutionField.getValue());
                }
            }
        }

        return null;
    }
}
