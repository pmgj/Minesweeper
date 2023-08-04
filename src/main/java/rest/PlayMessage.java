package rest;

import model.State;

public record PlayMessage(int row, int col, State state) {

}