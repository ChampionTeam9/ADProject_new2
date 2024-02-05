document.addEventListener('DOMContentLoaded', function () {
    var dropdownSubmenu = document.querySelectorAll('.dropdown-submenu');
    dropdownSubmenu.forEach(function (submenu) {
        submenu.addEventListener('mouseover', function (event) {
            var submenuDropdown = this.querySelector('.dropdown-menu');
            submenuDropdown.style.display = 'block';
        });
        submenu.addEventListener('mouseout', function (event) {
            var submenuDropdown = this.querySelector('.dropdown-menu');
            submenuDropdown.style.display = 'none';
        });
    });
});
