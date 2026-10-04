// ========================================================
// HỆ THỐNG POS BÁN HÀNG - SCRIPT TƯƠNG TÁC
// ========================================================

// 1. Đồng hồ thời gian thực và ngày tháng
function updateClock() {
    const now = new Date();
    const days = ['Chủ Nhật', 'Thứ Hai', 'Thứ Ba', 'Thứ Tư', 'Thứ Năm', 'Thứ Sáu', 'Thứ Bảy'];
    const dayName = days[now.getDay()];
    const dateStr = String(now.getDate()).padStart(2, '0') + '/' + 
                    String(now.getMonth() + 1).padStart(2, '0') + '/' + 
                    now.getFullYear();
    const timeStr = now.toLocaleTimeString('vi-VN', { hour12: false });
    
    const clockEl = document.getElementById('clock');
    if (clockEl) {
        clockEl.textContent = dayName + ', ' + dateStr + ' • ' + timeStr;
    }
}
setInterval(updateClock, 1000);
updateClock();

// 2. Định dạng số VNĐ
function formatNumber(num) {
    return new Intl.NumberFormat('vi-VN').format(Math.round(num));
}

// 3. Lấy tổng tiền giỏ hàng hiện tại
function getTotalAmount() {
    const totalEl = document.getElementById('totalAmountInput');
    if (totalEl && totalEl.value) {
        return parseFloat(totalEl.value) || 0;
    }
    return 0;
}

// 4. Tính toán tiền thừa trả lại khách hàng
function calculateChange() {
    const cashInput = document.getElementById('customerCash');
    const changeDisplay = document.getElementById('changeMoneyDisplay');
    const changeBox = document.getElementById('changeBox');
    if (!cashInput || !changeDisplay) return;

    const totalAmount = getTotalAmount();
    const rawCash = cashInput.value.replace(/[^0-9]/g, '');
    const customerCash = rawCash ? parseFloat(rawCash) : 0;

    const change = customerCash - totalAmount;

    if (totalAmount === 0) {
        changeDisplay.textContent = '0 đ';
        if (changeBox) {
            changeBox.style.background = '#f1f5f9';
            changeBox.style.borderColor = '#e2e8f0';
        }
        changeDisplay.style.color = '#64748b';
        return;
    }

    if (customerCash < totalAmount) {
        const missing = totalAmount - customerCash;
        changeDisplay.textContent = 'Thiếu ' + formatNumber(missing) + ' đ';
        changeDisplay.style.color = '#dc2626';
        if (changeBox) {
            changeBox.style.background = '#fef2f2';
            changeBox.style.borderColor = '#fecaca';
        }
    } else {
        changeDisplay.textContent = formatNumber(change) + ' đ';
        changeDisplay.style.color = '#047857';
        if (changeBox) {
            changeBox.style.background = '#ecfdf5';
            changeBox.style.borderColor = '#a7f3d0';
        }
    }
}

// 5. Gán nhanh số tiền khách đưa
function setCash(amount) {
    const cashInput = document.getElementById('customerCash');
    if (cashInput) {
        cashInput.value = formatNumber(amount);
        calculateChange();
        cashInput.focus();
    }
}

// 6. Tìm kiếm trực tiếp sản phẩm (Client-side Instant Filter)
function setupInstantSearch() {
    const searchInput = document.querySelector('.search-input');
    const clearBtn = document.getElementById('searchClearBtn');
    const productCards = document.querySelectorAll('.product-card');
    const emptyState = document.getElementById('clientEmptySearch');

    if (!searchInput) return;

    searchInput.addEventListener('input', function() {
        const query = this.value.trim().toLowerCase();
        let matchCount = 0;

        if (clearBtn) {
            clearBtn.style.display = query.length > 0 ? 'flex' : 'none';
        }

        productCards.forEach(card => {
            const nameEl = card.querySelector('.product-name');
            const catEl = card.querySelector('.product-cat');
            const nameText = nameEl ? nameEl.textContent.toLowerCase() : '';
            const catText = catEl ? catEl.textContent.toLowerCase() : '';

            if (nameText.includes(query) || catText.includes(query)) {
                card.style.display = 'flex';
                matchCount++;
            } else {
                card.style.display = 'none';
            }
        });

        if (emptyState) {
            emptyState.style.display = (matchCount === 0 && productCards.length > 0) ? 'block' : 'none';
        }
    });

    if (clearBtn) {
        clearBtn.addEventListener('click', function() {
            searchInput.value = '';
            clearBtn.style.display = 'none';
            productCards.forEach(card => card.style.display = 'flex');
            if (emptyState) emptyState.style.display = 'none';
            searchInput.focus();
        });
    }
}

// 7. Chuyển đổi loại đơn: Tại quán / Mang về
function setOrderType(type) {
    const buttons = document.querySelectorAll('.type-btn');
    buttons.forEach(btn => btn.classList.remove('active'));
    const target = document.getElementById('type-' + type);
    if (target) target.classList.add('active');
}

// 8. Hiển thị thông báo Toast nhanh
function showToast(message) {
    const existing = document.querySelector('.toast-notice');
    if (existing) existing.remove();

    const toast = document.createElement('div');
    toast.className = 'toast-notice';
    toast.innerHTML = '<span>⚡</span> <span>' + message + '</span>';
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.transition = 'opacity 0.3s ease';
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 2200);
}

// 9. Lắng nghe phím tắt POS
window.addEventListener('keydown', function(e) {
    // Phím F9: Xác nhận thanh toán
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
    // Phím '/' hoặc Ctrl + K: Focus vào ô tìm kiếm món
    if ((e.key === '/' && document.activeElement.tagName !== 'INPUT') || 
        (e.ctrlKey && e.key.toLowerCase() === 'k')) {
        e.preventDefault();
        const searchInput = document.querySelector('.search-input');
        if (searchInput) {
            searchInput.focus();
            searchInput.select();
        }
    }
    // Phím Esc: Bỏ chọn hoặc xóa tìm kiếm
    if (e.key === 'Escape') {
        const searchInput = document.querySelector('.search-input');
        if (searchInput && document.activeElement === searchInput) {
            searchInput.value = '';
            searchInput.blur();
            const clearBtn = document.getElementById('searchClearBtn');
            if (clearBtn) clearBtn.click();
        }
    }
});

// 10. Khởi chạy ban đầu
document.addEventListener('DOMContentLoaded', function() {
    calculateChange();
    setupInstantSearch();

    // Tự động định dạng tiền khi nhập vào ô
    const cashInput = document.getElementById('customerCash');
    if (cashInput) {
        cashInput.addEventListener('focus', function() {
            this.select();
        });
        cashInput.addEventListener('blur', function() {
            const raw = this.value.replace(/[^0-9]/g, '');
            if (raw) {
                this.value = formatNumber(parseFloat(raw));
            }
        });
    }
});
