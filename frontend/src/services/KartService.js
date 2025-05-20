import axios from 'axios';

const API_URL = import.meta.env.VITE_KART_API_URL;

class KartService {
    getAllKarts() {
        return axios.get(API_URL);
    }
    
    createKart(kart) {
        return axios.post(API_URL, kart);
    }
    
    getKartById(id) {
        return axios.get(`${API_URL}/${id}`);
    }
    
    deleteKart(id) {
        return axios.delete(`${API_URL}/${id}`);
    }
}

export default new KartService();