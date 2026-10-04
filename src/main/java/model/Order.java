package model;

import java.sql.Timestamp;

/**
 * Lớp đại diện cho bảng 'orders' trong cơ sở dữ liệu.
 */
public class Order {
    private int id;
    private String orderCode;
    private int userId;
    private Timestamp orderDate;
    private double totalAmount;

    // Thuộc tính phụ trợ phục vụ hiển thị
    private String userName;

    // Constructor rỗng
    public Order() {
    }

    // Constructor đầy đủ theo CSDL
    public Order(int id, String orderCode, int userId, Timestamp orderDate, double totalAmount) {
        this.id = id;
        this.orderCode = orderCode;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
    }

    // Constructor phục vụ thêm mới (khi chưa có id)
    public Order(String orderCode, int userId, Timestamp orderDate, double totalAmount) {
        this.orderCode = orderCode;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
    }

    // Getter và Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderCode='" + orderCode + '\'' +
                ", userId=" + userId +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
