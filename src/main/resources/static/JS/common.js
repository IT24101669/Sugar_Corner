// Common JavaScript - Reorder, Toast, Mobile Menu
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `fixed bottom-4 right-4 px-6 py-3 rounded-2xl text-white text-sm shadow-xl ${type === 'success' ? 'bg-green-600' : 'bg-red-600'}`;
    toast.textContent = message;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.transition = 'all 0.3s';
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// Reorder function used by both customer and admin
function reorderOrder(orderId) {
    fetch(`/customer/orders/${orderId}/reorder`, { method: 'POST' })
        .then(res => {
            if (res.redirected) window.location.href = res.url;
            else showToast('Reorder failed', 'error');
        });
}