import axios from 'axios';

const API_URL = 'http://localhost:8085/ms-tarifadiaespecial/api/tarifas-dias-especiales/';

class TarifaDiaEspecialService {
    getAllTarifasDiaEspecial() {
        return axios.get(API_URL);
    }
}

export default new TarifaDiaEspecialService();