package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Field {

    private FieldValue value;
    private State state;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public Field(FieldValue value, State state) {
        this.value = value;
        this.state = state;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
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

    public void setState(State newState) {
        State oldState = this.state;
        this.state = newState;
        support.firePropertyChange("state", oldState, newState);
    }

    @Override
    public String toString() {
        return String.format("(%d, %s)", value, state);
    }
}
