function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

document.addEventListener('DOMContentLoaded', function() {
    const API_URL = "http://localhost:8080/v1/admin/users";
    const TOKEN = getCookie("token");

    const tableBody = document.querySelector('.subscribers-table tbody');
    const editModal = document.getElementById('editModal');
    const closeModalBtn = document.querySelector('.close-btn');
    const cancelBtn = document.querySelector('.btn-cancel');
    const saveBtn = document.querySelector('.btn-save');
    const allSubscribers = document.getElementById('all-subscribers');

    let currentUserId = null; // To track which user is being edited

    // === Load Users from API ===
    async function loadUsers() {
        try {
            const response = await fetch(API_URL, {
                headers: { "Authorization": "Bearer " + TOKEN }
            });
            const result = await response.json();
            const users = result.data.content;

            tableBody.innerHTML = "";
            allSubscribers.innerText = `All Subscribers (${result.data.totalElements})`;
            users.forEach(user => {
                const plan = user.payments.length > 0 ? user.payments[0].plan : "None";
                const status = user.payments.length > 0 ? user.payments[0].status : "INACTIVE";

                const row = `
                    <tr>
                        <td>
                            <div class="subscriber-info">
                                <img src="https://avatar.iran.liara.run/public/29" alt="User" class="subscriber-avatar">
                                <div>
                                    <div>${user.fullName}</div>
                                    <small>${user.email}</small>
                                </div>
                            </div>
                        </td>
                        <td>${user.joinDate}</td>
                        <td><span class="plan plan-${plan.toLowerCase()}">${plan}</span></td>
                        <td>${user.role}</td>
                        <td><span class="status status-${status.toLowerCase()}">${status}</span></td>
                        <td class="action-cell">
                            <button class="action-btn edit-btn" 
                                    data-id="${user.id}" 
                                    data-name="${user.fullName}" 
                                    data-email="${user.email}" 
                                    data-plan="${plan}" 
                                    data-status="${status}"
                                    data-role="${user.role}">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="action-btn delete-btn" data-id="${user.id}">
                                <i class="fas fa-trash"></i>
                            </button>
                        </td>
                    </tr>
                `;
                tableBody.insertAdjacentHTML('beforeend', row);
            });

            bindEditButtons();
        } catch (error) {
            console.error("Error loading users:", error);
            tableBody.innerHTML = `<tr><td colspan="7">Failed to load users</td></tr>`;
        }
    }

    // === Edit Modal Logic ===
    function bindEditButtons() {
        document.querySelectorAll('.edit-btn').forEach(button => {
            button.addEventListener('click', function() {
                currentUserId = this.dataset.id;
                document.getElementById('subscriberName').value = this.dataset.name;
                document.getElementById('subscriberEmail').value = this.dataset.email;
                document.getElementById('subscriberPlan').value = this.dataset.plan.toLowerCase();
                document.getElementById('subscriberStatus').value = this.dataset.status.toLowerCase();
                document.getElementById('subscriberRole').value = this.dataset.role.toLowerCase();
                openModal();
            });
        });
    }

    function openModal() {
        editModal.classList.add('active');
        document.body.style.overflow = 'hidden';
    }

    function closeModal() {
        editModal.classList.remove('active');
        document.body.style.overflow = '';
    }

    closeModalBtn.addEventListener('click', closeModal);
    cancelBtn.addEventListener('click', closeModal);
    editModal.addEventListener('click', e => { if (e.target === editModal) closeModal(); });
    document.addEventListener('keydown', e => { if (e.key === 'Escape' && editModal.classList.contains('active')) closeModal(); });

    // === Save changes to backend ===
    saveBtn.addEventListener('click', async function() {
        if (!currentUserId) return;

        const newRole = document.getElementById('subscriberRole').value.toUpperCase();

        try {
            const response = await fetch(`${API_URL}/${currentUserId}/role?role=${newRole}`, {
                method: 'PUT',
                headers: { "Authorization": "Bearer " + TOKEN }
            });

            if (!response.ok) throw new Error("Failed to update role");

            alert('Subscriber role updated successfully!');
            closeModal();
            loadUsers(); // Refresh table
        } catch (error) {
            console.error(error);
            alert('Error updating subscriber.');
        }
    });

    // === Navigation Highlight ===
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
        link.addEventListener('click', function() {
            navLinks.forEach(l => l.classList.remove('active'));
            this.classList.add('active');
        });
    });

    // === Pagination Highlight ===
    const paginationButtons = document.querySelectorAll('.pagination-btn');
    paginationButtons.forEach(button => {
        button.addEventListener('click', function() {
            if (!this.querySelector('i')) {
                paginationButtons.forEach(btn => btn.classList.remove('active'));
                this.classList.add('active');
            }
        });
    });

    loadUsers();
});

function exportAsCsv() {
    let csvContent = "data:text/csv;charset=utf-8,";
    csvContent += "Name,Email,Plan,Role,Status\n";

    document.querySelectorAll('.subscribers-table tbody tr').forEach(row => {
        const cols = row.querySelectorAll('td');
        const name = cols[0].innerText.replace(/\n/g, ' ').trim();
        const email = cols[0].querySelector('small').innerText.trim();
        const plan = cols[2].innerText.trim();
        const role = cols[3].innerText.trim();
        const status = cols[4].innerText.trim();
        csvContent += `${name},${email},${plan},${role},${status}\n`;
    });

    const encodedUri = encodeURI(csvContent);
    const link = document.createElement("a");
    link.setAttribute("href", encodedUri);
    link.setAttribute("download", "subscribers.csv");
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}