document.getElementById('loginForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const submitBtn = document.getElementById('submitBtn');

    submitBtn.classList.add('loading');

    fetch(`${BASE_URL}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`
    })
    .then(res => res.json())
    .then(data => {
        if(data.success) {
            showToast('Login successful!', 'success');
            // Cache the username locally so the main.js can use it immediately without another fetch
            localStorage.setItem('loggedUserName', data.name || email.split('@')[0]);
            
            setTimeout(() => {
                window.location.href = 'home.html';
            }, 1000);
        } else {
            showToast(data.message || 'Login failed. Please check your credentials.', 'error');
            submitBtn.classList.remove('loading');
        }
    })
    .catch(err => {
        console.error('Login error:', err);
        showToast('An error occurred. Please try again.', 'error');
        submitBtn.classList.remove('loading');
    });
});
