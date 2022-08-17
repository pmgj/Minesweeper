package model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Field implements PropertyChangeListener {

    private FieldValue value;
    private State state;
    private final PropertyChangeSupport squareObservers = new PropertyChangeSupport(this);
    private final PropertyChangeSupport fieldObservers = new PropertyChangeSupport(this);

    public Field(FieldValue value, State state) {
        this.value = value;
        this.state = state;
    }

    public void addSquareObservers(PropertyChangeListener pcl) {
        squareObservers.addPropertyChangeListener(pcl);
    }

    public void addFieldObservers(PropertyChangeListener pcl) {
        fieldObservers.addPropertyChangeListener(pcl);
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

    public void play(State newState) {
        switch (newState) {
            case FLAG:
                this.setState(this.state == State.HIDE ? State.FLAG : State.HIDE);
                break;
            case SHOW:
                this.setState(newState);
            default:
                break;
        }
    }

    public void setState(State newState) {
        if(newState == State.SHOW && this.state == State.FLAG) {
            return;
        }
        State oldState = this.state;
        this.state = newState;
        if (newState == State.SHOW && this.value == FieldValue.NONE) {
            fieldObservers.firePropertyChange("state", oldState, newState);
        }
        squareObservers.firePropertyChange("state", oldState, newState);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        this.setState(State.SHOW);
    }

    @Override
    public String toString() {
        return String.format("(%d, %s)", value, state);
    }
}
