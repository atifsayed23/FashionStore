const BASE_URL = '/FashionStore';

document.addEventListener('DOMContentLoaded', () => {
    updateNavbarState();
    setupSearch();
    setupRevealOnScroll();
});

// ===== TOAST SYSTEM =====
function showToast(message, type = 'success') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }
    
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    
    let icon = 'fa-check-circle';
    if (type === 'error') icon = 'fa-exclamation-circle';
    if (type === 'warning') icon = 'fa-exclamation-triangle';
    if (type === 'info') icon = 'fa-info-circle';
    
    toast.innerHTML = `
        <div class="toast-content">
            <i class="fas ${icon} toast-icon"></i>
            <span>${message}</span>
        </div>
        <button class="toast-close" onclick="this.parentElement.classList.remove('show'); setTimeout(() => this.parentElement.remove(), 500);"><i class="fas fa-times"></i></button>
    `;
    
    container.appendChild(toast);
    
    // Smooth entry
    setTimeout(() => {
        toast.classList.add('show');
    }, 50);
    
    // Auto-remove
    setTimeout(() => {
        if (toast.parentElement) {
            toast.classList.remove('show');
            setTimeout(() => {
                toast.remove();
            }, 500);
        }
    }, 4500);
}

// ===== SESSION AWARE NAVBAR =====
function updateNavbarState() {
    // Check local session by fetching the cart count/details
    fetch(`${BASE_URL}/cart`)
        .then(res => res.json())
        .then(data => {
            const isUserLoggedIn = data.success;
            const countBadge = document.getElementById('cartCount');
            if (countBadge) {
                countBadge.textContent = data.itemCount || 0;
                if (data.itemCount > 0) {
                    countBadge.style.display = 'flex';
                } else {
                    countBadge.style.display = 'none';
                }
            }

            const loginBtn = document.getElementById('loginBtn');
            if (loginBtn) {
                // We have a login button element. Let's make it a dropdown container!
                const parent = loginBtn.parentElement;
                
                // Remove existing to avoid duplicates if re-initialized
                const existingDropdown = parent.querySelector('.profile-dropdown');
                if (existingDropdown) existingDropdown.remove();

                if (isUserLoggedIn) {
                    const loggedUserName = localStorage.getItem('loggedUserName') || 'Account';
                    loginBtn.href = '#';
                    loginBtn.innerHTML = `<i class="fas fa-user-check" style="color: #d96b43;"></i>`;
                    loginBtn.title = `Logged in as ${loggedUserName}`;
                    
                    // Create dropdown menu
                    const dropdown = document.createElement('div');
                    dropdown.className = 'profile-dropdown';
                    dropdown.innerHTML = `
                        <div style="padding: 12px 20px; font-size: 12px; font-weight: 600; color: #d96b43; border-bottom: 1px solid #f2ede4; background: #fcfbfa;">
                            Hi, ${loggedUserName}
                        </div>
                        <a href="my-orders.html"><i class="fas fa-box"></i> My Orders</a>
                        <a href="cart.html"><i class="fas fa-shopping-bag"></i> My Cart</a>
                        <a href="#" id="logoutLink"><i class="fas fa-sign-out-alt"></i> Logout</a>
                    `;
                    
                    parent.classList.add('profile-dropdown-container');
                    parent.appendChild(dropdown);
                    
                    document.getElementById('logoutLink').addEventListener('click', (e) => {
                        e.preventDefault();
                        performLogout();
                    });
                } else {
                    // Not logged in
                    localStorage.removeItem('loggedUserName');
                    localStorage.removeItem('loggedUserId');
                    loginBtn.href = 'login.html';
                    loginBtn.innerHTML = `<i class="fas fa-user"></i>`;
                    loginBtn.title = 'Login';
                    
                    const dropdown = document.createElement('div');
                    dropdown.className = 'profile-dropdown';
                    dropdown.innerHTML = `
                        <a href="login.html"><i class="fas fa-sign-in-alt"></i> Login</a>
                        <a href="register.html"><i class="fas fa-user-plus"></i> Register</a>
                    `;
                    
                    parent.classList.add('profile-dropdown-container');
                    parent.appendChild(dropdown);
                }
            }
        })
        .catch(() => {
            // Network error/offline
        });
}

function performLogout() {
    fetch(`${BASE_URL}/logout`)
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                localStorage.removeItem('loggedUserName');
                localStorage.removeItem('loggedUserId');
                showToast('Logged out successfully!', 'info');
                setTimeout(() => {
                    window.location.href = 'home.html';
                }, 1000);
            } else {
                showToast('Logout failed.', 'error');
            }
        })
        .catch(() => {
            showToast('An error occurred during logout.', 'error');
        });
}

// ===== SEARCH ACTIONS =====
function setupSearch() {
    const searchBtn = document.getElementById('searchBtn');
    const searchBar = document.getElementById('searchBar');
    if (searchBtn && searchBar) {
        searchBtn.addEventListener('click', (e) => {
            e.preventDefault();
            searchBar.classList.toggle('active');
            if (searchBar.classList.contains('active')) {
                document.getElementById('searchInput').focus();
            }
        });
    }
}

function searchProducts() {
    const keyword = document.getElementById('searchInput').value.trim();
    if (keyword) {
        window.location.href = 'product-list.html?keyword=' + encodeURIComponent(keyword);
    }
}

// ===== SCROLL REVEAL ANIMATIONS =====
function setupRevealOnScroll() {
    const reveals = document.querySelectorAll('.reveal-on-scroll');
    if (reveals.length === 0) return;

    const revealCallback = (entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('revealed');
                observer.unobserve(entry.target);
            }
        });
    };

    const revealObserver = new IntersectionObserver(revealCallback, {
        root: null,
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    });

    reveals.forEach(el => revealObserver.observe(el));
}
