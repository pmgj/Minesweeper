package model.difficulty;

public class Difficulty {
    private final String text;
    protected int rows;
    protected int cols;
    protected int bombs;
    
    public Difficulty(String value) {
        this.text = value;
    }

    public String getText() {
        return text;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getBombs() {
        return bombs;
    }
    
    @Override
    public String toString() {
        return text;
    }
}
