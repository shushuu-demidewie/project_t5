package model;

/**
 * Lớp đại diện cho bảng 'categories' trong cơ sở dữ liệu.
 */
public class Category {
    private int id;
    private String categoryName;

    // Constructor rỗng
    public Category() {
    }

    // Constructor đầy đủ tham số
    public Category(int id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }

    // Constructor cho trường hợp thêm mới
    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    // Getter và Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return categoryName; // Để thuận tiện hiển thị trên JComboBox
    }
}
