/* =========================================================
   GREEN CAMPUS DATABASE API CLIENT
   Added without removing the previous validation/weather stages.
   The Java API stores registration and report data in MySQL.
   ========================================================= */

const GREEN_CAMPUS_API = "http://localhost:8080/api";

async function postForm(endpoint, data) {
    const body = new URLSearchParams(data);

    const response = await fetch(`${GREEN_CAMPUS_API}/${endpoint}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
        },
        body
    });

    const result = await response.json();

    if (!response.ok || !result.success) {
        throw new Error(result.message || "The server request failed.");
    }

    return result;
}

const GreenCampusAPI = {
    register(user) {
        return postForm("register", user);
    },

    login(credentials) {
        return postForm("login", credentials);
    },

    submitReport(report) {
        return postForm("report", report);
    }
};
