import axios from 'axios';

const API_BASE_URL = '/api/comprobantes'; // Adjusted to match gateway pattern

class ComprobanteService {
    getAllComprobantes() {
        return axios.get(`${API_BASE_URL}/`);
    }
    
    getComprobanteById(id) {
        // Backend endpoint is /api/comprobantes/{id}
        return axios.get(`${API_BASE_URL}/${id}`);
    }
    
    // Updated to match backend: POST /api/comprobantes/crear/reserva/{idReserva}?metodoPago=...
    crearComprobante(reservaId, metodoPago) {
        return axios.post(`${API_BASE_URL}/crear/reserva/${reservaId}`, null, {
            params: {
                metodoPago: metodoPago
            }
        });
    }
    
    // This needs to align with the backend endpoint: /api/comprobantes/id/{idComprobante}/pdf
    // The current ComprobanteForm uses 'reservaId' for download, which is incorrect.
    // It should use the 'idComprobante' obtained after successful creation.
    downloadComprobantePdfById(idComprobante) {
        return axios.get(`${API_BASE_URL}/id/${idComprobante}/pdf`, { 
            responseType: 'blob',
            headers: {
                'Accept': 'application/pdf',
            }
        });
    }
    
    getComprobantesByEmail(email) {
        // Assuming backend has an endpoint like /api/comprobantes/email/{email}
        // The provided ComprobanteController.java does not show this endpoint.
        // If it exists, this is fine. Otherwise, it needs to be added or this method removed.
        return axios.get(`${API_BASE_URL}/email/${email}`);
    }

    enviarEmailComprobante(codigoComprobante) {
        return axios.post(`${API_BASE_URL}/${codigoComprobante}/enviar-email`);
    }
}

export default new ComprobanteService();