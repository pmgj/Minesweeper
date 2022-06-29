package model.difficulty;

public class Beginner extends Difficulty {
    public Beginner() {
        super("Beginner (9x9, 10 bombs)");
        this.rows = 9;
        this.cols = 9;
        this.bombs = 10;
    }
}
