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
import model.State;
import model.Winner;

@MultipartConfig
@WebServlet(name = "MinesweeperServlet", urlPatterns = {"/MinesweeperServlet"})
public class MinesweeperServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nLinhas = request.getParameter("rows");
        String nColunas = request.getParameter("cols");
        String nBombas = request.getParameter("bombs");
        int rows = Integer.parseInt(nLinhas);
        int cols = Integer.parseInt(nColunas);
        int bombs = Integer.parseInt(nBombas);
        Minesweeper mine = new Minesweeper(rows, cols, bombs);
        HttpSession session = request.getSession();
        session.setAttribute("campo", mine);
        Message m = new Message(Winner.NONE, mine.getHiddenMatrix(), mine.getRemainingBombs());
        response.addHeader("Content-Type", "application/json");
        response.getWriter().print(JsonbBuilder.create().toJson(m));
    }

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
