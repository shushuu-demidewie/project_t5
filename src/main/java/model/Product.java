package model;

/**
 * Lớp đại diện cho bảng 'products' trong cơ sở dữ liệu.
 */
public class Product {
    private int id;
    private int categoryId;
    private String productName;
    private double price;
    private String status; // Ví dụ: 'Còn hàng', 'Hết hàng', 'Active', 'Inactive'

    // Thuộc tính bổ trợ phục vụ hiển thị tên danh mục
    private String categoryName;

    // Constructor rỗng
    public Product() {
    }

    // Constructor đầy đủ theo CSDL
    public Product(int id, int categoryId, String productName, double price, String status) {
        this.id = id;
        this.categoryId = categoryId;
        this.productName = productName;
        this.price = price;
        this.status = status;
    }

    // Constructor đầy đủ có kèm tên danh mục để tiện hiển thị lên giao diện
    public Product(int id, int categoryId, String productName, double price, String status, String categoryName) {
        this.id = id;
        this.categoryId = categoryId;
        this.productName = productName;
        this.price = price;
        this.status = status;
        this.categoryName = categoryName;
    }

    // Constructor phục vụ thêm mới
    public Product(int categoryId, String productName, double price, String status) {
        this.categoryId = categoryId;
        this.productName = productName;
        this.price = price;
        this.status = status;
    }

    // Getter và Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return productName + " (" + String.format("%,.0f đ", price) + ")";
    }
}
