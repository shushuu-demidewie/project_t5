package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Product;
import utils.DBContext;

/**
 * Lớp ProductDAO thực hiện các thao tác CRUD trên bảng 'products'.
 */
public class ProductDAO {

    /**
     * Lấy toàn bộ danh sách sản phẩm kèm tên danh mục.
     */
    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.id, p.category_id, p.product_name, p.price, p.status, c.category_name "
                   + "FROM products p "
                   + "LEFT JOIN categories c ON p.category_id = c.id "
                   + "ORDER BY p.id DESC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("id"),
                        rs.getInt("category_id"),
                        rs.getString("product_name"),
                        rs.getDouble("price"),
                        rs.getString("status"),
                        rs.getString("category_name")
                );
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAll() trong ProductDAO: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBContext.closeResources(conn, ps, rs);
        }
        return list;
    }

    /**
     * Lấy danh sách sản phẩm theo danh mục (category_id).
     *
     * @param catId Mã danh mục
     * @return Danh sách sản phẩm thuộc danh mục
     */
    public List<Product> getByCategory(int catId) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.id, p.category_id, p.product_name, p.price, p.status, c.category_name "
                   + "FROM products p "
                   + "LEFT JOIN categories c ON p.category_id = c.id "
                   + "WHERE p.category_id = ? "
                   + "ORDER BY p.id DESC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, catId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("id"),
                        rs.getInt("category_id"),
                        rs.getString("product_name"),
                        rs.getDouble("price"),
                        rs.getString("status"),
                        rs.getString("category_name")
                );
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getByCategory() trong ProductDAO: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBContext.closeResources(conn, ps, rs);
        }
        return list;
    }

    /**
     * Tìm kiếm sản phẩm theo tên.
     *
     * @param keyword Từ khóa tìm kiếm
     */
    public List<Product> searchByName(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.id, p.category_id, p.product_name, p.price, p.status, c.category_name "
                   + "FROM products p "
                   + "LEFT JOIN categories c ON p.category_id = c.id "
                   + "WHERE p.product_name LIKE ? "
                   + "ORDER BY p.id DESC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("id"),
                        rs.getInt("category_id"),
                        rs.getString("product_name"),
                        rs.getDouble("price"),
                        rs.getString("status"),
                        rs.getString("category_name")
                );
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi searchByName() trong ProductDAO: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBContext.closeResources(conn, ps, rs);
        }
        return list;
    }

    /**
     * Lấy chi tiết sản phẩm theo ID.
     */
    public Product getById(int id) {
        String sql = "SELECT p.id, p.category_id, p.product_name, p.price, p.status, c.category_name "
                   + "FROM products p "
                   + "LEFT JOIN categories c ON p.category_id = c.id "
                   + "WHERE p.id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                return new Product(
                        rs.getInt("id"),
                        rs.getInt("category_id"),
                        rs.getString("product_name"),
                        rs.getDouble("price"),
                        rs.getString("status"),
                        rs.getString("category_name")
                );
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getById() trong ProductDAO: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBContext.closeResources(conn, ps, rs);
        }
        return null;
    }

    /**
     * Thêm mới một sản phẩm vào CSDL.
     *
     * @param p Đối tượng sản phẩm cần thêm
     * @return true nếu thêm thành công, false nếu thất bại
     */
    public boolean insert(Product p) {
        String sql = "INSERT INTO products (category_id, product_name, price, status) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBContext.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, p.getCategoryId());
            ps.setString(2, p.getProductName());
            ps.setDouble(3, p.getPrice());
            ps.setString(4, p.getStatus());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi insert() trong ProductDAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBContext.closeResources(conn, ps);
        }
    }

    /**
     * Cập nhật thông tin sản phẩm.
     *
     * @param p Đối tượng sản phẩm có ID và thông tin mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean update(Product p) {
        String sql = "UPDATE products SET category_id = ?, product_name = ?, price = ?, status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBContext.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, p.getCategoryId());
            ps.setString(2, p.getProductName());
            ps.setDouble(3, p.getPrice());
            ps.setString(4, p.getStatus());
            ps.setInt(5, p.getId());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi update() trong ProductDAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBContext.closeResources(conn, ps);
        }
    }

    /**
     * Xóa sản phẩm theo ID.
     *
     * @param id Mã sản phẩm cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBContext.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi delete() trong ProductDAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            DBContext.closeResources(conn, ps);
        }
    }
}
