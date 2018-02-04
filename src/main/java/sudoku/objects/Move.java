package sudoku.objects;

import sudoku.Coordinate;

/**
 * Created by honza on 1.2.18.
 */

/**
 * Represents one move of the game.
 */
public class Move {
    private int row;
    private int column;
    private int number;

    public Move(int row, int column, int number) {
        this.row = row;
        this.column = column;
        this.number = number;
    }

    public Move(Coordinate coordinate, int number) {
        this.row = coordinate.getRow();
        this.column = coordinate.getColumn();
        this.number = number;
    }

    /**
     * Obtains row index of the field, where the player moved.
     * @return
     */
    public int getRow() {
        return row;
    }

    /**
     * Obtains column index of the field, where the player moved.
     * @return
     */
    public int getColumn() {
        return column;
    }

    /**
     * Obtains number, which player wrote to the field.
     * @return
     */
    public int getNumber() {
        return number;
    }
}
