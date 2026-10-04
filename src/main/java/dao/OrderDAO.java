package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
}
