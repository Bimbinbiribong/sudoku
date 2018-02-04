package sudoku;

/**
 * Created by honza on 3.2.18.
 */

/**
 * Represents difficulty of the game.
 */
public enum Difficulty {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard");

    private String value;

    Difficulty(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
