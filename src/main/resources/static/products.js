const API_BASE = "http://localhost:8081/api/products";

function viewDetails(id) {
    window.location.href = `/product-details.html?id=${id}`;
}

async function displayProducts(products, containerId) {
    const container = document.getElementById(containerId);
    container.innerHTML = "";

    if (products.length === 0) {
        container.innerHTML = '<p style="text-align:center; grid-column:1/-1;">No products found</p>';
        return;
    }

    products.forEach(p => {
        const availability = p.available
            ? '<span class="stock" style="color:#27ae60;">In Stock</span>'
            : '<span class="stock" style="color:#c0392b;">Out of Stock</span>';

        container.innerHTML += `
            <div class="product-card" onclick="viewDetails(${p.id})">
                <img src="${p.imageUrl || 'https://via.placeholder.com/260x220?text=' + encodeURIComponent(p.name)}" 
                     alt="${p.name}">
                <h3>${p.name}</h3>
                <p class="price">LKR ${p.price.toFixed(2)}</p>
                ${availability}
            </div>
        `;
    });
}

async function loadAllProducts() {
    try {
        const res = await fetch(API_BASE);
        if (!res.ok) throw new Error();
        const products = await res.json();
        displayProducts(products, "productContainer");
    } catch (err) {
        console.error("All products load failed", err);
    }
}

async function loadFeatured() {
    try {
        const res = await fetch(`${API_BASE}/featured`);
        if (!res.ok) throw new Error();
        const featured = await res.json();
        displayProducts(featured, "featuredContainer");
    } catch (err) {
        console.error("Featured load failed", err);
    }
}

window.searchProducts = async function() {
    const keyword = document.getElementById("searchInput").value.trim();
    if (!keyword) return loadAllProducts();

    try {
        const res = await fetch(`${API_BASE}/search?name=${encodeURIComponent(keyword)}`);
        if (!res.ok) throw new Error();
        const products = await res.json();
        displayProducts(products, "productContainer");
    } catch (err) {
        console.error("Search failed", err);
    }
};

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
        if (!res.ok) throw new Error();
        const products = await res.json();
        displayProducts(products, "productContainer");
    } catch (err) {
        console.error("Filter failed", err);
    }
};

// Initial loads
loadAllProducts();
loadFeatured();