// static/js/products.js
const API_BASE = "/api/products";

// Global cart count update කරන්න (common.js එකේ තියෙන function එක call කරනවා)
function updateCartCount() {
    const countEl = document.getElementById('cartCount');
    if (countEl) {
        const cart = JSON.parse(localStorage.getItem('sugarCart') || '[]');
        countEl.textContent = cart.reduce((sum, item) => sum + (item.quantity || 1), 0);
    }
}

// සියලුම Products Load කරන්න
async function loadAllProducts() {
    try {
        const res = await fetch(API_BASE);
        if (!res.ok) throw new Error("Failed to fetch products");

        const products = await res.json();
        displayProducts(products, "productContainer");
    } catch (err) {
        console.error("Failed to load products:", err);
        document.getElementById("productContainer").innerHTML = `
            <div class="col-span-full text-center py-12">
                <p class="text-red-500 text-xl">Failed to load products. Please try again later.</p>
            </div>`;
    }
}

// Featured Products Load කරන්න (Home page සඳහාත් භාවිතා වෙනවා)
async function loadFeaturedProducts() {
    try {
        const res = await fetch(`${API_BASE}/featured`);
        const featured = await res.json();
        displayProducts(featured, "featuredProducts");   // Home page එකේ id එක මෙන්න
    } catch (err) {
        console.error("Failed to load featured products", err);
    }
}

// Products Display කරන්න (Grid එකට)
function displayProducts(products, containerId) {
    const container = document.getElementById(containerId);
    if (!container) return;

    container.innerHTML = "";

    if (products.length === 0) {
        container.innerHTML = `
            <div class="col-span-full text-center py-12">
                <p class="text-gray-500 text-xl">No products found</p>
            </div>`;
        return;
    }

    products.forEach(product => {
        const isInStock = product.available && product.quantity > 0;

        const stockHTML = isInStock
            ? `<span class="inline-block px-3 py-1 bg-green-100 text-green-700 text-sm rounded-full">In Stock</span>`
            : `<span class="inline-block px-3 py-1 bg-red-100 text-red-700 text-sm rounded-full">Out of Stock</span>`;

        container.innerHTML += `
            <div class="bg-white rounded-3xl overflow-hidden shadow-lg hover:shadow-2xl transition-all duration-300 brownie-card">
                <div class="relative">
                    <img src="${product.imageUrl || '/images/default-brownie.jpg'}" 
                         alt="${product.name}"
                         class="w-full h-64 object-cover">
                    ${!isInStock ? `
                    <div class="absolute top-4 right-4 bg-red-600 text-white text-xs font-bold px-4 py-1 rounded-full">
                        OUT OF STOCK
                    </div>` : ''}
                </div>
                
                <div class="p-6">
                    <h3 class="font-semibold text-xl mb-2 line-clamp-2">${product.name}</h3>
                    <p class="text-orange-600 text-3xl font-bold mb-4">LKR ${product.price.toFixed(2)}</p>
                    
                    <div class="flex justify-between items-center mb-5">
                        ${stockHTML}
                        <span class="text-sm text-gray-500">${product.category || 'Brownie'}</span>
                    </div>

                    <div class="flex gap-3">
                        <!-- View Details Button -->
                        <button onclick="viewProductDetails(${product.id})" 
                                class="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 py-3 rounded-2xl font-medium transition">
                            View Details
                        </button>
                        
                        <!-- Add to Cart Button -->
                        <button onclick="event.stopImmediatePropagation(); addToCart(${product.id}, '${product.name.replace(/'/g, "\\'")}', ${product.price});" 
                                class="flex-1 ${isInStock ? 'bg-orange-600 hover:bg-orange-700' : 'bg-gray-300 cursor-not-allowed'} 
                                       text-white py-3 rounded-2xl font-medium transition">
                            ${isInStock ? 'Add to Cart' : 'Out of Stock'}
                        </button>
                    </div>
                </div>
            </div>
        `;
    });

    // Cart count update කරන්න
    updateCartCount();
}

// Search Products
async function searchProducts() {
    const keyword = document.getElementById("searchInput").value.trim();

    if (!keyword) {
        return loadAllProducts();
    }

    try {
        const res = await fetch(`${API_BASE}/search?name=${encodeURIComponent(keyword)}`);
        const products = await res.json();
        displayProducts(products, "productContainer");
    } catch (err) {
        console.error("Search failed", err);
        showToast("Search failed. Please try again.", "error");
    }
}

// Product Detail Page එකට යන්න
function viewProductDetails(productId) {
    window.location.href = `/customer/product/${productId}`;
}

// Toast Message (common.js එකේ තියෙන එක භාවිතා කරන්න)
function showToast(message, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `fixed bottom-4 right-4 px-6 py-3 rounded-2xl text-white shadow-2xl z-50 ${type === 'success' ? 'bg-green-600' : 'bg-red-600'}`;
    toast.textContent = message;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}

// Page Load වෙනකොට එකපාරටම Run වෙන්න
window.onload = function() {
    console.log("%cSugar Corner Products Page Loaded Successfully", "color: #f59e0b; font-weight: bold; font-size: 14px");

    loadAllProducts();

    // Search input එක Enter කළාම search වෙන්න
    const searchInput = document.getElementById("searchInput");
    if (searchInput) {
        searchInput.addEventListener("keypress", function(e) {
            if (e.key === "Enter") {
                searchProducts();
            }
        });
    }
};