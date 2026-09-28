/* =========================================================
   GREEN CAMPUS DATABASE API CLIENT
   All Java backend requests go through Tomcat on port 8081.
   ========================================================= */

const GREEN_CAMPUS_API =
    "http://localhost:8081/green-campus/api";


async function postForm(endpoint, data) {
    const body = new URLSearchParams(data);

    const response = await fetch(`${GREEN_CAMPUS_API}/${endpoint}`, {
        method: "POST",
        headers: {
            "Content-Type":
                "application/x-www-form-urlencoded;charset=UTF-8"
        },
        body: body
    });

    let result;

    try {
        result = await response.json();
    } catch (error) {
        throw new Error(
            `Server returned an invalid response (HTTP ${response.status}).`
        );
    }

    if (!response.ok || !result.success) {
        throw new Error(
            result.message || "The server request failed."
        );
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