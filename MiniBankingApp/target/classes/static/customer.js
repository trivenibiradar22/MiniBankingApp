
let allTransactions = [];

const token = localStorage.getItem("token");

if (!token || localStorage.getItem("role") !== "USER") {
    window.location.href = "login.html";
}

const authHeaders = {
    "Authorization": "Bearer " + token
};

document.getElementById("username").textContent =
    localStorage.getItem("username") || "";

document.getElementById("accountId").textContent =
    localStorage.getItem("accountId") || "";

async function loadBalance() {
    try {
        const response = await fetch("/api/accounts/balance", {
            headers: authHeaders
        });

        if (!response.ok) {
            throw new Error("Unable to load account balance");
        }

        const data = await response.json();

        document.getElementById("balance").textContent =
            "₹" + Number(data.balance).toLocaleString("en-IN", {
                minimumFractionDigits: 2
            });

        document.getElementById("accountId").textContent =
            data.accountId;

    } catch (error) {
        showMessage(error.message, false);
    }
}

async function performTransaction(url, body) {
    try {
        const response = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                ...authHeaders
            },
            body: JSON.stringify(body)
        });

        const data = await response.json().catch(() => ({}));

        if (!response.ok) {
            throw new Error(data.error || "Transaction failed");
        }

        showMessage(data.message || "Transaction successful!", true);

        await loadBalance();
        await loadTransactionHistory();
        await generateReceipt();

    } catch (error) {
        showMessage(error.message, false);
    }
}

function deposit() {
    const amount = Number(
        document.getElementById("depositAmount").value
    );

    if (!Number.isFinite(amount) || amount <= 0) {
        showMessage("Enter a valid deposit amount", false);
        return;
    }

    performTransaction("/api/accounts/deposit", {
        accountId: null,
        amount: amount
    });

    document.getElementById("depositAmount").value = "";
}

function withdraw() {
    const amount = Number(
        document.getElementById("withdrawAmount").value
    );

    if (!Number.isFinite(amount) || amount <= 0) {
        showMessage("Enter a valid withdrawal amount", false);
        return;
    }

    performTransaction("/api/accounts/withdraw", {
        accountId: null,
        amount: amount
    });

    document.getElementById("withdrawAmount").value = "";
}

function transfer() {
    const receiverId = Number(
        document.getElementById("receiverId").value
    );

    const amount = Number(
        document.getElementById("transferAmount").value
    );

    if (
        !Number.isInteger(receiverId) ||
        receiverId <= 0 ||
        !Number.isFinite(amount) ||
        amount <= 0
    ) {
        showMessage("Enter a valid receiver ID and amount", false);
        return;
    }

    performTransaction("/api/transactions/transfer", {
        senderId: null,
        receiverId: receiverId,
        amount: amount
    });

    document.getElementById("receiverId").value = "";
    document.getElementById("transferAmount").value = "";
}

async function loadTransactionHistory() {
    try {
        const response = await fetch("/api/transactions/history", {
            headers: authHeaders
        });

        if (!response.ok) {
            throw new Error("Unable to load transaction history");
        }

        allTransactions = await response.json();

        displayTransactions(allTransactions);

    } catch (error) {
        console.error(error);
        showMessage(error.message, false);
    }
}

function displayTransactions(transactions) {
    const table = document.getElementById("transactionTable");

    if (!transactions || transactions.length === 0) {
        table.innerHTML =
            '<tr><td colspan="5">No transactions found</td></tr>';
        return;
    }

    table.innerHTML = transactions.map(t => `
        <tr>
            <td>${t.transactionId}</td>
            <td>${t.transactionType}</td>
            <td>₹${Number(t.amount).toLocaleString("en-IN")}</td>
            <td>${t.transactionDate ?? "-"}</td>
            <td>${t.status}</td>
        </tr>
    `).join("");
}

function filterTransactions() {
    const id = document.getElementById("searchTransactionId").value.trim();
    const type = document.getElementById("transactionType").value;
    const from = document.getElementById("fromDate").value;
    const to = document.getElementById("toDate").value;

    if (from && to && from > to) {
        showMessage("From date cannot be after To date", false);
        return;
    }

    const filtered = allTransactions.filter(t => {
        const matchesId = !id ||
            String(t.transactionId) === id;

        const matchesType = type === "ALL" ||
            t.transactionType === type;

        const transactionDate = t.transactionDate
            ? t.transactionDate.substring(0, 10)
            : "";

        const matchesFrom = !from ||
            (transactionDate && transactionDate >= from);

        const matchesTo = !to ||
            (transactionDate && transactionDate <= to);

        return matchesId && matchesType &&
            matchesFrom && matchesTo;
    });

    displayTransactions(filtered);
}

function resetTransactionFilters() {
    document.getElementById("searchTransactionId").value = "";
    document.getElementById("transactionType").value = "ALL";
    document.getElementById("fromDate").value = "";
    document.getElementById("toDate").value = "";

    displayTransactions(allTransactions);
}

function showMessage(message, success) {
    const element = document.getElementById("message");

    element.textContent = message;
    element.style.color = success ? "green" : "red";
}

function logout() {
    localStorage.clear();
    window.location.href = "login.html";
}

async function generateReceipt() {
    try {
        const response = await fetch("/api/transactions/history", {
            headers: authHeaders
        });

        if (!response.ok) {
            throw new Error("Unable to generate receipt");
        }

        const transactions = await response.json();

        if (!transactions || transactions.length === 0) {
            throw new Error("Transaction record not found");
        }

        // Backend returns transactions newest first.
        const t = transactions[0];

        document.getElementById("receiptId").textContent =
            t.transactionId;

        document.getElementById("receiptName").textContent =
            localStorage.getItem("username");

        document.getElementById("receiptType").textContent =
            t.transactionType;

        document.getElementById("receiptSender").textContent =
            t.senderId ?? "-";

        document.getElementById("receiptReceiver").textContent =
            t.receiverId ?? "-";

        document.getElementById("receiptAmount").textContent =
            "₹" + Number(t.amount).toLocaleString("en-IN");

        document.getElementById("receiptDate").textContent =
            t.transactionDate ?? "-";

        document.getElementById("receiptStatus").textContent =
            t.status;

        document.getElementById("receiptSection").style.display = "block";

    } catch (error) {
        showMessage(error.message, false);
    }
}

function printReceipt() {
    const receipt = document.getElementById("receipt");

    const printWindow = window.open("", "_blank");

    if (!printWindow) {
        showMessage("Allow pop-ups to print the receipt", false);
        return;
    }

    printWindow.document.write(`
        <html>
        <head>
            <title>MiniBank Transaction Receipt</title>
            <style>
                body {
                    font-family: Arial;
                    padding: 30px;
                }
            </style>
        </head>
        <body>
            ${receipt.outerHTML}
            <script>
                window.onload = function() {
                    window.print();
                };
            <\/script>
        </body>
        </html>
    `);

    printWindow.document.close();
}

async function downloadStatement() {
    try {
        const response = await fetch("/api/transactions/history", {
            headers: authHeaders
        });

        if (!response.ok) {
            throw new Error("Unable to fetch account statement");
        }

        const transactions = await response.json();

        const rows = transactions.map(t => `
            <tr>
                <td>${t.transactionId}</td>
                <td>${t.transactionType}</td>
                <td>${t.senderId ?? "-"}</td>
                <td>${t.receiverId ?? "-"}</td>
                <td>₹${Number(t.amount).toLocaleString("en-IN")}</td>
                <td>${t.transactionDate ?? "-"}</td>
                <td>${t.status}</td>
            </tr>
        `).join("");

        const statementWindow = window.open("", "_blank");

        if (!statementWindow) {
            throw new Error("Please allow pop-ups to download");
        }

        statementWindow.document.write(`
            <html>
            <head>
                <title>MiniBank Account Statement</title>
                <style>
                    body {
                        font-family: Arial;
                        padding: 25px;
                    }

                    h1, h3 {
                        text-align: center;
                        color: #1d4ed8;
                    }

                    table {
                        width: 100%;
                        border-collapse: collapse;
                    }

                    th, td {
                        border: 1px solid #ccc;
                        padding: 10px;
                    }

                    th {
                        background: #dbeafe;
                    }
                </style>
            </head>
            <body>
                <h1>MiniBank</h1>
                <h3>Account Statement</h3>

                <p>Customer: ${localStorage.getItem("username")}</p>
                <p>Account ID: ${localStorage.getItem("accountId")}</p>
                <p>Generated: ${new Date().toLocaleString()}</p>

                <table>
                    <tr>
                        <th>ID</th>
                        <th>Type</th>
                        <th>Sender</th>
                        <th>Receiver</th>
                        <th>Amount</th>
                        <th>Date</th>
                        <th>Status</th>
                    </tr>
                    ${rows}
                </table>

                <script>
                    window.onload = function() {
                        window.print();
                    };
                <\/script>
            </body>
            </html>
        `);

        statementWindow.document.close();

    } catch (error) {
        showMessage(error.message, false);
    }
}

async function loadProfile() {
    try {
        const response = await fetch("/api/profile", {
            headers: authHeaders
        });

        if (!response.ok) {
            throw new Error("Unable to load profile");
        }

        const data = await response.json();

        document.getElementById("profileId").textContent =
            data.accountId;

        document.getElementById("profileUsername").textContent =
            data.username;

        document.getElementById("profileRole").textContent =
            data.role;

    } catch (error) {
        document.getElementById("profileMessage").textContent =
            error.message;
    }
}

async function changePassword() {
    const oldPassword =
        document.getElementById("oldPassword").value;

    const newPassword =
        document.getElementById("newPassword").value;

    if (!oldPassword || newPassword.length < 8) {
        alert("Enter your current password and a new password of at least 8 characters.");
        return;
    }

    try {
        const response = await fetch("/api/profile/change-password", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                ...authHeaders
            },
            body: JSON.stringify({
                oldPassword: oldPassword,
                newPassword: newPassword
            })
        });

        const data = await response.json().catch(() => ({}));

        if (!response.ok) {
            throw new Error(data.error || "Password change failed");
        }

        document.getElementById("profileMessage").textContent =
            data.message;

        document.getElementById("profileMessage").style.color = "green";

        document.getElementById("oldPassword").value = "";
        document.getElementById("newPassword").value = "";

    } catch (error) {
        document.getElementById("profileMessage").textContent =
            error.message;

        document.getElementById("profileMessage").style.color = "red";
    }
}

loadBalance();
loadTransactionHistory();
loadProfile();
