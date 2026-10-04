package controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
 * Servlet xử lý thanh toán đơn hàng, lưu vào CSDL bằng JDBC Transaction và xuất hóa đơn.
 */
@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout"})
public class CheckoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private OrderDAO orderDAO;

    @Override
    public void init() throws ServletException {
        orderDAO = new OrderDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");

        @SuppressWarnings("unchecked")
        List<OrderDetail> cart = (List<OrderDetail>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/pos?error=Giỏ hàng trống!");
            return;
        }

        // Tính tổng tiền đơn hàng
        double totalAmount = 0;
        for (OrderDetail item : cart) {
            totalAmount += item.getSubTotal();
        }

        // Lấy tiền khách đưa
        String cashParam = request.getParameter("customerCash");
        double customerCash = totalAmount;
        if (cashParam != null && !cashParam.trim().isEmpty()) {
            try {
                customerCash = Double.parseDouble(cashParam.replaceAll("[^0-9.]", ""));
            } catch (NumberFormatException ignored) {}
        }

        double changeMoney = customerCash - totalAmount;

        // Sinh mã hóa đơn duy nhất
        String orderCode = "HD" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        // Khởi tạo đối tượng Order
        Order order = new Order();
        order.setOrderCode(orderCode);
        order.setUserId(currentUser.getId());
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        order.setTotalAmount(totalAmount);

        // Lưu đơn hàng vào CSDL qua Transaction an toàn
        int createdOrderId = orderDAO.createOrder(order, cart);

        if (createdOrderId > 0) {
            // Lưu dữ liệu để hiển thị trên trang hóa đơn
            List<OrderDetail> invoiceDetails = new ArrayList<>(cart);
            request.setAttribute("order", order);
            request.setAttribute("invoiceDetails", invoiceDetails);
            request.setAttribute("customerCash", customerCash);
            request.setAttribute("changeMoney", changeMoney);
            request.setAttribute("cashierName", currentUser.getFullName());

            // Xóa sạch giỏ hàng sau khi thanh toán thành công
            cart.clear();

            // Chuyển tiếp tới trang xem & in hóa đơn
            request.getRequestDispatcher("/invoice.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/pos?error=Thanh toán thất bại do lỗi CSDL!");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/pos");
    }
}
