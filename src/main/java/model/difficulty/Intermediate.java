package model.difficulty;

public class Intermediate extends Difficulty {
    public Intermediate() {
        super("Intermediate (16x16, 40 bombs)");
        this.rows = 16;
        this.cols = 16;
        this.bombs = 40;
    }
}
