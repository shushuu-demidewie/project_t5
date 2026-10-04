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

    @SuppressWarnings("unchecked")
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    @SuppressWarnings("unchecked")
    List<Product> products = (List<Product>) request.getAttribute("products");
    @SuppressWarnings("unchecked")
    List<OrderDetail> cart = (List<OrderDetail>) session.getAttribute("cart");

    Integer selectedCatId = (Integer) request.getAttribute("selectedCatId");
    if (selectedCatId == null) selectedCatId = 0;

    String keyword = (String) request.getAttribute("keyword");
    if (keyword == null) keyword = "";

    Double totalCartAmount = (Double) request.getAttribute("totalCartAmount");
    if (totalCartAmount == null) totalCartAmount = 0.0;

    DecimalFormat df = new DecimalFormat("#,##0");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>POS Bán Hàng - Trà Sữa & Coffee</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --primary: #0d9488;
            --primary-dark: #0f766e;
            --primary-light: #ccfbf1;
            --secondary: #f59e0b;
            --danger: #ef4444;
            --success: #10b981;
            --text-main: #0f172a;
            --text-muted: #64748b;
            --bg-body: #f1f5f9;
            --bg-card: #ffffff;
            --border: #e2e8f0;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
        }

        body {
            background-color: var(--bg-body);
            color: var(--text-main);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }

        /* Header Bar */
        .pos-header {
            background: #ffffff;
            border-bottom: 1px solid var(--border);
            padding: 12px 24px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            position: sticky;
            top: 0;
            z-index: 100;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
        }

        .header-brand {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .brand-logo {
            width: 40px;
            height: 40px;
            background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            color: #ffffff;
        }

        .brand-title {
            font-size: 18px;
            font-weight: 700;
            color: var(--text-main);
        }

        .brand-sub {
            font-size: 12px;
            color: var(--text-muted);
        }

        .header-meta {
            display: flex;
            align-items: center;
            gap: 20px;
        }

        .clock-display {
            font-size: 13px;
            font-weight: 600;
            color: var(--primary-dark);
            background: var(--primary-light);
            padding: 6px 12px;
            border-radius: 20px;
        }

        .cashier-info {
            font-size: 13px;
            text-align: right;
        }

        .cashier-name {
            font-weight: 600;
            color: var(--text-main);
        }

        .cashier-role {
            font-size: 11px;
            color: var(--text-muted);
        }

        .btn-logout {
            padding: 7px 14px;
            font-size: 13px;
            font-weight: 600;
            color: var(--danger);
            background: #fef2f2;
            border: 1px solid #fecaca;
            border-radius: 8px;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.2s;
        }

        .btn-logout:hover {
            background: var(--danger);
            color: #ffffff;
        }

        /* POS Layout 2 Cột */
        .pos-container {
            display: flex;
            flex: 1;
            height: calc(100vh - 65px);
            overflow: hidden;
        }

        /* Cột Trái: Thực đơn & Menu (60%) */
        .pos-left {
            flex: 1.2;
            display: flex;
            flex-direction: column;
            padding: 16px 20px;
            overflow-y: auto;
            border-right: 1px solid var(--border);
        }

        /* Thanh Tìm kiếm và Danh mục */
        .menu-filter-bar {
            display: flex;
            flex-direction: column;
            gap: 12px;
            margin-bottom: 16px;
        }

        .search-form {
            display: flex;
            gap: 8px;
        }

        .search-input {
            flex: 1;
            padding: 10px 14px;
            font-size: 14px;
            border: 1px solid var(--border);
            border-radius: 10px;
            outline: none;
            background: #ffffff;
        }

        .search-input:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(13, 148, 136, 0.15);
        }

        .btn-search {
            padding: 10px 18px;
            font-size: 14px;
            font-weight: 600;
            background: var(--primary);
            color: #ffffff;
            border: none;
            border-radius: 10px;
            cursor: pointer;
        }

        .category-pills {
            display: flex;
            gap: 8px;
            overflow-x: auto;
            padding-bottom: 4px;
        }

        .category-pill {
            padding: 8px 16px;
            font-size: 13px;
            font-weight: 600;
            white-space: nowrap;
            background: #ffffff;
            color: var(--text-muted);
            border: 1px solid var(--border);
            border-radius: 20px;
            text-decoration: none;
            transition: all 0.2s;
        }

        .category-pill:hover,
        .category-pill.active {
            background: var(--primary);
            color: #ffffff;
            border-color: var(--primary);
        }

        /* Grid Danh Sách Món */
        .product-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
            gap: 14px;
            overflow-y: auto;
            padding-right: 4px;
        }

        .product-card {
            background: #ffffff;
            border-radius: 14px;
            border: 1px solid var(--border);
            padding: 14px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            transition: transform 0.2s, box-shadow 0.2s;
            position: relative;
        }

        .product-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 16px rgba(0, 0, 0, 0.06);
            border-color: #cbd5e1;
        }

        .product-cat {
            font-size: 11px;
            font-weight: 600;
            color: var(--primary);
            text-transform: uppercase;
            margin-bottom: 4px;
        }

        .product-name {
            font-size: 14px;
            font-weight: 700;
            color: var(--text-main);
            margin-bottom: 8px;
            line-height: 1.3;
        }

        .product-price {
            font-size: 15px;
            font-weight: 700;
            color: var(--danger);
            margin-bottom: 12px;
        }

        .btn-add-cart {
            width: 100%;
            padding: 8px;
            background: #f0fdf4;
            color: var(--success);
            border: 1px solid #bbf7d0;
            border-radius: 8px;
            font-size: 13px;
            font-weight: 600;
            cursor: pointer;
            text-decoration: none;
            text-align: center;
            display: block;
            transition: all 0.2s;
        }

        .btn-add-cart:hover {
            background: var(--success);
            color: #ffffff;
        }

        /* Cột Phải: Giỏ Hàng & Thanh Toán (40%) */
        .pos-right {
            flex: 0.9;
            display: flex;
            flex-direction: column;
            background: #ffffff;
            padding: 16px 20px;
            box-shadow: -2px 0 6px rgba(0, 0, 0, 0.02);
            overflow-y: auto;
        }

        .cart-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding-bottom: 12px;
            border-bottom: 1px solid var(--border);
            margin-bottom: 12px;
        }

        .cart-title {
            font-size: 16px;
            font-weight: 700;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .btn-clear-cart {
            font-size: 12px;
            color: var(--danger);
            background: none;
            border: none;
            cursor: pointer;
            text-decoration: underline;
        }

        /* Bảng Giỏ Hàng */
        .cart-table-wrapper {
            flex: 1;
            overflow-y: auto;
            margin-bottom: 14px;
        }

        .cart-table {
            width: 100%;
            border-collapse: collapse;
            font-size: 13px;
        }

        .cart-table th {
            text-align: left;
            padding: 8px 6px;
            background: #f8fafc;
            color: var(--text-muted);
            font-size: 12px;
            font-weight: 600;
            border-bottom: 1px solid var(--border);
        }

        .cart-table td {
            padding: 10px 6px;
            border-bottom: 1px solid #f1f5f9;
            vertical-align: middle;
        }

        .qty-controls {
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .btn-qty {
            width: 24px;
            height: 24px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 6px;
            border: 1px solid var(--border);
            background: #f8fafc;
            font-weight: 700;
            font-size: 13px;
            cursor: pointer;
            text-decoration: none;
            color: var(--text-main);
        }

        .btn-qty:hover {
            background: #e2e8f0;
        }

        .qty-text {
            font-weight: 600;
            min-width: 18px;
            text-align: center;
        }

        .btn-del-item {
            color: var(--danger);
            text-decoration: none;
            font-weight: bold;
            font-size: 16px;
            padding: 0 4px;
        }

        .empty-cart-msg {
            text-align: center;
            padding: 40px 10px;
            color: var(--text-muted);
            font-size: 13px;
        }

        /* Bảng Chi Tiết Thanh Toán */
        .payment-box {
            background: #f8fafc;
            border: 1px solid var(--border);
            border-radius: 12px;
            padding: 14px;
        }

        .pay-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;
            font-size: 13px;
        }

        .pay-row.total {
            border-top: 1px dashed var(--border);
            padding-top: 10px;
            margin-top: 6px;
        }

        .total-label {
            font-size: 16px;
            font-weight: 700;
        }

        .total-val {
            font-size: 20px;
            font-weight: 800;
            color: var(--danger);
        }

        .cash-input-row {
            margin-top: 8px;
            display: flex;
            flex-direction: column;
            gap: 6px;
        }

        .cash-label {
            font-size: 13px;
            font-weight: 600;
            color: var(--text-main);
        }

        .cash-input {
            width: 100%;
            padding: 10px 12px;
            font-size: 16px;
            font-weight: 700;
            text-align: right;
            border: 1.5px solid var(--border);
            border-radius: 8px;
            outline: none;
            background: #ffffff;
        }

        .cash-input:focus {
            border-color: var(--primary);
        }

        .quick-cash-tags {
            display: flex;
            gap: 6px;
            margin-top: 6px;
            flex-wrap: wrap;
        }

        .quick-tag {
            font-size: 11px;
            padding: 3px 8px;
            background: #ffffff;
            border: 1px solid var(--border);
            border-radius: 12px;
            cursor: pointer;
            font-weight: 600;
            color: var(--text-muted);
        }

        .quick-tag:hover {
            border-color: var(--primary);
            color: var(--primary);
        }

        .change-row {
            display: flex;
            justify-content: space-between;
            margin-top: 10px;
            font-size: 14px;
            font-weight: 700;
        }

        .change-val {
            color: var(--success);
        }

        .btn-checkout {
            width: 100%;
            padding: 14px;
            margin-top: 14px;
            background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
            color: #ffffff;
            border: none;
            border-radius: 10px;
            font-size: 15px;
            font-weight: 700;
            cursor: pointer;
            box-shadow: 0 4px 12px rgba(13, 148, 136, 0.3);
            transition: all 0.2s;
        }

        .btn-checkout:hover {
            opacity: 0.95;
            transform: translateY(-1px);
        }

        .btn-checkout:disabled {
            background: #cbd5e1;
            cursor: not-allowed;
            box-shadow: none;
            transform: none;
        }
    </style>
</head>
<body>

<!-- Header Bar -->
<header class="pos-header">
    <div class="header-brand">
        <div class="brand-logo">🧋</div>
        <div>
            <div class="brand-title">TRÀ SỮA & CAFE POS</div>
            <div class="brand-sub">Hệ thống thu ngân trực tuyến</div>
        </div>
    </div>

    <div class="header-meta">
        <div class="clock-display" id="clock">--:--:--</div>
        <div class="cashier-info">
            <div class="cashier-name"><%= currentUser.getFullName() %></div>
            <div class="cashier-role"><%= currentUser.getRole() %></div>
        </div>
        <a href="<%= request.getContextPath() %>/logout" class="btn-logout" onclick="return confirm('Bạn có chắc muốn đăng xuất?')">Đăng xuất</a>
    </div>
</header>

<!-- Main Container 2 Cột -->
<main class="pos-container">

    <!-- CỘT BÊN TRÁI: DANH MỤC & THỰC ĐƠN -->
    <section class="pos-left">
        <div class="menu-filter-bar">
            <!-- Ô tìm kiếm món -->
            <form action="<%= request.getContextPath() %>/pos" method="GET" class="search-form">
                <input type="text" name="keyword" class="search-input" 
                       placeholder="🔍 Tìm kiếm tên món..." value="<%= keyword %>">
                <button type="submit" class="btn-search">Tìm kiếm</button>
            </form>

            <!-- Danh mục dạng Pills -->
            <div class="category-pills">
                <a href="<%= request.getContextPath() %>/pos" 
                   class="category-pill <%= selectedCatId == 0 ? "active" : "" %>">
                    Tất cả thực đơn
                </a>
                <% 
                    if (categories != null) {
                        for (Category cat : categories) { 
                %>
                    <a href="<%= request.getContextPath() %>/pos?categoryId=<%= cat.getId() %>" 
                       class="category-pill <%= selectedCatId == cat.getId() ? "active" : "" %>">
                        <%= cat.getCategoryName() %>
                    </a>
                <% 
                        }
                    } 
                %>
            </div>
        </div>

        <!-- Lưới món ăn/đồ uống -->
        <div class="product-grid">
            <% 
                if (products != null && !products.isEmpty()) {
                    for (Product p : products) { 
            %>
                <div class="product-card">
                    <div>
                        <div class="product-cat"><%= p.getCategoryName() != null ? p.getCategoryName() : "Khác" %></div>
                        <div class="product-name"><%= p.getProductName() %></div>
                    </div>
                    <div>
                        <div class="product-price"><%= df.format(p.getPrice()) %> đ</div>
                        <a href="<%= request.getContextPath() %>/cart?action=add&productId=<%= p.getId() %>" 
                           class="btn-add-cart">
                            + Thêm vào giỏ
                        </a>
                    </div>
                </div>
            <% 
                    }
                } else { 
            %>
                <div style="grid-column: 1 / -1; text-align: center; padding: 40px; color: var(--text-muted);">
                    Không tìm thấy món nào phù hợp với điều kiện tìm kiếm.
                </div>
            <% } %>
        </div>
    </section>

    <!-- CỘT BÊN PHẢI: GIỎ HÀNG TẠM & THANH TOÁN -->
    <section class="pos-right">
        <div class="cart-header">
            <div class="cart-title">
                <span>🛒 Hóa đơn tạm tính</span>
                <span style="font-size: 12px; color: var(--primary); background: var(--primary-light); padding: 2px 8px; border-radius: 12px;">
                    <%= cart != null ? cart.size() : 0 %> món
                </span>
            </div>
            <% if (cart != null && !cart.isEmpty()) { %>
                <form action="<%= request.getContextPath() %>/cart" method="POST" style="margin: 0;">
                    <input type="hidden" name="action" value="clear">
                    <button type="submit" class="btn-clear-cart" onclick="return confirm('Bạn có chắc muốn xóa sạch giỏ hàng?')">Xóa giỏ</button>
                </form>
            <% } %>
        </div>

        <!-- Bảng giỏ hàng -->
        <div class="cart-table-wrapper">
            <% if (cart == null || cart.isEmpty()) { %>
                <div class="empty-cart-msg">
                    <p style="font-size: 32px; margin-bottom: 8px;">🛒</p>
                    <p>Giỏ hàng đang trống.</p>
                    <p style="font-size: 12px; margin-top: 4px;">Vui lòng chọn món từ thực đơn bên trái để thêm vào đơn.</p>
                </div>
            <% } else { %>
                <table class="cart-table">
                    <thead>
                        <tr>
                            <th>Món</th>
                            <th style="text-align: right;">Đ.Giá</th>
                            <th style="text-align: center;">SL</th>
                            <th style="text-align: right;">T.Tiền</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (OrderDetail item : cart) { %>
                            <tr>
                                <td>
                                    <div style="font-weight: 600;"><%= item.getProductName() %></div>
                                </td>
                                <td style="text-align: right; color: var(--text-muted);"><%= df.format(item.getUnitPrice()) %></td>
                                <td>
                                    <div class="qty-controls">
                                        <a href="<%= request.getContextPath() %>/cart?action=update&productId=<%= item.getProductId() %>&delta=-1" 
                                           class="btn-qty">-</a>
                                        <span class="qty-text"><%= item.getQuantity() %></span>
                                        <a href="<%= request.getContextPath() %>/cart?action=update&productId=<%= item.getProductId() %>&delta=1" 
                                           class="btn-qty">+</a>
                                    </div>
                                </td>
                                <td style="text-align: right; font-weight: 700; color: var(--text-main);">
                                    <%= df.format(item.getSubTotal()) %>
                                </td>
                                <td style="text-align: center;">
                                    <a href="<%= request.getContextPath() %>/cart?action=remove&productId=<%= item.getProductId() %>" 
                                       class="btn-del-item" title="Xóa món">×</a>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } %>
        </div>

        <!-- Khung Thanh Toán -->
        <form action="<%= request.getContextPath() %>/checkout" method="POST" id="checkoutForm">
            <div class="payment-box">
                <div class="pay-row total">
                    <span class="total-label">TỔNG TIỀN:</span>
                    <span class="total-val" id="totalAmountDisplay"><%= df.format(totalCartAmount) %> đ</span>
                </div>

                <div class="cash-input-row">
                    <label class="cash-label" for="customerCash">Tiền khách đưa (VNĐ):</label>
                    <input type="text" id="customerCash" name="customerCash" class="cash-input" 
                           placeholder="0" value="<%= df.format(totalCartAmount) %>" oninput="calculateChange()">
                    
                    <!-- Nút gợi ý tiền nhanh -->
                    <div class="quick-cash-tags">
                        <span class="quick-tag" onclick="setCash(<%= totalCartAmount %>)">Vừa đủ</span>
                        <span class="quick-tag" onclick="setCash(50000)">50.000</span>
                        <span class="quick-tag" onclick="setCash(100000)">100.000</span>
                        <span class="quick-tag" onclick="setCash(200000)">200.000</span>
                        <span class="quick-tag" onclick="setCash(500000)">500.000</span>
                    </div>
                </div>

                <div class="change-row">
                    <span>Tiền thừa trả khách:</span>
                    <span class="change-val" id="changeMoneyDisplay">0 đ</span>
                </div>
            </div>

            <button type="submit" class="btn-checkout" id="btnPay" 
                    <%= (cart == null || cart.isEmpty()) ? "disabled" : "" %>>
                XÁC NHẬN THANH TOÁN (F9)
            </button>
        </form>
    </section>

</main>

<script>
    // Đồng hồ chạy thời gian thực
    function updateClock() {
        const now = new Date();
        const timeStr = now.toLocaleTimeString('vi-VN', { hour12: false });
        document.getElementById('clock').textContent = timeStr;
    }
    setInterval(updateClock, 1000);
    updateClock();

    // Tính toán tiền thừa theo thời gian thực
    const totalAmount = <%= totalCartAmount %>;

    function formatNumber(num) {
        return new Intl.NumberFormat('vi-VN').format(num);
    }

    function calculateChange() {
        const cashInput = document.getElementById('customerCash');
        const changeDisplay = document.getElementById('changeMoneyDisplay');
        const rawCash = cashInput.value.replace(/[^0-9]/g, '');
        const customerCash = rawCash ? parseFloat(rawCash) : 0;

        const change = customerCash - totalAmount;

        if (customerCash < totalAmount) {
            changeDisplay.textContent = 'Còn thiếu: ' + formatNumber(Math.abs(change)) + ' đ';
            changeDisplay.style.color = '#ef4444'; // Đỏ
        } else {
            changeDisplay.textContent = formatNumber(change) + ' đ';
            changeDisplay.style.color = '#10b981'; // Xanh lá
        }
    }

    function setCash(amount) {
        document.getElementById('customerCash').value = formatNumber(amount);
        calculateChange();
    }

    // Phím tắt F9 để thanh toán nhanh
    window.addEventListener('keydown', function(e) {
        if (e.key === 'F9') {
            e.preventDefault();
            const btnPay = document.getElementById('btnPay');
            if (btnPay && !btnPay.disabled) {
                document.getElementById('checkoutForm').submit();
            }
        }
    });

    // Khởi chạy tính tiền thừa ban đầu
    calculateChange();
</script>

</body>
</html>
