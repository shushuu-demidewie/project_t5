// Đồng hồ thời gian thực
function updateClock() {
    const now = new Date();
    const timeStr = now.toLocaleTimeString('vi-VN', { hour12: false });
    const clockEl = document.getElementById('clock');
    if (clockEl) {
        clockEl.textContent = timeStr;
    }
}
setInterval(updateClock, 1000);
updateClock();

function formatNumber(num) {
    return new Intl.NumberFormat('vi-VN').format(num);
}

function getTotalAmount() {
    const totalEl = document.getElementById('totalAmountInput');
    if (totalEl && totalEl.value) {
        return parseFloat(totalEl.value) || 0;
    }
    return 0;
}

function calculateChange() {
    const cashInput = document.getElementById('customerCash');
    const changeDisplay = document.getElementById('changeMoneyDisplay');
    if (!cashInput || !changeDisplay) return;

    const totalAmount = getTotalAmount();
    const rawCash = cashInput.value.replace(/[^0-9]/g, '');
    const customerCash = rawCash ? parseFloat(rawCash) : 0;

    const change = customerCash - totalAmount;

    if (customerCash < totalAmount) {
        changeDisplay.textContent = 'Còn thiếu: ' + formatNumber(Math.abs(change)) + ' đ';
        changeDisplay.style.color = '#ef4444';
    } else {
        changeDisplay.textContent = formatNumber(change) + ' đ';
        changeDisplay.style.color = '#10b981';
    }
}

function setCash(amount) {
    const cashInput = document.getElementById('customerCash');
    if (cashInput) {
        cashInput.value = formatNumber(amount);
        calculateChange();
    }
}

// Phím tắt F9 để thanh toán nhanh
window.addEventListener('keydown', function(e) {
    if (e.key === 'F9') {
        e.preventDefault();
        const btnPay = document.getElementById('btnPay');
        if (btnPay && !btnPay.disabled) {
            const checkoutForm = document.getElementById('checkoutForm');
            if (checkoutForm) {
                checkoutForm.submit();
            }
        }
    }
});

// Khởi chạy tính toán tiền thừa ban đầu
document.addEventListener('DOMContentLoaded', function() {
    calculateChange();
});
