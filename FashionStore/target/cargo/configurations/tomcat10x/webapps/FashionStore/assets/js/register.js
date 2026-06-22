document.getElementById('registerForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const phone = document.getElementById('phone').value;
    const address = document.getElementById('address').value;
    const password = document.getElementById('password').value;
    const submitBtn = document.getElementById('submitBtn');

    submitBtn.classList.add('loading');

    fetch(`${BASE_URL}/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `name=${encodeURIComponent(name)}&email=${encodeURIComponent(email)}&phone=${encodeURIComponent(phone)}&address=${encodeURIComponent(address)}&password=${encodeURIComponent(password)}`
    })
    .then(res => res.json())
    .then(data => {
        if(data.success) {
            showToast('Registration successful! Please sign in.', 'success');
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 1500);
        } else {
            showToast(data.message || 'Registration failed. Please check your inputs.', 'error');
            submitBtn.classList.remove('loading');
        }
    })
    .catch(err => {
        console.error('Registration error:', err);
        showToast('An error occurred. Please try again.', 'error');
        submitBtn.classList.remove('loading');
    });
});
