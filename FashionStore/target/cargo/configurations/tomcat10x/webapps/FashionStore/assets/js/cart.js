let cartTotalAmount = 0;

document.addEventListener('DOMContentLoaded', () => {
    loadCart();
});

function loadCart() {
    const container = document.getElementById('cartItems');
    fetch(`${BASE_URL}/cart`)
        .then(res => res.json())
        .then(data => {
            if(data.success) {
                renderCart(data.cartItems);
                if (typeof updateNavbarState === 'function') {
                    updateNavbarState();
                }
            } else {
                if(data.message && data.message.toLowerCase().includes('login') || data.message && data.message.toLowerCase().includes('log in')) {
                    showToast('Please log in to view your shopping cart!', 'warning');
                    setTimeout(() => {
                        window.location.href = 'login.html';
                    }, 1500);
                } else {
                    container.innerHTML = `<h3 style="text-align:center; padding: 60px; color:var(--accent);">${data.message || 'Error loading cart.'}</h3>`;
                }
            }
        })
        .catch(() => {
            container.innerHTML = '<h3 style="text-align:center; padding: 60px; color:var(--accent);">Failed to connect to shopping cart services.</h3>';
        });
}

function renderCart(items) {
    const container = document.getElementById('cartItems');
    const summary = document.getElementById('cartSummary');
    
    if(!items || items.length === 0) {
        container.innerHTML = `
            <div class="empty-cart-view">
                <i class="fas fa-shopping-bag"></i>
                <h3>Your shopping cart is currently empty</h3>
                <p>Add some beautiful collections to make your style stand out.</p>
                <a href="product-list.html" class="btn-primary" style="margin-top:20px;">Browse Collections</a>
            </div>
        `;
        summary.style.display = 'none';
        return;
    }
    
    let html = '';
    let total = 0;
    
    items.forEach(item => {
        const name = item.productName || item.product?.name || 'Product Style';
        const price = item.price || item.product?.price || 0;
        const img = item.imageUrl || item.product?.imageUrl || 'assets/images/men1.jpg';
        const qty = item.quantity || 1;
        const size = item.size || 'M';
        const color = item.color || 'Default';
        
        total += (price * qty);
        
        html += `
            <div class="cart-item">
                <div class="item-image">
                    <img src="../${img}" alt="${name}" onerror="this.src='https://images.pexels.com/photos/1656684/pexels-photo-1656684.jpeg'">
                </div>
                <div class="item-details">
                    <p class="brand">${item.brand || 'FashionStore'}</p>
                    <h4>${name}</h4>
                    <p class="meta">Size: <span>${size}</span> &nbsp;|&nbsp; Color: <span>${color}</span></p>
                    <p class="unit-price">&#8377;${price.toLocaleString('en-IN')} each</p>
                </div>
                <div class="item-controls">
                    <div class="item-qty-adjuster">
                        <button class="adjust-btn" onclick="updateQty(${item.cartId}, ${qty - 1})">-</button>
                        <span class="qty-val">${qty}</span>
                        <button class="adjust-btn" onclick="updateQty(${item.cartId}, ${qty + 1})">+</button>
                    </div>
                    <button class="remove-item-btn" onclick="removeItem(${item.cartId})">
                        <i class="fas fa-trash-alt"></i> Remove
                    </button>
                </div>
                <div class="item-total-price">
                    &#8377;${(price * qty).toLocaleString('en-IN')}
                </div>
            </div>
        `;
    });
    
    container.innerHTML = html;
    summary.style.display = 'block';
    
    cartTotalAmount = total;
    
    document.getElementById('cartSubtotal').innerHTML = '&#8377;' + total.toLocaleString('en-IN');
    document.getElementById('cartTotal').innerHTML = '&#8377;' + total.toLocaleString('en-IN');
    document.getElementById('orderBtnAmount').textContent = total.toLocaleString('en-IN');
}

// ===== REST PUT UPDATE QUANTITY =====
function updateQty(cartId, newQuantity) {
    if (newQuantity < 1) {
        removeItem(cartId);
        return;
    }
    
    fetch(`${BASE_URL}/cart?cartId=${cartId}&quantity=${newQuantity}`, {
        method: 'PUT'
    })
    .then(res => res.json())
    .then(data => {
        if(data.success) {
            loadCart();
        } else {
            showToast(data.message || 'Could not update item quantity.', 'error');
        }
    })
    .catch(() => {
        showToast('Error syncing changes with server.', 'error');
    });
}

// ===== REST DELETE REMOVE ITEM =====
function removeItem(cartId) {
    fetch(`${BASE_URL}/cart?cartId=${cartId}`, {
        method: 'DELETE'
    })
    .then(res => res.json())
    .then(data => {
        if(data.success) {
            showToast('Style removed from your bag.', 'info');
            loadCart();
        } else {
            showToast(data.message || 'Error removing item from bag.', 'error');
        }
    })
    .catch(() => {
        showToast('Error connecting to shopping services.', 'error');
    });
}

// ===== CHECKOUT FLOWS =====
function openCheckoutModal() {
    const modal = document.getElementById('checkoutModal');
    if (modal) {
        // Pre-fill profile info if logged in and cached
        const username = localStorage.getItem('loggedUserName') || '';
        document.getElementById('shippingName').value = username;
        
        modal.classList.add('active');
    }
}

function closeCheckoutModal() {
    const modal = document.getElementById('checkoutModal');
    if (modal) {
        modal.classList.remove('active');
    }
}

// Form Formatting helpers
function formatCardNumber(input) {
    let value = input.value.replace(/\D/g, '');
    let formatted = '';
    for (let i = 0; i < value.length; i++) {
        if (i > 0 && i % 4 === 0) formatted += ' ';
        formatted += value[i];
    }
    input.value = formatted;

    // Simulated card brand detection
    const icon = document.getElementById('cardBrandIcon');
    if (value.startsWith('4')) {
        icon.className = 'fab fa-cc-visa';
        icon.style.color = '#1a1f71';
    } else if (value.startsWith('5')) {
        icon.className = 'fab fa-cc-mastercard';
        icon.style.color = '#eb001b';
    } else {
        icon.className = 'fas fa-credit-card';
        icon.style.color = 'var(--text-muted)';
    }
}

function formatCardExpiry(input) {
    let value = input.value.replace(/\D/g, '');
    if (value.length > 2) {
        input.value = value.substring(0, 2) + '/' + value.substring(2, 4);
    } else {
        input.value = value;
    }
}

function formatCvv(input) {
    input.value = input.value.replace(/\D/g, '');
}

// Place Order POST Submission
function submitCheckout(e) {
    e.preventDefault();
    
    const btn = document.querySelector('.place-order-btn');
    const originalText = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = `<span><i class="fas fa-spinner fa-spin"></i> Securing payment...</span>`;
    
    // Simulate slight secure gateway verification before hitting our backend CheckoutServlet
    setTimeout(() => {
        fetch(`${BASE_URL}/checkout`, {
            method: 'POST'
        })
        .then(res => res.json())
        .then(data => {
            btn.disabled = false;
            btn.innerHTML = originalText;
            
            if (data.success) {
                closeCheckoutModal();
                showToast("Order placed successfully! Connecting to delivery invoice...", "success");
                setTimeout(() => {
                    window.location.href = `order-confirmation.html?orderId=${data.orderId}`;
                }, 1500);
            } else {
                showToast(data.message || "Failed to process order. Please try again.", "error");
            }
        })
        .catch(() => {
            btn.disabled = false;
            btn.innerHTML = originalText;
            showToast("Server connection error during checkout. Please try again.", "error");
        });
    }, 1200);
}
