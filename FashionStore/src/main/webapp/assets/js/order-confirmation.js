document.addEventListener('DOMContentLoaded', () => {
    const urlParams = new URLSearchParams(window.location.search);
    const orderId = urlParams.get('orderId');

    if (!orderId) {
        document.getElementById('orderNotice').textContent = "No Order ID provided.";
        document.getElementById('receiptItems').innerHTML = '<p style="color:red; text-align:center;">Invalid Order Link</p>';
        return;
    }

    document.getElementById('orderNotice').textContent = `Order #${orderId} has been placed successfully. We will send you a shipping confirmation email soon.`;

    fetch(`${BASE_URL}/order-confirmation?orderId=${orderId}`)
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                const order = data.order;
                const items = data.orderItems;

                document.getElementById('orderDateStr').textContent = new Date(order.orderDate).toLocaleString();
                document.getElementById('orderTotal').textContent = `₹${order.totalAmount.toFixed(2)}`;

                // Update Progress status
                updateProgress(order.status);

                // Fetch Product details for each item to show name
                renderReceiptItems(items);
            } else {
                document.getElementById('receiptItems').innerHTML = `<p style="color:red; text-align:center;">${data.message}</p>`;
            }
        })
        .catch(err => {
            console.error(err);
            document.getElementById('receiptItems').innerHTML = '<p style="color:red; text-align:center;">Error loading receipt details.</p>';
        });
});

function updateProgress(status) {
    // Statuses: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    const st = status ? status.toUpperCase() : 'PENDING';
    
    if (st === 'PROCESSING' || st === 'SHIPPED' || st === 'DELIVERED') {
        document.getElementById('step-processing').classList.add('completed');
    }
    if (st === 'SHIPPED' || st === 'DELIVERED') {
        document.getElementById('step-shipped').classList.add('completed');
    }
    if (st === 'DELIVERED') {
        document.getElementById('step-delivered').classList.add('completed');
    }
    if (st === 'CANCELLED') {
        document.getElementById('step-processing').innerHTML = '<div class="step-icon" style="background:#f44336; color:#fff; border-color:#f44336;"><i class="fas fa-times"></i></div><div class="step-label" style="color:#f44336;">Cancelled</div>';
    }
}

async function renderReceiptItems(items) {
    const container = document.getElementById('receiptItems');
    container.innerHTML = '';

    if (!items || items.length === 0) {
        container.innerHTML = '<p style="text-align:center;">No items found.</p>';
        return;
    }

    for (const item of items) {
        try {
            const res = await fetch(`${BASE_URL}/product-detail?productId=${item.productId}`);
            const data = await res.json();
            const productName = data.success && data.product ? data.product.name : `Product #${item.productId}`;
            
            const itemDiv = document.createElement('div');
            itemDiv.className = 'receipt-item';
            itemDiv.innerHTML = `
                <span>${item.quantity}x ${productName}</span>
                <span>₹${(item.price * item.quantity).toFixed(2)}</span>
            `;
            container.appendChild(itemDiv);
        } catch (e) {
            console.error("Failed to load product details for", item.productId);
            const itemDiv = document.createElement('div');
            itemDiv.className = 'receipt-item';
            itemDiv.innerHTML = `
                <span>${item.quantity}x Item #${item.productId}</span>
                <span>₹${(item.price * item.quantity).toFixed(2)}</span>
            `;
            container.appendChild(itemDiv);
        }
    }
}
