package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.CategoryDAO;
import dao.ProductDAO;
import model.Category;
import model.OrderDetail;
import model.Product;
import model.User;

/**
 * Servlet hiển thị màn hình bán hàng POS trên Web.
 */
@WebServlet(name = "POSServlet", urlPatterns = {"/pos"})
public class POSServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
        categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 1. Kiểm tra xác thực phiên đăng nhập
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 2. Lấy danh sách danh mục để hiển thị thanh lọc menu
        List<Category> categories = categoryDAO.getAll();
        request.setAttribute("categories", categories);

        // 3. Xử lý lọc sản phẩm theo danh mục hoặc từ khóa tìm kiếm
        String catIdParam = request.getParameter("categoryId");
        String keyword = request.getParameter("keyword");

        List<Product> products;
        int selectedCatId = 0;

        if (keyword != null && !keyword.trim().isEmpty()) {
            products = productDAO.searchByName(keyword.trim());
            request.setAttribute("keyword", keyword.trim());
        } else if (catIdParam != null && !catIdParam.trim().isEmpty()) {
            try {
                selectedCatId = Integer.parseInt(catIdParam.trim());
                if (selectedCatId > 0) {
                    products = productDAO.getByCategory(selectedCatId);
                } else {
                    products = productDAO.getAll();
                }
            } catch (NumberFormatException e) {
                products = productDAO.getAll();
            }
        } else {
            products = productDAO.getAll();
        }

        request.setAttribute("selectedCatId", selectedCatId);
        request.setAttribute("products", products);

        // 4. Khởi tạo giỏ hàng trong Session nếu chưa có
        @SuppressWarnings("unchecked")
        List<OrderDetail> cart = (List<OrderDetail>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        // Tính tổng tiền giỏ hàng hiện tại
        double totalCartAmount = 0;
        for (OrderDetail item : cart) {
            totalCartAmount += item.getSubTotal();
        }
        request.setAttribute("totalCartAmount", totalCartAmount);

        // Forward sang trang giao diện pos.jsp
        request.getRequestDispatcher("/pos.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
