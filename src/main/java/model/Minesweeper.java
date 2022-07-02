package model;

import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class Minesweeper {

    private final Field[][] matrix;
    private final int rows;
    private final int cols;
    private final int bombs;
    private Winner lastMove;

    public Minesweeper(int numrows, int numcols, int numbombs) {
        this.rows = numrows;
        this.cols = numcols;
        this.bombs = numbombs;
        this.matrix = new Field[numrows][numcols];
        this.fillBombs();
    }

    public void addObserver(PropertyChangeListener p, int row, int col) {
        this.matrix[row][col].addPropertyChangeListener(p);
    }

    public long getNumOfFlags() {
        return this.bombs - Arrays.stream(this.matrix).flatMap(x -> Arrays.stream(x))
                .filter(x -> x.getState() == State.FLAG).count();
    }

    private void fillBombs() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                matrix[i][j] = new Field(FieldValue.NONE, State.HIDE);
            }
        }
        for (int i = 0; i < this.bombs;) {
            int row = (int) Math.floor((Math.random() * this.rows));
            int col = (int) Math.floor((Math.random() * this.cols));
            if (matrix[row][col].getValue() == FieldValue.NONE) {
                matrix[row][col].setValue(FieldValue.BOMB);
                i++;
            }
        }
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                if (matrix[i][j].getValue() == FieldValue.NONE) {
                    matrix[i][j].setValue(FieldValue.values()[this.countPosition(i, j)]);
                }
            }
        }
    }

    private boolean onBoard(int x, int y) {
        BiFunction<Integer, Integer, Boolean> inLimit = (value, limit) -> value >= 0 && value < limit;
        return inLimit.apply(x, this.rows) && inLimit.apply(y, this.cols);
    }

    private int countPosition(int row, int col) {
        int count = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (onBoard(i, j) && this.matrix[i][j].getValue() == FieldValue.BOMB) {
                    count++;
                }
            }
        }
        return count;
    }

    private void openCell(int row, int col) {
        Field f = this.matrix[row][col];
        if (f.getValue() != FieldValue.BOMB) {
            State old = f.getState();
            f.setState(State.SHOW);
            if (f.getValue() == FieldValue.NONE && old == State.HIDE) {
                this.openCells(row, col);
            }
        }
    }

    private void openCells(int row, int col) {
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (onBoard(i, j)) {
                    this.openCell(i, j);
                }
            }
        }
    }

    public Winner play(int row, int col, State state) {
        if (this.lastMove == Winner.LOSE || this.lastMove == Winner.WIN) {
            return Winner.NONE;
        }
        Field f = this.matrix[row][col];
        Winner ret = Winner.NONE;
        switch (state) {
            case FLAG:
                f.setState(f.getState() == State.HIDE ? State.FLAG : State.HIDE);
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

    private Winner play(int row, int col) {
        Supplier<Winner> endOfGame = () -> Arrays.stream(this.matrix).flatMap(x -> Arrays.stream(x))
                .filter(x -> x.getState() != State.SHOW).count() == this.bombs ? Winner.WIN : Winner.NONE;
        Winner ret;
        Field field = this.matrix[row][col];
        switch (field.getValue()) {
            case BOMB:
                ret = Winner.LOSE;
                break;
            case NONE:
                this.openCell(row, col);
                ret = endOfGame.get();
                break;
            default:
                field.setState(State.SHOW);
                ret = endOfGame.get();
                break;
        }
        if (ret == Winner.WIN || ret == Winner.LOSE) {
            Arrays.stream(this.matrix).flatMap(x -> Arrays.stream(x)).filter(f -> f.getValue() == FieldValue.BOMB)
                    .forEach(f -> f.setState(State.SHOW));
        }
        this.lastMove = ret;
        return ret;
    }

    public Field[][] getHiddenMatrix() {
        Field[][] ret = new Field[rows][cols];
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                Field cell = new Field(this.matrix[i][j].getValue(), this.matrix[i][j].getState());
                ret[i][j] = cell;
                if (cell.getState() != State.SHOW) {
                    ret[i][j].setValue(FieldValue.NONE);
                }
            }
        }
        return ret;
    }

    public long getRemainingBombs() {
        return this.bombs - Stream.of(this.matrix).flatMap(Stream::of).filter(c -> c.getState() == State.FLAG).count();
    }
}
