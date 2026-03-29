<!-- static/js/common.js -->
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `fixed bottom-4 right-4 px-6 py-3 rounded-2xl text-white shadow-2xl z-50 ${type === 'success' ? 'bg-green-600' : 'bg-red-600'}`;
    toast.textContent = message;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3500);
}

function isLoggedIn() {
    return document.body.dataset.loggedIn === 'true';
}

// Global Navigation Helper
function navigateTo(page) {
    window.location.href = page;
}

// Add to Cart (global function)
async function addToCart(productId, name, price) {
    let cart = JSON.parse(localStorage.getItem('sugarCart') || '[]');

    const existing = cart.find(item => item.id === productId);
    if (existing) {
        existing.quantity += 1;
    } else {
        cart.push({ id: productId, name, price, quantity: 1 });
    }

    localStorage.setItem('sugarCart', JSON.stringify(cart));
    showToast(`${name} added to cart!`);

    // Optional: update cart count in navbar
    updateCartCount();
}

function updateCartCount() {
    const countEl = document.getElementById('cartCount');
    if (countEl) {
        const cart = JSON.parse(localStorage.getItem('sugarCart') || '[]');
        countEl.textContent = cart.reduce((sum, item) => sum + item.quantity, 0);
    }
}

// Load cart count on every page
window.addEventListener('load', () => {
    updateCartCount();
});