package controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.OrderDAO;
import model.Order;
import model.OrderDetail;
import model.User;

/**
 * Servlet quản lý và xem lại lịch sử hóa đơn bán hàng.
 */
@WebServlet(name = "OrderHistoryServlet", urlPatterns = {"/orders"})
public class OrderHistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private OrderDAO orderDAO;

    @Override
    public void init() throws ServletException {
        orderDAO = new OrderDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if ("view".equals(action)) {
            // Xem lại chi tiết hóa đơn cụ thể (xuất ra giao diện hóa đơn để in lại)
            String idParam = request.getParameter("id");
            if (idParam != null && !idParam.trim().isEmpty()) {
                try {
                    int orderId = Integer.parseInt(idParam);
                    Order order = orderDAO.getOrderById(orderId);
                    if (order != null) {
                        List<OrderDetail> details = orderDAO.getOrderDetailsByOrderId(orderId);
                        request.setAttribute("order", order);
                        request.setAttribute("invoiceDetails", details);
                        request.setAttribute("customerCash", order.getTotalAmount());
                        request.setAttribute("changeMoney", 0.0);
                        request.setAttribute("cashierName", order.getUserName());

                        request.getRequestDispatcher("/invoice.jsp").forward(request, response);
                        return;
                    }
                } catch (NumberFormatException ignored) {}
            }
            response.sendRedirect(request.getContextPath() + "/orders");
            return;
        }

        // Danh sách toàn bộ hóa đơn
        String keyword = request.getParameter("keyword");
        if (keyword != null) {
            keyword = keyword.trim();
        }

        List<Order> orders = orderDAO.searchOrders(keyword);
        double totalRevenue = orderDAO.getTotalRevenue();
        double todayRevenue = orderDAO.getTodayRevenue();
        int todayOrdersCount = orderDAO.getTodayOrderCount();
        int totalOrdersCount = orders.size();

        request.setAttribute("orders", orders);
        request.setAttribute("keyword", keyword != null ? keyword : "");
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("todayRevenue", todayRevenue);
        request.setAttribute("todayOrdersCount", todayOrdersCount);
        request.setAttribute("totalOrdersCount", totalOrdersCount);

        request.getRequestDispatcher("/orders.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
