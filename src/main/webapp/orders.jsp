<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="model.Order"%>
<%@ page import="model.User"%>
<%
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    Object ordersObj = request.getAttribute("orders");
    List<Order> orders = (ordersObj instanceof List) ? (List<Order>) ordersObj : null;

    String keyword = (String) request.getAttribute("keyword");
    if (keyword == null) keyword = "";

    Double totalRevenue = (Double) request.getAttribute("totalRevenue");
    if (totalRevenue == null) totalRevenue = 0.0;

    Double todayRevenue = (Double) request.getAttribute("todayRevenue");
    if (todayRevenue == null) todayRevenue = 0.0;

    Integer todayOrdersCount = (Integer) request.getAttribute("todayOrdersCount");
    if (todayOrdersCount == null) todayOrdersCount = 0;

    Integer totalOrdersCount = (Integer) request.getAttribute("totalOrdersCount");
    if (totalOrdersCount == null) totalOrdersCount = (orders != null ? orders.size() : 0);

    DecimalFormat df = new DecimalFormat("#,##0");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lịch Sử Đơn Hàng - Boba & Coffee Station</title>
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <!-- Dedicated Orders Stylesheet -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/orders.css">
</head>
<body>

<!-- Header Bar -->
<header class="orders-header">
    <div class="brand-section">
        <div class="brand-info">
            <h1>LỊCH SỬ ĐƠN HÀNG & DOANH THU</h1>
            <p>Hệ thống tra cứu biên lai và thống kê bán hàng</p>
        </div>
    </div>

    <div class="nav-actions">
        <a href="<%= request.getContextPath() %>/products" class="btn-nav-pos" style="background: var(--bg-surface); color: var(--text-main); border: 1px solid var(--border); margin-right: 8px;">
            <span>Quản lý món</span>
        </a>
        <a href="<%= request.getContextPath() %>/pos" class="btn-nav-pos">
            <span>Quay lại Bán hàng (POS)</span>
        </a>
    </div>
</header>

<main class="orders-container">

    <!-- 4 Thẻ Thống Kê Tổng Quan -->
    <section class="stats-grid">
        <div class="stat-card">
            <div class="stat-info">
                <span class="stat-label">Doanh thu hôm nay</span>
                <span class="stat-val"><%= df.format(todayRevenue) %> đ</span>
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-info">
                <span class="stat-label">Đơn bán hôm nay</span>
                <span class="stat-val"><%= todayOrdersCount %> đơn</span>
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-info">
                <span class="stat-label">Tổng doanh thu</span>
                <span class="stat-val"><%= df.format(totalRevenue) %> đ</span>
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-info">
                <span class="stat-label">Tổng hóa đơn lưu</span>
                <span class="stat-val"><%= totalOrdersCount %> đơn</span>
            </div>
        </div>
    </section>

    <!-- Thanh Tìm Kiếm & Lọc Đơn Hàng -->
    <section class="toolbar-card">
        <form action="<%= request.getContextPath() %>/orders" method="GET" class="search-orders-form">
            <div class="search-input-box">
                <input type="text" name="keyword" placeholder="Nhập mã hóa đơn (#HD...) hoặc tên thu ngân..." 
                       value="<%= keyword %>">
            </div>
            <button type="submit" class="btn-filter">Tìm kiếm</button>
            <% if (keyword != null && !keyword.isEmpty()) { %>
                <a href="<%= request.getContextPath() %>/orders" style="font-size: 13px; color: var(--danger); text-decoration: underline; margin-left: 6px;">Xóa lọc</a>
            <% } %>
        </form>

        <div class="filter-meta-text">
            Tìm thấy <strong><%= (orders != null ? orders.size() : 0) %></strong> hóa đơn
        </div>
    </section>

    <!-- Bảng Danh Sách Hóa Đơn -->
    <section class="table-card">
        <% if (orders == null || orders.isEmpty()) { %>
            <div class="empty-orders-box">
                <span>📭</span>
                <h3 style="font-size: 16px; font-weight: 700; color: var(--text-main); margin-bottom: 6px;">
                    Không tìm thấy hóa đơn nào
                </h3>
                <p style="font-size: 13px;">
                    Chưa có đơn hàng nào được ghi nhận hoặc không có đơn khớp với từ khóa tìm kiếm.
                </p>
            </div>
        <% } else { %>
            <table class="orders-table">
                <thead>
                    <tr>
                        <th>Mã Hóa Đơn</th>
                        <th>Ngày Giờ Bán</th>
                        <th>Thu Ngân Ca</th>
                        <th style="text-align: right;">Tổng Tiền</th>
                        <th style="text-align: center;">Trạng Thái</th>
                        <th style="text-align: center;">Thao Tác</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Order o : orders) { %>
                        <tr>
                            <td>
                                <span class="order-code-badge">#<%= o.getOrderCode() %></span>
                            </td>
                            <td>
                                <span class="order-date-text"><%= (o.getOrderDate() != null ? sdf.format(o.getOrderDate()) : "N/A") %></span>
                            </td>
                            <td>
                                <span class="cashier-name-text"><%= (o.getUserName() != null ? o.getUserName() : "Thu ngân") %></span>
                            </td>
                            <td style="text-align: right;">
                                <span class="total-amount-badge"><%= df.format(o.getTotalAmount()) %> đ</span>
                            </td>
                            <td style="text-align: center;">
                                <span class="status-paid-pill">✓ Đã thanh toán</span>
                            </td>
                            <td style="text-align: center;">
                                <a href="<%= request.getContextPath() %>/orders?action=view&id=<%= o.getId() %>" 
                                   class="btn-view-receipt" title="Xem chi tiết và in lại hóa đơn này">
                                    <span>Xem & In</span>
                                </a>
                            </td>
                        </tr>
                    <% } %>
                </tbody>
            </table>
        <% } %>
    </section>

</main>

</body>
</html>
