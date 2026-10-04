package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Category;
import utils.DBContext;

/**
 * Lớp CategoryDAO xử lý truy vấn dữ liệu bảng 'categories'.
 */
public class CategoryDAO {

    /**
     * Lấy toàn bộ danh mục đồ uống/sản phẩm.
     */
    public List<Category> getAll() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT id, category_name FROM categories ORDER BY id ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Category c = new Category(
                        rs.getInt("id"),
                        rs.getString("category_name")
                );
                list.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách Category: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBContext.closeResources(conn, ps, rs);
        }
        return list;
    }

    /**
     * Lấy danh mục theo ID.
     */
    public Category getById(int id) {
        String sql = "SELECT id, category_name FROM categories WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                return new Category(
                        rs.getInt("id"),
                        rs.getString("category_name")
                );
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy Category theo ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBContext.closeResources(conn, ps, rs);
        }
        return null;
    }
}
