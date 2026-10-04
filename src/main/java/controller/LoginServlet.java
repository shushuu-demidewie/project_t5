package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.UserDAO;
import model.User;

/**
 * Servlet điều hướng và xử lý đăng nhập hệ thống Web POS.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Nếu người dùng đã đăng nhập sẵn trong Session -> chuyển thẳng sang POS
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            response.sendRedirect(request.getContextPath() + "/pos");
            return;
        }

        // Chuyển tiếp tới giao diện đăng nhập login.jsp
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMsg", "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        User user = userDAO.checkLogin(username.trim(), password.trim());
        if (user != null) {
            // Đăng nhập thành công -> Lưu người dùng vào Session
            HttpSession session = request.getSession(true);
            session.setAttribute("currentUser", user);

            // Chuyển hướng tới trang POS bán hàng
            response.sendRedirect(request.getContextPath() + "/pos");
        } else {
            // Đăng nhập thất bại -> Trả về thông báo lỗi
            request.setAttribute("username", username);
            request.setAttribute("errorMsg", "Tên đăng nhập hoặc mật khẩu không chính xác!");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
