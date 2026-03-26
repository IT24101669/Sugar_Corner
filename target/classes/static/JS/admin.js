// Admin Dashboard & Real-time updates
function updateOrderStatus(orderId, status) {
    fetch(`/admin/orders/${orderId}/status?status=${status}`, { method: 'POST' })
        .then(() => {
            showToast(`Order #${orderId} updated to ${status}`);
            location.reload();
        });
}

console.log("%cAdmin JS Loaded - Fully Responsive", "color: #f59e0b; font-weight: bold");