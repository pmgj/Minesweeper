package rest;

import model.State;

public class PlayMessage {
    private int row;
    private int col;
    private State state;

    public int getRow() {
        return row;
    }

    public void setRow(int rows) {
        this.row = rows;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int cols) {
        this.col = cols;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
