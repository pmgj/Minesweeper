package servlets;

import java.io.IOException;

import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Minesweeper;
import model.Winner;
import model.State;

@MultipartConfig
@WebServlet(name = "CheckMinesweeperServlet", urlPatterns = {"/CheckMinesweeperServlet"})
public class CheckMinesweeperServlet extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String linha = request.getParameter("row");
        String coluna = request.getParameter("col");
        String state = request.getParameter("state");
        int row = Integer.parseInt(linha);
        int col = Integer.parseInt(coluna);
        HttpSession session = request.getSession();
        Minesweeper mine = (Minesweeper) session.getAttribute("campo");        
        Winner mr = mine.play(row, col, state.equals("SHOW") ? State.SHOW : State.FLAG);
        Message m = new Message(mr, mine.getHiddenMatrix(), mine.getRemainingBombs());
        response.addHeader("Content-Type", "application/json");
        response.getWriter().print(JsonbBuilder.create().toJson(m));
    }
}
