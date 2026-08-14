/* =========================================================
   GREEN CAMPUS / RENEWABLE ENERGY PORTAL
   Comprehensive Client-Side Validation
   ========================================================= */

document.addEventListener("DOMContentLoaded", function () {

    /* =====================================================
       COMMON VALIDATION FUNCTIONS
       ===================================================== */

    function showError(input, message) {

        input.classList.add("is-invalid");
        input.classList.remove("is-valid");

        let error = input.parentElement.querySelector(".invalid-feedback");

        if (!error) {
            error = document.createElement("div");
            error.className = "invalid-feedback";
            input.parentElement.appendChild(error);
        }

        error.textContent = message;
    }


    function showSuccess(input) {

        input.classList.remove("is-invalid");
        input.classList.add("is-valid");

        const error = input.parentElement.querySelector(".invalid-feedback");

        if (error) {
            error.textContent = "";
        }
    }


    function clearValidation(input) {

        input.classList.remove("is-invalid");
        input.classList.remove("is-valid");

        const error = input.parentElement.querySelector(".invalid-feedback");

        if (error) {
            error.textContent = "";
        }
    }


    function isValidEmail(email) {

        const emailPattern =
            /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;

        return emailPattern.test(email);
    }


    function isStrongPassword(password) {

        /*
           Minimum:
           8 characters
           1 uppercase
           1 lowercase
           1 number
           1 special character
        */

        const passwordPattern =
            /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

        return passwordPattern.test(password);
    }


    function isValidName(name) {

        const namePattern =
            /^[A-Za-zÀ-ÿ]+(?:[ '-][A-Za-zÀ-ÿ]+)*$/;

        return namePattern.test(name);
    }


    function isValidID(id) {

        /*
           Allows letters, numbers, hyphen and underscore.
        */

        const idPattern =
            /^[A-Za-z0-9_-]{3,20}$/;

        return idPattern.test(id);
    }


    function isValidPhone(phone) {

        const phonePattern =
            /^[6-9]\d{9}$/;

        return phonePattern.test(phone);
    }


    function isPositiveNumber(value) {

        return !isNaN(value) && Number(value) > 0;
    }


    /* =====================================================
       REGISTRATION FORM
       ===================================================== */

    const registerForm =
        document.getElementById("registerForm");


    if (registerForm) {

        registerForm.addEventListener("submit", function (event) {

            event.preventDefault();

            let isFormValid = true;


            /* Full Name */

            const name =
                document.getElementById("name");

            if (!name.value.trim()) {

                showError(
                    name,
                    "Full name is required."
                );

                isFormValid = false;

            } else if (!isValidName(name.value.trim())) {

                showError(
                    name,
                    "Enter a valid name using letters only."
                );

                isFormValid = false;

            } else {

                showSuccess(name);
            }


            /* Student / Employee ID */

            const userID =
                document.getElementById("id");


            if (userID) {

                if (!userID.value.trim()) {

                    showError(
                        userID,
                        "Student / Employee ID is required."
                    );

                    isFormValid = false;

                } else if (!isValidID(userID.value.trim())) {

                    showError(
                        userID,
                        "ID must contain 3-20 letters, numbers, - or _."
                    );

                    isFormValid = false;

                } else {

                    showSuccess(userID);
                }
            }


            /* Email */

            const email =
                document.getElementById("email");


            if (!email.value.trim()) {

                showError(
                    email,
                    "Email address is required."
                );

                isFormValid = false;

            } else if (!isValidEmail(email.value.trim())) {

                showError(
                    email,
                    "Enter a valid email address."
                );

                isFormValid = false;

            } else {

                showSuccess(email);
            }


            /* Department */

            const department =
                document.getElementById("department");


            if (department) {

                if (!department.value) {

                    showError(
                        department,
                        "Please select a department."
                    );

                    isFormValid = false;

                } else {

                    showSuccess(department);
                }
            }


            /* Campus */

            const campus =
                document.getElementById("campus");


            if (campus) {

                if (!campus.value) {

                    showError(
                        campus,
                        "Please select a campus."
                    );

                    isFormValid = false;

                } else {

                    showSuccess(campus);
                }
            }


            /* Password */

            const password =
                document.getElementById("password");


            if (!password.value) {

                showError(
                    password,
                    "Password is required."
                );

                isFormValid = false;

            } else if (!isStrongPassword(password.value)) {

                showError(
                    password,
                    "Password must contain at least 8 characters, uppercase, lowercase, number and special character."
                );

                isFormValid = false;

            } else {

                showSuccess(password);
            }


            /* Confirm Password */

            const confirmPassword =
                document.getElementById("confirmPassword");


            if (!confirmPassword.value) {

                showError(
                    confirmPassword,
                    "Please confirm your password."
                );

                isFormValid = false;

            } else if (
                confirmPassword.value !== password.value
            ) {

                showError(
                    confirmPassword,
                    "Passwords do not match."
                );

                isFormValid = false;

            } else {

                showSuccess(confirmPassword);
            }


            /* Final Result */

            if (isFormValid) {

                /*
                   Save registration data locally for this
                   client-side demonstration.
                */

                const user = {

                    name: name.value.trim(),

                    id: userID
                        ? userID.value.trim()
                        : "",

                    email: email.value.trim(),

                    department: department
                        ? department.value
                        : "",

                    campus: campus
                        ? campus.value
                        : "",

                    password: password.value

                };


                localStorage.setItem(
                    "greenCampusUser",
                    JSON.stringify(user)
                );


                alert(
                    "Registration successful!"
                );


                window.location.href =
                    "login.html";
            }

        });


        /* Clear errors while typing */

        registerForm
            .querySelectorAll("input, select")
            .forEach(function (input) {

                input.addEventListener(
                    "input",
                    function () {

                        clearValidation(input);

                    }
                );

                input.addEventListener(
                    "change",
                    function () {

                        clearValidation(input);

                    }
                );

            });

    }


    /* =====================================================
       LOGIN FORM
       ===================================================== */

    const loginForm =
        document.getElementById("loginForm");


    if (loginForm) {

        loginForm.addEventListener("submit", function (event) {

            event.preventDefault();

            let isFormValid = true;


            const loginEmail =
                document.getElementById("loginEmail");

            const loginPassword =
                document.getElementById("loginPassword");


            /* Email */

            if (!loginEmail.value.trim()) {

                showError(
                    loginEmail,
                    "Email address is required."
                );

                isFormValid = false;

            } else if (
                !isValidEmail(loginEmail.value.trim())
            ) {

                showError(
                    loginEmail,
                    "Enter a valid email address."
                );

                isFormValid = false;

            } else {

                showSuccess(loginEmail);
            }


            /* Password */

            if (!loginPassword.value) {

                showError(
                    loginPassword,
                    "Password is required."
                );

                isFormValid = false;

            } else {

                showSuccess(loginPassword);
            }


            if (!isFormValid) {
                return;
            }


            /* Check registered user */

            const storedUser =
                localStorage.getItem("greenCampusUser");


            if (!storedUser) {

                showError(
                    loginEmail,
                    "No registered account found. Please register first."
                );

                return;
            }


            const user =
                JSON.parse(storedUser);


            if (
                loginEmail.value.trim().toLowerCase()
                !== user.email.toLowerCase()
            ) {

                showError(
                    loginEmail,
                    "Email does not match the registered account."
                );

                return;
            }


            if (
                loginPassword.value
                !== user.password
            ) {

                showError(
                    loginPassword,
                    "Incorrect password."
                );

                return;
            }


            /* Login successful */

            localStorage.setItem(
                "loggedIn",
                "true"
            );


            alert(
                "Login successful!"
            );


            window.location.href =
                "catalog.html";

        });


        loginForm
            .querySelectorAll("input")
            .forEach(function (input) {

                input.addEventListener(
                    "input",
                    function () {

                        clearValidation(input);

                    }
                );

            });

    }


    /* =====================================================
       CATALOG SEARCH VALIDATION
       ===================================================== */

    const searchInput =
        document.getElementById("searchInput");


    if (searchInput) {

        const cards =
            document.querySelectorAll(".card-item");


        searchInput.addEventListener(
            "input",
            function () {

                const searchValue =
                    searchInput.value
                        .trim()
                        .toLowerCase();


                /* Prevent extremely long search input */

                if (searchValue.length > 50) {

                    searchInput.value =
                        searchInput.value.substring(
                            0,
                            50
                        );

                    return;
                }


                cards.forEach(function (card) {

                    const text =
                        card.innerText.toLowerCase();


                    if (
                        text.includes(searchValue)
                    ) {

                        card.style.display = "";

                    } else {

                        card.style.display = "none";

                    }

                });

            }
        );

    }


    /* =====================================================
       USAGE FORM
       EXPERIMENT 1 - RENEWABLE ENERGY
       ===================================================== */

    const usageForm =
        document.getElementById("usageForm");


    if (usageForm) {

        usageForm.addEventListener(
            "submit",
            function (event) {

                event.preventDefault();

                let isFormValid = true;


                const resource =
                    document.getElementById("resource");


                const usage =
                    document.getElementById("usage");


                const date =
                    document.getElementById("usageDate");


                /* Resource */

                if (resource && !resource.value) {

                    showError(
                        resource,
                        "Please select an energy resource."
                    );

                    isFormValid = false;

                } else if (resource) {

                    showSuccess(resource);

                }


                /* Energy Usage */

                if (usage) {

                    if (!usage.value.trim()) {

                        showError(
                            usage,
                            "Energy usage is required."
                        );

                        isFormValid = false;

                    } else if (
                        !isPositiveNumber(usage.value)
                    ) {

                        showError(
                            usage,
                            "Enter a positive numerical value."
                        );

                        isFormValid = false;

                    } else {

                        showSuccess(usage);

                    }

                }


                /* Date */

                if (date) {

                    if (!date.value) {

                        showError(
                            date,
                            "Please select a date."
                        );

                        isFormValid = false;

                    } else {

                        const selectedDate =
                            new Date(date.value);

                        const today =
                            new Date();

                        today.setHours(
                            23, 59, 59, 999
                        );


                        if (
                            selectedDate > today
                        ) {

                            showError(
                                date,
                                "Future dates are not allowed."
                            );

                            isFormValid = false;

                        } else {

                            showSuccess(date);

                        }

                    }

                }


                if (isFormValid) {

                    alert(
                        "Energy usage submitted successfully!"
                    );

                    usageForm.reset();

                }

            }
        );

    }


    /* =====================================================
       REPORT FORM
       EXPERIMENT 2
       ===================================================== */

    const reportForm =
        document.getElementById("reportForm");


    if (reportForm) {

        reportForm.addEventListener(
            "submit",
            function (event) {

                event.preventDefault();

                let isFormValid = true;


                const metric =
                    document.getElementById("metric");


                const value =
                    document.getElementById("metricValue");


                const reportDate =
                    document.getElementById("reportDate");


                /* Metric */

                if (metric && !metric.value) {

                    showError(
                        metric,
                        "Please select a sustainability metric."
                    );

                    isFormValid = false;

                } else if (metric) {

                    showSuccess(metric);

                }


                /* Metric Value */

                if (value) {

                    if (!value.value.trim()) {

                        showError(
                            value,
                            "Metric value is required."
                        );

                        isFormValid = false;

                    } else if (
                        !isPositiveNumber(value.value)
                    ) {

                        showError(
                            value,
                            "Enter a valid positive number."
                        );

                        isFormValid = false;

                    } else {

                        showSuccess(value);

                    }

                }


                /* Report Date */

                if (reportDate) {

                    if (!reportDate.value) {

                        showError(
                            reportDate,
                            "Please select a report date."
                        );

                        isFormValid = false;

                    } else {

                        const selectedDate =
                            new Date(reportDate.value);

                        const today =
                            new Date();

                        today.setHours(
                            23, 59, 59, 999
                        );


                        if (
                            selectedDate > today
                        ) {

                            showError(
                                reportDate,
                                "Report date cannot be in the future."
                            );

                            isFormValid = false;

                        } else {

                            showSuccess(reportDate);

                        }

                    }

                }

                /* Description */

const reportDescription =
    document.getElementById("reportDescription");

if (reportDescription) {

    if (!reportDescription.value.trim()) {

        showError(
            reportDescription,
            "Please enter a description."
        );

        isFormValid = false;

    } else if (
        reportDescription.value.trim().length < 10
    ) {

        showError(
            reportDescription,
            "Description must contain at least 10 characters."
        );

        isFormValid = false;

    } else {

        showSuccess(reportDescription);

    }
}


/* Confirmation */

const reportAgreement =
    document.getElementById("reportAgreement");

if (reportAgreement) {

    if (!reportAgreement.checked) {

        showError(
            reportAgreement,
            "You must confirm that the information is accurate."
        );

        isFormValid = false;

    } else {

        showSuccess(reportAgreement);

    }

}


                if (isFormValid) {

                    alert(
                        "Report submitted successfully!"
                    );

                    reportForm.reset();

                }

            }
        );

    }


    /* =====================================================
       PREVENT INVALID CHARACTERS IN NUMBER FIELDS
       ===================================================== */

    document
        .querySelectorAll('input[type="number"]')
        .forEach(function (input) {

            input.addEventListener(
                "input",
                function () {

                    if (this.value < 0) {
                        this.value = "";
                    }

                }
            );

        });


    /* =====================================================
       PREVENT WHITESPACE-ONLY TEXT INPUT
       ===================================================== */

    document
        .querySelectorAll('input[type="text"], textarea')
        .forEach(function (input) {

            input.addEventListener(
                "blur",
                function () {

                    if (
                        this.value.trim() === ""
                        &&
                        this.value !== ""
                    ) {

                        this.value = "";

                    }

                }
            );

        });

    /* =====================================================
   SHOW / HIDE PASSWORD
===================================================== */

/* Registration Password */

const showPassword =
    document.getElementById("showPassword");

const password =
    document.getElementById("password");

if (showPassword && password) {

    showPassword.addEventListener("change", function () {

        if (this.checked) {

            password.type = "text";

        } else {

            password.type = "password";

        }

    });

}


/* Registration Confirm Password */

const showConfirmPassword =
    document.getElementById("showConfirmPassword");

const confirmPassword =
    document.getElementById("confirmPassword");

if (showConfirmPassword && confirmPassword) {

    showConfirmPassword.addEventListener("change", function () {

        if (this.checked) {

            confirmPassword.type = "text";

        } else {

            confirmPassword.type = "password";

        }

    });

}


/* Login Password */

const showLoginPassword =
    document.getElementById("showLoginPassword");

const loginPassword =
    document.getElementById("loginPassword");

if (showLoginPassword && loginPassword) {

    showLoginPassword.addEventListener("change", function () {

        if (this.checked) {

            loginPassword.type = "text";

        } else {

            loginPassword.type = "password";

        }

    });

}

});

/* =====================================================
   SOCIAL LOGIN CUSTOM POPUP
===================================================== */

function socialLogin() {

    const popup =
        document.getElementById("socialPopup");

    if (popup) {

        popup.style.display = "flex";

    }

}


/* Close Popup */

function closeSocialPopup() {

    const popup =
        document.getElementById("socialPopup");

    if (popup) {

        popup.style.display = "none";

    }

}