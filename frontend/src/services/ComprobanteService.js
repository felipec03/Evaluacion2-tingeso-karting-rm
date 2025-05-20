import axios from 'axios';

const API_URL = import.meta.env.VITE_COMPROBANTE_API_URL;
class ComprobanteService {
    getAllComprobantes() {
        return axios.get(`${API_URL}`);
    }
    
    getComprobanteById(id) {
        return axios.get(`${API_URL}${id}`);
    }
    
    generateComprobante(reservaId) {
        return axios.post(`${API_URL}generar/${reservaId}`);
    }
    
    downloadComprobantePdf(reservaId) {
        return axios.get(`${API_URL}download/reserva/${reservaId}`, { 
            responseType: 'blob',
            // Make sure we get the headers too
            headers: {
                'Accept': 'application/pdf',
            }
        });
    }
    
    getComprobantesByEmail(email) {
        return axios.get(`${API_URL}email/${email}`);
    }
}

export default new ComprobanteService();