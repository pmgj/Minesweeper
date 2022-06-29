package view;

import javax.swing.JButton;
import model.FieldValue;
import model.State;

public class Square extends JButton {
    
    private FieldValue value;
    private State state;

    public Square() {
        this.value = FieldValue.NONE;
        this.state = State.HIDE;
    }
    
    public FieldValue getValue() {
        return value;
    }

    public void setValue(FieldValue value) {
        this.value = value;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", value, state);
    }
}
