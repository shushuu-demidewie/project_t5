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
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/pos.css">
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
                   class="category-pill <%= (selectedCatId == 0) ? "active" : "" %>">
                    Tất cả thực đơn
                </a>
                <% 
                    if (categories != null) {
                        for (Category cat : categories) { 
                %>
                    <a href="<%= request.getContextPath() %>/pos?categoryId=<%= cat.getId() %>" 
                       class="category-pill <%= (selectedCatId == cat.getId()) ? "active" : "" %>">
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
                        <div class="product-cat"><%= (p.getCategoryName() != null) ? p.getCategoryName() : "Khác" %></div>
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
                    <%= (cart != null) ? cart.size() : 0 %> món
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
            <!-- Lưu tổng tiền dưới dạng hidden input cho Javascript đọc -->
            <input type="hidden" id="totalAmountInput" value="<%= totalCartAmount %>">

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
                        <span class="quick-tag" onclick="setCash(<%= totalCartAmount.longValue() %>)">Vừa đủ</span>
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

<!-- File script xử lý tính tiền và đồng hồ -->
<script src="<%= request.getContextPath() %>/js/pos.js"></script>

</body>
</html>
