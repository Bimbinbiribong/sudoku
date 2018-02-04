package sudoku;

/**
 * Created by honza on 3.2.18.
 */

/**
 * Is mutable version of Integer.
 */
public class IntegerRef {
    private int value;

    public IntegerRef() {}

    public IntegerRef(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Increments value of the integer.
     */
    public void increment() {
        value++;
    }
}
