<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="model.Order"%>
<%@ page import="model.OrderDetail"%>
<%
    Order order = (Order) request.getAttribute("order");
    if (order == null) {
        response.sendRedirect(request.getContextPath() + "/pos");
        return;
    }

    Object detailsObj = request.getAttribute("invoiceDetails");
    List<OrderDetail> details = (detailsObj instanceof List) ? (List<OrderDetail>) detailsObj : null;
    Double customerCash = (Double) request.getAttribute("customerCash");
    if (customerCash == null && order != null) customerCash = order.getTotalAmount();

    Double changeMoney = (Double) request.getAttribute("changeMoney");
    if (changeMoney == null) changeMoney = 0.0;

    String cashierName = (String) request.getAttribute("cashierName");
    if (cashierName == null) cashierName = "Thu ngân";

    DecimalFormat df = new DecimalFormat("#,##0");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hóa Đơn Thanh Toán - <%= order.getOrderCode() %></title>
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <!-- Dedicated Invoice Receipt Stylesheet -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/invoice.css">
</head>
<body>

<div class="bill-card">
    <!-- Thông tin cửa hàng -->
    <div class="bill-header">
        <div class="shop-name">BOBA & COFFEE STATION</div>
        <div class="shop-info">
            123 Đường Hoa Sữa, Quận 1, TP. Hồ Chí Minh<br>
            Hotline: 1900 6868 • Giờ mở cửa: 07:00 - 23:00
        </div>
        <div class="bill-title-wrap">
            <h2 class="bill-title">HÓA ĐƠN THANH TOÁN</h2>
            <span class="paid-badge">Đã thanh toán</span>
        </div>
    </div>

    <!-- Thông tin hóa đơn -->
    <div class="bill-meta">
        <div class="meta-row">
            <span class="meta-label">Số hóa đơn:</span>
            <span class="meta-val">#<%= order.getOrderCode() %></span>
        </div>
        <div class="meta-row">
            <span class="meta-label">Thời gian:</span>
            <span class="meta-val"><%= sdf.format(order.getOrderDate()) %></span>
        </div>
        <div class="meta-row">
            <span class="meta-label">Thu ngân phụ trách:</span>
            <span class="meta-val"><%= cashierName %></span>
        </div>
    </div>

    <!-- Danh sách món chi tiết -->
    <table class="bill-table">
        <thead>
            <tr>
                <th>Món</th>
                <th style="text-align: center;">SL</th>
                <th style="text-align: right;">Đơn giá</th>
                <th style="text-align: right;">T.Tiền</th>
            </tr>
        </thead>
        <tbody>
            <% 
                if (details != null) {
                    for (OrderDetail d : details) { 
            %>
                <tr>
                    <td>
                        <div class="item-name"><%= d.getProductName() %></div>
                    </td>
                    <td class="item-qty"><%= d.getQuantity() %></td>
                    <td class="item-price"><%= df.format(d.getUnitPrice()) %></td>
                    <td class="item-total"><%= df.format(d.getSubTotal()) %></td>
                </tr>
            <% 
                    }
                } 
            %>
        </tbody>
    </table>

    <!-- Khung tính tiền -->
    <div class="bill-calc">
        <div class="calc-row grand-total">
            <span>TỔNG CỘNG:</span>
            <span><%= df.format(order.getTotalAmount()) %> đ</span>
        </div>
        <div class="calc-row">
            <span class="meta-label">Tiền khách đưa:</span>
            <span class="meta-val"><%= df.format(customerCash) %> đ</span>
        </div>
        <div class="calc-row">
            <span class="meta-label">Tiền thừa trả khách:</span>
            <span class="meta-val" style="color: #047857; font-size: 14px;"><%= df.format(Math.max(0, changeMoney)) %> đ</span>
        </div>
    </div>

    <!-- Mô phỏng mã vạch biên lai -->
    <div class="barcode-section">
        <div class="simulated-barcode"></div>
        <div class="barcode-text">* <%= order.getOrderCode() %> *</div>
    </div>

    <!-- Lời cảm ơn và wifi -->
    <div class="bill-footer">
        <p>Cảm ơn quý khách và hẹn gặp lại!</p>
        <div class="wifi-note">Wi-Fi: BobaCoffee_Guest • Mật khẩu: 88888888</div>
    </div>
</div>

<!-- Nút hành động ngoài hóa đơn -->
<div class="action-buttons">
    <button class="btn btn-print" onclick="window.print()">
        <span>In Hóa Đơn (Ctrl + P)</span>
    </button>
    <a href="<%= request.getContextPath() %>/pos" class="btn btn-new-order">
        <span>Đơn Mới</span>
    </a>
    <a href="<%= request.getContextPath() %>/orders" class="btn btn-new-order">
        <span>Lịch Sử</span>
    </a>
</div>

</body>
</html>
