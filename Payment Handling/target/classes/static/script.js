// ================== INPUT RESTRICTIONS ==================
document.addEventListener('DOMContentLoaded', () => {
    const cardNumber = document.getElementById('cardNumber');
    const expiryDate = document.getElementById('expiryDate');
    const cvv = document.getElementById('cvv');

    cardNumber.addEventListener('input', () => {
        let v = cardNumber.value.replace(/\s/g, '').replace(/\D/g, '').slice(0,16);
        let formatted = '';
        for(let i = 0; i < v.length; i++) {
            if(i > 0 && i % 4 === 0) formatted += ' ';
            formatted += v[i];
        }
        cardNumber.value = formatted;
    });

    expiryDate.addEventListener('input', () => {
        let v = expiryDate.value.replace(/\D/g, '').slice(0,4);
        if(v.length >= 2) expiryDate.value = v.slice(0,2) + '/' + v.slice(2);
        else expiryDate.value = v;
    });

    cvv.addEventListener('input', () => {
        cvv.value = cvv.value.replace(/\D/g, '').slice(0,4);
    });
});

// ================== SHOW/HIDE FIELDS ==================
const paymentType = document.getElementById('paymentType');
const cardFields = document.getElementById('cardFields');
const codFields = document.getElementById('codFields');
const submitBtn = document.getElementById('submitBtn');

paymentType.addEventListener('change', () => {
    const type = paymentType.value;
    cardFields.style.display = type === 'CARD' ? 'block' : 'none';
    codFields.style.display = type === 'COD' ? 'block' : 'none';

    submitBtn.innerHTML = type === 'CARD'
        ? '<i class="fas fa-credit-card me-2"></i>Pay Now'
        : '<i class="fas fa-box me-2"></i>Confirm Order';
});

// ================== VALIDATION ==================
function validateCard() {
    let valid = true;
    const cardNum = document.getElementById('cardNumber').value.replace(/\s/g, '');
    const expiry = document.getElementById('expiryDate').value;
    const cvvVal = document.getElementById('cvv').value;
    const name = document.getElementById('cardName').value.trim();

    document.getElementById('cardNumberError').textContent = '';
    document.getElementById('expiryError').textContent = '';
    document.getElementById('cvvError').textContent = '';

    if (cardNum.length !== 16) {
        document.getElementById('cardNumberError').textContent = 'Card number must be exactly 16 digits';
        valid = false;
    }
    if (cvvVal.length < 3) {
        document.getElementById('cvvError').textContent = 'CVV must be 3 or 4 digits';
        valid = false;
    }
    if (!expiry.includes('/')) {
        document.getElementById('expiryError').textContent = 'Invalid format';
        valid = false;
    } else {
        const [month, year] = expiry.split('/').map(n => parseInt(n));
        const now = new Date();
        const currentYear = now.getFullYear() % 100;
        const currentMonth = now.getMonth() + 1;
        if (year < currentYear || (year === currentYear && month < currentMonth) || month < 1 || month > 12) {
            document.getElementById('expiryError').textContent = 'Card expired or invalid date';
            valid = false;
        }
    }
    if (!name) valid = false;
    return valid;
}

// ================== FORM SUBMIT ==================
const form = document.getElementById('paymentForm');
const modal = new bootstrap.Modal(document.getElementById('successModal'));

form.addEventListener('submit', (e) => {
    e.preventDefault();
    const type = paymentType.value;

    if (type === 'CARD') {
        if (!validateCard()) return;
        document.getElementById('modalIcon').className = 'fas fa-credit-card fa-6x text-success';
        document.getElementById('modalTitle').innerHTML = 'Payment Successful! 🍪';
        document.getElementById('modalMessage').innerHTML = 'Your brownies are on the way!<br>Thank you for choosing Brownies.';
        modal.show();
    }
    else if (type === 'COD') {
        document.getElementById('modalIcon').className = 'fas fa-box fa-6x text-success';
        document.getElementById('modalTitle').innerHTML = 'Order Confirmed! 🍪';
        document.getElementById('modalMessage').innerHTML = 'Your order is placed successfully.<br>Pay cash when delivered.';
        modal.show();
    }
});

function redirectToMain() {
    modal.hide();
    setTimeout(() => {
        window.location.href = 'main.html';
    }, 600);
}