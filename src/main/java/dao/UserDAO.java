package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.User;
import utils.DBContext;

/**
 * Lớp UserDAO xử lý các thao tác dữ liệu liên quan đến bảng 'user'.
 */
public class UserDAO {

    /**
     * Kiểm tra thông tin đăng nhập của người dùng.
     *
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return Đối tượng User nếu đăng nhập đúng, null nếu sai thông tin hoặc tài khoản không tồn tại
     */
    public User checkLogin(String username, String password) {
        String sql = "SELECT id, username, password, full_name, role FROM user WHERE username = ? AND password = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra đăng nhập trong UserDAO: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBContext.closeResources(conn, ps, rs);
        }
        return null;
    }

    /**
     * Lấy thông tin người dùng theo ID.
     */
    public User getById(int id) {
        String sql = "SELECT id, username, password, full_name, role FROM user WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy User theo ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBContext.closeResources(conn, ps, rs);
        }
        return null;
    }

    /**
     * Lấy danh sách toàn bộ người dùng trong hệ thống.
     */
    public List<User> getAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, username, password, full_name, role FROM user ORDER BY id DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("role")
                );
                list.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy danh sách User: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBContext.closeResources(conn, ps, rs);
        }
        return list;
    }
}
