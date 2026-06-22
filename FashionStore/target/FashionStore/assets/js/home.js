

document.addEventListener('DOMContentLoaded', function () {
    loadHomeData();
});

function loadHomeData() {
    showSkeletons();
    fetch(BASE_URL + '/home')
        .then(function(res) { return res.json(); })
        .then(function(data) {
            if (data.success) {
                renderCategories(data.categories);
                renderFeaturedProducts(data.featuredProducts);
                if (typeof setupRevealOnScroll === 'function') {
                    setupRevealOnScroll();
                }
            }
        })
        .catch(function(err) { console.error('Error:', err); });
}

function renderCategories(categories) {
    const grid = document.getElementById('categoriesGrid');
    grid.innerHTML = '';

    if (!categories || categories.length === 0) {
        grid.innerHTML = '<p>No categories found</p>';
        return;
    }

    const categoryImages = {
        'Men':   'https://images.unsplash.com/photo-1617137968427-85924c800a22?auto=format&fit=crop&q=80&w=800',
        'Women': 'https://images.unsplash.com/photo-1490481651871-ab68de25d43d?auto=format&fit=crop&q=80&w=800',
        'Kids':  'https://images.unsplash.com/photo-1514090458221-65bb69cf63e6?auto=format&fit=crop&q=80&w=800',
        'Shoes': 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&q=80&w=800',
        'Accessories': 'https://images.unsplash.com/photo-1523206489230-c012c64b2b48?auto=format&fit=crop&q=80&w=800'
    };

    const categoryClass = {
        'Men':   'men',
        'Women': 'women',
        'Kids':  'kids',
        'Shoes': 'shoes',
        'Accessories': 'accessories'
    };

    categories.forEach(function(cat) {
        const card = document.createElement('div');
        card.className = 'category-card ' + (categoryClass[cat.name] || '');
        card.onclick = function() {
            window.location.href = 'product-list.html?categoryId=' + cat.categoryId;
        };
        card.innerHTML =
            '<img src="' + (categoryImages[cat.name] || 'https://images.pexels.com/photos/1656684/pexels-photo-1656684.jpeg') + '" ' +
            'alt="' + cat.name + '" ' +
            'style="width:100%; height:100%; object-fit:cover; position:absolute; top:0; left:0;">' +
            '<div class="category-card-overlay"><span>' + cat.name + '</span></div>';
        grid.appendChild(card);
    });
}

function renderFeaturedProducts(products) {
    const grid = document.getElementById('productsGrid');
    grid.innerHTML = '';

    if (!products || products.length === 0) {
        grid.innerHTML = '<p style="text-align:center; padding:20px;">No products found.</p>';
        return;
    }

    products.forEach(function(product) {
        const card = document.createElement('div');
        card.className = 'product-card';
        card.onclick = function() {
            window.location.href = 'product-detail.html?productId=' + product.productId;
        };
        card.innerHTML =
            '<img src="../' + product.imageUrl + '" ' +
            'alt="' + product.name + '" ' +
            'style="width:100%; height:250px; object-fit:cover; background:#f5f0eb;" ' +
            'onerror="this.src=\'https://images.pexels.com/photos/1656684/pexels-photo-1656684.jpeg\'">' +
            '<div class="product-card-info">' +
            '<p class="brand">' + (product.brand || '') + '</p>' +
            '<h4>' + product.name + '</h4>' +
            '<p class="price">&#8377;' + product.price.toLocaleString('en-IN') + '</p>' +
            '</div>';
        grid.appendChild(card);
    });
}

function loadCartCount() {
    fetch(BASE_URL + '/cart')
        .then(function(res) { return res.json(); })
        .then(function(data) {
            if (data.success) {
                document.getElementById('cartCount').textContent = data.itemCount;
            }
        })
        .catch(function() {});
}

function setupSearch() {
    document.getElementById('searchBtn').addEventListener('click', function() {
        document.getElementById('searchBar').classList.toggle('active');
    });
}

function searchProducts() {
    const keyword = document.getElementById('searchInput').value.trim();
    if (keyword) {
        window.location.href = 'product-list.html?keyword=' + encodeURIComponent(keyword);
    }
}

function showSkeletons() {
    const grid = document.getElementById('productsGrid');
    grid.innerHTML = '';
    for (let i = 0; i < 8; i++) {
        grid.innerHTML += '<div class="skeleton"></div>';
    }
}