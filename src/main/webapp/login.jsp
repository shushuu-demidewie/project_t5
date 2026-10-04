<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập hệ thống POS - Trà Sữa & Cafe</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
        }

        body {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #0f766e 100%);
            padding: 20px;
        }

        .login-card {
            width: 100%;
            max-width: 440px;
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(16px);
            border-radius: 20px;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.35);
            overflow: hidden;
            border: 1px solid rgba(255, 255, 255, 0.2);
            animation: fadeIn 0.4s ease-out;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(15px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .card-header {
            background: linear-gradient(135deg, #0f766e 0%, #0d9488 100%);
            color: #ffffff;
            padding: 32px 28px 24px;
            text-align: center;
        }

        .brand-badge {
            display: inline-block;
            background: rgba(255, 255, 255, 0.2);
            padding: 4px 14px;
            border-radius: 30px;
            font-size: 12px;
            font-weight: 600;
            letter-spacing: 0.5px;
            text-transform: uppercase;
            margin-bottom: 12px;
        }

        .card-header h1 {
            font-size: 22px;
            font-weight: 700;
            margin-bottom: 6px;
        }

        .card-header p {
            font-size: 13px;
            color: #ccfbf1;
        }

        .card-body {
            padding: 32px 28px 28px;
        }

        .alert-error {
            background-color: #fef2f2;
            color: #dc2626;
            border: 1px solid #fecaca;
            padding: 12px 14px;
            border-radius: 10px;
            font-size: 13px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-label {
            display: block;
            font-size: 13px;
            font-weight: 600;
            color: #334155;
            margin-bottom: 7px;
        }

        .form-input {
            width: 100%;
            padding: 12px 14px;
            font-size: 14px;
            border: 1.5px solid #cbd5e1;
            border-radius: 10px;
            outline: none;
            transition: all 0.2s ease;
            background-color: #f8fafc;
        }

        .form-input:focus {
            background-color: #ffffff;
            border-color: #0d9488;
            box-shadow: 0 0 0 3px rgba(13, 148, 136, 0.15);
        }

        .btn-submit {
            width: 100%;
            padding: 13px;
            font-size: 15px;
            font-weight: 600;
            color: #ffffff;
            background: linear-gradient(135deg, #0d9488 0%, #0f766e 100%);
            border: none;
            border-radius: 10px;
            cursor: pointer;
            transition: all 0.2s ease;
            box-shadow: 0 4px 12px rgba(13, 148, 136, 0.3);
            margin-top: 10px;
        }

        .btn-submit:hover {
            opacity: 0.95;
            transform: translateY(-1px);
            box-shadow: 0 6px 16px rgba(13, 148, 136, 0.4);
        }

        .btn-submit:active {
            transform: translateY(0);
        }

        .demo-accounts {
            margin-top: 24px;
            padding: 14px;
            background-color: #f1f5f9;
            border-radius: 10px;
            border-left: 4px solid #0d9488;
            font-size: 12px;
            color: #475569;
        }

        .demo-accounts strong {
            color: #1e293b;
        }

        .demo-tag {
            display: inline-block;
            background: #e2e8f0;
            padding: 2px 6px;
            border-radius: 4px;
            font-family: monospace;
            font-size: 11px;
            color: #0f172a;
            cursor: pointer;
        }
    </style>
</head>
<body>

<div class="login-card">
    <div class="card-header">
        <span class="brand-badge">Trà Sữa & Coffee POS</span>
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

        <form action="<%= request.getContextPath() %>/login" method="POST">
            <div class="form-group">
                <label class="form-label" for="username">Tên đăng nhập</label>
                <input type="text" id="username" name="username" class="form-input" 
                       placeholder="Nhập tên tài khoản..." required autofocus
                       value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>">
            </div>

            <div class="form-group">
                <label class="form-label" for="password">Mật khẩu</label>
                <input type="password" id="password" name="password" class="form-input" 
                       placeholder="Nhập mật khẩu..." required>
            </div>

            <button type="submit" class="btn-submit">Đăng nhập</button>
        </form>

        <div class="demo-accounts">
            <strong>Tài khoản thử nghiệm:</strong><br>
            • Quản lý: <span class="demo-tag" onclick="fillForm('admin', '123456')">admin / 123456</span><br>
            • Thu ngân: <span class="demo-tag" onclick="fillForm('staff01', '123')">staff01 / 123</span>
        </div>
    </div>
</div>

<script>
    function fillForm(user, pass) {
        document.getElementById('username').value = user;
        document.getElementById('password').value = pass;
    }
</script>

</body>
</html>
