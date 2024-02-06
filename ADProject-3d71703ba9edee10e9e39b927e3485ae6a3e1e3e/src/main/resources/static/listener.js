document.addEventListener('DOMContentLoaded', function () {
    var dropdowns = document.querySelectorAll('.nav-item.dropdown'); // 选择所有顶级下拉菜单项
    dropdowns.forEach(function (dropdown) {
        dropdown.addEventListener('mouseover', function () {
            var dropdownMenu = this.querySelector('.dropdown-menu');
            dropdownMenu.style.display = 'block'; // 显示下拉菜单
        });
        dropdown.addEventListener('mouseout', function () {
            var dropdownMenu = this.querySelector('.dropdown-menu');
            dropdownMenu.style.display = 'none'; // 隐藏下拉菜单
        });
    });
});
