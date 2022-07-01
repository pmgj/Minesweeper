package servlets;

import model.Minesweeper;
import model.Winner;

import java.io.IOException;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@MultipartConfig
@WebServlet(name = "CreateMinesweeperServlet", urlPatterns = {"/CreateMinesweeperServlet"})
public class CreateMinesweeperServlet extends HttpServlet {

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
}
