package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Order;
import model.OrderDetail;
import utils.DBContext;

/**
 * Lớp OrderDAO xử lý các nghiệp vụ đơn hàng và chi tiết đơn hàng.
 * Áp dụng JDBC Transaction để đảm bảo tính toàn vẹn dữ liệu (ACID).
 */
public class OrderDAO {

    /**
     * Tạo đơn hàng mới kèm theo danh sách chi tiết đơn hàng sử dụng JDBC Transaction.
     *
     * @param order   Đối tượng Order chứa thông tin hóa đơn (order_code, user_id, order_date, total_amount)
     * @param details Danh sách các món trong hóa đơn (order_details)
     * @return orderId vừa sinh ra nếu thành công (> 0), hoặc -1 nếu thất bại
     */
    public int createOrder(Order order, List<OrderDetail> details) {
        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psDetail = null;
        ResultSet rsKeys = null;
        int generatedOrderId = -1;

        String sqlOrder = "INSERT INTO orders (order_code, user_id, order_date, total_amount) VALUES (?, ?, ?, ?)";
        String sqlDetail = "INSERT INTO order_details (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";

        try {
            conn = DBContext.getConnection();

            // 1. Tắt chế độ tự động commit để khởi động Transaction
            conn.setAutoCommit(false);

            // 2. Chuẩn bị câu lệnh chèn vào bảng 'orders' với tùy chọn lấy Generated Keys
            psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            psOrder.setString(1, order.getOrderCode());
            psOrder.setInt(2, order.getUserId());
            psOrder.setTimestamp(3, order.getOrderDate());
            psOrder.setDouble(4, order.getTotalAmount());

            int rowsAffected = psOrder.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Thêm hóa đơn thất bại, không có dòng nào được tạo.");
            }

            // 3. Lấy ID tự tăng vừa được sinh ra cho đơn hàng
            rsKeys = psOrder.getGeneratedKeys();
            if (rsKeys.next()) {
                generatedOrderId = rsKeys.getInt(1);
                order.setId(generatedOrderId);
            } else {
                throw new SQLException("Thêm hóa đơn thất bại, không lấy được ID đơn hàng vừa tạo.");
            }

            // 4. Lưu từng chi tiết đơn hàng vào bảng 'order_details'
            psDetail = conn.prepareStatement(sqlDetail);
            for (OrderDetail item : details) {
                psDetail.setInt(1, generatedOrderId);
                psDetail.setInt(2, item.getProductId());
                psDetail.setInt(3, item.getQuantity());
                psDetail.setDouble(4, item.getUnitPrice());
                psDetail.addBatch(); // Sử dụng batch để tối ưu hóa hiệu năng
            }
            psDetail.executeBatch();

            // 5. Nếu tất cả đều thành công -> Commit Transaction
            conn.commit();
            System.out.println("Giao dịch thành công! Mã đơn hàng #" + generatedOrderId + " (" + order.getOrderCode() + ")");

        } catch (SQLException e) {
            // Khi có lỗi xảy ra -> Rollback toàn bộ thay đổi để đảm bảo tính toàn vẹn
            System.err.println("Lỗi trong quá trình tạo đơn hàng, tiến hành Rollback: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Đã Rollback giao dịch thành công.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi thực hiện Rollback: " + ex.getMessage());
                }
            }
            return -1; // Trả về -1 nếu thất bại
        } finally {
            // Phục hồi lại chế độ auto-commit và giải phóng các tài nguyên
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi bật lại AutoCommit: " + ex.getMessage());
                }
            }
            DBContext.closeResultSet(rsKeys);
            DBContext.closeStatement(psDetail);
            DBContext.closeStatement(psOrder);
            DBContext.closeConnection(conn);
        }

        return generatedOrderId;
    }

    /**
     * Lấy toàn bộ danh sách đơn hàng đã bán (sắp xếp mới nhất lên đầu).
     */
    public List<Order> getAllOrders() {
        return searchOrders(null);
    }

    /**
     * Tìm kiếm đơn hàng theo mã hóa đơn hoặc tên thu ngân.
     */
    public List<Order> searchOrders(String keyword) {
        List<Order> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        StringBuilder sql = new StringBuilder(
            "SELECT o.id, o.order_code, o.user_id, o.order_date, o.total_amount, u.full_name " +
            "FROM orders o " +
            "LEFT JOIN user u ON o.user_id = u.id "
        );

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("WHERE o.order_code LIKE ? OR u.full_name LIKE ? ");
        }
        sql.append("ORDER BY o.order_date DESC");

        try {
            conn = DBContext.getConnection();
            ps = conn.prepareStatement(sql.toString());

            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.trim() + "%";
                ps.setString(1, pattern);
                ps.setString(2, pattern);
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setOrderCode(rs.getString("order_code"));
                order.setUserId(rs.getInt("user_id"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setUserName(rs.getString("full_name") != null ? rs.getString("full_name") : "Thu ngân");
                list.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBContext.closeResultSet(rs);
            DBContext.closeStatement(ps);
            DBContext.closeConnection(conn);
        }
        return list;
    }

    /**
     * Lấy thông tin đơn hàng theo ID.
     */
    public Order getOrderById(int orderId) {
        Order order = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT o.id, o.order_code, o.user_id, o.order_date, o.total_amount, u.full_name " +
                     "FROM orders o " +
                     "LEFT JOIN user u ON o.user_id = u.id " +
                     "WHERE o.id = ?";

        try {
            conn = DBContext.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);
            rs = ps.executeQuery();

            if (rs.next()) {
                order = new Order();
                order.setId(rs.getInt("id"));
                order.setOrderCode(rs.getString("order_code"));
                order.setUserId(rs.getInt("user_id"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setUserName(rs.getString("full_name") != null ? rs.getString("full_name") : "Thu ngân");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin đơn hàng theo ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBContext.closeResultSet(rs);
            DBContext.closeStatement(ps);
            DBContext.closeConnection(conn);
        }
        return order;
    }

    /**
     * Lấy danh sách chi tiết các món trong đơn hàng theo Order ID.
     */
    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        List<OrderDetail> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT od.id, od.order_id, od.product_id, od.quantity, od.unit_price, p.product_name " +
                     "FROM order_details od " +
                     "JOIN products p ON od.product_id = p.id " +
                     "WHERE od.order_id = ? " +
                     "ORDER BY od.id ASC";

        try {
            conn = DBContext.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);
            rs = ps.executeQuery();

            while (rs.next()) {
                OrderDetail d = new OrderDetail();
                d.setId(rs.getInt("id"));
                d.setOrderId(rs.getInt("order_id"));
                d.setProductId(rs.getInt("product_id"));
                d.setQuantity(rs.getInt("quantity"));
                d.setUnitPrice(rs.getDouble("unit_price"));
                d.setProductName(rs.getString("product_name"));
                list.add(d);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy chi tiết đơn hàng: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBContext.closeResultSet(rs);
            DBContext.closeStatement(ps);
            DBContext.closeConnection(conn);
        }
        return list;
    }

    /**
     * Tính tổng doanh thu toàn thời gian.
     */
    public double getTotalRevenue() {
        double total = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT SUM(total_amount) AS total FROM orders";

        try {
            conn = DBContext.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tính tổng doanh thu: " + e.getMessage());
        } finally {
            DBContext.closeResultSet(rs);
            DBContext.closeStatement(ps);
            DBContext.closeConnection(conn);
        }
        return total;
    }

    /**
     * Tính tổng doanh thu hôm nay.
     */
    public double getTodayRevenue() {
        double total = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT SUM(total_amount) AS total FROM orders WHERE DATE(order_date) = CURDATE()";

        try {
            conn = DBContext.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tính doanh thu hôm nay: " + e.getMessage());
        } finally {
            DBContext.closeResultSet(rs);
            DBContext.closeStatement(ps);
            DBContext.closeConnection(conn);
        }
        return total;
    }

    /**
     * Đếm số đơn hàng hôm nay.
     */
    public int getTodayOrderCount() {
        int count = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT COUNT(*) AS cnt FROM orders WHERE DATE(order_date) = CURDATE()";

        try {
            conn = DBContext.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("cnt");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đếm số đơn hôm nay: " + e.getMessage());
        } finally {
            DBContext.closeResultSet(rs);
            DBContext.closeStatement(ps);
            DBContext.closeConnection(conn);
        }
        return count;
    }
}
