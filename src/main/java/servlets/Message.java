package servlets;

import model.Field;
import model.Winner;

public class Message {
    private Field[][] board;
    private Winner winner;
    private long bombs;

    public Message() {
        
    }
    
    public Message(Winner win, Field[][] board, long bombs) {
        this.winner = win;
        this.board = board;
        this.bombs = bombs;
    }
    
    public Field[][] getBoard() {
        return board;
    }

    public void setBoard(Field[][] board) {
        this.board = board;
    }

    public Winner getWinner() {
        return winner;
    }

    public void setWinner(Winner winner) {
        this.winner = winner;
    }

    public long getBombs() {
        return bombs;
    }

    public void setBombs(long bombs) {
        this.bombs = bombs;
    }
}
