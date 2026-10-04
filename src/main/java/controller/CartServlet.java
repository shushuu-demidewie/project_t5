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

import dao.ProductDAO;
import model.OrderDetail;
import model.Product;

/**
 * Servlet quản lý các thao tác trên giỏ hàng (Thêm món, Sửa số lượng, Xóa món, Làm sạch giỏ).
 */
@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processCartAction(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processCartAction(request, response);
    }

    private void processCartAction(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        @SuppressWarnings("unchecked")
        List<OrderDetail> cart = (List<OrderDetail>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "view";
        }

        try {
            switch (action) {
                case "add": {
                    int productId = Integer.parseInt(request.getParameter("productId"));
                    int quantity = 1;
                    if (request.getParameter("quantity") != null) {
                        try {
                            quantity = Integer.parseInt(request.getParameter("quantity"));
                            if (quantity <= 0) quantity = 1;
                        } catch (NumberFormatException ignored) {}
                    }

                    // Kiểm tra món đã có trong giỏ chưa
                    boolean found = false;
                    for (OrderDetail item : cart) {
                        if (item.getProductId() == productId) {
                            item.setQuantity(item.getQuantity() + quantity);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        Product p = productDAO.getById(productId);
                        if (p != null) {
                            OrderDetail newItem = new OrderDetail(p.getId(), p.getProductName(), quantity, p.getPrice());
                            cart.add(newItem);
                        }
                    }
                    break;
                }

                case "update": {
                    int productId = Integer.parseInt(request.getParameter("productId"));
                    int delta = Integer.parseInt(request.getParameter("delta")); // +1 hoặc -1

                    for (int i = 0; i < cart.size(); i++) {
                        OrderDetail item = cart.get(i);
                        if (item.getProductId() == productId) {
                            int newQty = item.getQuantity() + delta;
                            if (newQty <= 0) {
                                cart.remove(i);
                            } else {
                                item.setQuantity(newQty);
                            }
                            break;
                        }
                    }
                    break;
                }

                case "remove": {
                    int productId = Integer.parseInt(request.getParameter("productId"));
                    cart.removeIf(item -> item.getProductId() == productId);
                    break;
                }

                case "clear": {
                    cart.clear();
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi xử lý giỏ hàng: " + e.getMessage());
        }

        // Quay trở lại trang POS
        response.sendRedirect(request.getContextPath() + "/pos");
    }
}
