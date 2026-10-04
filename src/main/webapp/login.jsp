<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập hệ thống POS - Boba & Coffee Station</title>
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <!-- Dedicated Login Stylesheet -->
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/login.css">
</head>
<body>

<!-- Ambient Boba Bubbles Background -->
<div class="bubble bubble-1"></div>
<div class="bubble bubble-2"></div>
<div class="bubble bubble-3"></div>

<!-- Login Card -->
<div class="login-card">
    <div class="card-header">
        <div class="brand-icon-box">🧋</div>
        <span class="brand-badge">Boba & Coffee POS</span>
        <h1>ĐĂNG NHẬP HỆ THỐNG</h1>
        <p>Quản lý bán hàng & thu ngân chuyên nghiệp</p>
    </div>

    <div class="card-body">
        <% 
            String errorMsg = (String) request.getAttribute("errorMsg");
            if (errorMsg != null && !errorMsg.isEmpty()) { 
        %>
            <div class="alert-error">
                <span>⚠️</span>
                <span><%= errorMsg %></span>
            </div>
        <% } %>

        <form action="<%= request.getContextPath() %>/login" method="POST" id="loginForm">
            <div class="form-group">
                <label class="form-label" for="username">Tên đăng nhập</label>
                <div class="input-wrapper">
                    <span class="input-icon">👤</span>
                    <input type="text" id="username" name="username" class="form-input" 
                           placeholder="Nhập tên tài khoản..." required autofocus
                           value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>">
                </div>
            </div>

            <div class="form-group">
                <label class="form-label" for="password">Mật khẩu</label>
                <div class="input-wrapper">
                    <span class="input-icon">🔒</span>
                    <input type="password" id="password" name="password" class="form-input" 
                           placeholder="Nhập mật khẩu..." required>
                    <button type="button" class="toggle-pwd-btn" id="togglePwdBtn" onclick="togglePasswordVisibility()" title="Ẩn/Hiện mật khẩu">👁️</button>
                </div>
            </div>

            <button type="submit" class="btn-submit" id="submitBtn">
                <span>Đăng nhập hệ thống</span>
                <span>→</span>
            </button>
        </form>

        <!-- Quick Demo Accounts Autofill -->
        <div class="demo-box">
            <div class="demo-title">
                <span>⚡</span>
                <span>Tài khoản thử nghiệm (Bấm để điền nhanh)</span>
            </div>
            <div class="demo-chips">
                <div class="demo-chip" onclick="fillForm('admin', '123456')">
                    <div class="demo-chip-role">
                        <span>👑</span>
                        <span>Quản Trị Viên</span>
                    </div>
                    <span class="demo-chip-cred">admin / 123456</span>
                </div>
                <div class="demo-chip" onclick="fillForm('staff01', '123')">
                    <div class="demo-chip-role">
                        <span>💼</span>
                        <span>Thu Ngân Ca Trực</span>
                    </div>
                    <span class="demo-chip-cred">staff01 / 123</span>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    function fillForm(user, pass) {
        var uInput = document.getElementById('username');
        var pInput = document.getElementById('password');
        uInput.value = user;
        pInput.value = pass;
        
        var btn = document.getElementById('submitBtn');
        btn.style.transform = 'scale(1.02)';
        setTimeout(function() {
            btn.style.transform = '';
        }, 200);
    }

    function togglePasswordVisibility() {
        var pInput = document.getElementById('password');
        var btn = document.getElementById('togglePwdBtn');
        if (pInput.type === 'password') {
            pInput.type = 'text';
            btn.textContent = '🙈';
        } else {
            pInput.type = 'password';
            btn.textContent = '👁️';
        }
    }
</script>

</body>
</html>
