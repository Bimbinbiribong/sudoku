package sudoku.fastObjects;

import sudoku.Coordinate;
import sudoku.IntegerRef;
import sudoku.objects.Board;
import sudoku.objects.Move;

import java.util.*;

/**
 * Created by honza on 2.2.18.
 */

/**
 * Represents board in compressed and evaluation mode => better for calculating,
 * is minified so copying is faster etc.
 */
public class EvaluationBoard implements Cloneable {
    /**
     * Represents field value on the board.
     * (i,j) == -1 => there is no value (empty cell)
     */
    private int[][] fields;

    /**
     * Represents numbers that cannot be used on (i, j) coordinate (0-9)
     * e.g. on coordinates [i][j][4] = true means that in the (i,j) coordinate number 5 cannot be used
     */
    private boolean[][][] unavailableNumbers;

    /**
     * Represents list of options for each (i,j) cell. For each there is a permutation of 1-10 numbers.
     * This represents a randomness in Sudoku board generation.
     */
    private List<List<List<Integer>>> options;

    private List<Move> solution;

    private EvaluationBoard(int[][] fields, boolean[][][] unavailableNumbers, List<List<List<Integer>>> options) {
        this.fields = fields;
        this.unavailableNumbers = unavailableNumbers;
        this.options = options;
    }

    private EvaluationBoard(int boardSize) {
        initialize(boardSize);
    }

    private void initialize(int boardSize) {
        // initialize fields
        fields = new int[boardSize][];
        for (int i = 0; i < boardSize; i++) {
            fields[i] = new int[boardSize];
        }

        // initialize unavailableNumbers
        unavailableNumbers = new boolean[boardSize][][];
        for (int i = 0; i < boardSize; i++) {
            unavailableNumbers[i] = new boolean[boardSize][];
            for (int j = 0; j < boardSize; j++) {
                unavailableNumbers[i][j] = new boolean[boardSize];
            }
        }

        // initialize options
        List<List<List<Integer>>> rows = new ArrayList<>();
        for (int j = 0; j < boardSize; j++) {
            List<List<Integer>> columns = new ArrayList<>();

            for (int k = 0; k < boardSize; k++) {
                List<Integer> cell = new ArrayList<>();
                for (int l = 1; l <= boardSize; l++) {
                    cell.add(l);
                }
                Collections.shuffle(cell);

                columns.add(cell);
            }
            rows.add(columns);
        }

        options = rows;
    }

    /**
     * Generates valid game of Sudoku.
     * @return Evaluation board representing game of Sudoku.
     */
    public static EvaluationBoard generateNew() {
        EvaluationBoard board = new EvaluationBoard(Board.BOARD_SIZE);
        List<EvaluationBoard> results = new ArrayList<>();
        try {
            board.generate(0, 0, results);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }

        // sudoku board with everything filled
        EvaluationBoard sudokuBoard = results.get(0);

        return sudokuBoard;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        int[][] newFields = new int[fields.length][];
        {
            for (int r = 0; r < fields.length; r++) {
                newFields[r] = fields[r].clone();
            }
        }
        boolean[][][] newOccupiedNumbers = new boolean[unavailableNumbers.length][][];
        {
            for (int i = 0; i < unavailableNumbers.length; i++) {
                newOccupiedNumbers[i] = new boolean[unavailableNumbers.length][];
                for (int j = 0; j < unavailableNumbers.length; j++) {
                    newOccupiedNumbers[i][j] = Arrays.copyOf(unavailableNumbers[i][j], unavailableNumbers[i][j].length);
                }
            }
        }
        return new EvaluationBoard(newFields, newOccupiedNumbers, options);
    }

    /**
     * Generates valid Sudoku games returning result in the results list using backtracking.
     * @param rowIndex
     * @param columnIndex
     * @param results
     * @throws CloneNotSupportedException
     */
    private void generate(int rowIndex, int columnIndex, List<EvaluationBoard> results) throws CloneNotSupportedException {
        if (results.size() >= 1) {
            return;
        }

        // strategy = for each field recursively find a correct sudoku solution
        // if you find it => jump out of the function
        for (int k = 0; k < unavailableNumbers[rowIndex][columnIndex].length; k++) {
            int value = options.get(rowIndex).get(columnIndex).get(k);
            if (!unavailableNumbers[rowIndex][columnIndex][value - 1]) {
                EvaluationBoard newBoard = (EvaluationBoard)this.clone();
                newBoard.setField(rowIndex, columnIndex, value);

                if (columnIndex + 1 >= Board.BOARD_SIZE) {
                    // rowIndex will surely increase
                    if (rowIndex + 1 >= Board.BOARD_SIZE) {
                        results.add(newBoard);
                        return;
                    }
                    newBoard.generate(rowIndex + 1, 0, results);
                }
                else {
                    newBoard.generate(rowIndex, columnIndex + 1, results);
                }
            }
        }
    }

    /**
     * Resets fields such way so the board has unique solution (only one solution). Is computationally expensive.
     * @param numberOfFieldsToReset Number of fields that the algorithm will reset.
     */
    public EvaluationBoard resetFields(int numberOfFieldsToReset) {
        EvaluationBoard resetBoard = null;
        try {
            resetBoard = (EvaluationBoard)this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
        Random random = new Random();
        // strategy = reset random field, check if it still has unique solution, repeat at most numberOfFieldsToReset times
        for (int i = 0; i < numberOfFieldsToReset; i++) {
            for (int j = 0; ; j++) {
                int row = random.nextInt(9);
                int column = random.nextInt(9);

                int fieldValue = resetBoard.fields[row][column];

                // this field has already been reset => continue
                if (fieldValue < 0) {
                    continue;
                }
                // throwing errors
                resetBoard.resetField(row, column);

                // doesn't have unique solution => revert it back
                try {
                    if (resetBoard.hasUniqueSolution()) {
                        break;
                    }
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                    return resetBoard;
                }

                // doesn't have unique solution => revert it back
                resetBoard.setField(row, column, fieldValue);

                // algorithm has tried enough times => use what you have
                if (j >= numberOfFieldsToReset * 5) {
                    return resetBoard;
                }
            }
        }
        return resetBoard;
    }

    private boolean isEmpty(int rowIndex, int columnIndex) {
        return fields[rowIndex][columnIndex] == -1;
    }

    /**
     * Reports whether this board has (one) unique solution. Is computationaly expensive.
     * @return
     * @throws CloneNotSupportedException
     */
    public boolean hasUniqueSolution() throws CloneNotSupportedException {
        List<Coordinate> unoccupiedFieldsCoordinates = getUnoccupiedFieldsCoordinates();

        if (unoccupiedFieldsCoordinates.size() < 1) {
            return false;
        }

        return hasUniqueSolution(unoccupiedFieldsCoordinates, 0, new IntegerRef());
    }

    private boolean hasUniqueSolution(List<Coordinate> unoccupiedFieldsCoordinates, int currentIndex, IntegerRef solutionCount) throws CloneNotSupportedException {
        // if algorithm tried all possibilities
        if (currentIndex >= unoccupiedFieldsCoordinates.size()) {
            solutionCount.increment();

            if (solutionCount.getValue() > 1) {
                return false;
            }

            return true;
        }

        Coordinate current = unoccupiedFieldsCoordinates.get(currentIndex);

        int row = current.getRow();
        int column = current.getColumn();
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            if (!unavailableNumbers[row][column][i]) {
                int value = i + 1;

                EvaluationBoard newBoard = (EvaluationBoard)this.clone();
                newBoard.setField(row, column, value);

                boolean hasUniqueSolution = newBoard.hasUniqueSolution(unoccupiedFieldsCoordinates, currentIndex + 1, solutionCount);
                if (!hasUniqueSolution) {
                    return false;
                }
            }
        }

        return true;
    }

    private List<Coordinate> getUnoccupiedFieldsCoordinates() {
        List<Coordinate> coordinates = new ArrayList<>();
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            for (int j = 0; j < Board.BOARD_SIZE; j++) {
                if (fields[i][j] == -1) {
                    coordinates.add(new Coordinate(i, j));
                }
            }
        }
        return coordinates;
    }

    /**
     * Sets field to the new value, refreshing @unavailableNumbers.
     * @param rowIndex
     * @param columnIndex
     * @param value
     */
    private void setField(int rowIndex, int columnIndex, int value) {
        fields[rowIndex][columnIndex] = value;

        int indexFromValue = value - 1;

        // update row
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            unavailableNumbers[rowIndex][i][indexFromValue] = true;
        }

        // update column
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            unavailableNumbers[i][columnIndex][indexFromValue] = true;
        }

        Coordinate coordinate = getAreaFirstCoordinate(rowIndex, columnIndex);

        int firstRowIndex = coordinate.getRow();
        int firstColumnIndex = coordinate.getColumn();

        for (int i = firstRowIndex; i < firstRowIndex + Board.AREA_SIZE; i++) {
            for (int j = firstColumnIndex; j < firstColumnIndex + Board.AREA_SIZE; j++) {
                // if it is a different cell and there is same number in that same cell => cell is not correct after move
                unavailableNumbers[i][j][indexFromValue] = true;
            }
        }
    }

    /**
     * Resets field, refreshing @unavailableNumbers.
     * @param rowIndex
     * @param columnIndex
     */
    private void resetField(int rowIndex, int columnIndex) {
        int previousValueIndex = fields[rowIndex][columnIndex] - 1;

        fields[rowIndex][columnIndex] = -1;

        // update row
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            unavailableNumbers[rowIndex][i][previousValueIndex] = false;
        }

        // update column
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            unavailableNumbers[i][columnIndex][previousValueIndex] = false;
        }

        Coordinate coordinate = getAreaFirstCoordinate(rowIndex, columnIndex);

        int firstRowIndex = coordinate.getRow();
        int firstColumnIndex = coordinate.getColumn();

        for (int i = firstRowIndex; i < firstRowIndex + Board.AREA_SIZE; i++) {
            for (int j = firstColumnIndex; j < firstColumnIndex + Board.AREA_SIZE; j++) {
                // if it is a different cell and there is same number in that same cell => cell is not correct after move
                unavailableNumbers[i][j][previousValueIndex] = false;
            }
        }
    }

    /**
     * Obtains area first coordinate (left-upper most coordinate) from the field coordinates (@rowIndex and @columnIndex).
     * @param rowIndex Row index of the field whose area we want to identify.
     * @param columnIndex Column index of the field whose area we want to identify.
     * @return Left-upper most coordinate of the area containing the field.
     */
    private Coordinate getAreaFirstCoordinate(int rowIndex, int columnIndex) {
        // update cell
        // initialize to get first row and column index of the cell so we can iterate through it
        int firstRowIndex;
        int firstColumnIndex;
        if (rowIndex < Board.AREA_SIZE && columnIndex < Board.AREA_SIZE) {
            firstRowIndex = 0;
            firstColumnIndex = 0;
        }
        else if (rowIndex < Board.AREA_SIZE && columnIndex >= Board.AREA_SIZE && columnIndex < 2 * Board.AREA_SIZE) {
            firstRowIndex = 0;
            firstColumnIndex = Board.AREA_SIZE;
        }
        else if (rowIndex < Board.AREA_SIZE && columnIndex >= 2 * Board.AREA_SIZE) {
            firstRowIndex = 0;
            firstColumnIndex = 2 * Board.AREA_SIZE;
        }
        else if ((rowIndex >= Board.AREA_SIZE && rowIndex < 2 * Board.AREA_SIZE) && columnIndex < Board.AREA_SIZE) {
            firstRowIndex = Board.AREA_SIZE;
            firstColumnIndex = 0;
        }
        else if (rowIndex >= Board.AREA_SIZE && rowIndex < 2 * Board.AREA_SIZE && columnIndex >= Board.AREA_SIZE && columnIndex < 2 * Board.AREA_SIZE) {
            firstRowIndex = Board.AREA_SIZE;
            firstColumnIndex = Board.AREA_SIZE;
        }
        else if (rowIndex >= Board.AREA_SIZE && rowIndex < 2 * Board.AREA_SIZE && columnIndex >= 2 * Board.AREA_SIZE) {
            firstRowIndex = Board.AREA_SIZE;
            firstColumnIndex = 2 * Board.AREA_SIZE;
        }
        else if (rowIndex >= 2 * Board.AREA_SIZE && columnIndex < Board.AREA_SIZE) {
            firstRowIndex = 2 * Board.AREA_SIZE;
            firstColumnIndex = 0;
        }
        else if (rowIndex >= 2 * Board.AREA_SIZE && columnIndex >= Board.AREA_SIZE && columnIndex < 2 * Board.AREA_SIZE) {
            firstRowIndex = 2 * Board.AREA_SIZE;
            firstColumnIndex = Board.AREA_SIZE;
        }
        else {
            firstRowIndex = 2 * Board.AREA_SIZE;
            firstColumnIndex = 2 * Board.AREA_SIZE;
        }

        return new Coordinate(firstRowIndex, firstColumnIndex);
    }

    /**
     * Obtains value of the field.
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    public int getField(int rowIndex, int columnIndex) {
        return fields[rowIndex][columnIndex];
    }
}
