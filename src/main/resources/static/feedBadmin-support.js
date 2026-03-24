const API_BASE = "http://localhost:8081/api/support";

document.addEventListener('DOMContentLoaded', () => {
    loadFeedback();
    loadInquiries();
});

// 1. Feedback ලැයිස්තුව ලබා ගැනීම සහ පෙන්වීම
async function loadFeedback() {
    try {
        const res = await fetch(`${API_BASE}/feedback/all`);
        if (!res.ok) throw new Error("Failed to load feedback");
        const data = await res.json();

        const tbody = document.getElementById('feedbackTableBody');
        tbody.innerHTML = '';

        data.forEach(f => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${new Date(f.submittedAt).toLocaleString()}</td>
                <td>${f.customerName || '-'}</td>
                <td>${f.rating ? '★'.repeat(f.rating) : '-'}</td>
                <td title="${f.message}">${f.message.substring(0, 50)}${f.message.length > 50 ? '...' : ''}</td>
                <td class="${f.status === 'Resolved' ? 'status-resolved' : 'status-new'}">${f.status}</td>
                <td>
                    <div style="display: flex; gap: 5px;">
                        ${f.adminReply
                ? '<span style="color:#059669; font-weight:bold; font-size:13px;">Replied</span>'
                : `<button onclick="replyViaPrompt('feedback', ${f.id})" style="background:#8B5E3C; color:white; border:none; padding:5px 10px; border-radius:5px; cursor:pointer;">Reply</button>`}
                        
                        <button onclick="deleteEntry('feedback', ${f.id})" style="background:#ef4444; color:white; border:none; padding:5px 10px; border-radius:5px; cursor:pointer;">Delete</button>
                    </div>
                </td>
            `;
            tbody.appendChild(row);
        });
    } catch (err) {
        console.error(err);
        document.getElementById('feedbackTableBody').innerHTML = '<tr><td colspan="6" style="text-align:center;">Error loading feedback</td></tr>';
    }
}

// 2. Inquiries ලැයිස්තුව ලබා ගැනීම සහ පෙන්වීම
async function loadInquiries() {
    try {
        const res = await fetch(`${API_BASE}/inquiry/all`);
        if (!res.ok) throw new Error("Failed to load inquiries");
        const data = await res.json();

        const tbody = document.getElementById('inquiryTableBody');
        tbody.innerHTML = '';

        data.forEach(i => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${new Date(i.submittedAt).toLocaleString()}</td>
                <td>${i.name || '-'}</td>
                <td>${i.email || '-'}</td>
                <td title="${i.inquiryText}">${i.inquiryText.substring(0, 50)}...</td>
                <td class="${i.status === 'Resolved' ? 'status-resolved' : 'status-new'}">${i.status}</td>
                <td>
                    <div style="display: flex; gap: 5px;">
                        <a href="admin-chat.html?id=${i.id}" target="_blank" style="text-decoration:none;">
                            <button style="background:#2563eb; color:white; border:none; padding:5px 10px; border-radius:5px; cursor:pointer;">Chat</button>
                        </a>
                        <button onclick="deleteEntry('inquiry', ${i.id})" style="background:#ef4444; color:white; border:none; padding:5px 10px; border-radius:5px; cursor:pointer;">Delete</button>
                    </div>
                </td>
            `;
            tbody.appendChild(row);
        });
    } catch (err) {
        console.error(err);
        document.getElementById('inquiryTableBody').innerHTML = '<tr><td colspan="6" style="text-align:center;">Error loading inquiries</td></tr>';
    }
}

// 3. Feedback සඳහා පිළිතුරු ලබා දීම (Prompt එකක් හරහා)
async function replyViaPrompt(type, id) {
    const replyText = prompt(`Enter your reply for this ${type}:`);
    if (!replyText || replyText.trim() === '') return;

    try {
        const res = await fetch(`${API_BASE}/${type}/${id}/reply`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ reply: replyText })
        });

        if (res.ok) {
            alert('Reply sent successfully!');
            loadFeedback();
        } else {
            alert('Failed to send reply.');
        }
    } catch (err) {
        alert('Error communicating with server.');
    }
}

// 4. Feedback හෝ Inquiry මකා දැමීම (Delete)
async function deleteEntry(type, id) {
    if (!confirm(`Are you sure you want to delete this ${type}?`)) return;

    try {
        const res = await fetch(`${API_BASE}/${type}/${id}`, {
            method: 'DELETE'
        });

        if (res.ok) {
            alert(`${type.charAt(0).toUpperCase() + type.slice(1)} deleted successfully!`);
            // දත්ත නැවත පෙන්වීම
            if (type === 'feedback') loadFeedback();
            else loadInquiries();
        } else {
            alert("Failed to delete the entry.");
        }
    } catch (err) {
        console.error("Delete Error:", err);
        alert("Error: Could not connect to the server.");
    }
}