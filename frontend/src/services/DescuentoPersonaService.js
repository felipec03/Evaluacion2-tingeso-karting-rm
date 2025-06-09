import axios from 'axios';

const API_URL = 'http://localhost:8085/ms-descuentoporpersona/api/descuento-persona/';

class DescuentoPersonaService {
    getAllDescuentosPersona() {
        return axios.get(API_URL);
    }
}

export default new DescuentoPersonaService();