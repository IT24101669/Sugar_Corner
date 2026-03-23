const API_BASE = "/api/products";

const form = document.getElementById("productForm");
const msg = document.getElementById("message");
const cancelBtn = document.getElementById("cancelEdit");
let editingId = null;

form.addEventListener("submit", async e => {
    e.preventDefault();

    const formData = new FormData();

    // Build product data
    const product = {
        name: document.getElementById("name").value.trim(),
        description: document.getElementById("description").value.trim() || null,
        category: document.getElementById("category").value.trim() || null,
        price: parseFloat(document.getElementById("price").value),
        quantity: parseInt(document.getElementById("quantity").value) || 0,
        featured: document.getElementById("featured").checked
    };

    // Validation
    if (!product.name || isNaN(product.price) || product.price <= 0) {
        showMessage("Product name and valid price (> 0) are required!", "red");
        return;
    }

    formData.append("product", new Blob([JSON.stringify(product)], { type: "application/json" }));

    // Image file
    const imageFile = document.getElementById("imageFile").files[0];
    if (imageFile) {
        // Optional: client-side size/type validation
        if (imageFile.size > 5 * 1024 * 1024) { // 5MB max
            showMessage("Image file is too large (max 5MB)", "red");
            return;
        }
        if (!imageFile.type.startsWith("image/")) {
            showMessage("Please select a valid image file", "red");
            return;
        }
        formData.append("image", imageFile);
    }

    const url = editingId ? `${API_BASE}/${editingId}` : API_BASE;
    const method = editingId ? "PUT" : "POST";

    try {
        const res = await fetch(url, {
            method,
            body: formData
        });

        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(errorText || "Failed to save product");
        }

        showMessage(editingId ? "Product updated successfully!" : "Product added successfully!", "green");
        form.reset();
        document.getElementById("currentImageName").textContent = "None";
        document.getElementById("previewImg").style.display = "none";
        editingId = null;
        cancelBtn.style.display = "none";
        loadProducts();
    } catch (err) {
        showMessage("Error: " + err.message, "red");
        console.error("Save error:", err);
    }
});

cancelBtn.addEventListener("click", () => {
    form.reset();
    document.getElementById("currentImageName").textContent = "None";
    document.getElementById("previewImg").style.display = "none";
    editingId = null;
    cancelBtn.style.display = "none";
    showMessage("Edit cancelled", "#e67e22");
});

async function loadProducts() {
    try {
        const res = await fetch(API_BASE);
        if (!res.ok) throw new Error("Failed to load products");
        const products = await res.json();
        renderTable(products);
    } catch (err) {
        showMessage("Cannot load products – is the backend running?", "red");
        console.error(err);
    }
}

function renderTable(products) {
    const tbody = document.querySelector("#productTable tbody");
    tbody.innerHTML = "";

    if (products.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;padding:30px;">No products added yet</td></tr>';
        return;
    }

    products.forEach(p => {
        const tr = document.createElement("tr");
        const imgCell = p.imageUrl
            ? `<img src="${p.imageUrl}" alt="${p.name}" style="max-width:60px; border-radius:4px;">`
            : "No image";

        tr.innerHTML = `
            <td>${p.id}</td>
            <td>${p.name}</td>
            <td>${p.category || '-'}</td>
            <td>LKR ${p.price.toFixed(2)}</td>
            <td>${p.quantity || 0} ${p.available ? '(In stock)' : '(Out of stock)'}</td>
            <td>${imgCell}</td>
            <td>${p.featured ? '<span style="color:#27ae60;">Yes</span>' : '-'}</td>
            <td>
                <button class="edit-btn" onclick="editProduct(${p.id})">Edit</button>
                <button class="delete-btn" onclick="deleteProduct(${p.id})">Delete</button>
            </td>`;
        tbody.appendChild(tr);
    });
}

window.editProduct = async function(id) {
    try {
        const res = await fetch(`${API_BASE}/${id}`);
        if (!res.ok) throw new Error("Cannot load product");
        const p = await res.json();

        document.getElementById("name").value = p.name;
        document.getElementById("description").value = p.description || "";
        document.getElementById("category").value = p.category || "";
        document.getElementById("price").value = p.price;
        document.getElementById("quantity").value = p.quantity || 0;
        document.getElementById("featured").checked = !!p.featured;

        // Show current image preview (cannot set file input value)
        if (p.imageUrl) {
            document.getElementById("currentImageName").textContent = p.imageUrl.split('/').pop();
            document.getElementById("previewImg").src = p.imageUrl;
            document.getElementById("previewImg").style.display = "block";
        } else {
            document.getElementById("currentImageName").textContent = "None";
            document.getElementById("previewImg").style.display = "none";
        }

        editingId = id;
        cancelBtn.style.display = "inline-block";
        showMessage(`Now editing product #${id}`, "#3498db");
    } catch (err) {
        showMessage("Cannot load product for editing", "red");
        console.error(err);
    }
};

window.deleteProduct = async function(id) {
    if (!confirm(`Delete product #${id}? This action cannot be undone.`)) return;

    try {
        const res = await fetch(`${API_BASE}/${id}`, { method: "DELETE" });
        if (!res.ok) throw new Error(await res.text());
        showMessage("Product deleted successfully", "green");
        loadProducts();
    } catch (err) {
        showMessage("Delete failed: " + err.message, "red");
    }
};

function showMessage(text, color) {
    msg.textContent = text;
    msg.style.color = color;
    setTimeout(() => { msg.textContent = ""; }, 6000);
}

// Load products when page opens
loadProducts();