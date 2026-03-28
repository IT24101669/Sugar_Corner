const API_BASE = "/api/products";

// Load All Products
async function loadAllProducts() {
    try {
        const res = await fetch(API_BASE);
        const products = await res.json();
        displayProducts(products, "productContainer");
    } catch (err) {
        console.error("Failed to load products", err);
        document.getElementById("productContainer").innerHTML =
            `<p class="text-center col-span-full text-red-500">Failed to load products. Please try again.</p>`;
    }
}

// Load Featured Products
async function loadFeatured() {
    try {
        const res = await fetch(`${API_BASE}/featured`);
        const featured = await res.json();
        displayProducts(featured, "featuredContainer");
    } catch (err) {
        console.error("Failed to load featured products", err);
    }
}

// Display products in grid
function displayProducts(products, containerId) {
    const container = document.getElementById(containerId);
    container.innerHTML = "";

    if (products.length === 0) {
        container.innerHTML = `<p class="text-center col-span-full py-10">No products found</p>`;
        return;
    }

    products.forEach(p => {
        const availability = p.available
            ? `<span class="stock" style="color:#27ae60;">In Stock</span>`
            : `<span class="stock" style="color:#c0392b;">Out of Stock</span>`;

        container.innerHTML += `
            <div class="product-card" onclick="viewProductDetails(${p.id})">
                <img src="${p.imageUrl || 'https://via.placeholder.com/260x220?text=' + encodeURIComponent(p.name)}" 
                     alt="${p.name}">
                <h3>${p.name}</h3>
                <p class="price">LKR ${p.price.toFixed(2)}</p>
                ${availability}
                <button onclick="event.stopImmediatePropagation(); addToCart(${p.id}, '${p.name.replace("'", "\\'")}', ${p.price});" 
                        class="add-to-cart-btn mt-4">
                    Add to Cart
                </button>
            </div>
        `;
    });
}

// Search Products
window.searchProducts = async function() {
    const keyword = document.getElementById("searchInput").value.trim();
    if (!keyword) return loadAllProducts();

    try {
        const res = await fetch(`${API_BASE}/search?name=${encodeURIComponent(keyword)}`);
        const products = await res.json();
        displayProducts(products, "productContainer");
    } catch (err) {
        console.error("Search failed", err);
    }
};

// Filter Products
window.filterProducts = async function() {
    let url = `${API_BASE}/filter?`;
    const cat = document.getElementById("categoryFilter").value.trim();
    const min = document.getElementById("minPrice").value;
    const max = document.getElementById("maxPrice").value;

    if (cat) url += `category=${encodeURIComponent(cat)}&`;
    if (min) url += `minPrice=${min}&`;
    if (max) url += `maxPrice=${max}`;

    try {
        const res = await fetch(url);
        const products = await res.json();
        displayProducts(products, "productContainer");
    } catch (err) {
        console.error("Filter failed", err);
    }
};

// Initial Load
window.onload = function() {
    loadAllProducts();
    loadFeatured();
};