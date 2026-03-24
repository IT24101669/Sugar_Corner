// Toggle password visibility
function togglePassword(fieldId, icon) {
    const input = document.getElementById(fieldId);
    if (input.type === 'password') {
        input.type = 'text';
        icon.textContent = '🙈';
    } else {
        input.type = 'password';
        icon.textContent = '👁';
    }
}

// Password strength checker
function checkStrength(password) {
    let strength = 0;
    if (password.length >= 8) strength++;
    if (/[A-Z]/.test(password)) strength++;
    if (/[0-9]/.test(password)) strength++;
    if (/[^A-Za-z0-9]/.test(password)) strength++;
    return strength;
}

// Password strength live feedback
const passwordInput = document.getElementById('password') ||
    document.getElementById('newPassword');
const strengthBar = document.getElementById('strengthBar');
const strengthText = document.getElementById('strengthText');

if (passwordInput && strengthBar) {
    passwordInput.addEventListener('input', function () {
        const strength = checkStrength(this.value);
        const levels = [
            { width: '0%',   color: '#eee',    text: '' },
            { width: '25%',  color: '#e74c3c', text: 'Weak' },
            { width: '50%',  color: '#f39c12', text: 'Fair' },
            { width: '75%',  color: '#3498db', text: 'Good' },
            { width: '100%', color: '#27ae60', text: 'Strong' },
        ];
        const level = levels[strength];
        strengthBar.style.width = level.width;
        strengthBar.style.background = level.color;
        if (strengthText) {
            strengthText.textContent = this.value.length > 0 ? level.text : '';
            strengthText.style.color = level.color;
        }
    });
}

// Password match live feedback
const confirmInput = document.getElementById('confirmPassword');
const matchText = document.getElementById('matchText');

if (confirmInput && matchText) {
    confirmInput.addEventListener('input', function () {
        const original = document.getElementById('password') ||
            document.getElementById('newPassword');
        if (this.value === '') {
            matchText.textContent = '';
        } else if (this.value === original.value) {
            matchText.textContent = '✓ Passwords match';
            matchText.style.color = '#27ae60';
        } else {
            matchText.textContent = '✗ Passwords do not match';
            matchText.style.color = '#e74c3c';
        }
    });
}

// Form submit validation
const registerForm = document.querySelector('form[action="/register"]');
if (registerForm) {
    registerForm.addEventListener('submit', function (e) {
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        if (password !== confirmPassword) {
            e.preventDefault();
            alert('Passwords do not match!');
            return;
        }
        if (checkStrength(password) < 2) {
            e.preventDefault();
            alert('Password is too weak! Use at least 8 characters with numbers and letters.');
        }
    });
}

const resetForm = document.querySelector('form[action="/reset-password/confirm"]');
if (resetForm) {
    resetForm.addEventListener('submit', function (e) {
        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        if (newPassword !== confirmPassword) {
            e.preventDefault();
            alert('Passwords do not match!');
            return;
        }
        if (checkStrength(newPassword) < 2) {
            e.preventDefault();
            alert('Password is too weak! Use at least 8 characters with numbers and letters.');
        }
    });
}

// Auto hide alerts after 4 seconds
document.addEventListener('DOMContentLoaded', function () {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            alert.style.transition = 'opacity 0.5s';
            alert.style.opacity = '0';
            setTimeout(function () { alert.remove(); }, 500);
        }, 4000);
    });
});