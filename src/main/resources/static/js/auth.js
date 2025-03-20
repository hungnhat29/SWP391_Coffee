document.addEventListener("DOMContentLoaded", function () {
    const logoutLink = document.querySelector(".logout-link");
    const logoutMenu = document.querySelector(".logout-menu");

    // Hàm kiểm tra xem jwtToken có trong cookie không
    function checkLogin() {
        const cookies = document.cookie.split("; ");
        const jwtToken = cookies.find(row => row.startsWith("jwtToken="));

        if (!jwtToken) {
            // Ẩn nút Logout nếu không có token
            if (logoutLink) logoutMenu.style.display = "none";
        }
    }

    // Kiểm tra ngay khi trang tải
    checkLogin();

    if (logoutLink) {
        logoutLink.addEventListener("click", function (event) {
            event.preventDefault(); // Ngăn chặn chuyển trang ngay lập tức

            fetch("/auth/logout", {
                method: "POST",
                credentials: "include" // Gửi cookie trong request
            }).then(response => {
                if (response.ok) {
                    // Xóa token khỏi cookie (cách JS)
                    document.cookie = "jwtToken=; Path=/; Expires=Thu, 01 Jan 1970 00:00:00 UTC; Secure; HttpOnly";

                    // Kiểm tra lại trạng thái đăng nhập
                    checkLogin();
                    window.location.href = "/auth/login";
                }
            }).catch(error => console.error("Logout failed:", error));
        });
    }
});
