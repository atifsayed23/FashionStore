

let allProducts       = [];
let currentCategoryId = null;

document.addEventListener('DOMContentLoaded', function () {
    loadCategories();
    readURLParams();
});

function readURLParams() {
    const params     = new URLSearchParams(window.location.search);
    const categoryId = params.get('categoryId');
    const keyword    = params.get('keyword');

    if (keyword) {
        document.getElementById('pageTitle').textContent    = 'Search Results';
        document.getElementById('pageSubtitle').textContent = 'Results for: ' + keyword;
        document.getElementById('searchInput').value        = keyword;
        searchByKeyword(keyword);
    } else if (categoryId) {
        currentCategoryId = categoryId;
        loadProductsByCategory(categoryId);
    } else {
        loadAllProducts();
    }
}

function loadAllProducts() {
    showSkeletons();
    document.getElementById('pageTitle').textContent    = 'All Products';
    document.getElementById('pageSubtitle').textContent = 'Discover our latest collection';

    fetch(BASE_URL + '/products')
        .then(function(res) { return res.json(); })
        .then(function(data) {
            if (data.success) {
                allProducts = data.products;
                renderProducts(allProducts);
            }
        })
        .catch(function(err) { console.error('Error:', err); });
}

function loadProductsByCategory(categoryId) {
    showSkeletons();
    fetch(BASE_URL + '/products?categoryId=' + categoryId)
        .then(function(res) { return res.json(); })
        .then(function(data) {
            if (data.success) {
                allProducts = data.products;
                renderProducts(allProducts);
                updatePageTitle(categoryId);
                setActiveCategory(categoryId);
            }
        })
        .catch(function(err) { console.error('Error:', err); });
}

function searchByKeyword(keyword) {
    showSkeletons();
    fetch(BASE_URL + '/products?keyword=' + encodeURIComponent(keyword))
        .then(function(res) { return res.json(); })
        .then(function(data) {
            if (data.success) {
                allProducts = data.products;
                renderProducts(allProducts);
            }
        })
        .catch(function(err) { console.error('Error:', err); });
}

function loadCategories() {
    fetch(BASE_URL + '/home')
        .then(function(res) { return res.json(); })
        .then(function(data) {
            if (data.success) {
                renderCategoryFilters(data.categories);
            }
        })
        .catch(function(err) { console.error('Error:', err); });
}

function renderCategoryFilters(categories) {
    const list = document.getElementById('categoryFilter');
    categories.forEach(function(cat) {
        const li       = document.createElement('li');
        li.textContent = cat.name;
        li.dataset.id  = cat.categoryId;
        li.onclick     = function() { filterByCategory(cat.categoryId, li); };
        list.appendChild(li);
    });

    const params     = new URLSearchParams(window.location.search);
    const categoryId = params.get('categoryId');
    if (categoryId) setActiveCategory(categoryId);
}

function filterByCategory(categoryId, element) {
    document.querySelectorAll('.filter-list li').forEach(function(li) {
        li.classList.remove('active');
    });
    element.classList.add('active');
    currentCategoryId = categoryId;

    if (categoryId === null) {
        loadAllProducts();
    } else {
        loadProductsByCategory(categoryId);
    }
}

function filterByPrice() {
    const minPrice = document.getElementById('minPrice').value;
    const maxPrice = document.getElementById('maxPrice').value;

    if (!minPrice || !maxPrice) {
        alert('Please enter both min and max price!');
        return;
    }

    showSkeletons();
    let url = BASE_URL + '/products?minPrice=' + minPrice + '&maxPrice=' + maxPrice;
    if (currentCategoryId) url += '&categoryId=' + currentCategoryId;

    fetch(url)
        .then(function(res) { return res.json(); })
        .then(function(data) {
            if (data.success) {
                allProducts = data.products;
                renderProducts(allProducts);
            }
        })
        .catch(function(err) { console.error('Error:', err); });
}

function sortProducts() {
    const sortValue = document.getElementById('sortSelect').value;
    let sorted      = [...allProducts];

    if (sortValue === 'price-low') {
        sorted.sort(function(a, b) { return a.price - b.price; });
    } else if (sortValue === 'price-high') {
        sorted.sort(function(a, b) { return b.price - a.price; });
    } else if (sortValue === 'name') {
        sorted.sort(function(a, b) { return a.name.localeCompare(b.name); });
    }

    renderProducts(sorted);
}

function clearFilters() {
    document.getElementById('minPrice').value   = '';
    document.getElementById('maxPrice').value   = '';
    document.getElementById('sortSelect').value = 'default';
    currentCategoryId = null;

    document.querySelectorAll('.filter-list li').forEach(function(li) {
        li.classList.remove('active');
    });
    document.querySelector('.filter-list li:first-child').classList.add('active');
    loadAllProducts();
}

function renderProducts(products) {
    const grid = document.getElementById('productsGrid');
    grid.innerHTML = '';

    document.getElementById('productCount').textContent = products.length + ' products found';

    if (!products || products.length === 0) {
        grid.innerHTML =
            '<div class="no-products">' +
            '<i class="fas fa-box-open"></i>' +
            '<p>No products found.</p>' +
            '</div>';
        return;
    }

    products.forEach(function(product) {
        const card     = document.createElement('div');
        card.className = 'product-card reveal-on-scroll';
        card.onclick   = function() {
            window.location.href = 'product-detail.html?productId=' + product.productId;
        };
        card.innerHTML =
            '<img src="../' + product.imageUrl + '" ' +
            'alt="' + product.name + '" ' +
            'style="width:100%; height:280px; object-fit:cover; background:#f5f0eb;" ' +
            'onerror="this.src=\'https://images.pexels.com/photos/1656684/pexels-photo-1656684.jpeg\'">' +
            '<div class="product-card-info">' +
            '<p class="brand">' + (product.brand || '') + '</p>' +
            '<h4>' + product.name + '</h4>' +
            '<p class="price">&#8377;' + product.price.toLocaleString('en-IN') + '</p>' +
            '</div>';
        grid.appendChild(card);
    });
    if (typeof setupRevealOnScroll === 'function') {
        setupRevealOnScroll();
    }
}

function updatePageTitle(categoryId) {
    const titles = {
        '1': { title: 'Men Collection',   subtitle: 'Premium clothing for men' },
        '2': { title: 'Women Collection', subtitle: 'Latest fashion for women' },
        '3': { title: 'Kids Collection',  subtitle: 'Trendy clothes for kids' },
        '4': { title: 'Shoes',            subtitle: 'Step out in style' },
        '5': { title: 'Accessories',      subtitle: 'Complete your look' }
    };

    const t = titles[categoryId] || { title: 'All Products', subtitle: 'Discover our collection' };
    document.getElementById('pageTitle').textContent    = t.title;
    document.getElementById('pageSubtitle').textContent = t.subtitle;
}

function setActiveCategory(categoryId) {
    document.querySelectorAll('.filter-list li').forEach(function(li) {
        li.classList.remove('active');
        if (li.dataset.id == categoryId) li.classList.add('active');
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
    for (let i = 0; i < 6; i++) {
        grid.innerHTML += '<div class="skeleton"></div>';
    }
}