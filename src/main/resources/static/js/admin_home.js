const API_URL = "http://localhost:8080/v1/movies";
const TOKEN = getCookie("token");
const TMDB_KEY = "8eaf4b353f53d2952b8fb3f8bedbbddd";
const SUBTITLEAPI = "https://managapi-eak67.kinsta.app/download_subtitles?name="

const notyf = new Notyf();
let currentPage = 0;
let totalPages = 1;
const pageSize = 16;
let genreMap = {};

function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

// Load genres mapping once
async function loadGenres() {
    const res = await fetch(
        `https://api.themoviedb.org/3/genre/movie/list?api_key=${TMDB_KEY}&language=en-US`
    );
    const data = await res.json();
    genreMap = Object.fromEntries(data.genres.map(g => [g.id, g.name]));
}

// ================== Load Movies ==================
async function loadMovies(page = 0) {
    const response = await fetch(`${API_URL}/all?page=${page}&size=${pageSize}`, {
        headers: { "Authorization": `Bearer ${TOKEN}` }
    });
    if (response.status === 403) {
        window.location.href = "login.html";
        return;
    }
    const result = await response.json();
    const data = result.data;

    const moviesContainer = document.getElementById("movies-container");
    moviesContainer.innerHTML = "";

    data.content.forEach(movie => {
        const card = document.createElement("div");
        card.className = "movie-card";
        card.innerHTML = `
            <img src="${movie.imageUrl}" alt="${movie.title}">
            <div class="info">
            <h4>${movie.title}</h4>
            <small>${movie.year}</small>
            </div>
        `;
        card.addEventListener("click", () => openEditModal(movie));
        moviesContainer.appendChild(card);
    });

    currentPage = data.number;
    totalPages = data.totalPages;
    renderPagination();
}

function renderPagination() {
    const controls = document.getElementById("pagination-controls");
    controls.innerHTML = `
    <button class="pagination-btn" onclick="loadMovies(${currentPage - 1})" ${currentPage === 0 ? "disabled" : ""}>Prev</button>
    <span class="page-indicator">Page ${currentPage + 1} of ${totalPages}</span>
    <button class="pagination-btn" onclick="loadMovies(${currentPage + 1})" ${currentPage + 1 >= totalPages ? "disabled" : ""}>Next</button>
    `;
}

// ================== Modal Logic ==================
const modal = document.getElementById("movieModal");
const openBtn = document.querySelector(".add-movie-btn");
const closeBtn = document.getElementById("closeModal");
const modalTitle = document.getElementById("modalTitle");
const form = document.getElementById("movieForm");

openBtn.addEventListener("click", () => openAddModal());
closeBtn.addEventListener("click", () => modal.style.display = "none");
window.addEventListener("click", e => { if (e.target === modal) modal.style.display = "none"; });

function openAddModal() {
    modalTitle.textContent = "Add Movie";
    form.reset();
    document.getElementById("movieId").value = "";
    modal.style.display = "flex";
}

function deleteMovie(id) {
    if (!confirm("Are you sure you want to delete this movie?")) return;
    fetch(`${API_URL}/delete/${id}`, {
        method: "DELETE",
        headers: { "Authorization": `Bearer ${TOKEN}` }
    })
    .then(res => res.json())
    .then(data => {
        if (data.code === 200) {
            notyf.success({
                message: 'Movie deleted successfully',
                duration: 9000
            });
            modal.style.display = "none";
            loadMovies(currentPage);
        }
        else {
            notyf.error({
                message: data.data,
                duration: 9000
            });
        }
    })
    .catch(err => {
        console.error("Error deleting movie:", err);
    });
}

function openEditModal(movie) {
    modalTitle.textContent = "Edit Movie";
    document.getElementById("movieId").value = movie.id;
    document.getElementById("title").value = movie.title;
    document.getElementById("year").value = movie.year;
    document.getElementById("genres").value = movie.genres.join(", ");
    document.getElementById("description").value = movie.description;
    document.getElementById("imageUrl").value = movie.imageUrl;
    document.getElementById("videoUrl").value = movie.videoUrl;
    document.getElementById("subtitleUrl").value = movie.subtitleUrl;
    document.getElementById("deleteMovieBtn").onclick = () => deleteMovie(movie.id);
    modal.style.display = "flex";
}

// ================== TMDB Search ==================
const tmdbSearch = document.getElementById("tmdbSearch");
const tmdbResults = document.getElementById("tmdbResults");
let searchTimeout;

tmdbSearch.addEventListener("input", () => {
    clearTimeout(searchTimeout);
    const query = tmdbSearch.value.trim();
    if (query.length < 2) {
        tmdbResults.style.display = "none";
        return;
    }
    searchTimeout = setTimeout(() => searchTMDB(query), 400);
});
// Fetch subtitles from external API and add loading indicator
async function fetchSubtitles(movieName) {
    const loadingIndicator = document.getElementById("loadingIndicator");
    loadingIndicator.style.display = "block";
    try {
        const response = await fetch(SUBTITLEAPI + encodeURIComponent(movieName));
        if (!response.ok) throw new Error("Failed to fetch subtitles");
        const srtFile = await response.json();
        console.log("Subtitle URL:", srtFile);
        loadingIndicator.style.display = "none";
        return srtFile.subtitle_url;
    } catch (error) {
        console.error("Error fetching subtitles:", error);
        return "";
    }
}

// Fetch movie details when selected
async function selectTMDBMovie(movieId) {
    const res = await fetch(
        `https://api.themoviedb.org/3/movie/${movieId}?api_key=${TMDB_KEY}&language=en-US`
    );
    const movie = await res.json();

    document.getElementById("title").value = movie.title || "";
    document.getElementById("year").value = movie.release_date ? movie.release_date.substring(0,4) : "";
    document.getElementById("description").value = movie.overview || "";
    document.getElementById("imageUrl").value = movie.poster_path ? `https://image.tmdb.org/t/p/original${movie.poster_path}` : "";
    document.getElementById("genres").value = movie.genres.map(g => g.name).join(", ");
    document.getElementById("videoUrl").value = `https://vidsrc.xyz/embed/movie?tmdb=${movie.id}`;

    tmdbResults.style.display = "none";
    tmdbSearch.value = movie.title;
    document.getElementById("subtitleUrl").value = await fetchSubtitles(movie.title);
}

// TMDB Search
async function searchTMDB(query) {
    const res = await fetch(
        `https://api.themoviedb.org/3/search/movie?api_key=${TMDB_KEY}&query=${encodeURIComponent(query)}`
    );
    const data = await res.json();
    tmdbResults.innerHTML = "";

    if (!data.results || data.results.length === 0) {
        tmdbResults.style.display = "none";
        return;
    }

    data.results.slice(0, 10).forEach(movie => {
        const div = document.createElement("div");
        //const genres = movie.genre_ids.map(id => genreMap[id]).filter(Boolean).join(", ");
        div.textContent = `${movie.title} (${movie.release_date ? movie.release_date.substring(0,4) : "N/A"})`;
        div.addEventListener("click", () => selectTMDBMovie(movie.id));
        tmdbResults.appendChild(div);
    });
    tmdbResults.style.display = "block";
}

// ================== Save Movie ==================
form.addEventListener("submit", async function(e) {
    e.preventDefault();
    const formData = new FormData(this);
    const movie = Object.fromEntries(formData.entries());
    movie.genres = movie.genres.split(",").map(g => g.trim());
    console.log("Saving movie:", movie);
    let url = API_URL;
    let method = "POST";

    if (movie.id) {
        url = `${API_URL}/update/${movie.id}`;
        method = "PUT";
    } else {
        url = API_URL+"/add";
    }
    
    const response = await fetch(url, {
        method,
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${TOKEN}`
        },
        body: JSON.stringify(movie)
    });
    const data = await response.json();
    console.log('Response:', data);
    if (response.ok) {
        // alert(movie.id ? "Movie updated!" : "Movie added!");
        if (data.code===200){
                notyf.success({
                    message: 'Login successfully',
                    duration: 9000
                })
            }
            else{
                notyf.error({
                    message: data.data,
                    duration: 9000
                })
            }
        modal.style.display = "none";
        this.reset();
        loadMovies(currentPage);
    } else {
        alert("Failed to save movie!");
    }
});

// Initial load
loadMovies();