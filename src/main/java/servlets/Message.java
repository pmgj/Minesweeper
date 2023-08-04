package servlets;

import model.Field;
import model.Winner;

public record Message(Winner winner, Field[][] board, long bombs) {

}