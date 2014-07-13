package webapp.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import webapp.model.Formel0SinglePlayerGame;

public class Servlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public Servlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(true);

        String action = request.getParameter("action");
        if (action != null && action.equals("restart")) {
            Formel0SinglePlayerGame bean = new Formel0SinglePlayerGame();
            session.setAttribute("gameBean", bean);
        }
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/table.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(true);

        String action = request.getParameter("action");

        if (action != null && action.equals("dice")) {
            if (session.getAttribute("gameBean") == null) {
                Formel0SinglePlayerGame bean = new Formel0SinglePlayerGame();
                session.setAttribute("gameBean", bean);
            }

            Formel0SinglePlayerGame gb = (Formel0SinglePlayerGame) session.getAttribute("gameBean");
            gb.doRound();

            session.setAttribute("gameBean", gb);
        } else if (action != null && action.equals("restart")) {
            Formel0SinglePlayerGame bean = new Formel0SinglePlayerGame();
            session.setAttribute("gameBean", bean);
        }
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/table.jsp");
        dispatcher.forward(request, response);
    }
}
