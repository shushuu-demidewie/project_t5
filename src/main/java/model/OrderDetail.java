package model;

/**
 * Lớp đại diện cho bảng 'order_details' trong cơ sở dữ liệu.
 */
public class OrderDetail {
    private int id;
    private int orderId;
    private int productId;
    private int quantity;
    private double unitPrice;

    // Thuộc tính phụ trợ phục vụ hiển thị trên JTable giỏ hàng & hóa đơn
    private String productName;

    // Constructor rỗng
    public OrderDetail() {
    }

    // Constructor đầy đủ theo CSDL
    public OrderDetail(int id, int orderId, int productId, int quantity, double unitPrice) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Constructor tạo chi tiết đơn hàng (thường dùng khi bán hàng)
    public OrderDetail(int productId, String productName, int quantity, double unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Constructor lưu DB không cần id tự tăng
    public OrderDetail(int orderId, int productId, int quantity, double unitPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Getter và Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    // Tính thành tiền của dòng chi tiết
    public double getSubTotal() {
        return quantity * unitPrice;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", subTotal=" + getSubTotal() +
                '}';
    }
}
