// Animate initials avatar on hover
const avatarInner = document.querySelector('.avatar-inner');
if (avatarInner) {
    avatarInner.addEventListener('mouseenter', function () {
        this.style.transform = 'scale(1.1)';
        this.style.transition = 'transform 0.3s ease';
    });
    avatarInner.addEventListener('mouseleave', function () {
        this.style.transform = 'scale(1)';
    });
}

// Animate form inputs on focus
const inputs = document.querySelectorAll('.profile-form input');
inputs.forEach(input => {
    input.addEventListener('focus', function () {
        this.parentElement.style.transform = 'translateY(-2px)';
        this.parentElement.style.transition = 'transform 0.2s ease';
    });
    input.addEventListener('blur', function () {
        this.parentElement.style.transform = 'translateY(0)';
    });
});

// Update sidebar name live as user types
const fullNameInput = document.querySelector('input[name="fullName"]');
const sidebarName = document.querySelector('.profile-sidebar h3');
const avatarInitials = document.querySelector('.avatar-initials');

if (fullNameInput) {
    fullNameInput.addEventListener('input', function () {
        if (sidebarName) sidebarName.textContent = this.value;
        if (avatarInitials && this.value.length > 0) {
            avatarInitials.textContent = this.value.charAt(0).toUpperCase();
        }
    });
}

// Save button loading animation
const saveBtn = document.querySelector('.btn-save');
if (saveBtn) {
    saveBtn.addEventListener('click', function () {
        this.innerHTML = '<span>Saving...</span>';
        this.style.opacity = '0.8';
    });
}