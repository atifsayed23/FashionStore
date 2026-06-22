let currentProduct = null;
let variants = [];
let selectedSize = null;
let selectedColor = null;
let quantitySelected = 1;

document.addEventListener('DOMContentLoaded', () => {
    const params = new URLSearchParams(window.location.search);
    const productId = params.get('productId');
    if (productId) {
        loadProductDetail(productId);
    } else {
        document.getElementById('productContainer').innerHTML = '<h2 style="text-align:center; padding: 100px 0;">Product not found</h2>';
    }
});

function loadProductDetail(id) {
    fetch(`${BASE_URL}/product-detail?productId=${id}`)
        .then(res => res.json())
        .then(data => {
            if(data.success) {
                currentProduct = data.product;
                variants = data.variants || [];
                renderProduct(data);
                
                // Update breadcrumb
                const bcrumb = document.getElementById('breadcrumbCurrent');
                if (bcrumb) bcrumb.textContent = data.product.name;
            } else {
                document.getElementById('productContainer').innerHTML = `<h2 style="text-align:center; padding: 100px 0; color:var(--accent);">${data.message || 'Product not found'}</h2>`;
            }
        })
        .catch(() => {
            document.getElementById('productContainer').innerHTML = '<h2 style="text-align:center; padding: 100px 0; color:var(--accent);">Error loading product details</h2>';
        });
}

function renderProduct(data) {
    const container = document.getElementById('productContainer');
    const p = data.product;
    
    let sizesHtml = data.sizes.map(s => `<button class="size-btn" onclick="selectSize('${s}', this)">${s}</button>`).join('');
    let colorsHtml = data.colors.map(c => `<button class="color-btn" onclick="selectColor('${c}', this)">${c}</button>`).join('');

    container.innerHTML = `
        <div class="product-image">
            <div class="image-wrapper">
                <img src="../${p.imageUrl}" alt="${p.name}" onerror="this.src='https://images.pexels.com/photos/1656684/pexels-photo-1656684.jpeg'">
            </div>
        </div>
        <div class="product-info">
            <p class="brand-tag">${p.brand || 'Premium Brand'}</p>
            <h1>${p.name}</h1>
            <div class="price">&#8377;${p.price.toLocaleString('en-IN')}</div>
            <p class="desc">${p.description || 'Elevate your wardrobe with this exquisite, premium apparel. Meticulously designed for a refined silhouette, it offers superior comfort and unmatched elegance for any occasion.'}</p>
            
            <div class="options-group">
                <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
                    <h4>Select Size</h4>
                    <button class="btn-size-guide" onclick="toggleSizeGuide()">
                        <i class="fas fa-ruler-horizontal"></i> Size Guide
                    </button>
                </div>
                <div class="chips-container">${sizesHtml || '<span class="out-stock">Out of stock</span>'}</div>
            </div>
            
            <div class="options-group">
                <h4>Select Color</h4>
                <div class="chips-container">${colorsHtml || '<span class="out-stock">Out of stock</span>'}</div>
            </div>
            
            <div class="purchase-controls">
                <div class="qty-selector">
                    <button class="qty-btn" onclick="changeQty(-1)">-</button>
                    <input type="number" id="qtyDisplay" value="1" min="1" readonly>
                    <button class="qty-btn" onclick="changeQty(1)">+</button>
                </div>
                
                <button class="add-to-cart-btn" onclick="addToCart()">
                    <i class="fas fa-shopping-bag"></i> Add to Cart
                </button>
            </div>
        </div>
    `;

    // 3D Parallax Tilt Effect
    setTimeout(() => {
        const wrapper = container.querySelector('.image-wrapper');
        const img = wrapper.querySelector('img');
        
        if (wrapper && img) {
            wrapper.addEventListener('mouseenter', () => {
                img.style.transition = 'transform 0.1s ease-out';
            });
            
            wrapper.addEventListener('mousemove', (e) => {
                const rect = wrapper.getBoundingClientRect();
                const x = e.clientX - rect.left;
                const y = e.clientY - rect.top;
                
                const centerX = rect.width / 2;
                const centerY = rect.height / 2;
                
                const deltaX = (x - centerX) / centerX;
                const deltaY = (y - centerY) / centerY;
                
                const maxTilt = 20; // 20 degrees max rotation
                const rotateX = -deltaY * maxTilt;
                const rotateY = deltaX * maxTilt;
                
                img.style.transform = `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) scale3d(1.05, 1.05, 1.05)`;
            });

            wrapper.addEventListener('mouseleave', () => {
                img.style.transition = 'transform 0.8s cubic-bezier(0.16, 1, 0.3, 1)';
                img.style.transform = `perspective(1000px) rotateX(0deg) rotateY(0deg) scale3d(1, 1, 1)`;
            });
        }
    }, 50);
}

function selectSize(size, btn) {
    selectedSize = size;
    document.querySelectorAll('.size-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
    
    // Auto reset selection details when size switches to match stock if necessary
    quantitySelected = 1;
    const qtyInput = document.getElementById('qtyDisplay');
    if (qtyInput) qtyInput.value = 1;
}

function selectColor(color, btn) {
    selectedColor = color;
    document.querySelectorAll('.color-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
}

function changeQty(amount) {
    const qtyInput = document.getElementById('qtyDisplay');
    if (!qtyInput) return;
    
    let currentQty = parseInt(qtyInput.value);
    let newQty = currentQty + amount;
    
    if (newQty < 1) newQty = 1;
    
    // Check max stock for selected variant if available
    if (selectedSize && selectedColor) {
        const variant = variants.find(v => v.size === selectedSize && v.color === selectedColor);
        if (variant && newQty > variant.stock) {
            newQty = variant.stock;
            showToast(`Only ${variant.stock} items left in stock for this style!`, 'warning');
        }
    } else {
        // Limit general items to 10 if size/color is not selected yet
        if (newQty > 10) {
            newQty = 10;
            showToast("Please choose your preferred Size and Color first!", "info");
        }
    }
    
    quantitySelected = newQty;
    qtyInput.value = newQty;
}

function addToCart() {
    if(!selectedSize || !selectedColor) {
        showToast("Please select both a Size and Color variant!", "warning");
        return;
    }
    
    const variant = variants.find(v => v.size === selectedSize && v.color === selectedColor);
    if(!variant) {
        showToast("We're sorry, this style is currently out of stock!", "error");
        return;
    }
    
    // Disable add to cart button to prevent rapid multiple clicks
    const btn = document.querySelector('.add-to-cart-btn');
    const originalText = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = `<i class="fas fa-spinner fa-spin"></i> Adding...`;

    fetch(`${BASE_URL}/cart`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `action=add&variantId=${variant.variantId}&quantity=${quantitySelected}`
    })
    .then(res => {
        if (res.redirected) {
            window.location.href = res.url;
            return new Promise(() => {}); // Halt the promise chain
        }
        return res.json();
    })
    .then(data => {
        btn.disabled = false;
        btn.innerHTML = originalText;

        if(data.success) {
            if (typeof updateNavbarState === 'function') {
                updateNavbarState();
            }
            triggerFlyToCartAnimation();
        } else {
            showToast(data.message || 'Please log in to add items to your cart.', 'error');
            if(data.message && data.message.toLowerCase().includes('login') || data.message && data.message.toLowerCase().includes('log in')) {
                setTimeout(() => {
                    window.location.href = 'login.html';
                }, 1500);
            }
        }
    })
    .catch(() => {
        btn.disabled = false;
        btn.innerHTML = originalText;
        showToast("Failed to add item to cart. Please check your internet connection.", "error");
    });
}

function triggerFlyToCartAnimation() {
    const productImg = document.querySelector('.product-image img');
    const cartIcon = document.querySelector('a[href="cart.html"]');

    if (!productImg || !cartIcon) return;

    const imgRect = productImg.getBoundingClientRect();
    const cartRect = cartIcon.getBoundingClientRect();

    const clone = productImg.cloneNode();
    clone.style.position = 'fixed';
    clone.style.zIndex = '9999';
    clone.style.left = imgRect.left + 'px';
    clone.style.top = imgRect.top + 'px';
    clone.style.width = imgRect.width + 'px';
    clone.style.height = imgRect.height + 'px';
    clone.style.borderRadius = '8px';
    clone.style.objectFit = 'cover';
    clone.style.boxShadow = '0 10px 30px rgba(0,0,0,0.3)';
    clone.style.transition = 'all 1.5s cubic-bezier(0.25, 0.1, 0.25, 1)'; // Slower, smoother transition

    document.body.appendChild(clone);

    // Force reflow
    clone.getBoundingClientRect();

    // Move and shrink clone
    const targetWidth = 30;
    clone.style.left = (cartRect.left + cartRect.width / 2 - targetWidth / 2) + 'px';
    clone.style.top = (cartRect.top + cartRect.height / 2 - targetWidth / 2) + 'px';
    clone.style.width = targetWidth + 'px';
    clone.style.height = targetWidth + 'px';
    clone.style.opacity = '0';
    clone.style.transform = 'rotate(360deg)';

    // Pop the cart icon when item arrives
    setTimeout(() => {
        cartIcon.style.transition = 'transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275)';
        cartIcon.style.transform = 'scale(1.4)';
        
        setTimeout(() => {
            cartIcon.style.transform = 'scale(1)';
        }, 300);
        
        if (clone.parentNode) {
            clone.parentNode.removeChild(clone);
        }
    }, 1500);
}

// ===== DIALOG ACTIONS =====
function toggleSizeGuide() {
    const modal = document.getElementById('sizeGuideModal');
    if (modal) {
        modal.classList.toggle('active');
    }
}
