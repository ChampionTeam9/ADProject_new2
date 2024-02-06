
document.addEventListener('DOMContentLoaded', (event) => {

  const username = sessionStorage.getItem("username"); // 或者其他存储用户信息的方法
  const loginLogoutItem = document.getElementById('login-logout-item');
  const userIcon = document.getElementById('navbarDropdown');

  if (username) {
    // 如果用户已登录
    loginLogoutItem.textContent = '注销';
    loginLogoutItem.href = '/logout'; // 设置为实际的注销路径
    userIcon.innerHTML += ' ' + username; // 在用户图标旁边添加用户名
  } else {
    // 如果用户未登录
    loginLogoutItem.textContent = '登录';
    loginLogoutItem.href = '/login'; // 设置为实际的登录路径
  }
});
