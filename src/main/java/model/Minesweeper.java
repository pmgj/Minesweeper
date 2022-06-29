package model;

import java.beans.PropertyChangeListener;
import java.util.Arrays;

public final class Minesweeper {

    private final Field[][] matrix;
    private final int rows;
    private final int cols;
    private final int bombs;
    private Move lastMove;

    public Minesweeper(int numrows, int numcols, int numbombs) {
        this.rows = numrows;
        this.cols = numcols;
        this.bombs = numbombs;
        this.matrix = new Field[numrows][numcols];
        this.fillBombs();
        this.setNumbers();
    }

    public void addObserver(PropertyChangeListener p, int row, int col) {
        this.matrix[row][col].addPropertyChangeListener(p);
    }

    public long getNumOfFlags() {
        return this.bombs - Arrays.stream(matrix).flatMap(x -> Arrays.stream(x)).filter(x -> x.getState() == State.FLAG).count();
    }

    private void fillBombs() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = new Field(FieldValue.NONE, State.HIDE);
            }
        }
        for (int i = 0; i < bombs;) {
            int row = (int) Math.floor((Math.random() * rows));
            int col = (int) Math.floor((Math.random() * cols));
            if (matrix[row][col].getValue() == FieldValue.NONE) {
                matrix[row][col].setValue(FieldValue.BOMB);
                i++;
            }
        }
    }

    private int countPosition(int row, int col) {
        int count = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < rows && j >= 0 && j < cols && matrix[i][j].getValue() == FieldValue.BOMB) {
                    count++;
                }
            }
        }
        return count;
    }

    private void setNumbers() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j].getValue() == FieldValue.NONE) {
                    matrix[i][j].setValue(FieldValue.values()[countPosition(i, j)]);
                }
            }
        }
    }

    private Move endOfGame() {
        long blocked = Arrays.stream(matrix).flatMap(x -> Arrays.stream(x)).filter(x -> x.getState() != State.SHOW).count();
        if (blocked == this.bombs) {
            return Move.WIN;
        } else {
            return Move.VALID;
        }
    }

    private void openCell(int row, int col) {
        Field f = this.matrix[row][col];
        if (f.getValue() != FieldValue.BOMB) {
            if (f.getValue() == FieldValue.NONE && f.getState() == State.HIDE) {
                showCell(row, col);
                openCells(row, col);
            } else {
                showCell(row, col);
            }
        }
    }

    private void showCell(int row, int col) {
        Field f = this.matrix[row][col];
        f.setState(State.SHOW);
    }

    private void openCells(int row, int col) {
        int rowMax = this.rows, colMax = this.cols;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < rowMax && j >= 0 && j < colMax) {
                    openCell(i, j);
                }
            }
        }
    }

    private void showBombs() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                FieldValue value = this.matrix[i][j].getValue();
                if (value == FieldValue.BOMB) {
                    this.showCell(i, j);
                }
            }
        }
    }

    public Move play(int row, int col, State state) {
        if(this.lastMove == Move.LOSE || this.lastMove == Move.WIN) {
            return Move.VALID;
        }
        Field f = this.matrix[row][col];
        Move ret = Move.VALID;
        switch (state) {
            case FLAG:
                if (f.getState() == State.HIDE) {
                    f.setState(State.FLAG);
                } else {
                    f.setState(State.HIDE);
                }
                break;
            case SHOW:
                if (f.getState() == State.HIDE) {
                    ret = this.play(row, col);
                }
            default:
                break;
        }
        return ret;
    }

    private Move play(int row, int col) {
        Move ret;
        Field f = this.matrix[row][col];
        switch (f.getValue()) {
            case BOMB:
                ret = Move.LOSE;
                break;
            case NONE:
                this.openCell(row, col);
                ret = this.endOfGame();
                break;
            default:
                this.showCell(row, col);
                ret = this.endOfGame();
                break;
        }
        if (ret == Move.WIN || ret == Move.LOSE) {
            this.showBombs();
        }
        this.lastMove = ret;
        return ret;
    }
}
