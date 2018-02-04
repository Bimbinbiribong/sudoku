package sudoku.objects;

/**
 * Created by honza on 1.2.18.
 */

/**
 * Represents one field of the board of the game.
 */
public class Field {
    /**
     * Value of the field.
     * -1 if the value is not present.
     */
    private int value;

    public Field(int value) {
        if (value == -1) {
            this.value = -1;
        }
        else {
            setValue(value);
        }
    }

    public Field() {
        value = -1;
    }

    /**
     * Obtains value of the field or throws an exception if it does not have a value.
     * @return
     */
    public int getValue() {
        if (value == -1) {
            throw new IllegalArgumentException("Field does not have a value.");
        }
        return value;
    }

    /**
     * Sets new value to the field
     * @param value
     */
    public void setValue(int value) {
        if (value > 9 || value < 1) {
            throw new IllegalArgumentException("Value must be between 0-9 (valid digit).");
        }

        this.value = value;
    }

    /**
     * Reports whether the field has a (correct) value.
     * @return
     */
    public boolean hasValue() {
        return value != -1;
    }

    /**
     * Resets value of the field.
     */
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
