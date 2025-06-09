import axios from 'axios';

const API_URL = 'http://localhost:8085/ms-tarifasconfig/api/tarifas/';

class TarifasService {
    getAllTarifas() {
        return axios.get(API_URL);
    }
}

export default new TarifasService();