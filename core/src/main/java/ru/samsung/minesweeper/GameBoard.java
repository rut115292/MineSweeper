package ru.samsung.minesweeper;

import java.util.Random;

public class GameBoard {
    public static final int SIZE = 8;
    public static final int TOTAL_MINES = 10;

    private Cell[][] board;
    private boolean[][] revealed;
    private boolean[][] flags;
    private boolean gameOver;
    private boolean gameWin;
    private Random random;

    public GameBoard() {
        board = new Cell[SIZE][SIZE];
        revealed = new boolean[SIZE][SIZE];
        flags = new boolean[SIZE][SIZE];
        random = new Random();
        gameOver = false;
        gameWin = false;
        initBoard();
    }

    private void initBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = Cell.EMPTY;
                revealed[i][j] = false;
                flags[i][j] = false;
            }
        }
    }

    public void placeMines(int firstX, int firstY) {
        int minesPlaced = 0;
        while (minesPlaced < TOTAL_MINES) {
            int x = random.nextInt(SIZE);
            int y = random.nextInt(SIZE);
            if (board[x][y] != Cell.MINE && (x != firstX || y != firstY)) {
                board[x][y] = Cell.MINE;
                minesPlaced++;
            }
        }
        calculateNumbers();
    }

    private void calculateNumbers() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] != Cell.MINE) {
                    int count = countAdjacentMines(i, j);
                    board[i][j] = Cell.fromInt(count);
                }
            }
        }
    }

    private int countAdjacentMines(int x, int y) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newX = x + i;
                int newY = y + j;
                if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE) {
                    if (board[newX][newY] == Cell.MINE) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public boolean revealCell(int x, int y) {
        if (gameOver || gameWin) return false;
        if (flags[x][y]) return false;
        if (revealed[x][y]) return false;

        if (board[x][y] == Cell.MINE) {
            gameOver = true;
            return true;
        }

        revealEmptyCells(x, y);
        checkWin();
        return false;
    }

    private void revealEmptyCells(int x, int y) {
        if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) return;
        if (revealed[x][y]) return;
        if (flags[x][y]) return;

        revealed[x][y] = true;

        if (board[x][y] == Cell.EMPTY) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    revealEmptyCells(x + i, y + j);
                }
            }
        }
    }

    public void chordReveal(int x, int y) {
        if (!revealed[x][y]) return;
        if (gameOver || gameWin) return;

        int adjacentFlags = countAdjacentFlags(x, y);
        int number = board[x][y].getValue();

        if (adjacentFlags == number && number > 0) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int newX = x + i;
                    int newY = y + j;
                    if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE) {
                        if (!flags[newX][newY] && !revealed[newX][newY]) {
                            if (board[newX][newY] == Cell.MINE) {
                                gameOver = true;
                                return;
                            }
                            revealEmptyCells(newX, newY);
                        }
                    }
                }
            }
            checkWin();
        }
    }

    private int countAdjacentFlags(int x, int y) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newX = x + i;
                int newY = y + j;
                if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE) {
                    if (flags[newX][newY]) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private void checkWin() {
        int revealedCount = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (revealed[i][j]) {
                    revealedCount++;
                }
            }
        }
        if (revealedCount == SIZE * SIZE - TOTAL_MINES) {
            gameWin = true;
        }
    }

    public void toggleFlag(int x, int y) {
        if (gameOver || gameWin) return;
        if (revealed[x][y]) return;
        flags[x][y] = !flags[x][y];
    }

    public void reset() {
        initBoard();
        gameOver = false;
        gameWin = false;
    }

    public void revealAllMines() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == Cell.MINE) {
                    revealed[i][j] = true;
                }
            }
        }
    }

    public Cell getCell(int x, int y) {
        return board[x][y];
    }

    public boolean isRevealed(int x, int y) {
        return revealed[x][y];
    }

    public boolean isFlagged(int x, int y) {
        return flags[x][y];
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGameWin() {
        return gameWin;
    }

    public int getRemainingFlags() {
        int flagCount = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (flags[i][j]) {
                    flagCount++;
                }
            }
        }
        return TOTAL_MINES - flagCount;
    }
}
