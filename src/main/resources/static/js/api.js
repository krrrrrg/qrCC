export const API = {
    baseURL: 'https://api.example.com',
    
    async get(endpoint) {
        const response = await fetch(`${this.baseURL}${endpoint}`);
        return response.json();
    },
    
    async post(endpoint, data) {
        const response = await fetch(`${this.baseURL}${endpoint}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });
        return response.json();
    }
}; 