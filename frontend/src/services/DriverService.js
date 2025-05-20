import axios from 'axios';

const API_URL = import.meta.env.VITE_USER_API_URL;

class DriverService {
    getAllDrivers() {
        return axios.get(API_URL);
    }
    
    createDriver(driver) {
        return axios.post(API_URL, driver);
    }
    
    getDriverById(id) {
        return axios.get(`${API_URL}/${id}`);
    }
    
    deleteDriver(id) {
        return axios.delete(`${API_URL}/${id}`);
    }
}

export default new DriverService();