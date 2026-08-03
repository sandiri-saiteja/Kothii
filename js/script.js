/* ==========================================
   Green Campus Sustainability Analytics Portal
   script.js
========================================== */

// ------------------------------
// Registration Form Validation
// ------------------------------

const registerForm = document.getElementById("registerForm");

if (registerForm) {

    registerForm.addEventListener("submit", function (e) {

        e.preventDefault();

        const name = document.getElementById("name").value.trim();
        const id = document.getElementById("id").value.trim();
        const email = document.getElementById("email").value.trim();
        const department = document.getElementById("department").value;
        const campus = document.getElementById("campus").value;
        const password = document.getElementById("password").value;
        const confirmPassword = document.getElementById("confirmPassword").value;

        if (
            name === "" ||
            id === "" ||
            email === "" ||
            department === "" ||
            campus === "" ||
            password === "" ||
            confirmPassword === ""
        ) {
            alert("Please fill in all the fields.");
            return;
        }

        const emailPattern =
            /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailPattern.test(email)) {
            alert("Please enter a valid email address.");
            return;
        }

        if (password.length < 6) {
            alert("Password must contain at least 6 characters.");
            return;
        }

        if (password !== confirmPassword) {
            alert("Passwords do not match.");
            return;
        }

        alert("Registration Successful!");

        window.location.href = "login.html";

    });

}

// ------------------------------
// Login Validation
// ------------------------------

const loginForm = document.getElementById("loginForm");

if (loginForm) {

    loginForm.addEventListener("submit", function (e) {

        e.preventDefault();

        const email =
            document.getElementById("loginEmail").value.trim();

        const password =
            document.getElementById("loginPassword").value;

        if (email === "" || password === "") {

            alert("Please enter Email and Password.");

            return;

        }

        alert("Login Successful!");

        window.location.href = "catalog.html";

    });

}

// ------------------------------
// Catalog Search
// ------------------------------

const searchInput = document.getElementById("searchInput");

if (searchInput) {

    searchInput.addEventListener("keyup", function () {

        const searchValue =
            searchInput.value.toLowerCase();

        const cards =
            document.querySelectorAll(".card-item");

        cards.forEach(function (card) {

            const text =
                card.innerText.toLowerCase();

            if (text.includes(searchValue)) {

                card.style.display = "";

            } else {

                card.style.display = "none";

            }

        });

    });

}

// ------------------------------
// Active Navigation Link
// ------------------------------

const currentPage = window.location.pathname.split("/").pop();

const links = document.querySelectorAll(".nav-links a");

links.forEach(function (link) {

    const file = link.getAttribute("href");

    if (file === currentPage) {

        link.style.color = "#dcedc8";
        link.style.fontWeight = "700";

    }

});

// ------------------------------
// Smooth Scroll
// ------------------------------

document.querySelectorAll('a[href^="#"]').forEach(function(anchor){

    anchor.addEventListener("click", function(e){

        e.preventDefault();

        const target =
            document.querySelector(this.getAttribute("href"));

        if(target){

            target.scrollIntoView({

                behavior:"smooth"

            });

        }

    });

});

// ------------------------------
// Welcome Message
// ------------------------------

window.addEventListener("load", function(){

    console.log("Green Campus Sustainability Analytics Portal Loaded Successfully.");

});