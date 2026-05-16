package ru.samsung.minesweeper;

public enum Cell {
    EMPTY(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    MINE(9);

    private int value;

    Cell(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Cell fromInt(int value) {
        switch(value) {
            case 0: return EMPTY;
            case 1: return ONE;
            case 2: return TWO;
            case 3: return THREE;
            case 4: return FOUR;
            case 5: return FIVE;
            case 6: return SIX;
            case 7: return SEVEN;
            case 8: return EIGHT;
            default: return EMPTY;
        }
    }
}
