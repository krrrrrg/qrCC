import { API } from '../common/api.js';

export class MenuService {
    async getMenuList() {
        return API.get('/menus');
    }
    
    async getMenuDetail(id) {
        return API.get(`/menus/${id}`);
    }
} 