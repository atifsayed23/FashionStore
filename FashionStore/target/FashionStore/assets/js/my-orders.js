document.addEventListener('DOMContentLoaded', () => {
    fetch(`${BASE_URL}/my-orders`)
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                renderOrders(data.orders);
            } else {
                if (data.message === "Please login first!") {
                    window.location.href = 'login.html';
                } else {
                    document.getElementById('ordersList').innerHTML = `<div class="empty-orders"><i class="fas fa-exclamation-triangle"></i><p>${data.message}</p></div>`;
                }
            }
        })
        .catch(err => {
            console.error(err);
            document.getElementById('ordersList').innerHTML = `<div class="empty-orders"><i class="fas fa-exclamation-circle"></i><p>Failed to load orders. Please try again later.</p></div>`;
        });
});

function renderOrders(orders) {
    const container = document.getElementById('ordersList');
    container.innerHTML = '';

    if (!orders || orders.length === 0) {
        container.innerHTML = `
            <div class="empty-orders">
                <i class="fas fa-box-open"></i>
                <p>You haven't placed any orders yet.</p>
                <a href="product-list.html" class="btn-primary">Start Shopping</a>
            </div>
        `;
        return;
    }

    // Sort orders by date descending
    orders.sort((a, b) => new Date(b.orderDate) - new Date(a.orderDate));

    orders.forEach(order => {
        const orderDate = new Date(order.orderDate).toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' });
        const statusClass = (order.status || 'pending').toLowerCase();
        
        const card = document.createElement('div');
        card.className = 'order-card';
        card.innerHTML = `
            <div class="order-summary" onclick="toggleOrderDetails(${order.orderId}, this)">
                <div class="summary-block">
                    <span class="summary-label">Order ID</span>
                    <span class="summary-value">#${order.orderId}</span>
                </div>
                <div class="summary-block">
                    <span class="summary-label">Date</span>
                    <span class="summary-value">${orderDate}</span>
                </div>
                <div class="summary-block">
                    <span class="summary-label">Total</span>
                    <span class="summary-value">₹${order.totalAmount.toFixed(2)}</span>
                </div>
                <div class="summary-block" style="text-align: right; display: flex; flex-direction: row; align-items: center; justify-content: flex-end; gap: 16px;">
                    <span class="status-badge ${statusClass}">${order.status}</span>
                    <i class="fas fa-chevron-down expand-icon"></i>
                </div>
            </div>
            <div class="order-details" id="details-${order.orderId}">
                <!-- Loaded dynamically on click -->
            </div>
        `;
        container.appendChild(card);
    });
}

window.toggleOrderDetails = async function(orderId, summaryElement) {
    const card = summaryElement.parentElement;
    const detailsContainer = document.getElementById(`details-${orderId}`);
    
    // Toggle state
    const isExpanded = card.classList.contains('expanded');
    
    // Collapse all other cards
    document.querySelectorAll('.order-card.expanded').forEach(c => {
        if (c !== card) c.classList.remove('expanded');
    });

    if (isExpanded) {
        card.classList.remove('expanded');
    } else {
        card.classList.add('expanded');
        
        // Fetch details if not already loaded
        if (detailsContainer.children.length === 0) {
            detailsContainer.innerHTML = '<div style="text-align: center; padding: 20px;"><i class="fas fa-circle-notch fa-spin"></i> Loading items...</div>';
            
            try {
                const res = await fetch(`${BASE_URL}/order-confirmation?orderId=${orderId}`);
                const data = await res.json();
                
                if (data.success && data.orderItems) {
                    detailsContainer.innerHTML = ''; // clear loading
                    
                    for (const item of data.orderItems) {
                        const productName = item.productName || `Product Variant #${item.variantId}`;
                        const productImg = item.imageUrl ? `../${item.imageUrl}` : '../assets/images/placeholder.jpg';
                        
                        const itemDiv = document.createElement('div');
                        itemDiv.className = 'detail-item';
                        itemDiv.innerHTML = `
                            <div class="detail-product">
                                <img src="${productImg}" alt="${productName}" class="detail-product-img" onerror="this.src='https://via.placeholder.com/60x80?text=No+Image'">
                                <div class="detail-product-info">
                                    <h4>${productName}</h4>
                                    <p>Size: ${item.size || 'N/A'} | Color: ${item.color || 'N/A'} | Qty: ${item.quantity}</p>
                                </div>
                            </div>
                            <div class="detail-price">
                                ₹${(item.price * item.quantity).toFixed(2)}
                            </div>
                        `;
                        detailsContainer.appendChild(itemDiv);
                    }
                    
                    // Add receipt link
                    const receiptLink = document.createElement('div');
                    receiptLink.style = "text-align: center; margin-top: 15px; padding-top: 15px; border-top: 1px solid var(--border-color);";
                    receiptLink.innerHTML = `<a href="order-confirmation.html?orderId=${orderId}" class="btn-secondary" style="font-size:12px; padding: 8px 16px;">View Full Receipt</a>`;
                    detailsContainer.appendChild(receiptLink);

                } else {
                    detailsContainer.innerHTML = `<p style="color:red;">Failed to load items.</p>`;
                }
            } catch (err) {
                console.error(err);
                detailsContainer.innerHTML = `<p style="color:red;">Error loading items.</p>`;
            }
        }
    }
};
