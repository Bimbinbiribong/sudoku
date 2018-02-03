package sudoku;

/**
 * Created by honza on 3.2.18.
 */

/**
 * Is non-immutable version of Integer.
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

    public void increment() {
        value++;
    }
}
