// Get JWT token from cookies
function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

// Get page number from URL query parameter
function getPageFromUrl() {
    const params = new URLSearchParams(window.location.search);
    const page = parseInt(params.get("page")) || 1; // default to page 1
    return page;
}

// Load movies from API
async function loadMovies() {
    try {
        const token = getCookie("token");
        const page = getPageFromUrl() - 1; // backend pages are zero-indexed
        const size = 10;

        const response = await fetch(`http://localhost:8080/v1/movies/all?page=${page}&size=${size}`, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });
        if(response.status === 403){
            window.location.href = "login.html"
        }
        const data = await response.json();
        if (data.code === 402) {
            window.location.href = "payment-plan.html";
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

            // Open player page on click
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
    }
}

// Render pagination links
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
            window.location = url; // reload with new page
        });
        paginationContainer.appendChild(btn);
    }
}

document.addEventListener("DOMContentLoaded", loadMovies);
