const loginButtons = [
    document.getElementById("loginBtn"),
    document.getElementById("heroLoginBtn"),
    document.getElementById("cardLoginBtn"),
    document.getElementById("ctaLoginBtn")
];

loginButtons.forEach(button => {
    if (button) {
        button.addEventListener("click", () => {
            window.location.href = "/login";
        });
    }
});