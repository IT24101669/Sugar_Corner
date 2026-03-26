// ==================== CUSTOMER.JS ====================
// Fully connected with your CustomerController + PaymentController

let cart = [];

// Render cart items
function renderCart() {
    const container = document.getElementById('cartContainer');
    if (!container) return;

    if (cart.length === 0) {
        container.innerHTML = `
            <div class="text-center py-20 text-gray-400">
                <p class="text-2xl">Your cart is empty</p>
                <p class="mt-2">Click "Add More Brownies" to start ordering</p>
            </div>`;
        return;
    }

    let html = '';
    cart.forEach((item, index) => {
        html += `
            <div class="flex justify-between items-center bg-gray-50 p-5 rounded-2xl mb-4">
                <div class="flex-1">
                    <p class="font-medium text-lg">${item.name}</p>
                    <p class="text-sm text-gray-500">LKR ${item.price} × ${item.quantity}</p>
                </div>
                <div class="text-right">
                    <p class="font-semibold text-lg">LKR ${item.price * item.quantity}</p>
                    <button onclick="removeFromCart(${index})" class="text-red-500 text-3xl mt-1">×</button>
                </div>
            </div>`;
    });
    container.innerHTML = html;
}

// Remove item from cart
function removeFromCart(index) {
    cart.splice(index, 1);
    renderCart();
}

// Add sample product (you can expand this later)
function addProductToCart() {
    const sampleProducts = [
        { name: "Classic Chocolate Brownie", price: 450 },
        { name: "Walnut Brownie", price: 520 },
        { name: "Red Velvet Brownie", price: 480 }
    ];
    const random = sampleProducts[Math.floor(Math.random() * sampleProducts.length)];
    cart.push({ ...random, quantity: 1 });
    renderCart();
}

// Submit Order → Call CustomerController + PaymentController
async function submitOrder() {
    if (cart.length === 0) {
        alert("Please add at least one brownie to your cart!");
        return;
    }

    // Prepare order request for CustomerController
    const orderRequest = {
        orderType: "DELIVERY",
        deliveryAddress: "Negombo, Western Province, Sri Lanka",
        customerNote: "",
        items: cart.map(item => ({
            productName: item.name,
            quantity: item.quantity,
            unitPrice: item.price
        }))
    };

    try {
        // Step 1: Place Order (CustomerController)
        const orderResponse = await fetch('/customer/orders/place', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(orderRequest)
        });

        if (!orderResponse.ok) throw new Error("Failed to place order");
        const order = await orderResponse.json();

        // Step 2: Initiate Payment (PaymentController)
        const paymentResponse = await fetch(
            `/api/payments/initiate?method=COD&orderId=${order.id}&amount=${order.totalAmount}`,
            { method: 'POST' }
        );

        if (!paymentResponse.ok) throw new Error("Failed to initiate payment");

        // Success
        alert(`✅ Order #${order.id} placed successfully!\nPayment: Cash on Delivery`);
        cart = []; // Clear cart
        window.location.href = "/customer/dashboard";

    } catch (error) {
        console.error(error);
        alert("❌ Something went wrong. Please try again.");
    }
}

// Load reorder items from session (if any)
window.onload = function () {
    // If backend sent reorder items via model attribute
    if (typeof reorderItems !== 'undefined' && reorderItems) {
        cart = reorderItems;
    }
    renderCart();
    console.log("%cCustomer Place Order Page Loaded - Connected to Backend", "color: #f59e0b; font-weight: bold");
};