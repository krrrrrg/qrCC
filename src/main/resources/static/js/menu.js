import { MenuService } from './menu-service.js';

const menuService = new MenuService();

document.addEventListener('DOMContentLoaded', function() {
    initializeMenus();
    
    if (document.getElementById('menu-list')) {
        renderMenuItems('recommended');
        document.querySelector('a[href="#recommended"]').parentElement.classList.add('active');
    }
});

// 카테고리 네비게이션 처리
const categoryLinks = document.querySelectorAll('.category-nav a');
categoryLinks.forEach(link => {
    link.addEventListener('click', (e) => {
        e.preventDefault();
        const category = link.getAttribute('href').replace('#', '');
        
        document.querySelector('.category-nav li.active')?.classList.remove('active');
        link.parentElement.classList.add('active');
        
        renderMenuItems(category);
    });
});

// 메뉴 토글 함수
window.toggleMenu = function() {
    const dropdownMenu = document.querySelector('.dropdown-menu');
    dropdownMenu.classList.toggle('active');
}

// 메뉴 외부 클릭 시 닫기
document.addEventListener('click', (e) => {
    const dropdownMenu = document.querySelector('.dropdown-menu');
    const menuButton = document.querySelector('.menu-button');
    
    if (!dropdownMenu.contains(e.target) && !menuButton.contains(e.target)) {
        dropdownMenu.classList.remove('active');
    }
});

// 장바구니 관련 코드도 추가... 