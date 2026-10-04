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
            background-color: #f1f5f9;
            color: #1e293b;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            padding: 30px 15px;
        }

        .bill-card {
            width: 100%;
            max-width: 420px;
            background: #ffffff;
            border-radius: 16px;
            box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
            padding: 28px 24px;
            border: 1px solid #e2e8f0;
        }

        .bill-header {
            text-align: center;
            border-bottom: 2px dashed #cbd5e1;
            padding-bottom: 18px;
            margin-bottom: 18px;
        }

        .shop-name {
            font-size: 20px;
            font-weight: 800;
            color: #0f172a;
            margin-bottom: 4px;
        }

        .shop-sub {
            font-size: 13px;
            color: #64748b;
        }

        .bill-meta {
            font-size: 12px;
            color: #475569;
            margin-bottom: 16px;
            line-height: 1.6;
        }

        .meta-row {
            display: flex;
            justify-content: space-between;
        }

        .bill-table {
            width: 100%;
            border-collapse: collapse;
            font-size: 13px;
            margin-bottom: 16px;
        }

        .bill-table th {
            text-align: left;
            padding: 8px 0;
            border-bottom: 1px solid #e2e8f0;
            color: #64748b;
            font-size: 12px;
        }

        .bill-table td {
            padding: 8px 0;
            border-bottom: 1px solid #f1f5f9;
        }

        .bill-calc {
            border-top: 2px dashed #cbd5e1;
            padding-top: 14px;
            margin-bottom: 20px;
        }

        .calc-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 8px;
            font-size: 13px;
        }

        .calc-row.grand-total {
            font-size: 16px;
            font-weight: 800;
            color: #0d9488;
            padding-top: 6px;
            border-top: 1px solid #e2e8f0;
            margin-top: 6px;
        }

        .bill-footer {
            text-align: center;
            font-size: 12px;
            color: #64748b;
            line-height: 1.5;
            border-top: 1px dashed #cbd5e1;
            padding-top: 14px;
        }

        .action-buttons {
            display: flex;
            gap: 12px;
            margin-top: 24px;
            width: 100%;
            max-width: 420px;
        }

        .btn {
            flex: 1;
            padding: 12px;
            font-size: 14px;
            font-weight: 600;
            border-radius: 10px;
            cursor: pointer;
            text-align: center;
            text-decoration: none;
            transition: all 0.2s;
            border: none;
        }

        .btn-print {
            background: #0d9488;
            color: #ffffff;
        }

        .btn-print:hover {
            background: #0f766e;
        }

        .btn-new-order {
            background: #ffffff;
            color: #0f172a;
            border: 1px solid #cbd5e1;
        }

        .btn-new-order:hover {
            background: #f8fafc;
        }

        @media print {
            body {
                background: #ffffff;
                padding: 0;
            }
            .bill-card {
                box-shadow: none;
                border: none;
                max-width: 100%;
            }
            .action-buttons {
                display: none;
            }
        }
    </style>
</head>
<body>

<div class="bill-card">
    <div class="bill-header">
        <div class="shop-name">🧋 TRÀ SỮA & COFFEE POS</div>
        <div class="shop-sub">HÓA ĐƠN THANH TOÁN</div>
    </div>

    <div class="bill-meta">
        <div class="meta-row">
            <span>Mã hóa đơn:</span>
            <strong style="color: #0f172a;"><%= order.getOrderCode() %></strong>
        </div>
        <div class="meta-row">
            <span>Ngày giờ:</span>
            <span><%= sdf.format(order.getOrderDate()) %></span>
        </div>
        <div class="meta-row">
            <span>Thu ngân:</span>
            <span><%= cashierName %></span>
        </div>
    </div>

    <table class="bill-table">
        <thead>
            <tr>
                <th>Tên món</th>
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
                    <td style="font-weight: 600;"><%= d.getProductName() %></td>
                    <td style="text-align: center;"><%= d.getQuantity() %></td>
                    <td style="text-align: right; color: #64748b;"><%= df.format(d.getUnitPrice()) %></td>
                    <td style="text-align: right; font-weight: 600;"><%= df.format(d.getSubTotal()) %></td>
                </tr>
            <% 
                    }
                } 
            %>
        </tbody>
    </table>

    <div class="bill-calc">
        <div class="calc-row grand-total">
            <span>TỔNG CỘNG:</span>
            <span><%= df.format(order.getTotalAmount()) %> đ</span>
        </div>
        <div class="calc-row" style="margin-top: 8px;">
            <span>Tiền khách đưa:</span>
            <span><%= df.format(customerCash) %> đ</span>
        </div>
        <div class="calc-row">
            <span>Tiền thừa trả khách:</span>
            <span style="font-weight: 700; color: #10b981;"><%= df.format(Math.max(0, changeMoney)) %> đ</span>
        </div>
    </div>

    <div class="bill-footer">
        <p>Cảm ơn quý khách và hẹn gặp lại!</p>
        <p style="font-size: 11px; margin-top: 4px;">Wifi: TraSua_Free • Pass: 88888888</p>
    </div>
</div>

<div class="action-buttons">
    <button class="btn btn-print" onclick="window.print()">🖨️ In Hóa Đơn</button>
    <a href="<%= request.getContextPath() %>/pos" class="btn btn-new-order">+ Đơn Hàng Mới</a>
</div>

</body>
</html>
