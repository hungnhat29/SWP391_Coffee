document.addEventListener("DOMContentLoaded", function () {
    const logoutMenu = document.querySelector(".logout-menu");

    fetch("/auth/check-login", {
        method: "GET",
        credentials: "include"
    })
        .then(response => response.json())
        .then(isLoggedIn => {
            if (isLoggedIn) {
                logoutMenu.style.display = "block";
            } else {
                logoutMenu.style.display = "none";
            }
        })
        .catch(error => console.error("Error checking login:", error));
});
