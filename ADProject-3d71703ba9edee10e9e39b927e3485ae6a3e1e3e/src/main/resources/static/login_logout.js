document.addEventListener('DOMContentLoaded', (event) => {
    fetch('/api/user/status')
        .then(response => response.json())
        .then(data => {
            const loginLogoutItem = document.getElementById('login-logout-item');
            const userNameDisplay = document.getElementById('user-name-display'); // 获取显示用户名的元素

            if (data.isLoggedIn) {
                loginLogoutItem.textContent = 'logout';
                loginLogoutItem.href = '/user/logout';
                userNameDisplay.textContent = 'Hello, ' + data.username; // 显示用户名
            } else {
                loginLogoutItem.textContent = 'login';
                loginLogoutItem.href = '/user/login';
                userNameDisplay.textContent = ''; // 清空用户名显示
            }
        });
});
