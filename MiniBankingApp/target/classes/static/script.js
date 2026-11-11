
const token = localStorage.getItem("token");

let bankChart;
let allTransactions = [];

// Load Dashboard
async function loadDashboard() {

    if (!token) {
        window.location.href = "login.html";
        return;
    }

    document.getElementById("adminName").textContent =
        localStorage.getItem("username") || "Administrator";

    try {

        const response = await fetch("/api/admin/dashboard", {
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (response.status === 401 || response.status === 403) {
            alert("Please login with an ADMIN account.");
            logout();
            return;
        }

        if (!response.ok) {
            throw new Error("Dashboard loading failed");
        }

        const data = await response.json();

        document.getElementById("customersCount").textContent =
            data.totalCustomers;

        document.getElementById("balance").textContent =
            "₹" + Number(data.totalBankBalance).toLocaleString("en-IN");

        document.getElementById("transactionsCount").textContent =
            data.totalTransactions;

        document.getElementById("deposits").textContent =
            "₹" + Number(data.totalDeposits).toLocaleString("en-IN");

        document.getElementById("withdrawals").textContent =
            "₹" + Number(data.totalWithdrawals).toLocaleString("en-IN");

        document.getElementById("transfers").textContent =
            "₹" + Number(data.totalTransfers).toLocaleString("en-IN");

        createChart(data);

    } catch (error) {
        console.error(error);
        alert(error.message);
    }
}

// Create Chart
function createChart(data) {

    if (bankChart) {
        bankChart.destroy();
    }

    bankChart = new Chart(document.getElementById("bankChart"), {

        type: "bar",

        data: {
            labels: ["Deposits", "Withdrawals", "Transfers"],

            datasets: [{
                label: "Amount (₹)",

                data: [
                    data.totalDeposits,
                    data.totalWithdrawals,
                    data.totalTransfers
                ],

                backgroundColor: [
                    "#2563eb",
                    "#f59e0b",
                    "#10b981"
                ]
            }]
        },

        options: {
            responsive: true,
            maintainAspectRatio: false
        }
    });
}

// Logout
function logout() {

    localStorage.removeItem("token");
    localStorage.removeItem("username");

    window.location.href = "login.html";
}

// Change Dashboard Sections
function showSection(sectionId, clickedLink) {

    document.querySelectorAll(".content-section").forEach(section => {
        section.style.display = "none";
    });

    document.getElementById(sectionId).style.display = "block";

    document.querySelectorAll(".sidebar a").forEach(link => {
        link.classList.remove("active");
    });

    if (clickedLink) {
        clickedLink.classList.add("active");
    }

    if (sectionId === "dashboard") {
        loadDashboard();
    }

    if (sectionId === "customers") {
        loadCustomers();
    }

    if (sectionId === "transactions") {
        loadTransactions();
    }

    if (sectionId === "profile") {
        loadAdminProfile();
    }
}

// Load All Customers
async function loadCustomers() {

    try {

        const response = await fetch("/api/accounts/all", {
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) {
            throw new Error("Unable to load customers: " + response.status);
        }

        const customers = await response.json();

        document.getElementById("customerTable").innerHTML =
            customers.map(customer => `
                <tr>
                    <td>${customer.accountId}</td>
                    <td>${customer.username}</td>
                    <td>₹${Number(customer.balance).toLocaleString("en-IN")}</td>
                </tr>
            `).join("");

    } catch (error) {

        console.error(error);
        alert(error.message);
    }
}

// Load All Transactions (ADMIN API)
async function loadTransactions() {

    try {

        const response = await fetch("/api/admin/transactions", {
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) {
            throw new Error("Unable to load all transactions");
        }

        allTransactions = await response.json();

        displayTransactions(allTransactions);

    } catch (error) {

        console.error(error);

        document.getElementById("transactionTable").innerHTML =
            `<tr><td colspan="7">${error.message}</td></tr>`;
    }
}

// Display Transactions
function displayTransactions(transactions) {

    const table = document.getElementById("transactionTable");

    table.innerHTML = transactions.length
        ? transactions.map(t => `
            <tr>
                <td>${t.transactionId}</td>
                <td>${t.transactionType || "-"}</td>
                <td>${t.senderId ?? "-"}</td>
                <td>${t.receiverId ?? "-"}</td>
                <td>₹${Number(t.amount).toLocaleString("en-IN")}</td>
                <td>${t.transactionDate ?? "-"}</td>
                <td>${t.status}</td>
            </tr>
        `).join("")
        : `<tr><td colspan="7">No transactions found</td></tr>`;
}

// Apply Transaction Filters
function applyTransactionFilters() {

    const id = document.getElementById("searchTransactionId").value;

    const type = document.getElementById("transactionType").value;

    const from = document.getElementById("fromDate").value;

    const to = document.getElementById("toDate").value;

    if (from && to && from > to) {
        alert("From Date cannot be after To Date");
        return;
    }

    const filtered = allTransactions.filter(t => {

        const date = t.transactionDate
            ? t.transactionDate.substring(0, 10)
            : "";

        return (
            (!id || String(t.transactionId) === id) &&
            (type === "ALL" || t.transactionType === type) &&
            (!from || date >= from) &&
            (!to || date <= to)
        );
    });

    displayTransactions(filtered);
}

// Reset Transaction Filters
function resetTransactionFilters() {

    document.getElementById("searchTransactionId").value = "";

    document.getElementById("transactionType").value = "ALL";

    document.getElementById("fromDate").value = "";

    document.getElementById("toDate").value = "";

    displayTransactions(allTransactions);
}

// Initial Dashboard Load
loadDashboard();

async function loadAdminProfile() {
    try {
        const response = await fetch("/api/profile", {
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) {
            throw new Error("Unable to load admin profile");
        }

        const data = await response.json();

        document.getElementById("adminUsername").textContent =
            data.username;

        document.getElementById("adminAccountId").textContent =
            data.accountId;

        document.getElementById("adminRole").textContent =
            data.role;

    } catch (error) {
        console.error(error);
        alert(error.message);
    }
}

async function changeAdminPassword() {
    const oldPassword =
        document.getElementById("oldPassword").value;

    const newPassword =
        document.getElementById("newPassword").value;

    const confirmPassword =
        document.getElementById("confirmPassword").value;

    const message =
        document.getElementById("profileMessage");

    if (!oldPassword || !newPassword || !confirmPassword) {
        message.style.color = "red";
        message.textContent = "Please fill all fields.";
        return;
    }

    if (newPassword.length < 8) {
        message.style.color = "red";
        message.textContent =
            "New password must contain at least 8 characters.";
        return;
    }

    if (newPassword !== confirmPassword) {
        message.style.color = "red";
        message.textContent = "New passwords do not match.";
        return;
    }

    try {
        const response = await fetch("/api/profile/change-password", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },
            body: JSON.stringify({
                oldPassword: oldPassword,
                newPassword: newPassword
            })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.error || "Password update failed");
        }

        message.style.color = "green";
        message.textContent = data.message;

        document.getElementById("oldPassword").value = "";
        document.getElementById("newPassword").value = "";
        document.getElementById("confirmPassword").value = "";

    } catch (error) {
        message.style.color = "red";
        message.textContent = error.message;
    }
}
async function loadAdminProfile() {
    try {
        const response = await fetch("/api/profile", {
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (!response.ok) {
            throw new Error("Unable to load admin profile");
        }

        const data = await response.json();

        document.getElementById("adminUsername").textContent = data.username;
        document.getElementById("adminAccountId").textContent = data.accountId;
        document.getElementById("adminRole").textContent = data.role;

    } catch (error) {
        console.error(error);
        alert(error.message);
    }
}
