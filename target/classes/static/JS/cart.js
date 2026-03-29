<!-- static/js/cart.js -->
let cart = [];

function loadCart() {
    cart = JSON.parse(localStorage.getItem('sugarCart') || '[]');
    renderCart();
}

function renderCart() {
    const container = document.getElementById('cartContainer');
    if (!container) return;

    if (cart.length === 0) {
        container.innerHTML = `<p class="text-center py-12 text-gray-400 text-xl">Your cart is empty</p>`;
        return;
    }

    let html = '';
    let total = 0;

    cart.forEach((item, index) => {
        const itemTotal = item.price * item.quantity;
        total += itemTotal;
        html += `
            <div class="flex justify-between items-center border-b pb-4 mb-4">
                <div>
                    <p class="font-medium">${item.name}</p>
                    <p class="text-sm text-gray-500">LKR ${item.price} × ${item.quantity}</p>
                </div>
                <div class="text-right">
                    <p class="font-semibold">LKR ${itemTotal}</p>
                    <button onclick="removeFromCart(${index})" class="text-red-500 mt-1">Remove</button>
                </div>
            </div>`;
    });

    container.innerHTML = html;
    document.getElementById('cartTotal') ? document.getElementById('cartTotal').textContent = total : null;
}

function removeFromCart(index) {
    cart.splice(index, 1);
    localStorage.setItem('sugarCart', JSON.stringify(cart));
    renderCart();
    updateCartCount();
}

// Place Order
async function placeOrder() {
    if (cart.length === 0) return showToast("Cart is empty", "error");

    const orderRequest = {
        orderType: "DELIVERY",
        deliveryAddress: document.getElementById('deliveryAddress')?.value || "Negombo",
        customerNote: document.getElementById('customerNote')?.value || "",
        items: cart.map(item => ({
            productName: item.name,
            quantity: item.quantity,
            unitPrice: item.price
        }))
    };

    try {
        const res = await fetch('/customer/orders/place', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(orderRequest)
        });

        if (res.ok) {
            const order = await res.json();
            localStorage.removeItem('sugarCart');
            showToast(`Order #${order.id} placed successfully!`, 'success');
            setTimeout(() => window.location.href = '/customer/orders/history', 1500);
        } else {
            showToast("Failed to place order", "error");
        }
    } catch (e) {
        showToast("Network error", "error");
    }
}

window.onload = loadCart;