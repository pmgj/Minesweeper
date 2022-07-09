package rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import model.Minesweeper;
import model.State;
import model.Winner;
import servlets.Message;

@Path("/minesweeper")
public class GenericResource {

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Message play(@Context HttpServletRequest request, PlayMessage msg) {
        HttpSession session = request.getSession();
        Minesweeper mine = (Minesweeper) session.getAttribute("campo");
        Winner mr = mine.play(msg.getRow(), msg.getCol(), msg.getState() == State.SHOW ? State.SHOW : State.FLAG);
        return new Message(mr, mine.getHiddenMatrix(), mine.getRemainingBombs());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Message createMinesweeper(@Context HttpServletRequest request, CreateMessage msg) {
        Minesweeper mine = new Minesweeper(msg.getRows(), msg.getCols(), msg.getBombs());
        HttpSession session = request.getSession();
        session.setAttribute("campo", mine);
        return new Message(Winner.NONE, mine.getHiddenMatrix(), mine.getRemainingBombs());
    }
}
