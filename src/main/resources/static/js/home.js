// Get Clerk token instead of old JWT cookie
async function loadMovies() {
    try {
        const token = await getClerkToken(); // <-- Clerk token
        if (!token) {
            window.location.href = "login.html";
            return;
        }

        const page = getPageFromUrl() - 1; // backend pages are zero-indexed
        const size = 10;

        const response = await fetch(`https://netflix-ldox1.sevalla.app/v1/movies/all?page=${page}&size=${size}`, {
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            }
        });

        if (response.status === 403) {
            window.location.href = "login.html";
            return;
        }

        const data = await response.json();

        if (data.code === 402) {
            window.location.href = "payment-plan.html";
            return;
        }

        const movies = data.data.content || data.content;

        const container = document.getElementById("trending-movies");
        container.innerHTML = "";

        movies.forEach(movie => {
            const card = document.createElement("div");
            card.className = "movie-card";

            const img = document.createElement("img");
            img.src = movie.imageUrl || "https://via.placeholder.com/300x450";
            img.className = "movie-poster";

            img.addEventListener("click", () => {
                window.location.href = `player.html?id=${movie.id}`;
            });

            const overlay = document.createElement("div");
            overlay.className = "movie-overlay";
            overlay.innerHTML = `<strong>${movie.title}</strong><br>${movie.description}`;

            card.appendChild(img);
            card.appendChild(overlay);
            container.appendChild(card);
        });

        // Render pagination buttons
        renderPagination(data.data.totalPages, page + 1);

    } catch (error) {
        console.error("Error fetching movies:", error);
        // Redirect if any auth error
        window.location.href = "login.html";
    }
}

// Get Clerk token
async function getClerkToken() {
    await Clerk.load();
    const session = Clerk.session;
    if (!session) return null;
    return await session.getToken();
}

// Get page number from URL query parameter
function getPageFromUrl() {
    const params = new URLSearchParams(window.location.search);
    return parseInt(params.get("page")) || 1;
}

// Pagination rendering remains the same
function renderPagination(totalPages, currentPage) {
    let paginationContainer = document.getElementById("pagination");
    if (!paginationContainer) {
        paginationContainer = document.createElement("div");
        paginationContainer.id = "pagination";
        paginationContainer.className = "d-flex gap-2 mt-3";
        document.querySelector(".movie-row").appendChild(paginationContainer);
    }
    paginationContainer.innerHTML = "";

    for (let i = 1; i <= totalPages; i++) {
        const btn = document.createElement("button");
        btn.textContent = i;
        btn.className = `btn btn-sm ${i === currentPage ? "btn-primary" : "btn-secondary"}`;
        btn.addEventListener("click", () => {
            const url = new URL(window.location);
            url.searchParams.set("page", i);
            window.location = url;
        });
        paginationContainer.appendChild(btn);
    }
}

// Load movies when page loads
document.addEventListener("DOMContentLoaded", loadMovies);
