package sudoku.objects;

import sudoku.Coordinate;

/**
 * Created by honza on 1.2.18.
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

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getNumber() {
        return number;
    }
}
