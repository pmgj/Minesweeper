package model.difficulty;

public class Advanced extends Difficulty {
    public Advanced() {
        super("Advanced (16x30, 99 bombs)");
        this.rows = 16;
        this.cols = 30;
        this.bombs = 99;
    }
}
