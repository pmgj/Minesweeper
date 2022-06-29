package servlets;

import jakarta.json.bind.JsonbBuilder;
import model.Field;
import model.FieldValue;
import model.Move;
import model.State;

public class Message {
    private Field[][] board;
    private Move winner;
    private long bombs;

    public Message() {
        
    }
    
    public Message(Move win, Field[][] board, long bombs) {
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

    public Move getWinner() {
        return winner;
    }

    public void setWinner(Move winner) {
        this.winner = winner;
    }

    public long getBombs() {
        return bombs;
    }

    public void setBombs(long bombs) {
        this.bombs = bombs;
    }

    public static void main(String[] args) {
        var board = new Field[2][2];
        board[0][0] = new Field(FieldValue.VL_1, State.SHOW);
        board[0][1] = new Field(FieldValue.BOMB, State.SHOW);
        board[1][0] = new Field(FieldValue.NONE, State.SHOW);
        board[1][1] = new Field(FieldValue.VL_2, State.HIDE);
        Message m = new Message(Move.VALID, board, 1);
        System.out.println(JsonbBuilder.create().toJson(m));
    }
}
