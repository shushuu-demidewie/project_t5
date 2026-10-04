<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="model.User"%>
<%@ page import="model.Category"%>
<%@ page import="model.Product"%>
<%@ page import="model.OrderDetail"%>
<%
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    Object catObj = request.getAttribute("categories");
    List<Category> categories = (catObj instanceof List) ? (List<Category>) catObj : null;

    Object prodObj = request.getAttribute("products");
    List<Product> products = (prodObj instanceof List) ? (List<Product>) prodObj : null;

    Object cartObj = session.getAttribute("cart");
    List<OrderDetail> cart = (cartObj instanceof List) ? (List<OrderDetail>) cartObj : null;

    Integer selectedCatId = (Integer) request.getAttribute("selectedCatId");
    if (selectedCatId == null) {
        selectedCatId = 0;
    }

    String keyword = (String) request.getAttribute("keyword");
    if (keyword == null) {
        keyword = "";
    }

    Double totalCartAmount = (Double) request.getAttribute("totalCartAmount");
    if (totalCartAmount == null) {
        totalCartAmount = 0.0;
    }

    int totalItemCount = 0;
    if (cart != null) {
        for (OrderDetail d : cart) {
            totalItemCount += d.getQuantity();
        }
    }

    DecimalFormat df = new DecimalFormat("#,##0");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>POS Bán Hàng - Trà Sữa & Coffee Station</title>
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <!-- Modern POS Stylesheet -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/pos.css">
</head>
<body>

<!-- Header Bar -->
<header class="pos-header">
    <div class="header-brand">
        <div class="brand-logo">🧋</div>
        <div class="brand-text">
            <div class="brand-title">
                BOBA & COFFEE STATION
                <span class="brand-tag">POS v2.0</span>
            </div>
            <div class="brand-sub">Hệ thống quản lý bán hàng & thu ngân trực tuyến</div>
        </div>
    </div>

    <div class="header-meta">
        <!-- Trạng thái kết nối trực tiếp -->
        <div class="system-status">
            <span class="status-dot"></span>
            <span>Hệ thống trực tuyến</span>
        </div>

        <!-- Đồng hồ & Ngày thời gian thực -->
        <div class="clock-display" id="clock">
            Đang tải giờ...
        </div>

        <!-- Thông tin nhân viên thu ngân -->
        <div class="cashier-badge">
            <div class="cashier-avatar">
                <%= (currentUser.getFullName() != null && !currentUser.getFullName().isEmpty()) 
                    ? currentUser.getFullName().substring(0, 1).toUpperCase() : "U" %>
            </div>
            <div class="cashier-info">
                <div class="cashier-name"><%= currentUser.getFullName() %></div>
                <div class="cashier-role"><%= currentUser.getRole() %></div>
            </div>
        </div>

        <!-- Nút Xem Lịch Sử Đơn Hàng -->
        <a href="<%= request.getContextPath() %>/orders" class="btn-orders-nav">
            <span>📋</span>
            <span>Lịch sử đơn</span>
        </a>

        <!-- Nút Đăng xuất -->
        <a href="<%= request.getContextPath() %>/logout" class="btn-logout" 
           onclick="return confirm('Bạn có chắc chắn muốn đăng xuất khỏi ca làm việc?')">
            <span>🚪</span>
            <span>Đăng xuất</span>
        </a>
    </div>
</header>

<!-- Main Container 2 Cột Tỷ Lệ Vàng -->
<main class="pos-container">

    <!-- CỘT BÊN TRÁI: DANH MỤC & THỰC ĐƠN MÓN -->
    <section class="pos-left">
        <div class="menu-filter-bar">
            <!-- Ô tìm kiếm món (Hỗ trợ Live Filter ngay khi gõ và Form submit) -->
            <form action="<%= request.getContextPath() %>/pos" method="GET" class="search-form" id="searchMenuForm">
                <div class="search-input-wrapper">
                    <span class="search-icon">🔍</span>
                    <input type="text" name="keyword" class="search-input" 
                           placeholder="Tìm kiếm tên món theo tên hoặc loại..." 
                           value="<%= keyword %>" autocomplete="off">
                    <button type="button" class="search-clear-btn" id="searchClearBtn" title="Xóa tìm kiếm">✕</button>
                    <span class="search-shortcut-hint">/</span>
                </div>
                <button type="submit" class="btn-search">
                    <span>Tìm</span>
                </button>
            </form>

            <!-- Danh mục dạng Pills có Icons sinh động -->
            <div class="category-pills">
                <a href="<%= request.getContextPath() %>/pos" 
                   class="category-pill <%= (selectedCatId == 0) ? "active" : "" %>">
                    <span class="pill-icon">✨</span>
                    <span>Tất cả thực đơn</span>
                </a>
                <% 
                    if (categories != null) {
                        for (Category cat : categories) { 
                            String catName = cat.getCategoryName();
                            String pillIcon = "🥤";
                            if (catName.contains("Trà Sữa")) pillIcon = "🧋";
                            else if (catName.contains("Quả") || catName.contains("Trái")) pillIcon = "🍑";
                            else if (catName.contains("Cà Phê") || catName.contains("Cafe")) pillIcon = "☕";
                            else if (catName.contains("Đá Xay") || catName.contains("Sinh Tố")) pillIcon = "🍧";
                            else if (catName.contains("Topping")) pillIcon = "🍮";
                %>
                    <a href="<%= request.getContextPath() %>/pos?categoryId=<%= cat.getId() %>" 
                       class="category-pill <%= (selectedCatId == cat.getId()) ? "active" : "" %>">
                        <span class="pill-icon"><%= pillIcon %></span>
                        <span><%= catName %></span>
                    </a>
                <% 
                        }
                    } 
                %>
            </div>
        </div>

        <!-- Lưới món ăn/đồ uống sinh động -->
        <div class="product-grid" id="productGrid">
            <% 
                if (products != null && !products.isEmpty()) {
                    for (Product p : products) { 
                        String cName = (p.getCategoryName() != null) ? p.getCategoryName() : "Khác";
                        String pEmoji = "🥤";
                        String visualBg = "linear-gradient(135deg, #f0fdfa 0%, #ccfbf1 100%)";
                        
                        if (cName.contains("Trà Sữa")) {
                            pEmoji = "🧋";
                            visualBg = "linear-gradient(135deg, #fef3c7 0%, #fde68a 100%)";
                        } else if (cName.contains("Quả") || cName.contains("Trái")) {
                            pEmoji = "🍑";
                            visualBg = "linear-gradient(135deg, #ffedd5 0%, #fed7aa 100%)";
                        } else if (cName.contains("Cà Phê") || cName.contains("Cafe")) {
                            pEmoji = "☕";
                            visualBg = "linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%)";
                        } else if (cName.contains("Đá Xay") || cName.contains("Sinh Tố")) {
                            pEmoji = "🍧";
                            visualBg = "linear-gradient(135deg, #f3e8ff 0%, #e9d5ff 100%)";
                        } else if (cName.contains("Topping")) {
                            pEmoji = "🍮";
                            visualBg = "linear-gradient(135deg, #ecfdf5 0%, #d1fae5 100%)";
                        }
            %>
                <div class="product-card" onclick="window.location.href='<%= request.getContextPath() %>/cart?action=add&productId=<%= p.getId() %>'">
                    <!-- Drink visual container -->
                    <div class="product-visual" style="background: <%= visualBg %>;">
                        <span class="product-emoji"><%= pEmoji %></span>
                        <span class="product-status-tag">Sẵn sàng</span>
                    </div>

                    <!-- Thông tin món -->
                    <div class="product-info">
                        <div class="product-cat"><%= cName %></div>
                        <div class="product-name" title="<%= p.getProductName() %>"><%= p.getProductName() %></div>
                    </div>

                    <!-- Giá tiền & Nút thêm giỏ -->
                    <div class="product-bottom" onclick="event.stopPropagation();">
                        <div class="product-price"><%= df.format(p.getPrice()) %> đ</div>
                        <a href="<%= request.getContextPath() %>/cart?action=add&productId=<%= p.getId() %>" 
                           class="btn-add-cart" title="Thêm vào đơn hàng">
                            <span>+ Thêm</span>
                        </a>
                    </div>
                </div>
            <% 
                    }
                } else { 
            %>
                <div class="empty-product-state">
                    <span class="empty-icon">🍃</span>
                    <h3 style="font-size: 16px; font-weight: 700; margin-bottom: 6px; color: var(--text-main);">
                        Không tìm thấy món phù hợp
                    </h3>
                    <p style="font-size: 13px; color: var(--text-muted);">
                        Vui lòng thử tìm từ khóa khác hoặc chọn danh mục thực đơn bên trên.
                    </p>
                </div>
            <% } %>

            <!-- Khung thông báo khi tìm kiếm tức thời không có món -->
            <div id="clientEmptySearch" class="empty-product-state" style="display: none;">
                <span class="empty-icon">🔍</span>
                <h3 style="font-size: 16px; font-weight: 700; margin-bottom: 6px; color: var(--text-main);">
                    Không có kết quả khớp với từ khóa
                </h3>
                <p style="font-size: 13px; color: var(--text-muted);">
                    Hãy thử nhập tên món khác hoặc bấm phím Esc để quay lại.
                </p>
            </div>
        </div>
    </section>

    <!-- CỘT BÊN PHẢI: HÓA ĐƠN TẠM TÍNH & KHUNG THANH TOÁN -->
    <section class="pos-right">
        <!-- Tiêu đề giỏ hàng & chuyển chế độ -->
        <div class="cart-header">
            <div class="cart-title">
                <span>🛒 Đơn Hàng</span>
                <span class="cart-badge"><%= totalItemCount %> món</span>
            </div>

            <!-- Tùy chọn mang về / tại quán -->
            <div class="order-type-switch">
                <button type="button" class="type-btn active" id="type-dinein" onclick="setOrderType('dinein')">Tại quán</button>
                <button type="button" class="type-btn" id="type-takeaway" onclick="setOrderType('takeaway')">Mang về</button>
            </div>

            <% if (cart != null && !cart.isEmpty()) { %>
                <form action="<%= request.getContextPath() %>/cart" method="POST" style="margin: 0;">
                    <input type="hidden" name="action" value="clear">
                    <button type="submit" class="btn-clear-cart" 
                            onclick="return confirm('Bạn có chắc muốn xóa sạch toàn bộ món trong đơn này?')">
                        Xóa sạch
                    </button>
                </form>
            <% } %>
        </div>

        <!-- Bảng danh sách món đã chọn -->
        <div class="cart-table-wrapper">
            <% if (cart == null || cart.isEmpty()) { %>
                <div class="empty-cart-msg">
                    <span class="empty-cart-icon">🛍️</span>
                    <h4 style="font-size: 15px; font-weight: 700; color: var(--text-main); margin-bottom: 4px;">
                        Chưa có món nào trong đơn
                    </h4>
                    <p style="font-size: 12px; color: var(--text-muted);">
                        Chọn các món đồ uống từ thực đơn bên trái để thêm vào đơn hàng.
                    </p>
                </div>
            <% } else { %>
                <table class="cart-table">
                    <thead>
                        <tr>
                            <th>Món đã chọn</th>
                            <th style="text-align: center;">SL</th>
                            <th style="text-align: right;">Thành tiền</th>
                            <th style="width: 28px;"></th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (OrderDetail item : cart) { %>
                            <tr class="cart-item-row">
                                <td>
                                    <div class="item-title"><%= item.getProductName() %></div>
                                    <div class="item-unit-price"><%= df.format(item.getUnitPrice()) %> đ</div>
                                </td>
                                <td style="text-align: center;">
                                    <div class="qty-controls">
                                        <a href="<%= request.getContextPath() %>/cart?action=update&productId=<%= item.getProductId() %>&delta=-1" 
                                           class="btn-qty" title="Giảm số lượng">-</a>
                                        <span class="qty-text"><%= item.getQuantity() %></span>
                                        <a href="<%= request.getContextPath() %>/cart?action=update&productId=<%= item.getProductId() %>&delta=1" 
                                           class="btn-qty" title="Tăng số lượng">+</a>
                                    </div>
                                </td>
                                <td style="text-align: right;">
                                    <div class="item-subtotal"><%= df.format(item.getSubTotal()) %> đ</div>
                                </td>
                                <td style="text-align: center;">
                                    <a href="<%= request.getContextPath() %>/cart?action=remove&productId=<%= item.getProductId() %>" 
                                       class="btn-del-item" title="Xóa món này khỏi đơn">×</a>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } %>
        </div>

        <!-- Khung Tính Tiền & Thanh Toán POS -->
        <form action="<%= request.getContextPath() %>/checkout" method="POST" id="checkoutForm">
            <!-- Lưu tổng tiền dạng số cho script tính toán -->
            <input type="hidden" id="totalAmountInput" value="<%= totalCartAmount %>">

            <div class="payment-box">
                <!-- Tạm tính -->
                <div class="pay-summary-row">
                    <span>Tạm tính (<%= totalItemCount %> món):</span>
                    <span class="pay-summary-val"><%= df.format(totalCartAmount) %> đ</span>
                </div>

                <!-- Thẻ Tổng Tiền Nổi Bật -->
                <div class="total-highlight-card">
                    <div class="total-title-box">
                        <span class="total-label">TỔNG CẦN THU</span>
                        <span class="total-items-count">Đã gồm thuế & phụ phí</span>
                    </div>
                    <span class="total-val" id="totalAmountDisplay"><%= df.format(totalCartAmount) %> đ</span>
                </div>

                <!-- Ô Nhập Tiền Khách Đưa -->
                <div class="cash-input-section">
                    <div class="cash-label-row">
                        <label class="cash-label" for="customerCash">Tiền khách đưa:</label>
                        <span style="font-size: 11px; color: var(--text-muted);">Bấm số hoặc chọn gợi ý</span>
                    </div>

                    <div class="cash-input-wrapper">
                        <input type="text" id="customerCash" name="customerCash" class="cash-input" 
                               placeholder="0" value="<%= df.format(totalCartAmount) %>" oninput="calculateChange()">
                        <span class="currency-badge">VNĐ</span>
                    </div>
                    
                    <!-- Các nút gợi ý mệnh giá tiền nhanh -->
                    <div class="quick-cash-tags">
                        <span class="quick-tag" onclick="setCash(<%= totalCartAmount.longValue() %>)">Vừa đủ</span>
                        <span class="quick-tag" onclick="setCash(50000)">50K</span>
                        <span class="quick-tag" onclick="setCash(100000)">100K</span>
                        <span class="quick-tag" onclick="setCash(200000)">200K</span>
                        <span class="quick-tag" onclick="setCash(500000)">500K</span>
                    </div>
                </div>

                <!-- Tiền thừa trả khách -->
                <div class="change-box" id="changeBox">
                    <span class="change-label">Tiền thừa trả khách:</span>
                    <span class="change-val" id="changeMoneyDisplay">0 đ</span>
                </div>
            </div>

            <!-- Nút Xác Nhận Thanh Toán To Rõ Ràng -->
            <button type="submit" class="btn-checkout" id="btnPay" 
                    <%= (cart == null || cart.isEmpty()) ? "disabled" : "" %>>
                <span>⚡ XÁC NHẬN THANH TOÁN</span>
                <span class="shortcut-badge">F9</span>
            </button>
        </form>
    </section>

</main>

<!-- File kịch bản tương tác POS -->
<script src="<%= request.getContextPath() %>/js/pos.js"></script>

</body>
</html>
