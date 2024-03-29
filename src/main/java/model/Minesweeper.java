package model;

import java.beans.PropertyChangeListener;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.awt.Point;

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
        this.createMatrix();
    }

    public void addObserver(PropertyChangeListener p, int row, int col) {
        this.matrix[row][col].addSquareObservers(p);
    }

    private void createMatrix() {
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
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                var surroundingFields = new Point[] { new Point(i - 1, j - 1), new Point(i - 1, j),
                        new Point(i - 1, j + 1), new Point(i, j - 1), new Point(i, j + 1), new Point(i + 1, j - 1),
                        new Point(i + 1, j), new Point(i + 1, j + 1) };
                for (var p : surroundingFields) {
                    if (this.onBoard(p.x, p.y)) {
                        var field = matrix[p.x][p.y];
                        field.addFieldObservers(matrix[i][j]);
                    }
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

    public Winner play(int row, int col, State state) {
        if (this.lastMove == Winner.LOSE || this.lastMove == Winner.WIN) {
            return Winner.NONE;
        }
        Field field = this.matrix[row][col];
        Winner ret;
        Runnable showBombs = () -> Stream.of(this.matrix).flatMap(Stream::of)
                .filter(f -> f.getValue() == FieldValue.BOMB).forEach(f -> f.setState(State.SHOW));
        if (state == State.SHOW && field.getValue() == FieldValue.BOMB) {
            ret = Winner.LOSE;
            this.lastMove = ret;
            showBombs.run();
            return ret;
        }
        field.play(state);
        ret = Stream.of(this.matrix).flatMap(Stream::of)
                .filter(x -> x.getState() != State.SHOW).count() == this.bombs ? Winner.WIN : Winner.NONE;
        if(ret == Winner.WIN) {
            showBombs.run();
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
