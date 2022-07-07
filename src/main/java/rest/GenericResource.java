package rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
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
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Message play(@Context HttpServletRequest request, @FormParam("row") int row, @FormParam("col") int col,
            @FormParam("state") String state) {
        System.out.println(row);
        HttpSession session = request.getSession();
        Minesweeper mine = (Minesweeper) session.getAttribute("campo");
        Winner mr = mine.play(row, col, state.equals("SHOW") ? State.SHOW : State.FLAG);
        return new Message(mr, mine.getHiddenMatrix(), mine.getRemainingBombs());
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Message createMinesweeper(@Context HttpServletRequest request, @FormParam("rows") int rows,
            @FormParam("cols") int cols, @FormParam("bombs") int bombs) {
        System.out.println(rows);
        Minesweeper mine = new Minesweeper(rows, cols, bombs);
        HttpSession session = request.getSession();
        session.setAttribute("campo", mine);
        return new Message(Winner.NONE, mine.getHiddenMatrix(), mine.getRemainingBombs());
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "Funciona";
    }
}
