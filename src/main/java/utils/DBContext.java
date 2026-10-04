package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Lớp DBContext quản lý việc kết nối và giải phóng tài nguyên CSDL MySQL.
 * Cấu hình cho dự án: project_quan_ts chạy trên phpMyAdmin (XAMPP).
 */
public class DBContext {

    // Thông tin cấu hình CSDL
    private static final String HOST_NAME = "localhost";
    private static final String PORT = "3306";
    private static final String DB_NAME = "project_quan_ts";
    private static final String USER_NAME = "root";
    private static final String PASSWORD = ""; // Mặc định trên XAMPP để trống

    // Chuỗi kết nối JDBC với tham số mã hóa UTF-8 và múi giờ
    private static final String DB_URL = "jdbc:mysql://" + HOST_NAME + ":" + PORT + "/" + DB_NAME
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";

    // Driver JDBC của MySQL 8.x
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            // Nạp Driver vào bộ nhớ
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi nạp MySQL Driver. Vui lòng kiểm tra file mysql-connector-j-8.x.x.jar trong Build Path!");
            e.printStackTrace();
        }
    }

    /**
     * Mở một kết nối mới tới CSDL MySQL.
     *
     * @return Connection đối tượng kết nối
     * @throws SQLException nếu kết nối thất bại
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Không thể kết nối đến CSDL [" + DB_NAME + "]: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Đóng an toàn kết nối Connection.
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng Connection: " + e.getMessage());
            }
        }
    }

    /**
     * Đóng an toàn Statement (hoặc PreparedStatement).
     */
    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng Statement: " + e.getMessage());
            }
        }
    }

    /**
     * Đóng an toàn ResultSet.
     */
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng ResultSet: " + e.getMessage());
            }
        }
    }

    /**
     * Giải phóng đồng thời cả Connection, PreparedStatement và ResultSet.
     */
    public static void closeResources(Connection conn, PreparedStatement ps, ResultSet rs) {
        closeResultSet(rs);
        closeStatement(ps);
        closeConnection(conn);
    }

    /**
     * Giải phóng đồng thời Connection và PreparedStatement.
     */
    public static void closeResources(Connection conn, PreparedStatement ps) {
        closeStatement(ps);
        closeConnection(conn);
    }
}
