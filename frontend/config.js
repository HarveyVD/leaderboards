const config = {
    get apiUrl() {
        const url = window.API_URL;
        if (!url || url.includes('${') || url.includes('%%')) {
            return 'http://localhost:8080/api/v1';
        }
        return url;
    },
    
    get wsUrl() {
        try {
            const baseUrl = this.apiUrl.startsWith('http') ? this.apiUrl : window.location.origin + this.apiUrl;
            const url = new URL(baseUrl);
            const protocol = url.protocol === 'https:' ? 'wss:' : 'ws:';
            return `${protocol}//${url.host}/api/v1/ws`;
        } catch (error) {
            console.error('Error parsing URL:', error);
            return 'ws://localhost:8080';
        }
    }
};

Object.freeze(config); 
