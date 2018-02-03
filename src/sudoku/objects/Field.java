package sudoku.objects;

/**
 * Created by honza on 1.2.18.
 */
public class Field {
    private int value;

    public Field(int value) {
        if (value == -1) {
            value = -1;
        }
        else {
            setValue(-1);
        }
    }

    public Field() {
        value = -1;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if (value > 9 || value < 0) {
            throw new IllegalArgumentException("Value must be between 0-9 (valid digit).");
        }

        this.value = value;
    }

    public boolean hasValue() {
        return value != -1;
    }

    public void resetValue() {
        value = -1;
    }

    @Override
    public String toString() {
        if (value == -1) {
            return "";
        }

        return Integer.toString(value);
    }
}
