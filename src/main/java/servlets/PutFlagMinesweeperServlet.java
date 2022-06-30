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
import model.Move;
import model.State;

@MultipartConfig
@WebServlet(name = "PutFlagMinesweeperServlet", urlPatterns = {"/PutFlagMinesweeperServlet"})
public class PutFlagMinesweeperServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String linha = request.getParameter("row");
        String coluna = request.getParameter("col");
        int row = Integer.parseInt(linha);
        int col = Integer.parseInt(coluna);
        HttpSession session = request.getSession();
        Minesweeper mine = (Minesweeper) session.getAttribute("campo");        
        Move mr = mine.play(row, col, State.FLAG);
        response.addHeader("Content-Type", "application/json");
        Message m = new Message(mr, mine.getHiddenMatrix(), mine.getRemainingBombs());
        response.getWriter().print(JsonbBuilder.create().toJson(m));
    }
}