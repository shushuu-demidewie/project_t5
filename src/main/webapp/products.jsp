<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="model.Product"%>
<%@ page import="model.Category"%>
<%@ page import="model.User"%>
<%
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    Object productsObj = request.getAttribute("products");
    List<Product> products = (productsObj instanceof List) ? (List<Product>) productsObj : null;

    Object categoriesObj = request.getAttribute("categories");
    List<Category> categories = (categoriesObj instanceof List) ? (List<Category>) categoriesObj : null;

    Integer selectedCatId = (Integer) request.getAttribute("selectedCatId");
    if (selectedCatId == null) selectedCatId = 0;

    String keyword = (String) request.getAttribute("keyword");
    if (keyword == null) keyword = "";

    String msg = request.getParameter("msg");
    String error = request.getParameter("error");

    DecimalFormat df = new DecimalFormat("#,##0");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Sản Phẩm - Boba & Coffee Station</title>
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <!-- Dedicated Products Stylesheet -->
    <link rel="stylesheet" href="<%= ctx %>/css/products.css">
</head>
<body data-context="<%= ctx %>">

<!-- Header Bar -->
<header class="products-header">
    <div class="brand-section">
        <div class="brand-icon">MENU</div>
        <div class="brand-info">
            <h1>QUẢN LÝ SẢN PHẨM & THỰC ĐƠN</h1>
            <p>Thêm món, cập nhật giá bán và trạng thái món trong quầy</p>
        </div>
    </div>

    <div class="nav-actions">
        <a href="<%= ctx %>/pos" class="btn-nav-link btn-nav-primary">
            <span>Quầy Bán Hàng (POS)</span>
        </a>
        <a href="<%= ctx %>/orders" class="btn-nav-link">
            <span>Lịch Sử Đơn</span>
        </a>
        <a href="<%= ctx %>/logout" class="btn-nav-link"
           onclick="return confirm('Bạn có chắc chắn muốn đăng xuất?')">
            <span>Đăng xuất</span>
        </a>
    </div>
</header>

<main class="products-container">

    <!-- Thông báo kết quả thao tác -->
    <% if ("add_success".equals(msg)) { %>
        <div class="alert-box alert-success">
            <span>Đã thêm món mới vào thực đơn thành công!</span>
            <button class="btn-alert-close" onclick="this.parentElement.remove()">&times;</button>
        </div>
    <% } else if ("update_success".equals(msg)) { %>
        <div class="alert-box alert-success">
            <span>Cập nhật thông tin món thành công!</span>
            <button class="btn-alert-close" onclick="this.parentElement.remove()">&times;</button>
        </div>
    <% } else if ("delete_success".equals(msg)) { %>
        <div class="alert-box alert-success">
            <span>Đã xóa món khỏi thực đơn thành công!</span>
            <button class="btn-alert-close" onclick="this.parentElement.remove()">&times;</button>
        </div>
    <% } else if ("fk_constraint".equals(error)) { %>
        <div class="alert-box alert-error">
            <span>Không thể xóa món này do đã có trong các đơn hàng đã thanh toán. Gợi ý: Hãy bấm "Sửa" và đổi trạng thái thành "Hết hàng".</span>
            <button class="btn-alert-close" onclick="this.parentElement.remove()">&times;</button>
        </div>
    <% } else if ("missing_fields".equals(error)) { %>
        <div class="alert-box alert-error">
            <span>Vui lòng điền đầy đủ tên món, danh mục và giá bán hợp lệ!</span>
            <button class="btn-alert-close" onclick="this.parentElement.remove()">&times;</button>
        </div>
    <% } else if (error != null) { %>
        <div class="alert-box alert-error">
            <span>Thao tác không thành công, vui lòng kiểm tra lại dữ liệu.</span>
            <button class="btn-alert-close" onclick="this.parentElement.remove()">&times;</button>
        </div>
    <% } %>

    <!-- Toolbar: Tìm kiếm, Lọc danh mục & Nút Thêm mới -->
    <section class="toolbar-card">
        <form action="<%= ctx %>/products" method="GET" class="search-filter-group">
            <div class="search-input-wrap">
                <span class="search-icon">&#128269;</span>
                <input type="text" name="keyword" class="search-input" 
                       placeholder="Tìm theo tên món..." value="<%= keyword %>">
            </div>

            <select name="catId" class="select-filter" onchange="this.form.submit()">
                <option value="0">-- Tất cả danh mục --</option>
                <% if (categories != null) {
                    for (Category c : categories) {
                        String isSelected = (selectedCatId == c.getId()) ? "selected" : "";
                %>
                    <option value="<%= c.getId() %>" <%= isSelected %>>
                        <%= c.getCategoryName() %>
                    </option>
                <%  }
                } %>
            </select>

            <button type="submit" class="btn-filter-submit">Tìm kiếm</button>

            <% if (!keyword.isEmpty() || selectedCatId > 0) { %>
                <a href="<%= ctx %>/products" class="btn-filter-reset">Đặt lại</a>
            <% } %>
        </form>

        <button type="button" class="btn-add-product" id="btnOpenAddModal">
            <span>+</span> Thêm Món Mới
        </button>
    </section>

    <!-- Bảng danh sách sản phẩm -->
    <section class="table-card">
        <div class="table-responsive">
            <table class="products-table">
                <thead>
                    <tr>
                        <th class="col-id">Mã</th>
                        <th>Tên Món</th>
                        <th>Danh Mục</th>
                        <th>Giá Bán</th>
                        <th>Trạng Thái</th>
                        <th style="text-align: right;">Thao Tác</th>
                    </tr>
                </thead>
                <tbody>
                <% if (products != null && !products.isEmpty()) {
                    for (Product p : products) { 
                        boolean isActive = "Còn hàng".equalsIgnoreCase(p.getStatus()) || "Active".equalsIgnoreCase(p.getStatus());
                        String pillClass = isActive ? "status-pill active" : "status-pill inactive";
                        String safeCatName = (p.getCategoryName() != null && !p.getCategoryName().isEmpty()) ? p.getCategoryName() : "Khác";
                        String safeStatus = (p.getStatus() != null && !p.getStatus().isEmpty()) ? p.getStatus() : "Còn hàng";
                %>
                    <tr data-id="<%= p.getId() %>"
                        data-name="<%= p.getProductName() %>"
                        data-catid="<%= p.getCategoryId() %>"
                        data-price="<%= (long) p.getPrice() %>"
                        data-status="<%= safeStatus %>">
                        <td class="col-id">#<%= p.getId() %></td>
                        <td class="col-name"><%= p.getProductName() %></td>
                        <td>
                            <span class="badge-cat"><%= safeCatName %></span>
                        </td>
                        <td class="col-price"><%= df.format(p.getPrice()) %> đ</td>
                        <td>
                            <span class="<%= pillClass %>">
                                <span class="status-dot-sm"></span>
                                <%= safeStatus %>
                            </span>
                        </td>
                        <td>
                            <div class="action-buttons" style="justify-content: flex-end;">
                                <button type="button" class="btn-action btn-edit">
                                    Sửa
                                </button>
                                <button type="button" class="btn-action btn-delete">
                                    Xóa
                                </button>
                            </div>
                        </td>
                    </tr>
                <%  } 
                } else { %>
                    <tr>
                        <td colspan="6">
                            <div class="empty-table-state">
                                <div class="empty-icon">&#128269;</div>
                                <p>Không tìm thấy món ăn nào phù hợp với điều kiện lọc.</p>
                            </div>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </section>

</main>

<!-- Modal Popup: Thêm / Sửa Sản Phẩm -->
<div class="modal-overlay" id="productModal">
    <div class="modal-card">
        <div class="modal-header">
            <h2 class="modal-title" id="modalTitle">Thêm Món Mới</h2>
            <button type="button" class="modal-close-btn" id="btnCloseModalX">&times;</button>
        </div>

        <form action="<%= ctx %>/products" method="POST" id="productForm">
            <input type="hidden" name="action" id="formAction" value="add">
            <input type="hidden" name="id" id="productId" value="">

            <div class="modal-body">
                <div class="form-group">
                    <label class="form-label" for="productName">Tên Món *</label>
                    <input type="text" name="productName" id="productName" class="form-control" 
                           placeholder="Ví dụ: Trà Sữa Trân Châu Đường Đen" required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="categoryId">Danh Mục *</label>
                    <select name="categoryId" id="categoryId" class="form-control" required>
                        <% if (categories != null) {
                            for (Category c : categories) { %>
                            <option value="<%= c.getId() %>"><%= c.getCategoryName() %></option>
                        <%  }
                        } %>
                    </select>
                </div>

                <div class="form-group">
                    <label class="form-label" for="price">Giá Bán (VNĐ) *</label>
                    <input type="number" name="price" id="price" class="form-control" 
                           placeholder="Ví dụ: 35000" min="0" step="1000" required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="status">Trạng Thái</label>
                    <select name="status" id="status" class="form-control">
                        <option value="Còn hàng">Còn hàng</option>
                        <option value="Hết hàng">Hết hàng (Tạm ngưng)</option>
                    </select>
                </div>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn-secondary" id="btnCancelModal">Hủy bỏ</button>
                <button type="submit" class="btn-primary" id="btnSubmitForm">Lưu Thay Đổi</button>
            </div>
        </form>
    </div>
</div>

<script>
    const contextPath = document.body.getAttribute('data-context') || '';
    const modal = document.getElementById('productModal');
    const modalTitle = document.getElementById('modalTitle');
    const formAction = document.getElementById('formAction');
    const productId = document.getElementById('productId');
    const productName = document.getElementById('productName');
    const categoryId = document.getElementById('categoryId');
    const price = document.getElementById('price');
    const status = document.getElementById('status');
    const btnSubmit = document.getElementById('btnSubmitForm');

    function openAddModal() {
        modalTitle.textContent = "Thêm Món Mới Vào Thực Đơn";
        formAction.value = "add";
        productId.value = "";
        productName.value = "";
        price.value = "";
        status.value = "Còn hàng";
        btnSubmit.textContent = "Thêm Món";
        modal.classList.add('open');
        productName.focus();
    }

    function openEditModal(id, name, catId, prc, stt) {
        modalTitle.textContent = "Chỉnh Sửa Món #" + id;
        formAction.value = "update";
        productId.value = id;
        productName.value = name;
        if (categoryId) categoryId.value = catId;
        price.value = prc;
        if (status) status.value = stt;
        btnSubmit.textContent = "Lưu Cập Nhật";
        modal.classList.add('open');
        productName.focus();
    }

    function closeModal() {
        modal.classList.remove('open');
    }

    function confirmDelete(id, name) {
        if (confirm("Bạn có chắc chắn muốn xóa món \"" + name + "\" khỏi thực đơn không?\n(Lưu ý: Không thể hoàn tác sau khi xóa)")) {
            window.location.href = contextPath + "/products?action=delete&id=" + id;
        }
    }

    document.addEventListener('DOMContentLoaded', function() {
        // Nút mở modal thêm mới
        const btnAdd = document.getElementById('btnOpenAddModal');
        if (btnAdd) {
            btnAdd.addEventListener('click', openAddModal);
        }

        // Nút đóng modal
        const btnCloseX = document.getElementById('btnCloseModalX');
        if (btnCloseX) btnCloseX.addEventListener('click', closeModal);

        const btnCancel = document.getElementById('btnCancelModal');
        if (btnCancel) btnCancel.addEventListener('click', closeModal);

        // Click ngoài backdrop để đóng modal
        if (modal) {
            modal.addEventListener('click', function(e) {
                if (e.target === modal) closeModal();
            });
        }

        // Sự kiện cho tất cả nút Sửa
        document.querySelectorAll('.btn-edit').forEach(function(btn) {
            btn.addEventListener('click', function() {
                const tr = this.closest('tr');
                if (!tr) return;
                const id = tr.getAttribute('data-id');
                const name = tr.getAttribute('data-name');
                const catId = tr.getAttribute('data-catid');
                const prc = tr.getAttribute('data-price');
                const stt = tr.getAttribute('data-status');
                openEditModal(id, name, catId, prc, stt);
            });
        });

        // Sự kiện cho tất cả nút Xóa
        document.querySelectorAll('.btn-delete').forEach(function(btn) {
            btn.addEventListener('click', function() {
                const tr = this.closest('tr');
                if (!tr) return;
                const id = tr.getAttribute('data-id');
                const name = tr.getAttribute('data-name');
                confirmDelete(id, name);
            });
        });

        // Nhấn phím Escape để đóng modal
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape' && modal.classList.contains('open')) {
                closeModal();
            }
        });
    });
</script>

</body>
</html>
