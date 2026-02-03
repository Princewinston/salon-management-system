const API_URL = '/api';

document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('product-list')) {
        loadProducts();
    }

    if (document.getElementById('inventory-list')) {
        loadInventory(); // For dashboard
    }

    if (document.getElementById('inventory-list')) {
        loadInventory(); // For dashboard
    }

    checkAuthStatus();
});

let userAppointments = [];
let currentUserIsAdmin = false;
let isUserLoggedIn = false;

async function checkAuthStatus() {
    console.log("Checking auth status...");
    try {
        const res = await fetch('/api/auth/me', {
            headers: {
                'Cache-Control': 'no-cache',
                'Pragma': 'no-cache'
            },
            credentials: 'include'
        });

        if (!res.ok) {
            console.error("Auth check failed with status:", res.status);
            return;
        }

        const user = await res.json();
        console.log("Auth response:", user);

        if (user.authenticated) {
            console.log("User is authenticated as:", user.username);
            isUserLoggedIn = true;

            // NEW: Load appointments to update UI state
            await loadUserAppointmentsState();

            // Check for ADMIN role
            const roles = user.roles || [];

            // Robust Check: Handle string array ["ROLE_ADMIN"] or object array [{authority: "ROLE_ADMIN"}]
            let isAdmin = false;
            if (roles.length > 0) {
                if (typeof roles[0] === 'string') {
                    isAdmin = roles.includes('ROLE_ADMIN') || roles.includes('ADMIN');
                } else if (typeof roles[0] === 'object' && roles[0].authority) {
                    isAdmin = roles.some(r => r.authority === 'ROLE_ADMIN' || r.authority === 'ADMIN');
                }
            }
            currentUserIsAdmin = isAdmin;

            console.log("Is Admin Check:", isAdmin);

            // Show "My Appointments" in Navbar for all logged-in users
            const apptLink = document.getElementById('nav-appointments');
            if (apptLink) {
                apptLink.style.display = 'inline';
                apptLink.style.marginRight = '20px';
            }

            if (isAdmin) {
                const dashLink = document.getElementById('nav-dashboard');
                if (dashLink) {
                    dashLink.style.display = 'inline';
                    dashLink.style.marginRight = '20px'; // spacing
                    console.log("Dashboard link shown in navbar");
                }
            }

            const loginBtn = document.getElementById('nav-login');
            if (loginBtn) {
                // Replace Login button with User Profile Dropdown
                const nav = document.querySelector('nav');

                // Remove login button
                loginBtn.remove();

                // Create Dropdown Container
                const dropdown = document.createElement('div');
                dropdown.className = 'user-dropdown';

                // Get first letter for avatar
                const initial = user.username.charAt(0).toUpperCase();

                dropdown.innerHTML = `
                    <div class="user-btn">
                        <div class="user-avatar">
                            ${initial}
                        </div>
                        <span class="user-name">${user.username}</span>
                        <i class="fas fa-chevron-down" style="font-size: 12px; color: var(--text-muted);"></i>
                    </div>
                    <div class="dropdown-content">
                        <!-- Removed My Appointments from here -->
                        <form action="/perform_logout" method="POST" style="margin:0;">
                            <button type="submit"><i class="fas fa-sign-out-alt"></i> Logout</button>
                        </form>
                    </div>
                `;

                nav.appendChild(dropdown);
            }
        } else {
            console.log("User is NOT authenticated");
        }
    } catch (e) {
        console.error("Auth check failed exception:", e);
    }
}

async function loadUserAppointmentsState() {
    try {
        const res = await fetch('/api/appointments/my-appointments');
        if (res.ok) {
            userAppointments = await res.json();
            console.log("Loaded user appointments for UI state:", userAppointments);
            // Re-render products if they are already loaded
            if (allProducts.length > 0) {
                renderProducts(allProducts);
            }
        }
    } catch (e) {
        console.error("Failed to load user appointments state", e);
    }
}

let allProducts = [];

async function loadProducts() {
    try {
        const response = await fetch(`${API_URL}/products`);
        allProducts = await response.json();
        renderProducts(allProducts);
    } catch (error) {
        console.error('Error loading products:', error);
    }
}

// Event Delegation for Product List
document.addEventListener('click', function (e) {
    // Check if the click is inside the product list
    const productList = document.getElementById('product-list');
    if (productList && productList.contains(e.target)) {

        // Find the closest card
        const card = e.target.closest('.card');
        if (card) {
            // Check if the clicked element is NOT the "Book Now" button
            // (The button has its own listener or onclick, but we want to be sure)
            if (e.target.tagName !== 'BUTTON' && !e.target.closest('button')) {
                const productId = card.getAttribute('data-id');
                console.log("Delegated click on card ID:", productId);
                if (productId) {
                    openServiceDetail(parseInt(productId));
                }
            }
        }
    }
});

function renderProducts(products) {
    const container = document.getElementById('product-list');
    container.innerHTML = '';

    if (products.length === 0) {
        container.innerHTML = '<p class="text-muted" style="grid-column: 1/-1; text-align: center;">No services found in this category.</p>';
        return;
    }

    products.forEach(product => {
        const card = document.createElement('div');
        card.className = 'card';
        card.setAttribute('data-id', product.productId); // Store ID for delegation
        card.style.animation = 'fadeIn 0.5s ease-out';
        card.style.cursor = 'pointer';

        // Check if user has already booked this service (and not cancelled)
        const isBooked = userAppointments.some(appt =>
            appt.service && appt.service.productId === product.productId && appt.status !== 'Cancelled'
        );

        // Random Rating Logic
        const rating = product.averageRating ? product.averageRating.toFixed(1) : (Math.random() * (5.0 - 4.0) + 4.0).toFixed(1);

        // Image handling
        const imgSrc = product.imageUrl || 'https://via.placeholder.com/300x200?text=Service';

        const imgHtml = `
            <div class="card-image">
                <img src="${imgSrc}" alt="${product.productName}" onerror="this.onerror=null;this.src='https://via.placeholder.com/300x200?text=Salon+Service';">
            </div>
        `;

        let btnHtml = `<button class="btn" id="book-btn-${product.productId}" onclick="event.stopPropagation(); openBookingModal(${product.productId})">Book Now</button>`;
        if (isBooked) {
            btnHtml = `<button class="btn" disabled style="background:#555; cursor:not-allowed;" onclick="event.stopPropagation()">Booked</button>`;
        }

        card.innerHTML = `
            ${imgHtml}
            <div class="card-content">
                <div class="author-row">
                    <span>${product.category ? product.category.categoryName : 'Premium Service'}</span>
                    <span class="rating"><i class="fas fa-star"></i> ${rating}</span>
                </div>
                <h3>${product.productName}</h3>
                <p>${product.description || 'Experience the best care with our premium mock services.'}</p>
                <div class="card-footer">
                    <span class="price">$${product.price}</span>
                    ${btnHtml}
                </div>
            </div>
        `;

        container.appendChild(card);
    });
}

window.openServiceDetail = function (productId) {
    console.log("Opening detail for ID:", productId);
    const product = allProducts.find(p => p.productId === productId);
    if (!product) {
        console.error("Product not found!");
        return;
    }

    const imgUrl = product.imageUrl || 'https://via.placeholder.com/800x400';
    document.getElementById('detail-image').src = imgUrl;
    document.getElementById('detail-image-bg').style.backgroundImage = `url('${imgUrl}')`;
    document.getElementById('detail-title').textContent = product.productName;
    document.getElementById('detail-price').textContent = `$${product.price}`;
    document.getElementById('detail-desc').textContent = product.description;

    // Setup Book Button
    const btn = document.getElementById('detail-book-btn');

    // Check if user has already booked this service
    const isBooked = userAppointments.some(appt =>
        appt.service && appt.service.productId === productId && appt.status !== 'Cancelled'
    );

    if (isBooked) {
        btn.textContent = "Booked";
        btn.disabled = true;
        btn.style.background = "#555";
        btn.style.cursor = "not-allowed";
        btn.onclick = null;
    } else {
        btn.textContent = "Book Now";
        btn.disabled = false;
        btn.style.background = "#ff0055"; // Reset to default theme color
        btn.style.cursor = "pointer";
        btn.onclick = () => {
            closeModal('service-detail-modal');
            openBookingModal(product.productId);
        };
    }

    // Load Real Reviews
    loadServiceReviews(productId);

    // Store current product ID for review submission
    document.getElementById('review-form').setAttribute('data-product-id', productId);

    document.getElementById('service-detail-modal').style.display = 'flex';
};

async function loadServiceReviews(productId) {
    const container = document.getElementById('detail-reviews');
    container.innerHTML = '<p style="color: var(--text-muted);">Loading reviews...</p>';

    try {
        const res = await fetch(`/api/feedback?productId=${productId}`);
        const reviews = await res.json();

        container.innerHTML = '';
        if (reviews.length === 0) {
            container.innerHTML = '<p style="color: var(--text-muted); font-style: italic;">No reviews yet. Be the first to review!</p>';
            return;
        }

        reviews.forEach(r => {
            const div = document.createElement('div');
            div.style.background = 'rgba(255,255,255,0.02)';
            div.style.padding = '15px';
            div.style.borderRadius = '10px';
            div.style.border = '1px solid rgba(255,255,255,0.05)';

            div.innerHTML = `
                <div style="display: flex; justify-content: space-between; margin-bottom: 8px;">
                    <strong style="color: white; font-size: 14px;">Customer</strong>
                    <span style="color: #ffd700; font-size: 14px;">${'★'.repeat(r.rating)}${'☆'.repeat(5 - r.rating)}</span>
                </div>
                <p style="font-size: 14px; color: var(--text-muted); line-height: 1.5;">${r.comment}</p>
            `;
            container.appendChild(div);
        });

        // Update Dynamic Header Count
        const avg = (reviews.reduce((a, b) => a + b.rating, 0) / reviews.length).toFixed(1);
        const count = reviews.length;
        const starEl = document.getElementById('detail-rating-count');
        if (starEl) {
            starEl.innerHTML = `<i class="fas fa-star"></i> ${avg} (${count} Reviews)`;
        }

    } catch (e) {
        console.error("Error loading reviews:", e);
        container.innerHTML = '<p style="color: #ff0055;">Failed to load reviews.</p>';
    }
}

window.submitReview = async function (e) {
    e.preventDefault();
    if (!isUserLoggedIn) {
        alert("Please log in to submit a review.");
        window.location.href = 'login.html';
        return;
    }
    const form = document.getElementById('review-form');
    const productId = form.getAttribute('data-product-id');
    const rating = parseInt(document.getElementById('review-rating').value);
    const comment = document.getElementById('review-comment').value;

    const data = {
        rating: rating,
        comment: comment,
        product: { productId: productId }
    };

    try {
        const res = await fetch('/api/feedback', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (res.ok) {
            form.reset();
            loadServiceReviews(productId); // Reload reviews dynamically
        } else {
            alert('Error submitting review');
        }
    } catch (e) {
        console.error(e);
        alert('Network error');
    }

    document.getElementById('service-detail-modal').style.display = 'flex';
}

function filterServices(category) {
    console.log("Filtering by:", category);

    if (category === 'All') {
        renderProducts(allProducts);
        return;
    }

    const filtered = allProducts.filter(p => {
        const pCat = p.category ? p.category.categoryName : "";
        // Case-insensitive check
        return pCat.toLowerCase().trim() === category.toLowerCase().trim();
    });

    console.log("Found matches:", filtered.length);
    renderProducts(filtered);
}

function openFeedbackModal(productId) {
    document.getElementById('modal-product-id').value = productId;
    document.getElementById('feedback-modal').style.display = 'flex';
}

// --- Modal Logic ---
function openBookingModal(productId) {
    if (!isUserLoggedIn) {
        alert("Please log in to book an appointment.");
        window.location.href = 'login.html';
        return;
    }

    if (currentUserIsAdmin) {
        alert("Administrators cannot book services.");
        return;
    }

    document.getElementById('booking-modal').style.display = 'flex';
    document.getElementById('book-product-id').value = productId;

    // Reset to Step 1
    document.getElementById('booking-step-1').style.display = 'block';
    document.getElementById('booking-step-2').style.display = 'none';
    document.getElementById('booking-form').reset();

    // Prevent past dates
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('book-date').min = today;
}

function goToPayment() {
    // Validate Step 1
    const name = document.getElementById('book-name').value;
    const email = document.getElementById('book-email').value;
    const phone = document.getElementById('book-phone').value;
    const date = document.getElementById('book-date').value;
    const time = document.getElementById('book-time').value;

    if (!name || !email || !phone || !date || !time) {
        alert("Please fill in all appointment details first.");
        return;
    }

    // Switch to Step 2
    document.getElementById('booking-step-1').style.display = 'none';
    document.getElementById('booking-step-2').style.display = 'block';
}

function goBackToDetails() {
    document.getElementById('booking-step-2').style.display = 'none';
    document.getElementById('booking-step-1').style.display = 'block';
}

function openModal(id) { document.getElementById(id).style.display = 'flex'; }
function closeModal(id) { document.getElementById(id).style.display = 'none'; }

async function submitBooking(e) {
    e.preventDefault();

    // Simple Payment Validation
    const card = document.getElementById('pay-card').value;
    const cvv = document.getElementById('pay-cvv').value;

    if (card.length < 13 || cvv.length < 3) {
        alert("Please enter valid payment details.");
        return;
    }

    // Show Loading
    const btn = document.getElementById('confirm-booking-btn');
    const btnText = document.getElementById('btn-text');
    const spinner = document.getElementById('btn-spinner');

    btn.disabled = true;
    btnText.textContent = "Processing Payment...";
    spinner.style.display = "inline-block";

    // Simulate 2s Payment Delay
    await new Promise(r => setTimeout(r, 2000));

    const data = {
        service: { productId: document.getElementById('book-product-id').value },
        customerName: document.getElementById('book-name').value,
        email: document.getElementById('book-email').value,
        contactNumber: document.getElementById('book-phone').value,
        appointmentDate: document.getElementById('book-date').value,
        appointmentTime: document.getElementById('book-time').value
    };

    try {
        const res = await fetch(`${API_URL}/appointments`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        if (res.ok) {
            closeModal('booking-modal');

            // Show new Success Modal
            const successModal = document.getElementById('success-modal');
            successModal.style.display = 'flex';

            // Refresh User Appointments State to update UI (Book Now -> Booked)
            await loadUserAppointmentsState();

            // Optional: trigger confetti effect if we had a library, but the modal is decorative enough
        } else {
            alert('Booking Failed.');
        }
    } catch (e) {
        console.error(e);
        alert('Error connecting to server.');
    } finally {
        // Reset Button
        btn.disabled = false;
        btnText.textContent = "Pay & Confirm Booking";
        spinner.style.display = "none";
    }
}

async function submitFeedback(e) {
    e.preventDefault();
    const productId = document.getElementById('modal-product-id').value;
    const rating = document.getElementById('feedback-rating').value;
    const comment = document.getElementById('feedback-comment').value;

    const feedbackData = {
        rating: parseInt(rating),
        comment: comment,
        product: { productId: parseInt(productId) }
    };

    try {
        const response = await fetch(`${API_URL}/feedback`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(feedbackData)
        });

        if (response.ok) {
            alert('Thank you for your feedback!');
            closeModal('feedback-modal');
        } else {
            alert('Failed to submit feedback.');
        }
    } catch (error) {
        console.error('Error submitting feedback:', error);
    }
}

window.onclick = function (event) {
    if (event.target.classList.contains('modal')) {
        event.target.style.display = "none";
    }
}

// --- My Appointments Logic ---
async function openMyAppointments() {
    console.log("Opening My Appointments...");
    const container = document.getElementById('my-appts-list');
    if (!container) return; // safety

    container.innerHTML = '<p style="color:var(--text-muted); text-align:center;">Loading your bookings...</p>';
    document.getElementById('my-appointments-modal').style.display = 'flex';

    try {
        const res = await fetch('/api/appointments/my-appointments');
        if (res.status === 401 || res.status === 403) {
            container.innerHTML = '<p style="color: #ff0055;">Please login to view appointments.</p>';
            return;
        }

        const appts = await res.json();

        container.innerHTML = '';
        if (appts.length === 0) {
            container.innerHTML = '<p style="text-align:center; color: var(--text-muted);">You have no upcoming appointments.</p>';
            return;
        }

        appts.forEach(a => {
            const div = document.createElement('div');
            div.className = 'appt-card'; // will add style
            div.style.background = 'rgba(255,255,255,0.05)';
            div.style.borderRadius = '10px';
            div.style.padding = '15px';
            div.style.marginBottom = '10px';
            div.style.borderLeft = `4px solid ${getStatusColor(a.status)}`;

            let reasonHtml = '';
            let actionBtns = '';

            if (a.status === 'Cancelled' && a.cancellationReason) {
                reasonHtml = `<p style="margin-top: 8px; font-size: 13px; color: #ff6b81; background: rgba(255,0,85,0.1); padding: 5px 10px; border-radius: 5px; border-left: 2px solid #ff0055;">
                    <i class="fas fa-info-circle"></i> Reason: ${a.cancellationReason}
                </p>`;
            }

            if (currentUserIsAdmin && a.status === 'Pending') {
                actionBtns = `
                    <div style="margin-top: 15px; display: flex; gap: 10px;">
                        <button class="btn" style="padding: 5px 15px; font-size: 12px; background: #00ff88; color: #000;" onclick="confirmAppointment(${a.appointmentId})">
                            <i class="fas fa-check"></i> Confirm
                        </button>
                        <button class="btn" style="padding: 5px 15px; font-size: 12px; background: #ff0055;" onclick="initiateCancel(${a.appointmentId})">
                            <i class="fas fa-times"></i> Cancel
                        </button>
                    </div>
                `;
            }

            div.innerHTML = `
                <div style="display:flex; justify-content:space-between; align-items:center;">
                    <h4 style="margin:0; color: white;">${a.service ? a.service.productName : 'Service'}</h4>
                    <span style="font-size:12px; padding:4px 8px; border-radius:4px; background:rgba(255,255,255,0.1); color:${getStatusColor(a.status)};">${a.status}</span>
                </div>
                <p style="margin: 5px 0; color:var(--text-muted); font-size:14px;">
                    <i class="far fa-calendar"></i> ${a.appointmentDate} &nbsp;&nbsp; 
                    <i class="far fa-clock"></i> ${a.appointmentTime}
                </p>
                ${reasonHtml}
                ${actionBtns}
            `;
            container.appendChild(div);
        });

    } catch (e) {
        console.error(e);
        container.innerHTML = '<p style="color: #ff0055;">Failed to load appointments.</p>';
    }
}

function getStatusColor(status) {
    if (status === 'Confirmed') return '#00ff88';
    if (status === 'Cancelled') return '#ff0055';
    return '#fca311'; // Pending
}

// Admin Actions
async function confirmAppointment(id) {
    if (!confirm("Confirm this appointment?")) return;
    try {
        const res = await fetch(`${API_URL}/appointments/${id}/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: 'Confirmed' })
        });
        if (res.ok) {
            openMyAppointments(); // Reload list
        } else {
            alert("Failed to confirm");
        }
    } catch (e) { console.error(e); }
}

function initiateCancel(id) {
    document.getElementById('cancel-appt-id').value = id;
    document.getElementById('cancel-reason').value = '';
    openModal('cancel-modal');
}

async function submitCancellation(e) {
    e.preventDefault();
    const id = document.getElementById('cancel-appt-id').value;
    const reason = document.getElementById('cancel-reason').value;

    try {
        const res = await fetch(`${API_URL}/appointments/${id}/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: 'Cancelled', reason: reason })
        });
        if (res.ok) {
            closeModal('cancel-modal');
            openMyAppointments(); // Reload list
        } else {
            alert("Failed to cancel");
        }
    } catch (e) { console.error(e); }
}
